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
package thebob.assetloader.map.structures.legacy;

import java.nio.ByteOrder;
import javolution.io.Struct;
import thebob.assetloader.map.helpers.GridPos;
import thebob.assetloader.map.helpers.AutoLoadingMapStruct;

/**
 *
 * @author the_bob
 */
public class OLD_WORLDITEM  extends AutoLoadingMapStruct {

    public final Struct.Bool fExists = new Struct.Bool();
    public final Struct.Signed16 sGridNo = new Struct.Signed16();
    public final Struct.Signed8 ubLevel = new Struct.Signed8();
    public final Struct.Unsigned16 usFlags = new Struct.Unsigned16();
    public final Struct.Signed8 bRenderZHeightAboveLevel = new Struct.Signed8();
    public final Struct.Signed8 bVisible = new Struct.Signed8();
    public final Struct.Unsigned8 ubNonExistChance = new Struct.Unsigned8();
    public final Struct.Signed8 soldierID = new Struct.Signed8();

    @Override
    public ByteOrder byteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }  

    @Override
    public String toString() {        
        return  "OLD_WORLDITEM:\n"
                + "fExists: " + fExists.get() + " @" + fExists.offset() + "\n\t"
                + "sGridNo: " + new GridPos(sGridNo.get()) + " @" + sGridNo.offset() + "\n\t"
                + "ubLevel: " + ubLevel.get() + " @" + ubLevel.offset() + "\n\t"
                + "usFlags: " + usFlags.get() + " @" + usFlags.offset() + "\n\t"
                + "bRenderZHeightAboveLevel: " + bRenderZHeightAboveLevel.get() + " @" + bRenderZHeightAboveLevel.offset() + "\n\t"
                + "bVisible: " + bVisible.get() + " @" + bVisible.offset() + "\n\t"
                + "ubNonExistChance: " + ubNonExistChance.get() + " @" + ubNonExistChance.offset() + "\n\t"
                + "soldierID: " + soldierID.get() + " @" + soldierID.offset() + "\n-----------------------------\n\t"
                + super.toString();
    }
    
}
