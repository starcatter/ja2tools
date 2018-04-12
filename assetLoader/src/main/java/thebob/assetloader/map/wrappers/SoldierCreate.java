/* 
 * The MIT License
 *
 * Copyright 2017 the_bob.
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
package thebob.assetloader.map.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.helpers.GridPos;
import thebob.assetloader.map.structures.BASIC_SOLDIERCREATE_STRUCT;
import thebob.assetloader.map.structures.SOLDIERCREATE_STRUCT;
import thebob.assetloader.map.structures.legacy.OLD_BASIC_SOLDIERCREATE_STRUCT;
import thebob.assetloader.map.structures.legacy.OLD_OBJECTTYPE_101;
import thebob.assetloader.map.structures.legacy.OLD_SOLDIERCREATE_STRUCT;
import thebob.assetloader.map.structures.legacy.OLD_SOLDIERCREATE_STRUCT_101;

/**
 *
 * @author the_bob
 */
public class SoldierCreate extends MapLoaderWrapperBase {

    BASIC_SOLDIERCREATE_STRUCT placementInfo = null;
    SOLDIERCREATE_STRUCT detailedPlacementInfo = null;
    List<ObjectStack> detailedPlacementInventory = new ArrayList<ObjectStack>();

    public boolean isDetailed() {
        return placementInfo != null && placementInfo.fDetailedPlacement.get();
    }

    private BASIC_SOLDIERCREATE_STRUCT getBasicStruct(ByteBuffer source) {
        if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.getBasicStruct(): start @ "+source.position());
        float dMajorMapVersion = map.getSettings().dMajorMapVersion;

        BASIC_SOLDIERCREATE_STRUCT soldier = new BASIC_SOLDIERCREATE_STRUCT();
        if (dMajorMapVersion < 7.0) {

            OLD_BASIC_SOLDIERCREATE_STRUCT oldStruct = new OLD_BASIC_SOLDIERCREATE_STRUCT();

            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.SoldierCreate.load(): loading <<OLD>> placement struct... (" + oldStruct.size() + "B)");
            }

            oldStruct.setSource(source);

            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.SoldierCreate.load(): " + oldStruct);
            }

            soldier.loadOld(oldStruct);
        } else {
            soldier.setSource(source);
            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.SoldierCreate.load(): " + soldier);
            }
        }

        // verify gridno makes sense!
        new GridPos(soldier.usStartingGridNo.get());
        if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.getBasicStruct(): end @ "+source.position());
        return soldier;
    }

    private SOLDIERCREATE_STRUCT getStruct(ByteBuffer source) {
        if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.getStruct(): start @ "+source.position());
        float dMajorMapVersion = map.getSettings().dMajorMapVersion;
        int ubMinorMapVersion = map.getSettings().ubMinorMapVersion;

        SOLDIERCREATE_STRUCT soldier = new SOLDIERCREATE_STRUCT();

        if (dMajorMapVersion >= 6.0 && ubMinorMapVersion > 26) {
            if (dMajorMapVersion < 7.0) {
                if (MapLoader.logEverything) {
                    System.out.println("loader.wrappers.SoldierCreate.load(): \tloading <<OLD>> detailed placement...");
                }
                OLD_SOLDIERCREATE_STRUCT OldSavedSoldierInfo = new OLD_SOLDIERCREATE_STRUCT();
                OldSavedSoldierInfo.setSource(source);

                if (MapLoader.logEverything) {
                    System.out.println("loader.wrappers.SoldierCreate.load(): " + OldSavedSoldierInfo);
                }

                soldier.loadOld(OldSavedSoldierInfo);

            } else {
                soldier.setSource(source);
            }

            if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.getStruct(): end @ "+source.position() + ", size="+soldier.size() + ", adjust="+soldier.getOffsetAdjustment());
            if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.getStruct():"+soldier);
            
            if (!loadInventory(source)) {
                return null;
            }
        } else {
            OLD_SOLDIERCREATE_STRUCT_101 OldSavedSoldierInfo101 = new OLD_SOLDIERCREATE_STRUCT_101();
            OldSavedSoldierInfo101.setSource(source);

            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.SoldierCreate.load(): \tloading !!!VERY OLD!!! detailed placement...(" + OldSavedSoldierInfo101.size() + "B)");
                System.out.println(OldSavedSoldierInfo101);
            }

            // 1 - convert to new struct
            soldier.loadOld(OldSavedSoldierInfo101);

            // 2 - build inventory
            for (OLD_OBJECTTYPE_101 oldItem : OldSavedSoldierInfo101.inv) {
                ObjectStack invStack = new ObjectStack();
                invStack.loadOld(oldItem);
                detailedPlacementInventory.add(invStack);
            }
        }

        // verify gridno makes sense!
         new GridPos(soldier.sInsertionGridNo.get());

        return soldier;
    }

    private boolean loadInventory(ByteBuffer source) {
        if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.loadInventory(): start @ "+source.position());
        int sizeInventory = source.getInt();

        if (sizeInventory < 0 || sizeInventory > 55) {
            System.out.println("loader.wrappers.SoldierCreate.load(): Inventory size mismatch: " + sizeInventory);
            return false;
        } else if (MapLoader.logEverything) {
            System.out.println("loader.wrappers.SoldierCreate.load(): \t\tLOADING INVENTORY (" + sizeInventory + ")");
        }

        for (int i = 0; i < sizeInventory; ++i) {

            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.SoldierCreate.load(): \t\tloading item slot " + (i + 1) + " / " + sizeInventory + "...");
            }

            ObjectStack stack = new ObjectStack();
            stack.loadAsset(map);
            detailedPlacementInventory.add(stack);

            int bNewItemCount = source.getInt();
            int bNewItemCycleCount = source.getInt();

            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.SoldierCreate.load(): \t\tbNewItemCount=" + bNewItemCount);
                System.out.println("loader.wrappers.SoldierCreate.load(): \t\tbNewItemCycleCount=" + bNewItemCycleCount);
            }

        }
        if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.loadInventory(): end @ "+source.position());
        return true;
    }

    protected void load(ByteBuffer source) {
        if (MapLoader.logEverything) {
            System.out.println("loader.wrappers.SoldierCreate.load(): loading basic placement...");
        }
        placementInfo = getBasicStruct(source);

        if (placementInfo.fDetailedPlacement.get()) {
            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.SoldierCreate.load(): \tloading detailed placement...");
            }
            detailedPlacementInfo = getStruct(source);

            if (detailedPlacementInfo == null) {
                throw new RuntimeException("Error loading soldier placement!");
            }
        }
    }

    public void saveTo(ByteBuffer outputBuffer) {
        try {
            ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();
            placementInfo.write(itemAsBytes);
            if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.saveTo() basic  @ "+outputBuffer.position());
            outputBuffer.put(itemAsBytes.toByteArray());
            itemAsBytes.flush();
            itemAsBytes.reset();
            
            if (placementInfo.fDetailedPlacement.get()) {
                detailedPlacementInfo.write(itemAsBytes);
                if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.saveTo() detailedPlacementInfo  @ "+outputBuffer.position());
                if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.saveTo():"+detailedPlacementInfo);
                outputBuffer.put(itemAsBytes.toByteArray());
                if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.saveTo() detailedPlacementInfo end @ "+outputBuffer.position());
                outputBuffer.position( outputBuffer.position() + detailedPlacementInfo.getOffsetAdjustment() );
                if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.saveTo() detailedPlacementInfo adjust for struct size @ "+outputBuffer.position());
                
                if(MapLoader.logFileIO) System.out.println("loader.wrappers.SoldierCreate.saveTo() detailedPlacementInventory.size @ "+outputBuffer.position());
                outputBuffer.putInt(detailedPlacementInventory.size());
                for (ObjectStack stack : detailedPlacementInventory) {
                    stack.saveTo(outputBuffer);
                    
                    outputBuffer.putInt(0);
                    outputBuffer.putInt(0);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
