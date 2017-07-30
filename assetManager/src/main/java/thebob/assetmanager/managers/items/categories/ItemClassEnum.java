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
package thebob.assetmanager.managers.items.categories;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author the_bob
 */
public enum ItemClassEnum {
    Gun,
    Knife,
    Throwing_Knife,
    Launcher,
    Tentacle,
    Thrown_Weapon,
    Blunt_Weapon,
    Grenade,
    Bomb,
    Ammo,
    Armour,
    Medkit,
    Kit,
    Face_Item,
    Key,
    Load_Bearing_Equipment,
    Misc,
    Money,
    Random_Item;

    public static Map<Long, ItemClassEnum> ItemClassMap = new HashMap<Long, ItemClassEnum>();

    static {
	ItemClassMap.put(2L, ItemClassEnum.Gun);
	ItemClassMap.put(4L, ItemClassEnum.Knife);
	ItemClassMap.put(8L, ItemClassEnum.Throwing_Knife);
	ItemClassMap.put(16L, ItemClassEnum.Launcher);
	ItemClassMap.put(32L, ItemClassEnum.Tentacle);
	ItemClassMap.put(64L, ItemClassEnum.Thrown_Weapon);
	ItemClassMap.put(128L, ItemClassEnum.Blunt_Weapon);
	ItemClassMap.put(256L, ItemClassEnum.Grenade);
	ItemClassMap.put(512L, ItemClassEnum.Bomb);
	ItemClassMap.put(1024L, ItemClassEnum.Ammo);
	ItemClassMap.put(2048L, ItemClassEnum.Armour);
	ItemClassMap.put(4096L, ItemClassEnum.Medkit);
	ItemClassMap.put(8192L, ItemClassEnum.Kit);
	ItemClassMap.put(32768L, ItemClassEnum.Face_Item);
	ItemClassMap.put(65536L, ItemClassEnum.Key);
	ItemClassMap.put(131072L, ItemClassEnum.Load_Bearing_Equipment);
	ItemClassMap.put(268435456L, ItemClassEnum.Misc);
	ItemClassMap.put(536870912L, ItemClassEnum.Money);
	ItemClassMap.put(1073741824L, ItemClassEnum.Random_Item);
    }
}
