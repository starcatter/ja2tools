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
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.map.structures.MAPCREATE_STRUCT;
import thebob.assetloader.map.structures.legacy.OLD_MAPCREATE_STRUCT;
import thebob.assetloader.map.wrappers.ObjectStack;
import thebob.assetloader.map.wrappers.WorldItemStack;

/**
 *
 * @author the_bob
 */
public class MapSettings extends MapComponent {

    private static int MAP_FULLSOLDIER_SAVED_MASK = 0x00000001;
    private static int MAP_WORLDONLY_SAVED_MASK = 0x00000002;
    private static int MAP_WORLDLIGHTS_SAVED_MASK = 0x00000004;
    private static int MAP_WORLDITEMS_SAVED_MASK = 0x00000008;
    private static int MAP_EXITGRIDS_SAVED_MASK = 0x00000010;
    private static int MAP_DOORTABLE_SAVED_MASK = 0x00000020;
    private static int MAP_EDGEPOINTS_SAVED_MASK = 0x00000040;
    private static int MAP_AMBIENTLIGHTLEVEL_SAVED_MASK = 0x00000080;
    private static int MAP_NPCSCHEDULES_SAVED_MASK = 0x00000100;

    // values for ensuring the header isn't garbage - at this time major=8, minor=31
    public static final float MIN_REASONABLE_MAJOR_MAP_VERSION = 4;
    public static final float MAX_REASONABLE_MAJOR_MAP_VERSION = 10;
    public static final int MIN_REASONABLE_MINOR_MAP_VERSION = 10;
    public static final int MAX_REASONABLE_MINOR_MAP_VERSION = 40;

    public static final int MINOR_MAP_OVERHEATING = 28;		// 27 -> 28: Flugente:: increased to 28 because of included weapon overheated and tripwire feature. See ObjectClass for the new Tags!
    public static final int MINOR_MAP_32BITROOMS = 29;		// 28 -> 29: increased range of roomnumbers to full UINT16 by DBrot
    public static final int MINOR_MAP_REPAIR_SYSTEM = 30;	// 30 -> 29: Flugente:: necessary change due to repair system
    public static final int MINOR_MAP_ITEMFLAG64 = 31;		// 30 -> 31: Flugente: changed sObjectFlag from INT32 to UINT64

    public static final float MAJOR_MAP_VERSION = 8.0f;
    public static final int MINOR_MAP_VERSION = MINOR_MAP_ITEMFLAG64;

    public static final int OLD_WORLD_COLS = 160;
    public static final int OLD_WORLD_ROWS = 160;
    public static final int OLD_WORLD_MAX = OLD_WORLD_COLS * OLD_WORLD_ROWS;
    public static int WORLD_MAX = OLD_WORLD_COLS * OLD_WORLD_ROWS;

    private int uiFlags;

    public boolean MAP_FULLSOLDIER_SAVED;
    public boolean MAP_WORLDONLY_SAVED;
    public boolean MAP_WORLDLIGHTS_SAVED;
    public boolean MAP_WORLDITEMS_SAVED;
    public boolean MAP_EXITGRIDS_SAVED;
    public boolean MAP_DOORTABLE_SAVED;
    public boolean MAP_EDGEPOINTS_SAVED;
    public boolean MAP_AMBIENTLIGHTLEVEL_SAVED;
    public boolean MAP_NPCSCHEDULES_SAVED;

    public boolean gfBasement;
    public boolean gfCaves;

    public int iRowSize;
    public int iColSize;
    public int iWorldSize;

    public float dMajorMapVersion;
    public byte ubMinorMapVersion;
    public int iTilesetID;
    public long uiSoldierSize;
    public MAPCREATE_STRUCT mapInfo;

    public MapSettings(MapData source) {
        super(source);
    }

    public void LoadMapInfo() {
        mapInfo = new MAPCREATE_STRUCT();

        if (dMajorMapVersion < 7.0) {
            OLD_MAPCREATE_STRUCT oldMapInfo = new OLD_MAPCREATE_STRUCT();
            oldMapInfo.setSource(byteBuffer);

            mapInfo.sNorthGridNo.set(oldMapInfo.sNorthGridNo.get());
            mapInfo.sEastGridNo.set(oldMapInfo.sEastGridNo.get());
            mapInfo.sSouthGridNo.set(oldMapInfo.sSouthGridNo.get());
            mapInfo.sWestGridNo.set(oldMapInfo.sWestGridNo.get());
            mapInfo.ubNumIndividuals.set(oldMapInfo.ubNumIndividuals.get());
            mapInfo.ubMapVersion.set(oldMapInfo.ubMapVersion.get());
            mapInfo.ubRestrictedScrollID.set(oldMapInfo.ubRestrictedScrollID.get());
            mapInfo.ubEditorSmoothingType.set(oldMapInfo.ubEditorSmoothingType.get());
            mapInfo.sCenterGridNo.set(oldMapInfo.sCenterGridNo.get());
            mapInfo.sIsolatedGridNo.set(oldMapInfo.sIsolatedGridNo.get());

        } else {
            mapInfo.setSource(byteBuffer);
        }
    }

    protected void parseFlags() {
        MAP_FULLSOLDIER_SAVED = (boolean) ((uiFlags & MAP_FULLSOLDIER_SAVED_MASK) > 0);
        MAP_WORLDONLY_SAVED = (boolean) ((uiFlags & MAP_WORLDONLY_SAVED_MASK) > 0);
        MAP_WORLDLIGHTS_SAVED = (boolean) ((uiFlags & MAP_WORLDLIGHTS_SAVED_MASK) > 0);
        MAP_WORLDITEMS_SAVED = (boolean) ((uiFlags & MAP_WORLDITEMS_SAVED_MASK) > 0);
        MAP_EXITGRIDS_SAVED = (boolean) ((uiFlags & MAP_EXITGRIDS_SAVED_MASK) > 0);
        MAP_DOORTABLE_SAVED = (boolean) ((uiFlags & MAP_DOORTABLE_SAVED_MASK) > 0);
        MAP_EDGEPOINTS_SAVED = (boolean) ((uiFlags & MAP_EDGEPOINTS_SAVED_MASK) > 0);
        MAP_AMBIENTLIGHTLEVEL_SAVED = (boolean) ((uiFlags & MAP_AMBIENTLIGHTLEVEL_SAVED_MASK) > 0);
        MAP_NPCSCHEDULES_SAVED = (boolean) ((uiFlags & MAP_NPCSCHEDULES_SAVED_MASK) > 0);
    }

    protected void encodeFlags() {
        uiFlags = 0;

        uiFlags |= MAP_FULLSOLDIER_SAVED ? MAP_FULLSOLDIER_SAVED_MASK : 0;
        uiFlags |= MAP_WORLDONLY_SAVED ? MAP_WORLDONLY_SAVED_MASK : 0;
        uiFlags |= MAP_WORLDLIGHTS_SAVED ? MAP_WORLDLIGHTS_SAVED_MASK : 0;
        uiFlags |= MAP_WORLDITEMS_SAVED ? MAP_WORLDITEMS_SAVED_MASK : 0;
        uiFlags |= MAP_EXITGRIDS_SAVED ? MAP_EXITGRIDS_SAVED_MASK : 0;
        uiFlags |= MAP_DOORTABLE_SAVED ? MAP_DOORTABLE_SAVED_MASK : 0;
        uiFlags |= MAP_EDGEPOINTS_SAVED ? MAP_EDGEPOINTS_SAVED_MASK : 0;
        uiFlags |= MAP_AMBIENTLIGHTLEVEL_SAVED ? MAP_AMBIENTLIGHTLEVEL_SAVED_MASK : 0;
        uiFlags |= MAP_NPCSCHEDULES_SAVED ? MAP_NPCSCHEDULES_SAVED_MASK : 0;
    }

    private boolean checkForBogusHeaderData(){
        if( dMajorMapVersion < MIN_REASONABLE_MAJOR_MAP_VERSION || dMajorMapVersion > MAX_REASONABLE_MAJOR_MAP_VERSION ) return false;
        if( ubMinorMapVersion < MIN_REASONABLE_MINOR_MAP_VERSION || ubMinorMapVersion > MAX_REASONABLE_MINOR_MAP_VERSION ) return false;
        
        return true;
    }
    
    public boolean loadMapHeader() {
        try {
            // Read JA2 Version ID
            dMajorMapVersion = byteBuffer.getFloat();
            ubMinorMapVersion = byteBuffer.get();
            iRowSize = OLD_WORLD_ROWS;
            iColSize = OLD_WORLD_COLS;
            if (dMajorMapVersion >= 7.00) {
                iRowSize = byteBuffer.getInt();
                iColSize = byteBuffer.getInt();
            }
            // Actual world size of the map we loaded!
            iWorldSize = iRowSize * iColSize;
            // Read FLAGS FOR WORLD
            uiFlags = byteBuffer.getInt();
            iTilesetID = byteBuffer.getInt();
            // Load soldier size
            uiSoldierSize = byteBuffer.getInt() & 0xffffffffl;
            parseFlags();
            if (MapLoader.logEverything) {
                System.out.println("loader.core.MapSettings.loadMapHeader(): "+toString());
                //System.out.println("loadMapHeader(): flags\n\t" + "MAP_FULLSOLDIER_SAVED: " + MAP_FULLSOLDIER_SAVED + "\n\t" + "MAP_WORLDONLY_SAVED: " + MAP_WORLDONLY_SAVED + "\n\t" + "MAP_WORLDLIGHTS_SAVED: " + MAP_WORLDLIGHTS_SAVED + "\n\t" + "MAP_WORLDITEMS_SAVED: " + MAP_WORLDITEMS_SAVED + "\n\t" + "MAP_EXITGRIDS_SAVED: " + MAP_EXITGRIDS_SAVED + "\n\t" + "MAP_DOORTABLE_SAVED: " + MAP_DOORTABLE_SAVED + "\n\t" + "MAP_EDGEPOINTS_SAVED: " + MAP_EDGEPOINTS_SAVED + "\n\t" + "MAP_AMBIENTLIGHTLEVEL_SAVED: " + MAP_AMBIENTLIGHTLEVEL_SAVED + "\n\t" + "MAP_NPCSCHEDULES_SAVED: " + MAP_NPCSCHEDULES_SAVED + "\n\t");
            }
            
            return checkForBogusHeaderData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        if (mapInfo != null) {
            return "\n\tiRowSize=" + iRowSize + "\n\tiColSize=" + iColSize + "\n\tdMajorMapVersion=" + dMajorMapVersion + "\n\tubMinorMapVersion=" + ubMinorMapVersion + "\n\tubMapVersion: " + mapInfo.ubMapVersion.get() + "\n\tubNumIndividuals: " + mapInfo.ubNumIndividuals.get() + "\n\tiWorldSize=" + iWorldSize + "\n\tuiFlags:" + "\n\t\tMAP_FULLSOLDIER_SAVED: " + MAP_FULLSOLDIER_SAVED + "\n\t\tMAP_WORLDONLY_SAVED: " + MAP_WORLDONLY_SAVED + "\n\t\tMAP_WORLDLIGHTS_SAVED: " + MAP_WORLDLIGHTS_SAVED + "\n\t\tMAP_WORLDITEMS_SAVED: " + MAP_WORLDITEMS_SAVED + "\n\t\tMAP_EXITGRIDS_SAVED: " + MAP_EXITGRIDS_SAVED + "\n\t\tMAP_DOORTABLE_SAVED: " + MAP_DOORTABLE_SAVED + "\n\t\tMAP_EDGEPOINTS_SAVED: " + MAP_EDGEPOINTS_SAVED + "\n\t\tMAP_AMBIENTLIGHTLEVEL_SAVED: " + MAP_AMBIENTLIGHTLEVEL_SAVED + "\n\t\tMAP_NPCSCHEDULES_SAVED: " + MAP_NPCSCHEDULES_SAVED + "\n\tiTilesetID=" + iTilesetID + "\n\tuiSoldierSize=" + uiSoldierSize;
        } else {
            return "\n\tiRowSize=" + iRowSize + "\n\tiColSize=" + iColSize + "\n\tdMajorMapVersion=" + dMajorMapVersion + "\n\tubMinorMapVersion=" + ubMinorMapVersion + "\n\tubMapVersion: MAPINFO FAILED TO LOAD \n\tubNumIndividuals: MAPINFO FAILED TO LOAD \n\tiWorldSize=" + iWorldSize + "\n\tuiFlags:" + "\n\t\tMAP_FULLSOLDIER_SAVED: " + MAP_FULLSOLDIER_SAVED + "\n\t\tMAP_WORLDONLY_SAVED: " + MAP_WORLDONLY_SAVED + "\n\t\tMAP_WORLDLIGHTS_SAVED: " + MAP_WORLDLIGHTS_SAVED + "\n\t\tMAP_WORLDITEMS_SAVED: " + MAP_WORLDITEMS_SAVED + "\n\t\tMAP_EXITGRIDS_SAVED: " + MAP_EXITGRIDS_SAVED + "\n\t\tMAP_DOORTABLE_SAVED: " + MAP_DOORTABLE_SAVED + "\n\t\tMAP_EDGEPOINTS_SAVED: " + MAP_EDGEPOINTS_SAVED + "\n\t\tMAP_AMBIENTLIGHTLEVEL_SAVED: " + MAP_AMBIENTLIGHTLEVEL_SAVED + "\n\t\tMAP_NPCSCHEDULES_SAVED: " + MAP_NPCSCHEDULES_SAVED + "\n\tiTilesetID=" + iTilesetID + "\n\tuiSoldierSize=" + uiSoldierSize;
        }
    }

    public void saveMapHeader(ByteBuffer outputBuffer) {
        encodeFlags();       
        
        if(MapLoader.logFileIO) System.out.println("loader.core.MapSettings.saveMapHeader()"+toString());
        
        outputBuffer.putFloat(MAJOR_MAP_VERSION);
        outputBuffer.put((byte)MINOR_MAP_VERSION);
        
        outputBuffer.putInt(iRowSize);
        outputBuffer.putInt(iColSize);
        
        outputBuffer.putInt(uiFlags);
        outputBuffer.putInt(iTilesetID);
        outputBuffer.putInt( (int)uiSoldierSize );
    }

    public void saveMapInfo(ByteBuffer outputBuffer) {
        try {
            ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();
            mapInfo.write(itemAsBytes);
            outputBuffer.put(itemAsBytes.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
