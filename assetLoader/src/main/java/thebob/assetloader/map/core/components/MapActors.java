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
package thebob.assetloader.map.core.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.map.structures.SCHEDULENODE;
import thebob.assetloader.map.structures.legacy.OLD_SCHEDULENODE;
import thebob.assetloader.map.wrappers.SoldierCreate;
import thebob.assetloader.map.wrappers.WorldItemStack;

/**
 *
 * @author the_bob
 */
public class MapActors extends MapComponent {

    public class ItemClassTypes {

        public static final int IC_NONE = 0x00000001;
        public static final int IC_GUN = 0x00000002;
        public static final int IC_BLADE = 0x00000004;
        public static final int IC_THROWING_KNIFE = 0x00000008;
        public static final int IC_LAUNCHER = 0x00000010;
        public static final int IC_TENTACLES = 0x00000020;
        public static final int IC_THROWN = 0x00000040;
        public static final int IC_PUNCH = 0x00000080;
        public static final int IC_GRENADE = 0x00000100;
        public static final int IC_BOMB = 0x00000200;
        public static final int IC_AMMO = 0x00000400;
        public static final int IC_ARMOUR = 0x00000800;
        public static final int IC_MEDKIT = 0x00001000;
        public static final int IC_KIT = 0x00002000;
        public static final int IC_APPLIABLE = 0x00004000;
        public static final int IC_FACE = 0x00008000;
        public static final int IC_KEY = 0x00010000;
        public static final int IC_LBEGEAR = 0x00020000; // Added for LBE items as part of the new inventory system;
        public static final int IC_BELTCLIP = 0x00040000; // Added for Belt Clip items;
        public static final int IC_MISC = 0x10000000;
        public static final int IC_MONEY = 0x20000000;
        public static final int IC_RANDOMITEM = 0x40000000; // added by Flugente for random items;    
    }

    private List<SCHEDULENODE> schedules = new ArrayList<SCHEDULENODE>();
    private List<WorldItemStack> items = new ArrayList<WorldItemStack>();
    private List<SoldierCreate> soldierPlacements = new ArrayList<SoldierCreate>();

    private int ubNumSchedules = -1;
    private long uiNumWorldItems = -1;
    private int numPlacements = -1;

    public MapActors(MapData source) {
        super(source);
    }

    public List<WorldItemStack> getItems() {
	return items;
    }

    public List<SoldierCreate> getSoldierPlacements() {
	return soldierPlacements;
    }

    
    public boolean loadSchedules() {
        schedules.clear();
        if(MapLoader.logFileIO) System.out.println("\n\nloader.core.MapActors.loadSchedules() start @"+byteBuffer.position());
        try {
            ubNumSchedules = byteBuffer.get() & 0xFF;
            if (ubNumSchedules < 1) {
                if (MapLoader.logEverything) {
                    System.out.println("\n\n\tloadData() Schedules saved yet count < 1!!!\n\n");
                }
            }
            for (int cnt = 0; cnt < ubNumSchedules; cnt++) {

                SCHEDULENODE schedule = new SCHEDULENODE();

                if (settings().dMajorMapVersion < 7.0) {
                    OLD_SCHEDULENODE schedule_old = new OLD_SCHEDULENODE();
                    schedule_old.setSource(byteBuffer);
                    schedule.loadOld(schedule_old);
                } else {
                    schedule.setSource(byteBuffer);
                }

                if (MapLoader.logEverything) {
                    System.out.println("loadSchedules(): Loaded\n\t" + schedule);
                }

                schedules.add(schedule);
            }
            if(MapLoader.logFileIO) System.out.println("\n\nloader.core.MapActors.loadSchedules() end @"+byteBuffer.position());
            return true;
        } catch (Exception e) {
            System.out.println("loadWorldItems(): " + e.toString());
            if (MapLoader.logEverything) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean loadWorldItems() {        
        items.clear();
        try {
            if(MapLoader.logFileIO) System.out.println("\n\nloader.core.MapActors.loadWorldItems() start @"+byteBuffer.position());
            uiNumWorldItems = byteBuffer.getInt() & 0xffffffffl;
            if (MapLoader.logEverything) {
                System.out.println("loadData(): Loading " + uiNumWorldItems + " items:");
            }
            for (int i = 0; i < uiNumWorldItems; i++) {
                if (MapLoader.logEverything) {
                    System.out.println("\n\nloadWorldItems(): reading item " + (i + 1) + " @" + byteBuffer.position());
                }
                WorldItemStack worldItemProxy = new WorldItemStack();
                worldItemProxy.loadAsset(map);
                items.add(worldItemProxy);
            }
            if(MapLoader.logFileIO) System.out.println("\n\nloader.core.MapActors.loadWorldItems() end @"+byteBuffer.position());
            return true;
        } catch (Exception e) {
            System.out.println("loadWorldItems(): " + e.toString());
            if (MapLoader.logEverything) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // TODO: check backward compat!
    public boolean loadPlacements() {
        if(MapLoader.logFileIO) System.out.println("\n\nloader.core.MapActors.loadPlacements() start @"+byteBuffer.position());
        soldierPlacements.clear();
        try {
            numPlacements = settings().mapInfo.ubNumIndividuals.get();
            for (int cnt = 0; cnt < numPlacements; cnt++) {
                if (MapLoader.logEverything) {
                    System.out.println("loadPlacements(): Loading soldier " + (cnt + 1) + " / " + numPlacements + " @"+byteBuffer.position());
                }
                SoldierCreate soldier = new SoldierCreate();
                soldier.loadAsset(map);
                soldierPlacements.add(soldier);
            }
            if(MapLoader.logFileIO) System.out.println("\n\nloader.core.MapActors.loadPlacements() end @"+byteBuffer.position());
            return true;
        } catch (Exception e) {
            System.out.println("loadWorldItems(): " + e.toString());
            if (MapLoader.logEverything) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String toString() {

        int advP = 0;
        if (soldierPlacements != null) {
            for (SoldierCreate p : soldierPlacements) {
                if (p.isDetailed()) {
                    advP++;
                }
            }
        }
        return "\tLoaded items: "
                + (items != null ? (items.size() + "/" + uiNumWorldItems) : "ERROR LOADING ITEMS, declared items: " + uiNumWorldItems)
                + "\n\tLoaded placements: "
                + (soldierPlacements != null ? (soldierPlacements.size() + "/" + settings().mapInfo.ubNumIndividuals.get() + ", " + advP + " detailed placements") : "ERROR LOADING PLACEMENTS!")
                + "\n\tLoaded schedules: "
                + (schedules != null ? (schedules.size() + "/" + ubNumSchedules) : "ERROR LOADING SCHEDULES!");

    }

    public void saveWorldItems(ByteBuffer outputBuffer) {
        if(MapLoader.logFileIO) System.out.println("\nloader.core.MapActors.saveWorldItems() start @"+outputBuffer.position());
        outputBuffer.putInt(items.size());
        for (WorldItemStack item : items) {
            item.saveTo(outputBuffer);
        }
        if(MapLoader.logFileIO) System.out.println("\nloader.core.MapActors.saveWorldItems() end @"+outputBuffer.position());
    }

    public void savePlacements(ByteBuffer outputBuffer) {
        if(MapLoader.logFileIO) System.out.println("\nloader.core.MapActors.savePlacements() start @"+outputBuffer.position());
        for (SoldierCreate item : soldierPlacements) {
            item.saveTo(outputBuffer);
        }
        if(MapLoader.logFileIO) System.out.println("\nloader.core.MapActors.savePlacements() end @"+outputBuffer.position());
    }

    public void saveSchedules(ByteBuffer outputBuffer) {
        if(MapLoader.logFileIO) System.out.println("\nloader.core.MapActors.saveSchedules() start @"+outputBuffer.position());
        outputBuffer.putInt(schedules.size());
        ByteArrayOutputStream schedulesAsBytes = new ByteArrayOutputStream();
        try {
            for (SCHEDULENODE item : schedules) {
                item.write(schedulesAsBytes);
            }
            outputBuffer.put(schedulesAsBytes.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(MapActors.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(MapLoader.logFileIO) System.out.println("\nloader.core.MapActors.saveSchedules() end @"+outputBuffer.position());
    }

}
