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
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.structures.DOOR;
import thebob.assetloader.map.structures.ObjectType;
import thebob.assetloader.map.structures.legacy.OLD_OBJECTTYPE_101;

/**
 *
 * @author the_bob
 */
public class ObjectStack extends MapLoaderWrapperBase {

    public static final int MAX_REASONABLE_STACKSIZE = 1024;
    
    int stackSize = 0;
    ObjectType object = new ObjectType();
    List<StackedObjectData> objects = new ArrayList<StackedObjectData>();

    protected void load(ByteBuffer source) {
        if (MapLoader.logEverything) 
            System.out.println("loader.wrappers.ObjectStack.load(): loading ObjectType @"+source.position());
        
        object.setSource(source);
        if (MapLoader.logEverything) System.out.println("loader.wrappers.ObjectStack.load(): "+object);
        
        stackSize = source.getInt();
        if (MapLoader.logEverything) System.out.println("loader.wrappers.ObjectStack.load(): stackSize="+stackSize+" @"+ (source.position()-4) );
        
        if( stackSize > MAX_REASONABLE_STACKSIZE || stackSize < 0 ){
            throw new RuntimeException("Object stack invalid!");
        }
        
        for (int i = 0; i < stackSize; i++) {
            if (MapLoader.logEverything) {
                System.out.println("loader.wrappers.ObjectStack.load(): loading object " + (i + 1) + " / " + stackSize +" @"+ (source.position()-4) );
            }
            StackedObjectData stackedObject = new StackedObjectData();
            stackedObject.loadAsset(map);
            objects.add(stackedObject);
        }
        if (MapLoader.logEverything) {
            System.out.println("loader.wrappers.ObjectStack.load(): loaded " + objects.size() + " objects: " + objects+" @"+ (source.position()-4) );
        }
    }

    void loadOld(OLD_OBJECTTYPE_101 oldItem) {
        object.loadOld(oldItem);

        stackSize = oldItem.ubNumberOfObjects.get();

        if (MapLoader.logEverything) {
            System.out.print("loader.wrappers.ObjectStack.loadOld(): loading " + stackSize + " items:");
        }

        for (int i = 0; i < oldItem.ubNumberOfObjects.get(); i++) {
            //if(MapLoader.logEverything) System.out.println("loader.wrappers.ObjectStack.loadOld() converting item "+(i+1)+"/"+oldItem.ubNumberOfObjects.get());
            StackedObjectData objectInStack = new StackedObjectData();
            objectInStack.loadOld(oldItem, i);
            objects.add(objectInStack);
        }
    }

    void saveTo(ByteBuffer outputBuffer) {
        try {
            ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();
            object.write(itemAsBytes);
            if(MapLoader.logFileIO) System.out.println("\nloader.wrappers.ObjectStack.saveTo(): save object @"+outputBuffer.position()+", size="+object.size());
            outputBuffer.put(itemAsBytes.toByteArray());
            if(MapLoader.logFileIO) System.out.println("loader.wrappers.ObjectStack.saveTo(): object end @"+outputBuffer.position());
            outputBuffer.position( outputBuffer.position() + object.getOffsetAdjustment() );
            if(MapLoader.logFileIO) System.out.println("loader.wrappers.ObjectStack.saveTo(): object offset adjust @"+outputBuffer.position() + "\n");
        } catch (IOException ex) {
            Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(MapLoader.logFileIO) System.out.println("loader.wrappers.ObjectStack.saveTo(): save stackSize ("+stackSize+") @"+outputBuffer.position());
        outputBuffer.putInt(stackSize);
        
        for (StackedObjectData object : objects) {
            object.saveTo(outputBuffer);
        }
    }

    public ObjectType getObject() {
	return object;
    }

    public List<StackedObjectData> getObjects() {
	return objects;
    }

    
}
