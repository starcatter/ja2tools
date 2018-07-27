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
package thebob.assetloader.map.helpers;

import javolution.io.Struct;
import thebob.assetloader.common.AutoLoadingStruct;
import thebob.assetloader.map.structures.legacy.OLD_OBJECTTYPE_101;

/**
 *
 * @author the_bob
 */
public abstract class AutoLoadingMapStruct extends AutoLoadingStruct {

    public AutoLoadingMapStruct() {
        super();
    }

    protected static String printInventoryArray(Struct[] array) {
        StringBuilder output = new StringBuilder();
        boolean atLeastOneItem = false;

        if (array[0] instanceof OLD_OBJECTTYPE_101) {
            output.append(array.length);
            output.append(" slots:");
            for (Struct m : array) {
                OLD_OBJECTTYPE_101 character = (OLD_OBJECTTYPE_101) m;
                if (character.usItem.get() != 0) {
                    atLeastOneItem = true;
                    output.append(character.toString());
                    output.append(";");
                }
            }
            if( !atLeastOneItem ) output.append(" (all contained empty items!) ");
        }

        return output.toString();
    }

    public static String printArrayAsGrids(Member[] array){
        StringBuilder output = new StringBuilder(array.length*3);
        for (Member m : array) {
            long c;

            if (m instanceof Signed8) {
                c = ((Signed8) m).get();
            } else if (m instanceof Signed16) {
                c = ((Signed16) m).get();
            } else if (m instanceof Signed32) {
                c = ((Signed32) m).get();
            } else if (m instanceof Unsigned8) {
                c = ((Unsigned8) m).get();
            } else if (m instanceof Unsigned16) {
                c = ((Unsigned16) m).get();
            } else if (m instanceof Unsigned32) {
                c = ((Unsigned32) m).get();
            } else {
                throw new RuntimeException("Unknown data type: " + m.getClass().getSimpleName());
            }

            output.append('[');
            output.append( c == 0 || c == 65535 ? "Blank" : new GridPos(c) );
            output.append(']');
        }

        return output.toString();
    }

}
