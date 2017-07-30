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
package thebob.assetloader.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import javolution.io.Struct;

/**
 *
 * @author the_bob
 */
public abstract class AutoLoadingStruct extends Struct {

    public AutoLoadingStruct() {
        super();
    }

    public void setSource(ByteBuffer source) {
        setByteBuffer(source, source.position());
        source.position(source.position() + size() + getOffsetAdjustment());
    }

    public int getOffsetAdjustment() {
        return 0;
    }

    @Override
    public ByteOrder byteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public boolean isPacked() {
        return false;
    }

    public static String mappedArrayToString(Member[] array){
        StringBuilder output = new StringBuilder(array.length);

        if (array[0] instanceof Signed8) {
            for (Member m : array) {
                Signed8 character = (Signed8) m;
                char c = (char) character.get();
                if (c != 0) {
                    output.append(c);
                }
            }
        }

        if (array[0] instanceof Signed16) {
            for (Member m : array) {
                Signed16 character = (Signed16) m;
                char c = (char) character.get();
                if (c != 0) {
                    output.append(c);
                }
            }
        }
        return output.toString();
    }
    
    public static String printArrayAsChars(Member[] array) {
        return mappedArrayToString(array);
    }

    public void scanNearbyMemory(int distance) {
        System.out.println("\n\nthebob.assetloader.common.AutoLoadingStruct.scanNearbyMemory():");
        int pos = getByteBufferPosition();
        for (int i = 0; i < distance * 2; i++) {
            int offset = i - distance;
            int newPos = pos + offset;
            setByteBufferPosition(newPos);

            System.out.println("\tscan @" + offset + ":\t" + toString());
        }
        setByteBufferPosition(pos);
    }
}
