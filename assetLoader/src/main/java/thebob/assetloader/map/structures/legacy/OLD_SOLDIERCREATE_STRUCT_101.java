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

import thebob.assetloader.map.helpers.AutoLoadingMapStruct;
import thebob.assetloader.map.structures.SOLDIERTYPE;

/**
 *
 * @author the_bob
 */
public class OLD_SOLDIERCREATE_STRUCT_101 extends AutoLoadingMapStruct {

    public final Bool fStatic = new Bool();
    //Profile information used for special NPCs and player mercs.
    public final Unsigned8 ubProfile = new Unsigned8();
    public final Bool fPlayerMerc = new Bool();
    public final Bool fPlayerPlan = new Bool();
    public final Bool fCopyProfileItemsOver = new Bool();

    //Location information
    public final Signed16 sSectorX = new Signed16();
    public final Signed16 sSectorY = new Signed16();
    public final Unsigned8 ubDirection = new Unsigned8();
    public final Signed16 sInsertionGridNo = new Signed16(); //dnl ch42 290909

    // Can force a team, but needs flag set
    public final Signed8 bTeam = new Signed8();
    public final Signed8 ubBodyType = new Signed8();

    //Orders and attitude settings
    public final Signed8 bAttitude = new Signed8();
    public final Signed8 bOrders = new Signed8();

    //Attributes
    public final Signed8 bLifeMax = new Signed8();
    public final Signed8 bLife = new Signed8();
    public final Signed8 bAgility = new Signed8();
    public final Signed8 bDexterity = new Signed8();
    public final Signed8 bExpLevel = new Signed8();
    public final Signed8 bMarksmanship = new Signed8();
    public final Signed8 bMedical = new Signed8();
    public final Signed8 bMechanical = new Signed8();
    public final Signed8 bExplosive = new Signed8();
    public final Signed8 bLeadership = new Signed8();
    public final Signed8 bStrength = new Signed8();
    public final Signed8 bWisdom = new Signed8();
    public final Signed8 bMorale = new Signed8();
    public final Signed8 bAIMorale = new Signed8();

    public final OLD_OBJECTTYPE_101[] inv = array(new OLD_OBJECTTYPE_101[19]);

    //Palette information for soldiers.
    public final Unsigned8[] HeadPal = array(new Unsigned8[30]);
    public final Unsigned8[] PantsPal = array(new Unsigned8[30]);
    public final Unsigned8[] VestPal = array(new Unsigned8[30]);
    public final Unsigned8[] SkinPal = array(new Unsigned8[30]);
    public final Unsigned8[] MiscPal = array(new Unsigned8[30]);

    //Waypoint information for patrolling
    public final Signed16[] sPatrolGrid = array(new Signed16[10]); //dnl ch42 290909
    public final Signed8 bPatrolCnt = new Signed8();

    //Kris:	Additions November 16, 1997 (padding down to 129 from 150)
    public final Bool fVisible = new Bool();
    public final Signed16[] name = array(new Signed16[10]);

    public final Unsigned8 ubSoldierClass = new Unsigned8(); //army, administrator, elite
    public final Bool fOnRoof = new Bool();
    public final Signed8 bSectorZ = new Signed8();

    public final Reference32<SOLDIERTYPE> pExistingSoldier = new Reference32<SOLDIERTYPE>();
    public final Bool fUseExistingSoldier = new Bool();
    public final Unsigned8 ubCivilianGroup = new Unsigned8();

    public final Bool fKillSlotIfOwnerDies = new Bool();
    public final Unsigned8 ubScheduleID = new Unsigned8();

    public final Bool fUseGivenVehicle = new Bool();
    public final Signed8 bUseGivenVehicleID = new Signed8();
    public final Bool fHasKeys = new Bool();

    Signed8[] bPadding = array(new Signed8[117]);

    @Override
    public int getOffsetAdjustment() {
        return 0;
    }

    @Override
    public String toString() {

        return "OLD_SOLDIERCREATE_STRUCT_101( " + size() + " + " + getOffsetAdjustment() + " ):\n\t"
                + "fStatic: " + fStatic.get() + "\n\t"
                + "ubProfile: " + ubProfile.get() + "\n\t"
                + "fPlayerMerc: " + fPlayerMerc.get() + "\n\t"
                + "fPlayerPlan: " + fPlayerPlan.get() + "\n\t"
                + "fCopyProfileItemsOver: " + fCopyProfileItemsOver.get() + "\n\t"
                + "sSectorX: " + sSectorX.get() + "\n\t"
                + "sSectorY: " + sSectorY.get() + "\n\t"
                + "ubDirection: " + ubDirection.get() + "\n\t"
                + "sInsertionGridNo: " + sInsertionGridNo.get() + "\n\t"
                + "bTeam: " + bTeam.get() + "\n\t"
                + "ubBodyType: " + ubBodyType.get() + "\n\t"
                + "bAttitude: " + bAttitude.get() + "\n\t"
                + "bOrders: " + bOrders.get() + "\n\t"
                + "bLifeMax: " + bLifeMax.get() + "\n\t"
                + "bLife: " + bLife.get() + "\n\t"
                + "bAgility: " + bAgility.get() + "\n\t"
                + "bDexterity: " + bDexterity.get() + "\n\t"
                + "bExpLevel: " + bExpLevel.get() + "\n\t"
                + "bMarksmanship: " + bMarksmanship.get() + "\n\t"
                + "bMedical: " + bMedical.get() + "\n\t"
                + "bMechanical: " + bMechanical.get() + "\n\t"
                + "bExplosive: " + bExplosive.get() + "\n\t"
                + "bLeadership: " + bLeadership.get() + "\n\t"
                + "bStrength: " + bStrength.get() + "\n\t"
                + "bWisdom: " + bWisdom.get() + "\n\t"
                + "bMorale: " + bMorale.get() + "\n\t"
                + "bAIMorale: " + bAIMorale.get() + "\n\t"
                + "inventory: " + printInventoryArray(inv) + "\n\t"
                + "HeadPal: " + printArrayAsChars(HeadPal) + "\n\t"
                + "PantsPal: " + printArrayAsChars(PantsPal) + "\n\t"
                + "VestPal: " + printArrayAsChars(VestPal) + "\n\t"
                + "SkinPal: " + printArrayAsChars(SkinPal) + "\n\t"
                + "MiscPal: " + printArrayAsChars(MiscPal) + "\n\t"
                + "sPatrolGrid: " + printArrayAsGrids(sPatrolGrid) + "\n\t"
                + "bPatrolCnt: " + bPatrolCnt.get() + "\n\t"
                + "fVisible: " + fVisible.get() + "\n\n\t"
                + "name: " + printArrayAsChars(name) + "\n\n\t"
                + "ubSoldierClass: " + ubSoldierClass.get() + "\n\t"
                + "fOnRoof: " + fOnRoof.get() + "\n\t"
                + "bSectorZ: " + bSectorZ.get() + "\n\t"
                + "pExistingSoldier: " + pExistingSoldier.get() + "\n\t"
                + "fUseExistingSoldier: " + fUseExistingSoldier.get() + "\n\t"
                + "ubCivilianGroup: " + ubCivilianGroup.get() + "\n\t"
                + "fKillSlotIfOwnerDies: " + fKillSlotIfOwnerDies.get() + "\n\t"
                + "ubScheduleID: " + ubScheduleID.get() + "\n\t"
                + "fUseGivenVehicle: " + fUseGivenVehicle.get() + "\n\t"
                + "bUseGivenVehicleID: " + bUseGivenVehicleID.get() + "\n\t"
                + "fHasKeys: " + fHasKeys.get() + "\n\n\t" //+ super.toString()
                ;
    }

}
