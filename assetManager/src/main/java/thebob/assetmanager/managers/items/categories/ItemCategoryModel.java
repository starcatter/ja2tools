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

import ja2.xml.ammo.calibers.AMMO;
import ja2.xml.items.armor.ARMOURLIST;
import ja2.xml.items.explosives.EXPLOSIVELIST;
import ja2.xml.items.lbe.LOADBEARINGEQUIPMENTLIST;
import ja2.xml.items.magazines.MAGAZINELIST;
import ja2.xml.items.weapons.WEAPONLIST;
import ja2.xml.lookup.itemclass.JA2Data;
import java.util.HashMap;
import java.util.Map;
import thebob.assetloader.xml.XmlLoader;
import thebob.assetmanager.managers.items.Item;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Ammo;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Armour;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Blunt_Weapon;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Bomb;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Grenade;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Gun;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.ItemClassMap;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Knife;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Launcher;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Load_Bearing_Equipment;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Tentacle;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Throwing_Knife;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.Thrown_Weapon;

/**
 *
 * @author the_bob
 */
public class ItemCategoryModel {

	XmlLoader xml;
	Map<Integer, Item> items;
	ItemCategory root = new ItemCategory("Items", null);
	Map<Long, JA2Data.ItemClass> itemclass = new HashMap<Long, JA2Data.ItemClass>();

	// maps for quick access to categories
	// --	Categories by item class
	Map<Long, ItemCategory> itemClassMap = new HashMap<Long, ItemCategory>();
	// --	Categories by item lookup types
	Map<Integer, ItemCategory> armorClassMap = new HashMap<Integer, ItemCategory>();
	Map<Integer, ItemCategory> explosionTypeMap = new HashMap<Integer, ItemCategory>();
	Map<Integer, ItemCategory> bombExplosionTypeMap = new HashMap<Integer, ItemCategory>();
	Map<Integer, ItemCategory> lbeClassMap = new HashMap<Integer, ItemCategory>();
	Map<Integer, ItemCategory> magazinetypemap = new HashMap<Integer, ItemCategory>();
	Map<Integer, ItemCategory> weapontypemap = new HashMap<Integer, ItemCategory>();
	// --	Categories by caliber
	HashMap<Integer, HashMap<Integer, ItemCategory>> magCaliberMap = new HashMap<Integer, HashMap<Integer, ItemCategory>>();
	HashMap<Integer, HashMap<Integer, ItemCategory>> gunCaliberMap = new HashMap<Integer, HashMap<Integer, ItemCategory>>();
	// -- food and drugs (and eventually clothes)
	ItemCategory food = null;
	ItemCategory drugs = null;
	ItemCategory clothes = null;
	ItemCategory attachments = null;

	public Map<Integer, Item> getItems() {
		return items;
	}

	public Map<Long, JA2Data.ItemClass> getItemclass() {
		return itemclass;
	}

	public Map<Long, ItemCategory> getItemClassMap() {
		return itemClassMap;
	}

	public Map<Integer, ItemCategory> getArmorClassMap() {
		return armorClassMap;
	}

	public Map<Integer, ItemCategory> getExplosionTypeMap() {
		return explosionTypeMap;
	}

	public Map<Integer, ItemCategory> getBombExplosionTypeMap() {
		return bombExplosionTypeMap;
	}

	public Map<Integer, ItemCategory> getLbeClassMap() {
		return lbeClassMap;
	}

	public Map<Integer, ItemCategory> getMagazinetypemap() {
		return magazinetypemap;
	}

	public Map<Integer, ItemCategory> getWeapontypemap() {
		return weapontypemap;
	}

	public HashMap<Integer, HashMap<Integer, ItemCategory>> getMagCaliberMap() {
		return magCaliberMap;
	}

	public HashMap<Integer, HashMap<Integer, ItemCategory>> getGunCaliberMap() {
		return gunCaliberMap;
	}

	public ItemCategory getFood() {
		return food;
	}

	public ItemCategory getDrugs() {
		return drugs;
	}

	public ItemCategory getClothes() {
		return clothes;
	}

	public ItemCategory getAttachments() {
		return attachments;
	}

	public ItemCategoryModel(XmlLoader xml, Map<Integer, Item> items) {
		this.xml = xml;
		this.items = items;
		for (JA2Data.ItemClass lookup : xml.getItemClass().getItemClass()) {
			itemclass.put(lookup.getId(), lookup);
		}
	}

	public void buildCategoryTree() {
		buildCategories();
		populateCategories();
	}

	private void buildCategories() {
		for (long itemClassId : itemclass.keySet()) {
			ItemCategory node = new ItemCategory(itemclass.get(itemClassId).getName(), root);
			itemClassMap.put(itemClassId, node);
			ItemClassEnum itemClassEnum = ItemClassMap.get(itemClassId);
			if (itemClassEnum != null) {
				switch (itemClassEnum) {
					case Ammo:
						for (ja2.xml.lookup.magazinetype.JA2Data.MagazineType lookup : xml.getMagazineType().getMagazineType()) {
							ItemCategory magNode = new ItemCategory(lookup.getName(), node);
							magazinetypemap.put((int) lookup.getId(), magNode);
							HashMap<Integer, ItemCategory> typeCalibers = new HashMap<Integer, ItemCategory>();
							magCaliberMap.put((int) lookup.getId(), typeCalibers);
							for (AMMO caliber : xml.getCalibers().getAMMO()) {
								typeCalibers.put((int) caliber.getUiIndex(), new ItemCategory(caliber.getAmmoCaliber(), magNode));
							}
						}
						break;
					case Gun:
						for (ja2.xml.lookup.weapontype.JA2Data.WeaponType lookup : xml.getWeaponType().getWeaponType()) {
							ItemCategory gunNode = new ItemCategory(lookup.getName(), node);
							weapontypemap.put((int) lookup.getId(), gunNode);
							HashMap<Integer, ItemCategory> typeCalibers = new HashMap<Integer, ItemCategory>();
							gunCaliberMap.put((int) lookup.getId(), typeCalibers);
							for (AMMO caliber : xml.getCalibers().getAMMO()) {
								typeCalibers.put((int) caliber.getUiIndex(), new ItemCategory(caliber.getAmmoCaliber(), gunNode));
							}
						}
						break;

					case Knife:
					case Throwing_Knife:
					case Launcher:
					case Tentacle:
					case Thrown_Weapon:
					case Blunt_Weapon:
						// ???
						// for (WeaponClass lookup : xml.getWeaponClass().getWeaponClass()) {
						//     weaponclassmap.put((int) lookup.getId(), new ItemCategory(lookup.getName(), node));
						// }
						break;

					case Grenade:
						for (ja2.xml.lookup.explosiontype.JA2Data.ExplosionType lookup : xml.getExplosionType().getExplosionType()) {
							explosionTypeMap.put((int) lookup.getId(), new ItemCategory(lookup.getName(), node));
						}
						break;
					case Bomb:
						for (ja2.xml.lookup.explosiontype.JA2Data.ExplosionType lookup : xml.getExplosionType().getExplosionType()) {
							bombExplosionTypeMap.put((int) lookup.getId(), new ItemCategory(lookup.getName(), node));
						}
						break;
					case Armour:
						for (ja2.xml.lookup.armourclass.JA2Data.ArmourClass lookup : xml.getArmourClass().getArmourClass()) {
							armorClassMap.put((int) lookup.getId(), new ItemCategory(lookup.getName(), node));
						}
						break;
					case Load_Bearing_Equipment:
						for (ja2.xml.lookup.lbeclass.JA2Data.LBEClass lookup : xml.getLBEClass().getLBEClass()) {
							lbeClassMap.put((int) lookup.getId(), new ItemCategory(lookup.getName(), node));
						}
						break;
					case Medkit:
						break;
					case Kit:
						break;
					case Face_Item:
						break;
					case Key:
						break;
					case Misc:
						food = new ItemCategory("Food", node);
						drugs = new ItemCategory("Drugs", node);
						clothes = new ItemCategory("Clothes", node);
						attachments = new ItemCategory("Attachments", node);
						break;
					case Money:
						break;
					case Random_Item:
						break;
					default:
				}
			}
		}
	}

	private void populateCategories() {
		for (int itemId : items.keySet()) {
			Item item = items.get(itemId);
			long itemType = item.getItemType();
			ItemClassEnum itemClass = ItemClassMap.get(itemType);

			if (itemClass != null) {
				switch (itemClass) {
					case Ammo:
						MAGAZINELIST.MAGAZINE magData = item.getMagazineData();
						if (magData != null) {
							Short magType = magData.getUbMagType();
							Short magCaliber = magData.getUbCalibre();
							if (magType != null && magCaliber != null) {
								magCaliberMap.get((int) magType).get((int) magCaliber).addItem(item);
								break;
							}
						}
						itemClassMap.get(itemType).addItem(item);
						break;
					case Gun:
						WEAPONLIST.WEAPON gunData = item.getWeaponData();
						if (gunData != null) {
							Short gunType = gunData.getUbWeaponType();
							Short gunCaliber = gunData.getUbCalibre();
							if (gunType != null && gunCaliber != null) {
								gunCaliberMap.get((int) gunType).get((int) gunCaliber).addItem(item);
								break;
							}
						}
						itemClassMap.get(itemType).addItem(item);
						break;
					case Grenade:
						EXPLOSIVELIST.EXPLOSIVE grenadeData = item.getExplosiveData();
						if (grenadeData != null) {
							ItemCategory explosionCategory = explosionTypeMap.get((int) grenadeData.getUbType());
							if (explosionCategory != null) {
								explosionCategory.addItem(item);
							}
						}
						break;
					case Bomb:
						EXPLOSIVELIST.EXPLOSIVE bombData = item.getExplosiveData();
						if (bombData != null) {
							ItemCategory explosionCategory = bombExplosionTypeMap.get((int) bombData.getUbType());
							if (explosionCategory != null) {
								explosionCategory.addItem(item);
							}
						}
						break;
					case Armour:
						ARMOURLIST.ARMOUR armorData = item.getArmorData();
						if (armorData != null) {
							armorClassMap.get((int) armorData.getUbArmourClass()).addItem(item);
						}
						break;
					case Load_Bearing_Equipment:
						LOADBEARINGEQUIPMENTLIST.LOADBEARINGEQUIPMENT lbeData = item.getLbeData();
						if (lbeData != null) {
							lbeClassMap.get((int) lbeData.getLbeClass()).addItem(item);
						}
						break;

					//case Kit:			
					//case Face_Item:	
					case Misc:
						if (item.getFoodType() > 0) {
							food.addItem(item);
							break;
						} else if (item.getDrugType() > 0) {
							drugs.addItem(item);
							break;
						} else if (item.getClothesType() > 0) {
							clothes.addItem(item);
							break;
						} else if (item.getNasAttachmentClass() > 0) {
							attachments.addItem(item);
							break;
						}

					default:
						itemClassMap.get(itemType).addItem(item);
				}
			}
		}
	}

	public ItemCategory getRootNode() {
		return root;
	}

}
