/*
 * The MIT License
 *
 * Copyright 2017 starcatter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package thebob.ja2maptool.util.compositor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabViewModel;
import static thebob.ja2maptool.util.compositor.SnippetIO.decodeSnippet;
import static thebob.ja2maptool.util.compositor.SnippetIO.encodeSnippet;
import thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementLayer;

/**
 *
 * @author starcatter
 */
public class PlacementIO {

    public static final String PLACEMENTLIST_FILE_ID = "PLACEMENTLIST";
    public static final String PLACEMENTLIST_GROUP_FILE_ID = "PLACEMENTLIST_GROUP";

    public static void savePlacementListGroup(String fileName, List<MapSnippetPlacementLayer> layers) {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');

        // create a map of snippets used in this list
        Map<SelectedTiles, Integer> snippetLib = new HashMap<>();
        int idCounter = 0;
        for (MapSnippetPlacementLayer layer : layers) {
            for (SnippetPlacement placement : layer.getPlacements().values()) {
                if (!snippetLib.containsKey(placement.getSnippet())) {
                    System.out.println("thebob.ja2maptool.util.compositor.PlacementIO.savePlacementListGroup() save " + placement.getSnippet().getName() + " @" + idCounter);
                    snippetLib.put(placement.getSnippet(), idCounter++);
                }
            }
        }

        Object[] FILE_HEADER = {
            PLACEMENTLIST_GROUP_FILE_ID,
            layers.size(),
            snippetLib.size()
        };

        try (FileWriter fileWriter = new FileWriter(fileName); CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat)) {
            csvFilePrinter.printRecord(FILE_HEADER);

            snippetLib.forEach((snippet, id) -> {
                try {
                    List recordArray = new ArrayList();
                    recordArray.add(id);
                    recordArray.add(snippet.getName());
                    recordArray.add(encodeSnippet(snippet));
                    csvFilePrinter.printRecord(recordArray);
                } catch (IOException ex) {
                    Logger.getLogger(PlacementIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            for (MapSnippetPlacementLayer layer : layers) {

                List recordArray = new ArrayList();
                recordArray.add(layer.getName());
                recordArray.add(layer.getPlacements().size());
                csvFilePrinter.printRecord(recordArray);

                layer.getPlacements().forEach((cell, placement) -> {
                    try {
                        List recordArray2 = serializePlacement(placement, snippetLib);
                        // write file
                        csvFilePrinter.printRecord(recordArray2);
                    } catch (IOException ex) {
                        Logger.getLogger(PlacementIO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }

            System.out.println("CSV file was created successfully !!!");
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }
    }

    public static void savePlacementList(String fileName, Map<Integer, SnippetPlacement> snippets) {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');

        // create a map of snippets used in this list
        Map<SelectedTiles, Integer> snippetLib = new HashMap<>();
        snippets.forEach((cell, placement) -> {
            if (!snippetLib.containsKey(placement.getSnippet())) {
                snippetLib.put(placement.getSnippet(), snippetLib.size());
            }
        });

        Object[] FILE_HEADER = {
            PLACEMENTLIST_FILE_ID,
            snippets.size(),
            snippetLib.size()
        };

        try (FileWriter fileWriter = new FileWriter(fileName); CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat)) {
            csvFilePrinter.printRecord(FILE_HEADER);

            snippetLib.forEach((snippet, id) -> {
                try {
                    List recordArray = new ArrayList();
                    recordArray.add(id);
                    recordArray.add(snippet.getName());
                    recordArray.add(encodeSnippet(snippet));
                    csvFilePrinter.printRecord(recordArray);
                } catch (IOException ex) {
                    Logger.getLogger(PlacementIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            snippets.forEach((cell, placement) -> {
                try {
                    List recordArray = serializePlacement(placement, snippetLib);
                    // write file
                    csvFilePrinter.printRecord(recordArray);
                } catch (IOException ex) {
                    Logger.getLogger(PlacementIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            System.out.println("CSV file was created successfully !!!");
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }
    }

    public static List<MapSnippetPlacementLayer> loadPlacementListGroup(String fileName) {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');
        List<MapSnippetPlacementLayer> snippetGroup = new ArrayList<MapSnippetPlacementLayer>();
        try (CSVParser csv = CSVParser.parse(new File(fileName), Charset.forName("UTF-8"), csvFileFormat)) {

            CSVRecord header = csv.iterator().next();

            String fileTypeId = header.get(0);
            if (!fileTypeId.equals(PLACEMENTLIST_GROUP_FILE_ID)) {
                throw new RuntimeException("Not a placement list group file!");
            }

            int layerCount = Integer.valueOf(header.get(1));
            int snippetCount = Integer.valueOf(header.get(2));

            Map<Integer, SelectedTiles> snippetLib = new HashMap<Integer, SelectedTiles>();
            for (int i = 0; i < snippetCount; i++) {
                CSVRecord snippetRecord = csv.iterator().next();

                int id = Integer.valueOf(snippetRecord.get(0));
                String name = snippetRecord.get(1);
                String snippet64 = snippetRecord.get(2);

                SelectedTiles snippet = decodeSnippet(snippet64);
                snippet.setName(name);

                snippetLib.put(id, snippet);
            }

            for (int i = 0; i < layerCount; i++) {
                CSVRecord snippetGroupRecord = csv.iterator().next();

                String name = snippetGroupRecord.get(0);
                int count = Integer.valueOf(snippetGroupRecord.get(1));

                Map<Integer, SnippetPlacement> snippets = new HashMap<Integer, SnippetPlacement>(count);
                for (int j = 0; j < count; j++) {
                    CSVRecord snippetRecord = csv.iterator().next();
                    SnippetPlacement placement = loadPlacement(snippetRecord, snippetLib);
                    snippets.put(placement.getCell(), placement);
                }

                MapSnippetPlacementLayer layer = new MapSnippetPlacementLayer(name);
                layer.getPlacements().putAll(snippets);

                snippetGroup.add(layer);
            }
        } catch (IOException ex) {
            Logger.getLogger(TilesetMappingTabViewModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return snippetGroup;
    }

    public static Map<Integer, SnippetPlacement> loadPlacementList(String fileName) {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');
        Map<Integer, SnippetPlacement> snippets = new HashMap<Integer, SnippetPlacement>();
        try (CSVParser csv = CSVParser.parse(new File(fileName), Charset.forName("UTF-8"), csvFileFormat)) {

            CSVRecord header = csv.iterator().next();

            String fileTypeId = header.get(0);
            if (!fileTypeId.equals(PLACEMENTLIST_FILE_ID)) {
                throw new RuntimeException("Not a placement list file!");
            }

            int count = Integer.valueOf(header.get(1));
            int snippetCount = Integer.valueOf(header.get(2));

            Map<Integer, SelectedTiles> snippetLib = new HashMap<Integer, SelectedTiles>();
            for (int i = 0; i < snippetCount; i++) {
                CSVRecord snippetRecord = csv.iterator().next();

                int id = Integer.valueOf(snippetRecord.get(0));
                String name = snippetRecord.get(1);
                String snippet64 = snippetRecord.get(2);

                SelectedTiles snippet = decodeSnippet(snippet64);
                snippet.setName(name);

                snippetLib.put(id, snippet);
            }

            for (int i = 0; i < count; i++) {
                CSVRecord snippetRecord = csv.iterator().next();
                SnippetPlacement placement = loadPlacement(snippetRecord, snippetLib);
                snippets.put(placement.getCell(), placement);
            }
        } catch (IOException ex) {
            Logger.getLogger(TilesetMappingTabViewModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return snippets;
    }

    protected static List serializePlacement(SnippetPlacement placement, Map<SelectedTiles, Integer> snippetLib) {
        List recordArray = new ArrayList();
        // save placement locations
        recordArray.add(placement.getCellX());
        recordArray.add(placement.getCellY());
        recordArray.add(placement.getCell());

        // save placement options
        SelectionPlacementOptions options = placement.getEnabledLayers();
        if (options != null) {
            boolean[] layers = placement.getEnabledLayers().getAsArray();
            recordArray.add(layers[0]);
            recordArray.add(layers[1]);
            recordArray.add(layers[2]);
            recordArray.add(layers[3]);
            recordArray.add(layers[4]);
            recordArray.add(layers[5]);
            recordArray.add(placement.getEnabledLayers().isPlace_land_floors());
            recordArray.add(placement.getEnabledLayers().isPlace_structures_walls());
        } else {
            for (int i = 0; i < 8; i++) {
                recordArray.add(true);
            }
        }

        recordArray.add(snippetLib.get(placement.getSnippet()));
        // save places snippet        
        //recordArray.add(placement.getSnippet().getName());
        //recordArray.add(encodeSnippet(placement.getSnippet()));

        return recordArray;
    }

    protected static SnippetPlacement loadPlacement(CSVRecord snippetRecord, Map<Integer, SelectedTiles> snippetLib) {
        // load placement locations
        int cellX = Integer.valueOf(snippetRecord.get(0));
        int cellY = Integer.valueOf(snippetRecord.get(1));
        int cell = Integer.valueOf(snippetRecord.get(2));

        // load placement options
        boolean[] layers = new boolean[8];
        layers[0] = Boolean.valueOf(snippetRecord.get(3));
        layers[1] = Boolean.valueOf(snippetRecord.get(4));
        layers[2] = Boolean.valueOf(snippetRecord.get(5));
        layers[3] = Boolean.valueOf(snippetRecord.get(6));
        layers[4] = Boolean.valueOf(snippetRecord.get(7));
        layers[5] = Boolean.valueOf(snippetRecord.get(8));
        layers[6] = Boolean.valueOf(snippetRecord.get(9));
        layers[7] = Boolean.valueOf(snippetRecord.get(10));
        SelectionPlacementOptions options = new SelectionPlacementOptions(layers);

        int snippetId = Integer.valueOf(snippetRecord.get(11));
        SelectedTiles snippet = snippetLib.get(snippetId);
        // load snippet        
        //String name = snippetRecord.get(11);
        //String snippet64 = snippetRecord.get(12);

        //SelectedTiles snippet = decodeSnippet(snippet64);
        //snippet.setName(name);
        SnippetPlacement placement = new SnippetPlacement(cellX, cellY, cell, snippet, options);
        return placement;
    }

}
