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
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.structures.WORLDITEM;
import thebob.assetloader.map.structures.legacy.OLD_WORLDITEM;
import thebob.assetloader.map.structures.legacy.OLD_WORLDITEM_101;

// holds the worldItem and its contents
public class WorldItemStack extends MapLoaderWrapperBase {

    WORLDITEM item = new WORLDITEM();
    ObjectStack stack = new ObjectStack();

    protected void load(ByteBuffer source) {
        float dMajorMapVersion = map.getSettings().dMajorMapVersion;
        int ubMinorMapVersion = map.getSettings().ubMinorMapVersion;

        if (dMajorMapVersion >= 6.0 && ubMinorMapVersion > 26) {
            if (dMajorMapVersion < 7.0) {
                if (MapLoader.logEverything) {
                    System.out.println("loader.wrappers.WorldItem.load(): loading <<OLD>> WorldItemData...");
                }

                OLD_WORLDITEM oldWorldItem = new OLD_WORLDITEM();
                oldWorldItem.setSource(source);

                if (MapLoader.logEverything) System.out.println("loader.wrappers.WorldItem.load(): " + oldWorldItem);

                item.loadOld(oldWorldItem);
            } else {
                if (MapLoader.logEverything) {
                    System.out.println("loader.wrappers.WorldItem.load(): loading WorldItemData...");
                }
                item.setSource(source);
            }

            // Load the OO OBJECTTYPE
            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.WorldItem.load(): loading ObjectStack...");
            }
            stack.loadAsset(map);
        } else {

            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.WorldItem.load(): loading !!VERY OLD!!! WorldItemData...");
            }
            // Load the old item into a suitable structure, it's just POD
            OLD_WORLDITEM_101 oldWorldItem = new OLD_WORLDITEM_101();
            oldWorldItem.setSource(source);

            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.WorldItem.load() oldWorldItem (" + oldWorldItem.size() + "):" + oldWorldItem);
            }

            item.loadOld(oldWorldItem);
            stack.loadOld(oldWorldItem.oldObject);
        }
    }

    public void saveTo(ByteBuffer outputBuffer) {
        try {
            ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();
            item.write(itemAsBytes);
            
            if(MapLoader.logFileIO) System.out.println("loader.wrappers.WorldItemStack.saveTo(): save WorldItemStack @"+outputBuffer.position());
            outputBuffer.put(itemAsBytes.toByteArray());

            stack.saveTo(outputBuffer);
        } catch (IOException ex) {
            Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public WORLDITEM getItem() {
	return item;
    }

    public ObjectStack getStack() {
	return stack;
    }
    
    
    
}
