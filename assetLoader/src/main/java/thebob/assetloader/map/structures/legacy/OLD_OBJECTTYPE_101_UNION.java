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
import javolution.io.Union;

public class OLD_OBJECTTYPE_101_UNION  extends Union {
 
    public final OLD_OBJECT_GUN gun = inner(new OLD_OBJECT_GUN());
    
    public final Unsigned8 ubShotsLeft = new Unsigned8(); //holds the same value as ubShotsLeft[0]
    public final Signed8 objectStatus = new Signed8(); //holds the same value as bStatus[0]    
    
    public final OLD_OBJECT_MONEY money = inner(new OLD_OBJECT_MONEY());
    public final OLD_OBJECT_BOMBS_AND_OTHER misc = inner(new OLD_OBJECT_BOMBS_AND_OTHER());
    public final OLD_OBJECT_KEY key = inner(new OLD_OBJECT_KEY());
    public final OLD_OBJECT_OWNER owner = inner(new OLD_OBJECT_OWNER());

    @Override
    public ByteOrder byteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public boolean isPacked() {
        return false;
    }

    @Override
    public String toString() {
        return "ObjectData Union:\n\t"
                + "UNION AS: objectStatus=" + objectStatus.get() + "\n\t"
                + "UNION AS: ubShotsLeft=" + ubShotsLeft.get() + "\n\t"
                + "UNION AS: gun=\n" + gun.toString() + "\n\t"
                + "UNION AS: money=\n" + money.toString() + "\n\t"
                + "UNION AS: misc=\n" + misc.toString() + "\n\t"
                + "UNION AS: key=\n" + key.toString() + "\n\t"
                + "UNION AS: owner=\n" + owner.toString() + "\n\t"
                + super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
public class OLD_OBJECT_GUN extends Struct {

    public final Signed8 bGunStatus = new Signed8(); // status % of gun
    public final Unsigned8 ubGunAmmoType = new Unsigned8(); // ammo type, as per weapons.h
    public final Unsigned8 ubGunShotsLeft = new Unsigned8(); // duh, amount of ammo left
    public final Unsigned16 usGunAmmoItem = new Unsigned16(); // the item # for the item table
    public final Signed8 bGunAmmoStatus = new Signed8(); // only for "attached ammo" - grenades, mortar shells
    public final Unsigned8 ubGunState = new Unsigned8(); // SB manual recharge
    //warning, this unused space is the wrong size, 7 bytes above, 2 in the array, but it's been saved like that
    public final Unsigned8[] ubGunUnused = array(new Unsigned8[2]);	

    @Override
    public String toString() {
        return "\t\tbGunStatus=" + bGunStatus.get() + "\n"
                + "\t\tubGunAmmoType=" + ubGunAmmoType.get() + "\n"
                + "\t\tubGunShotsLeft=" + ubGunShotsLeft.get() + "\n"
                + "\t\tusGunAmmoItem=" + usGunAmmoItem.get() + "\n"
                + "\t\tbGunAmmoStatus=" + bGunAmmoStatus.get() + "\n"
                + "\t\tubGunState=" + ubGunState.get() + "\n";
    }

}

public class OLD_OBJECT_MONEY extends Struct {

    public final Signed8 bMoneyStatus = new Signed8();
    public final Unsigned32 uiMoneyAmount = new Unsigned32();

    public final Unsigned8[] ubMoneyUnused = array(new Unsigned8[3]);	
    
    @Override
    public String toString() {
        return "\t\tbMoneyStatus=" + bMoneyStatus.get() + "\n"
                + "\t\tuiMoneyAmount=" + uiMoneyAmount.get() + "\n";
    }

}

public class OLD_OBJECT_BOMBS_AND_OTHER extends Struct { // this is used by placed bombs, switches, and the action item

    public final Signed8 bBombStatus = new Signed8(); // % status
    public final Signed8 bDetonatorType = new Signed8(); // timed, remote, or pressure-activated
    public final Unsigned16 usBombItem = new Unsigned16(); // the usItem of the bomb.

    public final OLD_OBJECT_BOMBS_AND_OTHER_U1 u1 = inner(new OLD_OBJECT_BOMBS_AND_OTHER_U1());

    public final Unsigned8 ubBombOwner = new Unsigned8(); // side which placed the bomb
    public final Unsigned8 bActionValue = new Unsigned8(); // this is used by the ACTION_ITEM fake item

    public final OLD_OBJECT_BOMBS_AND_OTHER_U2 u2 = inner(new OLD_OBJECT_BOMBS_AND_OTHER_U2());

    @Override
    public String toString() {
        return "\t\tbBombStatus=" + bBombStatus.get() + "\n"
                + "\t\tbDetonatorType=" + bDetonatorType.get() + "\n"
                + "\t\tusBombItem=" + usBombItem.get() + "\n"
                + "\t\tu1=\n" + u1.toString() + "\n"
                + "\t\tubBombOwner=" + ubBombOwner.get() + "\n"
                + "\t\tbActionValue=" + bActionValue.get() + "\n"
                + "\t\tu2=\n" + u2.toString() + "\n";
    }

}

public class OLD_OBJECT_BOMBS_AND_OTHER_U1 extends Union {

    public final Signed8 bDelay = new Signed8(); // >=0 values used only
    public final Signed8 bFrequency = new Signed8(); // >=0 values used only

    @Override
    public String toString() {
        return "\t\t\tbDelay=" + bDelay.get() + "\n"
                + "\t\t\tbFrequency=" + bFrequency.get() + "\n";
    }

}

public class OLD_OBJECT_BOMBS_AND_OTHER_U2 extends Union {

    public final Unsigned8 ubTolerance = new Unsigned8(); // tolerance value for panic triggers
    public final Unsigned8 ubLocationID = new Unsigned8(); // location value for remote non-bomb (special!) triggers

    @Override
    public String toString() {
        return "\t\t\tubTolerance=" + ubTolerance.get() + "\n"
                + "\t\t\tubLocationID=" + ubLocationID.get() + "\n";
    }

}

public class OLD_OBJECT_KEY extends Struct {

    public final Signed8 bKeyStatus = new Signed8();
    public final Unsigned8 ubKeyID = new Unsigned8();
    public final Unsigned8[] ubKeyUnused = array(new Unsigned8[1]);	

    @Override
    public String toString() {
        return "\t\tbKeyStatus=" + bKeyStatus.get() + "\n"
                + "\t\tubKeyID=" + ubKeyID.get() + "\n";
    }

}

public class OLD_OBJECT_OWNER extends Struct {

    public final Unsigned8 ubOwnerProfile = new Unsigned8();
    public final Unsigned8 ubOwnerCivGroup = new Unsigned8();
    public final Unsigned8[] ubOwnershipUnused = array(new Unsigned8[6]);	

    @Override
    public String toString() {
        return "\t\tubOwnerProfile=" + ubOwnerProfile.get() + "\n"
                + "\t\tubOwnerCivGroup=" + ubOwnerCivGroup.get() + "\n";
    }

}

}
