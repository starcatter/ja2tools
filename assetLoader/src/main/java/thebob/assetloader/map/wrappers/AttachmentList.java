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

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import thebob.assetloader.map.MapLoader;

/**
 *
 * @author the_bob
 */
public class AttachmentList extends MapLoaderWrapperBase{

    public static final int MAX_REASONABLE_ATTACHMENTS = 255;
    
    int size = 0;
    List<ObjectStack> objects = new ArrayList<ObjectStack>();

    protected void load(ByteBuffer source) {
        if (MapLoader.logEverything) System.out.println("loader.wrappers.AttachmentList.load(): size @"+ source.position());
        size = source.getInt();
        if (MapLoader.logEverything) System.out.println("loader.wrappers.AttachmentList.load(): will load " + size + " attachments...");
        
        if( size > MAX_REASONABLE_ATTACHMENTS ){
            throw new RuntimeException("too many attachments!");
        }
        
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if(MapLoader.logEverything) System.out.println("loader.wrappers.AttachmentList.load(): loading attachment " + (i + 1) + " / " + size + "...");
                ObjectStack stack = new ObjectStack();
                stack.loadAsset(map);
                objects.add(stack);
            }
        } else {
            if(MapLoader.logEverything) System.out.println("loader.wrappers.AttachmentList.load(): no attachments.");
        }
    }

    void saveTo(ByteBuffer outputBuffer) {
        if (MapLoader.logEverything) System.out.println("loader.wrappers.AttachmentList.saveTo(): size ("+size+") @"+ outputBuffer.position());
        outputBuffer.putInt(size);
        for( ObjectStack object : objects ){
            if (MapLoader.logEverything) System.out.println("loader.wrappers.AttachmentList.saveTo(): ObjectStack @"+ outputBuffer.position());
            object.saveTo(outputBuffer);            
        }
        if (MapLoader.logEverything) System.out.println("loader.wrappers.AttachmentList.saveTo(): end @"+ outputBuffer.position());
    }

}
