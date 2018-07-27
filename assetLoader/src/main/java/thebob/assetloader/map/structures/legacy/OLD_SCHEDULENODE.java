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
import java.util.Arrays;

import thebob.assetloader.map.helpers.AutoLoadingMapStruct;
import thebob.assetloader.map.structures.SCHEDULENODE;

/**
 * @author the_bob
 */
public class OLD_SCHEDULENODE extends AutoLoadingMapStruct {
    public static final int OLD_MAX_SCHEDULE_ACTIONS = 4;

    public final Reference32<SCHEDULENODE> next = new Reference32<SCHEDULENODE>();
    public final Unsigned16[] usTime = array(new Unsigned16[OLD_MAX_SCHEDULE_ACTIONS]);
    // Converted to minutes 12:30PM would be 12*60 + 30 = 750
    public final Unsigned16[] usData1 = array(new Unsigned16[OLD_MAX_SCHEDULE_ACTIONS]); // Typically the gridno, but depends on the action
    public final Unsigned16[] usData2 = array(new Unsigned16[OLD_MAX_SCHEDULE_ACTIONS]); // Secondary information, not used by most actions
    public final Unsigned8[] ubAction = array(new Unsigned8[OLD_MAX_SCHEDULE_ACTIONS]);
    public final Unsigned8 ubScheduleID = new Unsigned8();
    public final Unsigned8 ubSoldierID = new Unsigned8();
    public final Unsigned16 usFlags = new Unsigned16();

    @Override
    public String toString() {
        return "OLD_SCHEDULENODE{" +
                "next=" + next +
                ", usTime=" + mappedArrayToNumberString(usTime) +
                ", usData1=" + mappedArrayToNumberString(usData1) +
                ", usData2=" + mappedArrayToNumberString(usData2) +
                ", ubAction=" + mappedArrayToNumberString(ubAction) +
                ", ubScheduleID=" + ubScheduleID.get() +
                ", ubSoldierID=" + ubSoldierID.get() +
                ", usFlags=" + usFlags.get() +
                '}';
    }
}
