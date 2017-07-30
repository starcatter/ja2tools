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

/**
 *
 * @author the_bob
 */
public class OLD_MAPCREATE_STRUCT extends AutoLoadingMapStruct {
    //These are the mandatory entry points for a map.	If any of the values are -1, then that means that
    //the point has been specifically not used and that the map is not traversable to or from an adjacent 
    //sector in that direction.	The >0 value points must be validated before saving the map.	This is 
    //done by simply checking if those points are sittable by mercs, and that you can plot a path from 
    //these points to each other.	These values can only be set by the editor : mapinfo tab

    public final Signed16 sNorthGridNo = new Signed16();
    public final Signed16 sEastGridNo = new Signed16();
    public final Signed16 sSouthGridNo = new Signed16();
    public final Signed16 sWestGridNo = new Signed16();
    //This contains the number of individuals in the map.
    //Individuals include NPCs, enemy placements, creatures, civilians, rebels, and animals.
    public final Unsigned8 ubNumIndividuals = new Unsigned8();
    public final Unsigned8 ubMapVersion = new Unsigned8();
    public final Unsigned8 ubRestrictedScrollID = new Unsigned8();
    public final Unsigned8 ubEditorSmoothingType = new Unsigned8();	//normal, basement, or caves
    public final Signed16 sCenterGridNo = new Signed16();
    public final Signed16 sIsolatedGridNo = new Signed16();
    Signed8[] bPadding = array(new Signed8[83]);	//I'm sure lots of map info will be added    
}
