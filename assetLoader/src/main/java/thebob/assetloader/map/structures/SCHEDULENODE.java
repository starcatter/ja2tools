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
import thebob.assetloader.map.structures.legacy.OLD_SCHEDULENODE;

/**
 *
 * @author the_bob
 */
public class SCHEDULENODE extends AutoLoadingMapStruct {

    public final Reference32<SCHEDULENODE> next = new Reference32<SCHEDULENODE>();
    public final Unsigned16[] usTime = array(new Unsigned16[4]);
    // Converted to minutes 12:30PM would be 12*60 + 30 = 750
    public final Unsigned32[] usData1 = array(new Unsigned32[4]); // Typically the gridno, but depends on the action
    public final Unsigned32[] usData2 = array(new Unsigned32[4]); // Secondary information, not used by most actions
    public final Unsigned8[] ubAction = array(new Unsigned8[4]);
    public final Unsigned8 ubScheduleID = new Unsigned8();
    public final Unsigned8 ubSoldierID = new Unsigned8();
    public final Unsigned16 usFlags = new Unsigned16();

    public void loadOld(OLD_SCHEDULENODE old) {
        next.set(old.next.get());
        ubScheduleID.set(old.ubScheduleID.get());
        ubSoldierID.set(old.ubSoldierID.get());
        usFlags.set(old.usFlags.get());

        for (int i = 0; i < usTime.length; i++) {
            usTime[i].set(old.usTime[i].get());
        }

        for (int i = 0; i < usData1.length; i++) {
            usData1[i].set(old.usData1[i].get());
        }

        for (int i = 0; i < usData2.length; i++) {
            usData2[i].set(old.usData2[i].get());
        }

        for (int i = 0; i < ubAction.length; i++) {
            ubAction[i].set(old.ubAction[i].get());
        }

    }

}
