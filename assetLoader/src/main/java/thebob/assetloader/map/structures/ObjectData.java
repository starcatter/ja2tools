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
import ja2.xml.items.ITEMTYPE;
import java.nio.ByteOrder;
import static thebob.assetloader.map.MapLoader.logEverything;
import thebob.assetloader.map.structures.legacy.OLD_OBJECTTYPE_101;
import static thebob.assetloader.map.core.components.MapActors.ItemClassTypes.*;
import thebob.assetloader.map.structures.legacy.OLD_OBJECTTYPE_101_UNION;
import static thebob.assetloader.map.core.MapData.xmlDataSource;

/**
 *
 * @author the_bob
 */
public class ObjectData extends AutoLoadingMapStruct {

    public final int TRIPWIRE_NETWORK_OWNER_ENEMY = 0x00000001;	//1			// this wire belongs to an enemy network
    public final int TRIPWIRE_NETWORK_OWNER_PLAYER = 0x00000002;	//2			// this wire was set by the player

    public final int TRIPWIRE_NETWORK_NET_1 = 0x00000010;	//16		// network number  of the wire
    public final int TRIPWIRE_NETWORK_NET_2 = 0x00000020;	//32
    public final int TRIPWIRE_NETWORK_NET_3 = 0x00000040;	//64
    public final int TRIPWIRE_NETWORK_NET_4 = 0x00000080;	//128

    public final int TRIPWIRE_NETWORK_LVL_1 = 0x00100000;	//1048576	// hierarchy level of the wire

    //public final Signed8[] unionPlaceholder = array(new Signed8[12]);
    public final ObjectDataUnion data = inner(new ObjectDataUnion());

    public final Signed8 bTrap = new Signed8(); // 1-10 exp_lvl to detect
    public final Unsigned8 fUsed = new Unsigned8(); // flags for whether the item is used or not
    public final Unsigned8 ubImprintID = new Unsigned8(); // ID of merc that item is imprinted on

    // these variables should belong to a different position. However, I am forced to put them here, otherwise loading of WF maps and other old data would not work properly
    public final Float32 bTemperature = new Float32(); // Flugente FTW 1.2: temperature of gun
    // should belong to misc, but was moved here because of the old maps issue

    // added by Flugente 12-04-15
    public final Unsigned8 ubDirection = new Unsigned8(); // direction the bomb faces (for directional explosives)
    public final Unsigned32 ubWireNetworkFlag = new Unsigned32(); // flags for the tripwire network
    public final Signed8 bDefuseFrequency = new Signed8(); // frequency for defusing, >=0 values used only

    // Flugente: advanced repair system
    public final Signed16 sRepairThreshold = new Signed16(); // repair only possible up to this value
    public final Float32 bFiller = new Float32(); // unused for now
    // FIXME: should be unsigned!
    public final Signed64 sObjectFlag = new Signed64(); // used to notify of various states that apply to this object, but not the item in general

    // TODO: support the case when there's more than one item in stack, the way 1.13 code does.
    public void loadOld(OLD_OBJECTTYPE_101 oldItem, int i) {
        bTrap.set(oldItem.bTrap.get());
        fUsed.set(oldItem.fUsed.get());
        sObjectFlag.set(oldItem.fFlags.get());

        //
        int itemId = oldItem.usItem.get();
        OLD_OBJECTTYPE_101_UNION oldData = oldItem.data;
        ITEMTYPE itemData = xmlDataSource.getItems().getITEM().get(itemId);
        int itemClass = (int) itemData.getUsItemClass();

        //CHRISL: Instead of a memcpy, copy values individually so we can account for the larger union size
        //memcpy(&((*this)[0]->data.gun), &src.ugYucky, __min(SIZEOF_OLD_OBJECTTYPE_101_UNION,sizeof(ObjectData)));
        //Start by setting status as this is the same for every structure in the union
        data.gun.bGunStatus.set(oldData.gun.bGunStatus.get());
        //Next, clear the rest of the union so we are working with a clean setup.  I'm clearing gun simply because it's
        //	the largest structure and should therefore clear all values.  There may be a better way to do this.
        data.gun.ubGunAmmoType.set((byte) 0);
        data.gun.ubGunShotsLeft.set(0);
        data.gun.usGunAmmoItem.set(0);
        data.gun.bGunAmmoStatus.set((byte) 0);
        data.gun.ubGunState.set((byte) 0);

        switch (itemClass) {
            case IC_MONEY:
                if(logEverything) System.out.println("loader.structures.ObjectData.loadOld(): AS MONEY");
                data.money.uiMoneyAmount.set(oldData.money.uiMoneyAmount.get());
                break;
            case IC_KEY:
                if(logEverything) System.out.println("loader.structures.ObjectData.loadOld(): AS KEY");
                data.key.ubKeyID.set(oldData.key.ubKeyID.get());
                break;
            case IC_GRENADE:
            case IC_BOMB:
                if(logEverything) System.out.println("loader.structures.ObjectData.loadOld(): AS EXPLOSIVE");
                data.misc.bDetonatorType.set(oldData.misc.bDetonatorType.get());
                data.misc.usBombItem.set(oldData.misc.usBombItem.get());
                data.misc.u1.bDelay.set(oldData.misc.u1.bDelay.get());
                data.misc.ubBombOwner.set(oldData.misc.ubBombOwner.get());
                data.misc.bActionValue.set(oldData.misc.bActionValue.get());
                data.misc.u2.ubTolerance.set(oldData.misc.u2.ubTolerance.get());
                ubDirection.set((byte) 9);
                ubWireNetworkFlag.set((TRIPWIRE_NETWORK_OWNER_ENEMY | TRIPWIRE_NETWORK_NET_1 | TRIPWIRE_NETWORK_LVL_1));
                bDefuseFrequency.set((byte) 0);
                break;
            default:
                if(logEverything) System.out.println("loader.structures.ObjectData.loadOld(): AS GUN");
                data.gun.ubGunAmmoType.set(oldData.gun.ubGunAmmoType.get());
                data.gun.ubGunShotsLeft.set(oldData.gun.ubGunShotsLeft.get());
                data.gun.usGunAmmoItem.set(oldData.gun.usGunAmmoItem.get());
                data.gun.bGunAmmoStatus.set(oldData.gun.bGunAmmoStatus.get());
                data.gun.ubGunState.set(oldData.gun.ubGunState.get());
        }

        sRepairThreshold.set((short) 100);
        bTemperature.set((byte) 0);
    }

    @Override
    public String toString() {
        return "ObjectData " + size() + " @" + getByteBufferPosition() + ":\n\t"
                + "data: " + data.toString() + " @" + data.size() + "\n\t"
                + "bTrap: " + bTrap.get() + " @" + bTrap.offset() + "\n\t"
                + "fUsed: " + fUsed.get() + " @" + fUsed.offset() + "\n\t"
                + "ubImprintID: " + ubImprintID.get() + " @" + ubImprintID.offset() + "\n\t"
                + "bTemperature: " + bTemperature.get() + " @" + bTemperature.offset() + "\n\t"
                + "---\n\t"
                + "ubDirection: " + ubDirection.get() + " @" + ubDirection.offset() + "\n\t"
                + "ubWireNetworkFlag: " + ubWireNetworkFlag.get() + " @" + ubWireNetworkFlag.offset() + "\n\t"
                + "bDefuseFrequency: " + bDefuseFrequency.get() + " @" + bDefuseFrequency.offset() + "\n\t"
                + "sRepairThreshold: " + sRepairThreshold.get() + " @" + sRepairThreshold.offset() + "\n\t"
                + "bFiller: " + bFiller.get() + " @" + bFiller.offset() + "\n\t"
                + "sObjectFlag: " + sObjectFlag.get() + " @" + sObjectFlag.offset() + "\n--------------\n\t"
                + super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

}
