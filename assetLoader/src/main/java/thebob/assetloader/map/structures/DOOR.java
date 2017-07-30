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
import thebob.assetloader.map.structures.legacy.OLD_DOOR;

/**
 *
 * @author the_bob
 */
public class DOOR extends AutoLoadingMapStruct {
    
    public final Signed32 sGridNo = new Signed32();
    public final Bool fLocked = new Bool(); // is the door locked
    public final Unsigned8 ubTrapLevel = new Unsigned8(); // difficulty of finding the trap, 0-10
    public final Unsigned8 ubTrapID = new Unsigned8(); // the trap type (0 is no trap)
    public final Unsigned8 ubLockID = new Unsigned8(); // the lock (0 is no lock)
    public final Signed8 bPerceivedLocked = new Signed8(); // The perceived lock value can be different than the fLocked.
    // Values for this include the fact that we don't know the status of
    // the door, etc
    public final Signed8 bPerceivedTrapped = new Signed8(); // See above, but with respect to traps rather than locked status
    public final Signed8 bLockDamage = new Signed8(); // Damage to the lock
    
    public void loadOld(OLD_DOOR old){
        sGridNo.set( old.sGridNo.get() );
        fLocked.set( old.fLocked.get() );
        ubTrapLevel.set( old.ubTrapLevel.get() );
        ubTrapID.set( old.ubTrapID.get() );
        ubLockID.set( old.ubLockID.get() );
        bPerceivedLocked.set( old.bPerceivedLocked.get() );
        bPerceivedTrapped.set( old.bPerceivedTrapped.get() );
        bLockDamage.set( old.bLockDamage.get() );    
    }
    
}
