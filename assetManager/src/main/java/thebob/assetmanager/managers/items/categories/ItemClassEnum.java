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

    protected static final Map<Long, ItemClassEnum> itemClassMap = new HashMap<Long, ItemClassEnum>();

    public static Map<Long, ItemClassEnum> getItemClassMap() {
        return itemClassMap;
    }

    static {
	itemClassMap.put(2L, ItemClassEnum.Gun);
	itemClassMap.put(4L, ItemClassEnum.Knife);
	itemClassMap.put(8L, ItemClassEnum.Throwing_Knife);
	itemClassMap.put(16L, ItemClassEnum.Launcher);
	itemClassMap.put(32L, ItemClassEnum.Tentacle);
	itemClassMap.put(64L, ItemClassEnum.Thrown_Weapon);
	itemClassMap.put(128L, ItemClassEnum.Blunt_Weapon);
	itemClassMap.put(256L, ItemClassEnum.Grenade);
	itemClassMap.put(512L, ItemClassEnum.Bomb);
	itemClassMap.put(1024L, ItemClassEnum.Ammo);
	itemClassMap.put(2048L, ItemClassEnum.Armour);
	itemClassMap.put(4096L, ItemClassEnum.Medkit);
	itemClassMap.put(8192L, ItemClassEnum.Kit);
	itemClassMap.put(32768L, ItemClassEnum.Face_Item);
	itemClassMap.put(65536L, ItemClassEnum.Key);
	itemClassMap.put(131072L, ItemClassEnum.Load_Bearing_Equipment);
	itemClassMap.put(268435456L, ItemClassEnum.Misc);
	itemClassMap.put(536870912L, ItemClassEnum.Money);
	itemClassMap.put(1073741824L, ItemClassEnum.Random_Item);
    }
}
