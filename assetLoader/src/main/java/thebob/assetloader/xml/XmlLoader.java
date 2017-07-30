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
package thebob.assetloader.xml;

import com.google.common.collect.ImmutableMap;
import ja2.xml.ammo.calibers.AMMO;
import ja2.xml.ammo.calibers.AMMOLIST;
import ja2.xml.ammo.types.AMMOTYPELIST;
import ja2.xml.items.ITEMLIST;
import ja2.xml.items.armor.ARMOURLIST;
import ja2.xml.items.drugs.DRUGSLIST;
import ja2.xml.items.explosives.EXPLOSIVELIST;
import ja2.xml.items.food.FOODSLIST;
import ja2.xml.items.lbe.LOADBEARINGEQUIPMENTLIST;
import ja2.xml.items.magazines.MAGAZINELIST;
import ja2.xml.items.weapons.WEAPONLIST;
import ja2.xml.lookup.weapontype.JA2Data;
import ja2.xml.tilesets.JA2SET;
import ja2.xml.tilesets.TilesetDef;
import ja2.xml.tilesets.TilesetList;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;

public class XmlLoader {

    private static class XmlAssetDef {

	String assetPath;
	Class assetClass;

	public XmlAssetDef(String assetPath, Class assetClass) {
	    this.assetPath = assetPath.replace('/', '\\').toUpperCase();
	    this.assetClass = assetClass;
	}
    }

    private static final Map<String, XmlAssetDef> xmlAssetDefs = ImmutableMap.<String, XmlAssetDef>builder()
	    // items
	    .put("Items", new XmlAssetDef("/TABLEDATA/ITEMS/ITEMS.XML", ITEMLIST.class))
	    // specific item type data
	    .put("Weapons", new XmlAssetDef("/TABLEDATA/ITEMS/WEAPONS.XML", WEAPONLIST.class))
	    .put("Magazines", new XmlAssetDef("/TABLEDATA/ITEMS/Magazines.XML", MAGAZINELIST.class))
	    .put("LBE", new XmlAssetDef("/TABLEDATA/ITEMS/LoadBearingEquipment.xml", LOADBEARINGEQUIPMENTLIST.class))
	    .put("Food", new XmlAssetDef("/TABLEDATA/ITEMS/Food.XML", FOODSLIST.class))
	    .put("Explosives", new XmlAssetDef("/TABLEDATA/ITEMS/Explosives.XML", EXPLOSIVELIST.class))
	    .put("Drugs", new XmlAssetDef("/TABLEDATA/ITEMS/Drugs.XML", DRUGSLIST.class))
	    .put("Armours", new XmlAssetDef("/TABLEDATA/ITEMS/Armours.XML", ARMOURLIST.class))
	    // AMMO
	    .put("AmmoTypes", new XmlAssetDef("/TABLEDATA/ITEMS/AmmoTypes.xml", AMMOTYPELIST.class))
	    .put("Calibers", new XmlAssetDef("/TABLEDATA/ITEMS/AmmoStrings.xml", AMMOLIST.class))
	    // tiles
	    .put("Tilesets", new XmlAssetDef("/Ja2Set.dat.xml", JA2SET.class))
	    // lookup
	    .put("AmmoChoices", new XmlAssetDef("/TABLEDATA/lookup/AmmoChoices.xml", ja2.xml.lookup.ammochoices.JA2Data.class))
	    .put("AmmoFlag", new XmlAssetDef("/TABLEDATA/lookup/AmmoFlag.xml", ja2.xml.lookup.ammoflag.JA2Data.class))
	    .put("ArmourClass", new XmlAssetDef("/TABLEDATA/lookup/ArmourClass.xml", ja2.xml.lookup.armourclass.JA2Data.class))
	    .put("AttachmentClass", new XmlAssetDef("/TABLEDATA/lookup/AttachmentClass.xml", ja2.xml.lookup.attachmentclass.JA2Data.class))
	    .put("AttachmentPoint", new XmlAssetDef("/TABLEDATA/lookup/AttachmentPoint.xml", ja2.xml.lookup.attachmentpoint.JA2Data.class))
	    .put("AttachmentSystem", new XmlAssetDef("/TABLEDATA/lookup/AttachmentSystem.xml", ja2.xml.lookup.attachmentsystem.JA2Data.class))
	    .put("Cursor", new XmlAssetDef("/TABLEDATA/lookup/Cursor.xml", ja2.xml.lookup.cursor.JA2Data.class))
	    .put("DrugType", new XmlAssetDef("/TABLEDATA/lookup/DrugType.xml", ja2.xml.lookup.drugtype.JA2Data.class))
	    .put("ExplosionSize", new XmlAssetDef("/TABLEDATA/lookup/ExplosionSize.xml", ja2.xml.lookup.explosionsize.JA2Data.class))
	    .put("ExplosionType", new XmlAssetDef("/TABLEDATA/lookup/ExplosionType.xml", ja2.xml.lookup.explosiontype.JA2Data.class))
	    .put("ItemClass", new XmlAssetDef("/TABLEDATA/lookup/ItemClass.xml", ja2.xml.lookup.itemclass.JA2Data.class))
	    .put("ItemFlag", new XmlAssetDef("/TABLEDATA/lookup/ItemFlag.xml", ja2.xml.lookup.itemflag.JA2Data.class))
	    .put("LBEClass", new XmlAssetDef("/TABLEDATA/lookup/LBEClass.xml", ja2.xml.lookup.lbeclass.JA2Data.class))
	    .put("MagazineType", new XmlAssetDef("/TABLEDATA/lookup/MagazineType.xml", ja2.xml.lookup.magazinetype.JA2Data.class))
	    .put("MergeType", new XmlAssetDef("/TABLEDATA/lookup/MergeType.xml", ja2.xml.lookup.mergetype.JA2Data.class))
	    .put("NasAttachmentClass", new XmlAssetDef("/TABLEDATA/lookup/NasAttachmentClass.xml", ja2.xml.lookup.nasattachmentclass.JA2Data.class))
	    .put("NasLayoutClass", new XmlAssetDef("/TABLEDATA/lookup/NasLayoutClass.xml", ja2.xml.lookup.naslayoutclass.JA2Data.class))
	    .put("PocketSize", new XmlAssetDef("/TABLEDATA/lookup/PocketSize.xml", ja2.xml.lookup.pocketsize.JA2Data.class))
	    .put("Separability", new XmlAssetDef("/TABLEDATA/lookup/Separability.xml", ja2.xml.lookup.separability.JA2Data.class))
	    .put("Silhouette", new XmlAssetDef("/TABLEDATA/lookup/Silhouette.xml", ja2.xml.lookup.silhouette.JA2Data.class))
	    .put("SkillCheckType", new XmlAssetDef("/TABLEDATA/lookup/SkillCheckType.xml", ja2.xml.lookup.skillchecktype.JA2Data.class))
	    .put("WeaponClass", new XmlAssetDef("/TABLEDATA/lookup/WeaponClass.xml", ja2.xml.lookup.weaponclass.JA2Data.class))
	    .put("WeaponType", new XmlAssetDef("/TABLEDATA/lookup/WeaponType.xml", ja2.xml.lookup.weapontype.JA2Data.class))
	    .build();

    Map<String, Object> xmlAssets = new HashMap<String, Object>();

    String basePath = "src/main/resources/xml/113/items/items.xml";
    VFSConfig config;

    private void loadXmlAssets(boolean useVfs) {
	for (Map.Entry<String, XmlAssetDef> assetDef : xmlAssetDefs.entrySet()) {
	    try {
		// System.out.println("thebob.assetloader.xml.XmlLoader.loadXmlAssets() loading asset: " + assetDef.getKey());

		XmlNamespaceWrapper wrapper;

		if (useVfs) {
		    wrapper = new XmlNamespaceWrapper(config.getFileStream(assetDef.getValue().assetPath), assetDef.getValue().assetClass);
		} else {
		    wrapper = new XmlNamespaceWrapper(basePath + assetDef.getValue(), assetDef.getValue().assetClass);
		}

		JAXBContext context = JAXBContext.newInstance(assetDef.getValue().assetClass);
		Unmarshaller um = context.createUnmarshaller();
		Object assetData = um.unmarshal(wrapper);

		xmlAssets.put(assetDef.getKey(), assetData);

	    } catch (XMLStreamException ex) {
		Logger.getLogger(XmlLoader.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (FileNotFoundException ex) {
		Logger.getLogger(XmlLoader.class.getName()).log(Level.SEVERE, null, ex);
	    } catch (JAXBException ex) {
		Logger.getLogger(XmlLoader.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    public XmlLoader(VFSConfig config) {
	this.config = config;
	loadXmlAssets(true);
    }

    // point it at a data folder
    public XmlLoader(String path) {
	this.basePath = path;
	loadXmlAssets(false);
    }

    public ITEMLIST getItems() {
	return (ITEMLIST) xmlAssets.get("Items");
    }

    public WEAPONLIST getWeapons() {
	return (WEAPONLIST) xmlAssets.get("Weapons");
    }

    public MAGAZINELIST getMagazines() {
	return (MAGAZINELIST) xmlAssets.get("Magazines");
    }

    public LOADBEARINGEQUIPMENTLIST getLBE() {
	return (LOADBEARINGEQUIPMENTLIST) xmlAssets.get("LBE");
    }

    public FOODSLIST getFood() {
	return (FOODSLIST) xmlAssets.get("Food");
    }

    public EXPLOSIVELIST getExplosives() {
	return (EXPLOSIVELIST) xmlAssets.get("Explosives");
    }

    public DRUGSLIST getDrugs() {
	return (DRUGSLIST) xmlAssets.get("Drugs");
    }

    public ARMOURLIST getArmours() {
	return (ARMOURLIST) xmlAssets.get("Armours");
    }

    public AMMOTYPELIST getAmmoTypes() {
	return (AMMOTYPELIST) xmlAssets.get("AmmoTypes");
    }

    public AMMOLIST getCalibers() {
	return (AMMOLIST) xmlAssets.get("Calibers");
    }

    public JA2SET getTilesets() {
	return (JA2SET) xmlAssets.get("Tilesets");
    }

    public ja2.xml.lookup.ammochoices.JA2Data getAmmoChoices() {
	return (ja2.xml.lookup.ammochoices.JA2Data) xmlAssets.get("AmmoChoices");
    }

    public ja2.xml.lookup.ammoflag.JA2Data getAmmoFlag() {
	return (ja2.xml.lookup.ammoflag.JA2Data) xmlAssets.get("AmmoFlag");
    }

    public ja2.xml.lookup.armourclass.JA2Data getArmourClass() {
	return (ja2.xml.lookup.armourclass.JA2Data) xmlAssets.get("ArmourClass");
    }

    public ja2.xml.lookup.attachmentclass.JA2Data getAttachmentClass() {
	return (ja2.xml.lookup.attachmentclass.JA2Data) xmlAssets.get("AttachmentClass");
    }

    public ja2.xml.lookup.attachmentpoint.JA2Data getAttachmentPoint() {
	return (ja2.xml.lookup.attachmentpoint.JA2Data) xmlAssets.get("AttachmentPoint");
    }

    public ja2.xml.lookup.attachmentsystem.JA2Data getAttachmentSystem() {
	return (ja2.xml.lookup.attachmentsystem.JA2Data) xmlAssets.get("AttachmentSystem");
    }

    public ja2.xml.lookup.cursor.JA2Data getCursor() {
	return (ja2.xml.lookup.cursor.JA2Data) xmlAssets.get("Cursor");
    }

    public ja2.xml.lookup.drugtype.JA2Data getDrugType() {
	return (ja2.xml.lookup.drugtype.JA2Data) xmlAssets.get("DrugType");
    }

    public ja2.xml.lookup.explosionsize.JA2Data getExplosionSize() {
	return (ja2.xml.lookup.explosionsize.JA2Data) xmlAssets.get("ExplosionSize");
    }

    public ja2.xml.lookup.explosiontype.JA2Data getExplosionType() {
	return (ja2.xml.lookup.explosiontype.JA2Data) xmlAssets.get("ExplosionType");
    }

    public ja2.xml.lookup.itemclass.JA2Data getItemClass() {
	return (ja2.xml.lookup.itemclass.JA2Data) xmlAssets.get("ItemClass");
    }

    public ja2.xml.lookup.itemflag.JA2Data getItemFlag() {
	return (ja2.xml.lookup.itemflag.JA2Data) xmlAssets.get("ItemFlag");
    }

    public ja2.xml.lookup.lbeclass.JA2Data getLBEClass() {
	return (ja2.xml.lookup.lbeclass.JA2Data) xmlAssets.get("LBEClass");
    }

    public ja2.xml.lookup.magazinetype.JA2Data getMagazineType() {
	return (ja2.xml.lookup.magazinetype.JA2Data) xmlAssets.get("MagazineType");
    }

    public ja2.xml.lookup.mergetype.JA2Data getMergeType() {
	return (ja2.xml.lookup.mergetype.JA2Data) xmlAssets.get("MergeType");
    }

    public ja2.xml.lookup.nasattachmentclass.JA2Data getNasAttachmentClass() {
	return (ja2.xml.lookup.nasattachmentclass.JA2Data) xmlAssets.get("NasAttachmentClass");
    }

    public ja2.xml.lookup.naslayoutclass.JA2Data getNasLayoutClass() {
	return (ja2.xml.lookup.naslayoutclass.JA2Data) xmlAssets.get("NasLayoutClass");
    }

    public ja2.xml.lookup.pocketsize.JA2Data getPocketSize() {
	return (ja2.xml.lookup.pocketsize.JA2Data) xmlAssets.get("PocketSize");
    }

    public ja2.xml.lookup.separability.JA2Data getSeparability() {
	return (ja2.xml.lookup.separability.JA2Data) xmlAssets.get("Separability");
    }

    public ja2.xml.lookup.silhouette.JA2Data getSilhouette() {
	return (ja2.xml.lookup.silhouette.JA2Data) xmlAssets.get("Silhouette");
    }

    public ja2.xml.lookup.skillchecktype.JA2Data getSkillCheckType() {
	return (ja2.xml.lookup.skillchecktype.JA2Data) xmlAssets.get("SkillCheckType");
    }

    public ja2.xml.lookup.weaponclass.JA2Data getWeaponClass() {
	return (ja2.xml.lookup.weaponclass.JA2Data) xmlAssets.get("WeaponClass");
    }

    public ja2.xml.lookup.weapontype.JA2Data getWeaponType() {
	return (ja2.xml.lookup.weapontype.JA2Data) xmlAssets.get("WeaponType");
    }

    public static void main(String[] args) throws JAXBException, FileNotFoundException, ParserConfigurationException, SAXException, XMLStreamException {

	VirtualFileSystem vfs = new VirtualFileSystem("../gameData");
	//VFSConfig config = vfs.getConfig(vfs.getConfigs().iterator().next());
	//for (String configName : vfs.getConfigs()) {
	
	String configName= "vfs_config.JA2113.ini";
	{
	    VFSConfig config = vfs.getConfig(configName);
	    XmlLoader loader = new XmlLoader(config);

	    /*
	    for (ITEMTYPE item : loader.items.getITEM()) {
		System.out.println(item.getSzItemName());
	    }

	    JA2SET tileSetTop = loader.getTilesets();
	    TilesetList tileSets = tileSetTop.getTilesets();
	    for (TilesetDef tileset : tileSets.getTileset()) {
		System.out.println("thebob.xmlloader.XmlLoader.loadTilesets() tileset " + tileset.getIndex() + ":" + tileset.getName());
	    }
	     */
	    
	    List<JA2Data.WeaponType> WeaponTypeList = loader.getWeaponType().getWeaponType();
	    System.out.println("thebob.assetloader.xml.XmlLoader.main() WeaponTypeList: " + WeaponTypeList.size());
	    for (JA2Data.WeaponType item : WeaponTypeList) {
		System.out.println("thebob.xmlloader.XmlLoader.loadTilesets() tileset " + item.getId() + " -> " + item.getName());
	    }

	    List<AMMO> AmmoTypeList = loader.getCalibers().getAMMO();
	    System.out.println("thebob.assetloader.xml.XmlLoader.main() AmmoTypeList: " + AmmoTypeList.size());
	    for (AMMO item : AmmoTypeList) {
		System.out.println("thebob.xmlloader.XmlLoader.loadTilesets() tileset " + item.getUiIndex() + " -> " + item.getAmmoCaliber());
	    }
	}

    }

}
