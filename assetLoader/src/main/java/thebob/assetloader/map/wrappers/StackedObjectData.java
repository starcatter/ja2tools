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
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.structures.ObjectData;
import thebob.assetloader.map.structures.legacy.OLD_OBJECTTYPE_101;

/**
 *
 * @author the_bob
 */
public class StackedObjectData extends MapLoaderWrapperBase{
    
    ObjectData data = new ObjectData();
    AttachmentList attachments = new AttachmentList();    

    // try to match current map version. Preserved original comments.
    private void loadProperObjectDataForCurrentMapVersion(ByteBuffer source){
        float dMajorMapVersion = map.getSettings().dMajorMapVersion;
        int ubMinorMapVersion = map.getSettings().ubMinorMapVersion;
        
        if (dMajorMapVersion >= 7 && ubMinorMapVersion >= map.getSettings().MINOR_MAP_VERSION)
        {
            // Flugente: changed this, otherwise game would crash when reading WF maps if class ObjectData was different. this is a rough fix and by no means perfect
            data.setSource(source);
            if (MapLoader.logEverything) System.out.println("loader.wrappers.StackedObjectData.loadProperObjectDataForCurrentMapVersion(): Loading objectData variant 1: "+data);
        }
        else if (dMajorMapVersion >= 7 && ubMinorMapVersion >= map.getSettings().MINOR_MAP_REPAIR_SYSTEM)
        {
            // sObjectFlag'  size changed			
            //LOADDATA(&(this->data), *hBuffer, sizeof(ObjectData) - sizeof(this->data.sObjectFlag) );
            data.setSource(source);
            source.position(source.position() - 8 );
            if (MapLoader.logEverything) System.out.println("loader.wrappers.StackedObjectData.loadProperObjectDataForCurrentMapVersion(): Loading objectData variant 2 (-8B): "+data);
        }
        // When saving maps with the new map editor that has weapon overheated feature included!
        else if (dMajorMapVersion >= 7 && ubMinorMapVersion >= map.getSettings().MINOR_MAP_OVERHEATING)
        {
            // Flugente 12-09-30: ok, this will look really odd, so I'll explain. We have to account for padding of structures. Before map version 30, ObjectData had size 32 due to padding. 
            // Up to ubWireNetworkFlag the size is 28, then bDefuseFrequency adds size 1 - but due to padding, the size was 32. Now, upon adding sRepairThreshold, the size is still 32, as the padded 3 bytes
            // are now used. Due to this, we only have to reduce for the sizes of bDirtLevel and sObjectFlag, but not sRepairThreshold (sizeof(ObjectData) is now 40).
            // But of course, we now 'read' the values for sRepairThreshold, but there weren't any in older map versions, resulting in garbage values - we therefore set that manually to 100
            // Of course, once the ObjectData-Class is altered, this has to be altered as well!
            //dnl ch74 241013 We cannot change past so hardcode 32 simply because that was sizeof(ObjectData) before current and all future changes ;-)
            data.setSource(source);
            source.position(source.position() - 16 );
            if (MapLoader.logEverything) System.out.println("loader.wrappers.StackedObjectData.loadProperObjectDataForCurrentMapVersion(): Loading objectData variant 3 (-16B): "+data);
            //data.sRepairThreshold.set((short)100);
        }
        else
        {
            // WF Maps have old format
            // +1 because we have to account for endOfPOD itself
            //LOADDATA(&(this->data), *hBuffer, SIZEOF_OBJECTDATA_POD+1 );
            data.setSource(source);
            int backtrackBytes = data.size() - ( data.bTemperature.offset() );
            if (MapLoader.logEverything) System.out.println("loader.wrappers.StackedObjectData.loadProperObjectDataForCurrentMapVersion(): Loading objectData variant 4  (-"+backtrackBytes+"B): "+data);
            source.position(source.position() - backtrackBytes );
        }
        
    }
    
    protected void load(ByteBuffer source) {
        if(MapLoader.logEverything) System.out.println("loader.wrappers.StackedObjectData.load(): loading ObjectData @"+source.position());
        // data.setSource(source);        
        loadProperObjectDataForCurrentMapVersion(source);
        if(MapLoader.logEverything) System.out.println("loader.wrappers.StackedObjectData.load(): loading AttachmentList @"+source.position());
        attachments.loadAsset(map);
    }

    void loadOld(OLD_OBJECTTYPE_101 oldItem, int i) {
        data.loadOld(oldItem,i);
        
        if(MapLoader.logEverything) System.out.println("loader.wrappers.StackedObjectData.loadOld() converted item: " + data);
    }

    void saveTo(ByteBuffer outputBuffer) {
        try {
            ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();
            data.write(itemAsBytes);
            
            if(MapLoader.logFileIO) System.out.println("loader.wrappers.StackedObjectData.saveTo(): save data @"+outputBuffer.position()+", size="+data.size());
            outputBuffer.put(itemAsBytes.toByteArray());
            
            attachments.saveTo(outputBuffer);
        } catch (IOException ex) {
            Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
