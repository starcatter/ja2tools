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
import thebob.assetloader.map.structures.legacy.OLD_EXITGRID;

/**
 *
 * @author the_bob
 */
public class EXITGRID extends AutoLoadingMapStruct {
    
    public final Signed32 iMapIndex = new Signed32(); //dnl ch86 170214
    public final Signed32 usGridNo = new Signed32(); // Sweet spot for placing mercs in new sector.
    public final Unsigned8 ubGotoSectorX = new Unsigned8();
    public final Unsigned8 ubGotoSectorY = new Unsigned8();
    public final Unsigned8 ubGotoSectorZ = new Unsigned8();

    public void loadOld(OLD_EXITGRID old){
        iMapIndex.set( old.iMapIndex.get() );
        usGridNo.set( old.usGridNo.get() );
        ubGotoSectorX.set( old.ubGotoSectorX.get() );
        ubGotoSectorY.set( old.ubGotoSectorY.get() );
        ubGotoSectorZ.set( old.ubGotoSectorZ.get() );
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
