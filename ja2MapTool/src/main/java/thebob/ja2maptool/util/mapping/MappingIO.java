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
package thebob.ja2maptool.util.mapping;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;
import thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabViewModel;

public class MappingIO {

    // tile mapping file header
    public static final String TILEMAP_FILE_ID = "TILEMAP";
    public static final String ITEMMAP_FILE_ID = "ITEMMAP";

    public static TileMappingFileData tilesetMappingScopeToFileData(TilesetMappingScope mappingScope) {
	return new TileMappingFileData(
		mappingScope.getMappingList(),
		mappingScope.getSourceTilesetId(),
		mappingScope.getTargetTilesetId(),
		mappingScope.getSourceAssets().getTilesets().getNumFiles(),
		mappingScope.getSourceAssets().getVfs().getPath().getParent().toAbsolutePath().toString(),
		mappingScope.getSourceAssets().getVfs().getPath().getFileName().toString(),
		mappingScope.getTargetAssets().getVfs().getPath().getParent().toAbsolutePath().toString(),
		mappingScope.getTargetAssets().getVfs().getPath().getFileName().toString()
	);
    }

    public static void saveTilesetMapping(String fileName, TilesetMappingScope mappingScope) {
	saveTilesetMapping(fileName, tilesetMappingScopeToFileData(mappingScope));
    }

    public static void saveTilesetMapping(String fileName, TileMappingFileData mappingFileData) {
	CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');

	Object[] FILE_HEADER = {
	    TILEMAP_FILE_ID,
	    String.valueOf(mappingFileData.getSourceTilesetId()),
	    String.valueOf(mappingFileData.getTargetTilesetId()),
	    String.valueOf(mappingFileData.getTileCategoryCount()),
	    mappingFileData.getSrcConfDir(),
	    mappingFileData.getSrcConf(),
	    mappingFileData.getDstConfDir(),
	    mappingFileData.getDstConf()
	};

	try (FileWriter fileWriter = new FileWriter(fileName); CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat)) {
	    csvFilePrinter.printRecord(FILE_HEADER);

	    for (Map.Entry<Integer, TileCategoryMapping> mappingForType : mappingFileData.getMappingList().entrySet()) {
		List mappingArray = new ArrayList();
		mappingArray.add(String.valueOf(mappingForType.getKey()));
		mappingArray.add(String.valueOf(mappingForType.getValue().getMappings().size()));
		csvFilePrinter.printRecord(mappingArray);

		for (thebob.ja2maptool.model.TileMapping mapping : mappingForType.getValue().getMappings()) {
		    List mappingData = new ArrayList();
		    mappingData.add(String.valueOf(mapping.getSourceIndex()));
		    mappingData.add(String.valueOf(mapping.getTargetType()));
		    mappingData.add(String.valueOf(mapping.getTargetIndex()));

		    csvFilePrinter.printRecord(mappingData);
		}
	    }

	    System.out.println("CSV file was created successfully !!!");

	} catch (Exception e) {
	    System.out.println("Error in CsvFileWriter !!!");
	    e.printStackTrace();
	}
    }

    public static TileMappingFileData loadTilesetMapping(String fileName) {
	TileMappingFileData mappingFileData = new TileMappingFileData();
	CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');

	try (CSVParser csv = CSVParser.parse(new File(fileName), Charset.forName("UTF-8"), csvFileFormat)) {

	    CSVRecord header = csv.iterator().next();

	    String fileTypeId = header.get(0);
	    if (!fileTypeId.equals(TILEMAP_FILE_ID)) {
		throw new RuntimeException("Not a tile mapping file!");
	    }

	    int srcId = Integer.valueOf(header.get(1));
	    int dstId = Integer.valueOf(header.get(2));

	    int count = Integer.valueOf(header.get(3));

	    String srcConfDir = header.get(4);
	    String srcConf = header.get(5);
	    String dstConfDir = header.get(6);
	    String dstConf = header.get(7);

	    mappingFileData.setSourceTilesetId(srcId);
	    mappingFileData.setTargetTilesetId(dstId);

	    mappingFileData.setTileCategoryCount(count);

	    mappingFileData.setSrcConfDir(srcConfDir);
	    mappingFileData.setSrcConf(srcConf);
	    mappingFileData.setDstConfDir(dstConfDir);
	    mappingFileData.setDstConf(dstConf);

	    // TODO: check if file looks good, ask user to confirm load if something looks off
	    for (int i = 0; i < count; i++) {
		CSVRecord typeMappingHeader = csv.iterator().next();
		int type = Integer.valueOf(typeMappingHeader.get(0));
		int tileCount = Integer.valueOf(typeMappingHeader.get(1));

		ObservableList<TileMapping> mappingListForType = FXCollections.observableArrayList();
		mappingFileData.getMappingList().put(type, new TileCategoryMapping(type, mappingListForType));

		for (int j = 0; j < tileCount; j++) {
		    CSVRecord typeMapping = csv.iterator().next();
		    int srcIndex = Integer.valueOf(typeMapping.get(0));
		    int dstType = Integer.valueOf(typeMapping.get(1));
		    int dstIndex = Integer.valueOf(typeMapping.get(2));

		    mappingListForType.add(new TileMapping(type, srcIndex, dstType, dstIndex));
		}
	    }
	} catch (IOException ex) {
	    Logger.getLogger(TilesetMappingTabViewModel.class.getName()).log(Level.SEVERE, null, ex);
	}

	return mappingFileData;
    }

    public static ItemMappingFileData itemMappingScopeToFileData(ItemMappingScope mappingScope) {
	return new ItemMappingFileData(
		mappingScope.getMappingAsMap(),
		mappingScope.getSourceAssets().getVfs().getPath().getParent().toAbsolutePath().toString(),
		mappingScope.getSourceAssets().getVfs().getPath().getFileName().toString(),
		mappingScope.getTargetAssets().getVfs().getPath().getParent().toAbsolutePath().toString(),
		mappingScope.getTargetAssets().getVfs().getPath().getFileName().toString()
	);
    }

    public static void saveItemMapping(String fileName, ItemMappingScope mappingScope) {
	saveItemMapping(fileName, itemMappingScopeToFileData(mappingScope));
    }

    public static void saveItemMapping(String fileName, ItemMappingFileData mappingFileData) {
	CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');

	Map<Integer, Integer> mapping = mappingFileData.getMapping();
	// TODO add mod info here
	Object[] FILE_HEADER = {
	    ITEMMAP_FILE_ID,
	    String.valueOf(mapping.size()),
	    mappingFileData.getSrcConfDir(),
	    mappingFileData.getSrcConf(),
	    mappingFileData.getDstConfDir(),
	    mappingFileData.getDstConf()
	};

	try (FileWriter fileWriter = new FileWriter(fileName); CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat)) {
	    csvFilePrinter.printRecord(FILE_HEADER);

	    for (Map.Entry<Integer, Integer> map : mapping.entrySet()) {
		List mappingArray = new ArrayList();
		mappingArray.add(String.valueOf(map.getKey()));
		mappingArray.add(String.valueOf(map.getValue()));
		csvFilePrinter.printRecord(mappingArray);
	    }

	    fileWriter.flush();

	    System.out.println("CSV file was created successfully !!!");
	} catch (Exception e) {
	    System.out.println("Error in CsvFileWriter !!!");
	    e.printStackTrace();
	}
    }

    public static ItemMappingFileData loadItemMapping(String fileName) {
	ItemMappingFileData mappingFileData = new ItemMappingFileData();
	Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');

	try (CSVParser csv = CSVParser.parse(new File(fileName), Charset.forName("UTF-8"), csvFileFormat)) {

	    CSVRecord header = csv.iterator().next();

	    String fileTypeId = header.get(0);
	    if (!fileTypeId.equals(ITEMMAP_FILE_ID)) {
		throw new RuntimeException("Not a item mapping file!");
	    }

	    int count = Integer.valueOf(header.get(1));

	    String srcConfDir = header.get(2);
	    String srcConf = header.get(3);
	    String dstConfDir = header.get(4);
	    String dstConf = header.get(5);

	    mappingFileData.setSrcConfDir(srcConfDir);
	    mappingFileData.setSrcConf(srcConf);
	    mappingFileData.setDstConfDir(dstConfDir);
	    mappingFileData.setDstConf(dstConf);

	    for (int i = 0; i < count; i++) {
		CSVRecord typeMappingHeader = csv.iterator().next();
		int srcId = Integer.valueOf(typeMappingHeader.get(0));
		int dstId = Integer.valueOf(typeMappingHeader.get(1));

		mapping.put(srcId, dstId);
	    }
	} catch (IOException ex) {
	    Logger.getLogger(TilesetMappingTabViewModel.class.getName()).log(Level.SEVERE, null, ex);
	}

	mappingFileData.setMapping(mapping);

	return mappingFileData;
    }

}
