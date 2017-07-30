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

import thebob.assetloader.map.helpers.GridPos;
import thebob.assetloader.map.helpers.AutoLoadingMapStruct;

/**
 *
 * @author the_bob
 */
public class OLD_BASIC_SOLDIERCREATE_STRUCT extends AutoLoadingMapStruct {

    public final Bool fDetailedPlacement = new Bool(); // Specialized information, has a counterpart containing all info
    public final Signed16 usStartingGridNo = new Signed16(); // Where the placement position is
    public final Signed8 bTeam = new Signed8(); // The team this individual is part of
    public final Signed8 bRelativeAttributeLevel = new Signed8();
    public final Signed8 bRelativeEquipmentLevel = new Signed8();
    public final Unsigned8 ubDirection = new Unsigned8(); // 1 of 8 values (always mandatory)

    public final Signed8 bOrders = new Signed8();
    public final Signed8 bAttitude = new Signed8();
    public final Signed8 ubBodyType = new Signed8(); // Up to 128 body types, -1 means random

    public final Signed16[] sPatrolGrid = array(new Signed16[10]); // Possible locations to visit, patrol, etc.
    public final Signed8 bPatrolCnt = new Signed8();
    public final Bool fOnRoof = new Bool();
    public final Unsigned8 ubSoldierClass = new Unsigned8(); // Army, administrator, elite
    public final Unsigned8 ubCivilianGroup = new Unsigned8();
    public final Bool fPriorityExistance = new Bool(); // These slots are used first
    public final Bool fHasKeys = new Bool();

    Signed8[] PADDINGSLOTS = array(new Signed8[14]);

    @Override
    public int getOffsetAdjustment() {
        return 0;
    }

    @Override
    public String toString() {
        return "OLD basic placement info:\n"
                + "fDetailedPlacement: " + fDetailedPlacement.get() + "\n\t"
                + "usStartingGridNo: " + new GridPos(usStartingGridNo.get()) + "\n\t"
                + "bTeam: " + bTeam.get() + "\n\t"
                + "bRelativeAttributeLevel: " + bRelativeAttributeLevel.get() + "\n\t"
                + "bRelativeEquipmentLevel: " + bRelativeEquipmentLevel.get() + "\n\t"
                + "ubDirection: " + ubDirection.get() + "\n\t"
                + "bOrders: " + bOrders.get() + "\n\t"
                + "bAttitude: " + bAttitude.get() + "\n\t"
                + "ubBodyType: " + ubBodyType.get() + "\n\t"
                + "sPatrolGrid: " + printArrayAsGrids(sPatrolGrid) + "\n\t"
                + "bPatrolCnt: " + bPatrolCnt.get() + "\n\t"
                + "fOnRoof: " + fOnRoof.get() + "\n\t"
                + "ubSoldierClass: " + ubSoldierClass.get() + "\n\t"
                + "ubCivilianGroup: " + ubCivilianGroup.get() + "\n\t"
                + "fPriorityExistance: " + fPriorityExistance.get() + "\n\t"
                + "fHasKeys: " + fHasKeys.get() + "\n------------\n\t" + super.toString();
    }
}
