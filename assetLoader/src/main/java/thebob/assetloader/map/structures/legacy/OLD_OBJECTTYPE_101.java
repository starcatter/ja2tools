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
import thebob.assetloader.map.helpers.AutoLoadingMapStruct;

/**
 *
 * @author the_bob
 */

/*
	UINT16		usItem;
	UINT8		ubNumberOfObjects;

	Version101::OLD_OBJECTTYPE_101_UNION	ugYucky;

	UINT16		usAttachItem[OLD_MAX_ATTACHMENTS_101];
	INT8		bAttachStatus[OLD_MAX_ATTACHMENTS_101];

	INT8		fFlags;
	UINT8		ubMission;

        INT8		bTrap;        // 1-10 exp_lvl to detect
	UINT8		ubImprintID;	// ID of merc that item is imprinted on
	UINT8		ubWeight;
	UINT8		fUsed;				// flags for whether the item is used or not
*/

public class OLD_OBJECTTYPE_101 extends AutoLoadingMapStruct {

    public final Unsigned16 usItem = new Unsigned16();
    public final Unsigned8 ubNumberOfObjects = new Unsigned8();
    
    public final OLD_OBJECTTYPE_101_UNION data = inner( new OLD_OBJECTTYPE_101_UNION() );

    //public final Signed8[] unionPlaceholder = array(new Signed8[12]);

    public final Unsigned16[] usAttachItem = array(new Unsigned16[4]);
    public final Signed8[] bAttachStatus = array(new Signed8[4]);

    public final Signed8 fFlags = new Signed8();
    public final Unsigned8 ubMission = new Unsigned8();

    public final Signed8 bTrap = new Signed8();
    public final Unsigned8 ubImprintID = new Unsigned8();
    public final Unsigned8 ubWeight = new Unsigned8();
    public final Unsigned8 fUsed = new Unsigned8(16);

    @Override
    public String toString() {
        return "[item #"+usItem+" * "+ubNumberOfObjects+"]";
    }
    
    
}
