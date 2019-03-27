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
package thebob.assetmanager.managers.items;

import ja2.xml.ammo.calibers.AMMO;
import ja2.xml.ammo.types.AMMOTYPELIST;
import ja2.xml.items.ITEMTYPE;
import ja2.xml.items.armor.ARMOURLIST;
import ja2.xml.items.drugs.DRUGSLIST.DRUG;
import ja2.xml.items.explosives.EXPLOSIVELIST;
import ja2.xml.items.food.FOODSLIST.FOOD;
import ja2.xml.items.lbe.LOADBEARINGEQUIPMENTLIST;
import ja2.xml.items.magazines.MAGAZINELIST;
import ja2.xml.items.weapons.WEAPONLIST;
import javafx.scene.image.Image;
import thebob.assetloader.common.ImageAdapter;
import thebob.assetloader.sti.StiLoader;
import thebob.assetmanager.managers.items.categories.ItemCategory;

/**
 *
 * @author the_bob
 */
public class Item {

	int id;
	String name;
	long itemType;
	int itemTypeId;
	
	private int nasAttachmentClass;

	int coolness;

	int imageType;
	int imageId;
	Image image = null;
	boolean imageLoadFailed = false;

	int clothesType;
	int foodType;
	int drugType;

	MAGAZINELIST.MAGAZINE magazineData = null;
	AMMO CaliberData = null;
	AMMOTYPELIST.AMMOTYPE AmmoTypeData = null;
	WEAPONLIST.WEAPON WeaponData = null;
	EXPLOSIVELIST.EXPLOSIVE explosiveData = null;
	ARMOURLIST.ARMOUR armorData = null;
	LOADBEARINGEQUIPMENTLIST.LOADBEARINGEQUIPMENT lbeData = null;

	StiLoader loader = null;
	ITEMTYPE itemDef = null;
	private ItemCategory parentCategory;
	

	Item(int uiIndex, String szItemName) {
		id = uiIndex;
		name = szItemName;
	}

	public Item(int id, String name, long itemType, int itemTypeId, int imageType, int imageId, int coolness, int nasAttachmentClass) {
		this.id = id;
		this.name = name;
		this.itemType = itemType;
		this.itemTypeId = itemTypeId;
		this.imageType = imageType;
		this.imageId = imageId;
		this.coolness = coolness;
		this.nasAttachmentClass = nasAttachmentClass;
	}

	public Item(ITEMTYPE itemDef) {
		this(itemDef.getUiIndex(), 
			itemDef.getSzLongItemName(), 
			itemDef.getUsItemClass(), 
			itemDef.getUbClassIndex(), 
			itemDef.getUbGraphicType(), 
			itemDef.getUbGraphicNum(), 
			itemDef.getUbCoolness(), 
			itemDef.getNasAttachmentClass());
		
		this.itemDef = itemDef;
	}

	public Image getImage() {
		if (image == null && !imageLoadFailed) {
			loadImage();
		}
		return image;
	}

	private void loadImage() {
		image = loader.getJFXImage(imageId);
		if(image == null){
			imageLoadFailed = true;
			System.err.println("Failed loading image id: " + imageId + " for " + this + ", from: " + loader.toString());
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getItemType() {
		return itemType;
	}

	public void setItemType(long itemType) {
		this.itemType = itemType;
	}

	public int getItemTypeId() {
		return itemTypeId;
	}

	public void setItemTypeId(int itemTypeId) {
		this.itemTypeId = itemTypeId;
	}

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public int getClothesType() {
		return clothesType;
	}

	public void setClothesType(int clothesType) {
		this.clothesType = clothesType;
	}

	public int getFoodType() {
		return foodType;
	}

	public void setFoodType(int foodType) {
		this.foodType = foodType;
	}

	public int getDrugType() {
		return drugType;
	}

	public void setDrugType(int drugType) {
		this.drugType = drugType;
	}

	public MAGAZINELIST.MAGAZINE getMagazineData() {
		return magazineData;
	}

	public void setMagazineData(MAGAZINELIST.MAGAZINE magazineData) {
		this.magazineData = magazineData;
	}

	public AMMO getCaliberData() {
		return CaliberData;
	}

	public void setCaliberData(AMMO CaliberData) {
		this.CaliberData = CaliberData;
	}

	public AMMOTYPELIST.AMMOTYPE getAmmoTypeData() {
		return AmmoTypeData;
	}

	public void setAmmoTypeData(AMMOTYPELIST.AMMOTYPE AmmoTypeData) {
		this.AmmoTypeData = AmmoTypeData;
	}

	public WEAPONLIST.WEAPON getWeaponData() {
		return WeaponData;
	}

	public void setWeaponData(WEAPONLIST.WEAPON WeaponData) {
		this.WeaponData = WeaponData;
	}

	public EXPLOSIVELIST.EXPLOSIVE getExplosiveData() {
		return explosiveData;
	}

	public void setExplosiveData(EXPLOSIVELIST.EXPLOSIVE explosiveData) {
		this.explosiveData = explosiveData;
	}

	public ARMOURLIST.ARMOUR getArmorData() {
		return armorData;
	}

	public void setArmorData(ARMOURLIST.ARMOUR armorData) {
		this.armorData = armorData;
	}

	public LOADBEARINGEQUIPMENTLIST.LOADBEARINGEQUIPMENT getLbeData() {
		return lbeData;
	}

	public void setLbeData(LOADBEARINGEQUIPMENTLIST.LOADBEARINGEQUIPMENT lbeData) {
		this.lbeData = lbeData;
	}

	DRUG drugData;
	FOOD foodData;

	public DRUG getDrugData() {
		return drugData;
	}

	public void setDrugData(DRUG drugData) {
		this.drugData = drugData;
	}

	public FOOD getFoodData() {
		return foodData;
	}

	public void setFoodData(FOOD foodData) {
		this.foodData = foodData;
	}

	public void setImageSource(StiLoader get) {
		loader = get;
	}

	public int getCoolness() {
		return coolness;
	}

	public void setCoolness(int coolness) {
		this.coolness = coolness;
	}

	public int getNasAttachmentClass() {
		return nasAttachmentClass;
	}

	public void setNasAttachmentClass(int nasAttachmentClass) {
		this.nasAttachmentClass = nasAttachmentClass;
	}

	// ---
	
	public ITEMTYPE getItemDef() {
		return itemDef;
	}

	public void setItemDef(ITEMTYPE itemDef) {
		this.itemDef = itemDef;
	}

	public ItemCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(ItemCategory aThis) {
		parentCategory = aThis;
	}

	@Override
	public String toString() {
		return "Item{" + "id=" + id + ", name=" + name + ", category=" + parentCategory.getNameWithPath() + '}';
	}

	
}
