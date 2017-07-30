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

/*
	BOOLEAN				fExists;
	INT16				sGridNo;
	UINT8				ubLevel;
	OLD_OBJECTTYPE_101		oldObject;
	UINT16				usFlags;
	INT8				bRenderZHeightAboveLevel;
	INT8				bVisible;
	UINT8				ubNonExistChance;	
 */
public class OLD_WORLDITEM_101 extends AutoLoadingMapStruct {

    public final Struct.Bool fExists = new Struct.Bool();
    public final Struct.Signed16 sGridNo = new Struct.Signed16();
    public final Struct.Signed8 ubLevel = new Struct.Signed8(32);

    public final OLD_OBJECTTYPE_101 oldObject = inner(new OLD_OBJECTTYPE_101());

    public final Struct.Unsigned16 usFlags = new Struct.Unsigned16();
    public final Struct.Signed8 bRenderZHeightAboveLevel = new Struct.Signed8();
    public final Struct.Signed8 bVisible = new Struct.Signed8();
    public final Struct.Unsigned8 ubNonExistChance = new Struct.Unsigned8(32);

    @Override
    public String toString() {
        return "\tfExists=" + fExists.get() + " @" + fExists.offset() + ", " + fExists.bitLength() + "\n\t"
                + "sGridNo=" + new GridPos(sGridNo.get()) + " @" + sGridNo.offset() + "\n\t"
                + "ubLevel=" + ubLevel.get() + " @" + ubLevel.offset() + "\n\t"
                + "oldObject: " + oldObject.size() + "\n\t\t"
                + "usItem=" + oldObject.usItem.get() + "@ " + oldObject.usItem.offset() + "\n\t\t"
                + "ubNumberOfObjects=" + oldObject.ubNumberOfObjects.get() + "@ " + oldObject.ubNumberOfObjects.offset() + "\n\t\t"
                + "unionPlaceholder=" + oldObject.data.toString()+ "\n\t\t"
                + "usAttachItem=" + oldObject.usAttachItem[0].offset() + "-" + oldObject.usAttachItem[3].offset() + "\n\t\t"
                + "bAttachStatus=" + oldObject.bAttachStatus[0].offset() + "-" + oldObject.bAttachStatus[3].offset() + "\n\t\t"
                + "fFlags=" + oldObject.fFlags.get() + "@ " + oldObject.fFlags.offset() + "\n\t\t"
                + "ubMission=" + oldObject.ubMission.get() + "@ " + oldObject.ubMission.offset() + "\n\t\t"
                + "bTrap=" + oldObject.bTrap.get() + "@ " + oldObject.bTrap.offset() + "\n\t\t"
                + "ubImprintID=" + oldObject.ubImprintID.get() + "@ " + oldObject.ubImprintID.offset() + "\n\t\t"
                + "ubWeight=" + oldObject.ubWeight.get() + "@ " + oldObject.ubWeight.offset() + "\n\t\t"
                + "fUsed=" + oldObject.fUsed.get() + "@ " + oldObject.fUsed.offset() + "\n\t"
                + "usFlags=" + usFlags.get() + " @" + usFlags.offset() + "\n\t"
                + "bRenderZHeightAboveLevel=" + bRenderZHeightAboveLevel.get() + " @" + bRenderZHeightAboveLevel.offset() + "\n\t"
                + "bVisible=" + bVisible.get() + " @" + bVisible.offset() + "\n\t"
                + "ubNonExistChance=" + ubNonExistChance.get() + " @" + ubNonExistChance.offset()
                + super.toString();
    }

}
