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
import javolution.io.Struct;

/**
 *
 * @author the_bob
 */
public class LIGHT_SPRITE extends AutoLoadingMapStruct {
    
    Signed16 iX = new Signed16();
    Signed16 iY = new Signed16();
    Signed16 iOldX = new Signed16();
    Signed16 iOldY = new Signed16();
    Signed16 iAnimSpeed = new Signed16();
    Signed32 iTemplate = new Signed32();
    Unsigned32 uiFlags = new Unsigned32();
    Unsigned32 uiLightType = new Unsigned32();
    
}
