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

import javolution.io.Struct;
import javolution.io.Union;

import java.nio.ByteOrder;

class OBJECT_GUN extends Struct {

    public final Signed16 bGunStatus = new Signed16(); // status % of gun
    public final Unsigned8 ubGunAmmoType = new Unsigned8(); // ammo type, as per weapons.h
    public final Unsigned16 ubGunShotsLeft = new Unsigned16(); // duh, amount of ammo left
    public final Unsigned16 usGunAmmoItem = new Unsigned16(); // the item # for the item table
    public final Signed16 bGunAmmoStatus = new Signed16(); // only for "attached ammo" - grenades, mortar shells
    public final Unsigned8 ubGunState = new Unsigned8(); // SB manual recharge

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

class OBJECT_MONEY extends Struct {

    public final Signed16 bMoneyStatus = new Signed16();
    public final Unsigned32 uiMoneyAmount = new Unsigned32();

    @Override
    public String toString() {
        return "\t\tbMoneyStatus=" + bMoneyStatus.get() + "\n"
                + "\t\tuiMoneyAmount=" + uiMoneyAmount.get() + "\n";
    }

}

class OBJECT_BOMBS_AND_OTHER extends Struct { // this is used by placed bombs, switches, and the action item

    public final Signed16 bBombStatus = new Signed16(); // % status
    public final Unsigned8 bDetonatorType = new Unsigned8(); // timed, remote, or pressure-activated
    public final Unsigned16 usBombItem = new Unsigned16(); // the usItem of the bomb.

    public final OBJECT_BOMBS_AND_OTHER_U1 u1 = inner(new OBJECT_BOMBS_AND_OTHER_U1());

    public final Unsigned8 ubBombOwner = new Unsigned8(); // side which placed the bomb
    public final Unsigned8 bActionValue = new Unsigned8(); // this is used by the ACTION_ITEM fake item

    public final OBJECT_BOMBS_AND_OTHER_U2 u2 = inner(new OBJECT_BOMBS_AND_OTHER_U2());

    @Override
    public String toString() {
        return "\t\tbBombStatus=" + bBombStatus.get() + "\n"
                + "\t\tbDetonatorType=" + bDetonatorType.get() + "\n"
                + "\t\tusBombItem=" + usBombItem.get() + "\n"
                + "\t\tu1=\n" + u1.toString() + "\n"
                + "\t\tubBombOwner=" + ubBombOwner.get() + "\n"
                + "\t\tbActionValue=" + bActionValue.get() + "\n"
                + "\t\tu2=\n" + u2.toString();
    }

}

class OBJECT_BOMBS_AND_OTHER_U1 extends Union {

    public final Unsigned8 bDelay = new Unsigned8(); // >=0 values used only
    public final Unsigned8 bFrequency = new Unsigned8(); // >=0 values used only

    @Override
    public String toString() {
        return "\t\t\tbDelay=" + bDelay.get() + "\n"
                + "\t\t\tbFrequency=" + bFrequency.get();
    }

}

class OBJECT_BOMBS_AND_OTHER_U2 extends Union {

    public final Unsigned8 ubTolerance = new Unsigned8(); // tolerance value for panic triggers
    public final Unsigned8 ubLocationID = new Unsigned8(); // location value for remote non-bomb (special!) triggers

    @Override
    public String toString() {
        return "\t\t\tubTolerance=" + ubTolerance.get() + "\n"
                + "\t\t\tubLocationID=" + ubLocationID.get();
    }

}

class OBJECT_KEY extends Struct {

    public final Signed16 bKeyStatus = new Signed16();
    public final Unsigned8 ubKeyID = new Unsigned8();

    @Override
    public String toString() {
        return "\t\tbKeyStatus=" + bKeyStatus.get() + "\n"
                + "\t\tubKeyID=" + ubKeyID.get();
    }

}

class OBJECT_OWNER extends Struct {

    public final Unsigned16 ubOwnerProfile = new Unsigned16();
    public final Unsigned8 ubOwnerCivGroup = new Unsigned8();

    @Override
    public String toString() {
        return "\t\tubOwnerProfile=" + ubOwnerProfile.get() + "\n"
                + "\t\tubOwnerCivGroup=" + ubOwnerCivGroup.get();
    }

}

class OBJECT_LBE extends Struct {

    public final Signed16 bLBEStatus = new Signed16();
    public final Unsigned8 bLBE = new Unsigned8(); // Marks item as LBENODE
    public final Signed32 uniqueID = new Signed32(); // how the LBENODE is accessed

    @Override
    public String toString() {
        return "\t\tbLBEStatus=" + bLBEStatus.get() + "\n"
                + "\t\tbLBE=" + bLBE.get() + "\n"
                + "\t\tuniqueID=" + uniqueID.get();
    }

};

class ObjectDataUnion extends Union {

    public final Signed16 objectStatus = new Signed16(); //holds the same value as bStatus[0]
    public final Unsigned16 ubShotsLeft = new Unsigned16(); //holds the same value as ubShotsLeft[0]

    public final OBJECT_GUN gun = inner(new OBJECT_GUN());
    public final OBJECT_MONEY money = inner(new OBJECT_MONEY());
    public final OBJECT_BOMBS_AND_OTHER misc = inner(new OBJECT_BOMBS_AND_OTHER());
    public final OBJECT_KEY key = inner(new OBJECT_KEY());
    public final OBJECT_OWNER owner = inner(new OBJECT_OWNER());
    public final OBJECT_LBE lbe = inner(new OBJECT_LBE());

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
                + "UNION AS: lbe=\n" + lbe.toString() + "\n-------------\n\tRAW: "
                + super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

}
