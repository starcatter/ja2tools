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
package thebob.assetloader.map.structures;

import thebob.assetloader.map.helpers.AutoLoadingMapStruct;
import java.nio.ByteOrder;
import thebob.assetloader.map.helpers.GridPos;
import thebob.assetloader.map.structures.legacy.OLD_WORLDITEM;
import thebob.assetloader.map.structures.legacy.OLD_WORLDITEM_101;

// holds the worldItem and its contents
// for loading and saving the worldItem header data
public class WORLDITEM extends AutoLoadingMapStruct {

    public final Bool fExists = new Bool();
    public final Signed32 sGridNo = new Signed32();
    public final Signed8 ubLevel = new Signed8();
    public final Unsigned16 usFlags = new Unsigned16();
    public final Signed8 bRenderZHeightAboveLevel = new Signed8();
    public final Signed8 bVisible = new Signed8();
    public final Unsigned8 ubNonExistChance = new Unsigned8();
    public final Signed8 soldierID = new Signed8();

    @Override
    public String toString() {
     return  "WORLDITEM:\n"
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

    public void loadOld(OLD_WORLDITEM old) {
        fExists.set(old.fExists.get());
        sGridNo.set(old.sGridNo.get());
        ubLevel.set(old.ubLevel.get());
        usFlags.set(old.usFlags.get());
        bRenderZHeightAboveLevel.set(old.bRenderZHeightAboveLevel.get());
        bVisible.set(old.bVisible.get());
        ubNonExistChance.set(old.ubNonExistChance.get());
        soldierID.set(old.soldierID.get());
    }

    public void loadOld(OLD_WORLDITEM_101 old) {
        fExists.set(old.fExists.get());
        sGridNo.set(old.sGridNo.get());
        ubLevel.set(old.ubLevel.get());
        usFlags.set(old.usFlags.get());
        bRenderZHeightAboveLevel.set(old.bRenderZHeightAboveLevel.get());
        bVisible.set(old.bVisible.get());
        ubNonExistChance.set(old.ubNonExistChance.get());
        soldierID.set((byte) 0);
    }

}
