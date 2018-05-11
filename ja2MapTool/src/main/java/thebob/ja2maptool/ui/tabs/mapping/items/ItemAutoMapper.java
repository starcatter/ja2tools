/*
 * The MIT License
 *
 * Copyright 2018 starcatter.
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
package thebob.ja2maptool.ui.tabs.mapping.items;

import ja2.xml.ammo.types.AMMOTYPELIST;
import ja2.xml.items.ITEMTYPE;
import ja2.xml.items.magazines.MAGAZINELIST;
import ja2.xml.items.weapons.WEAPONLIST;
import static java.lang.Double.max;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import thebob.assetmanager.managers.items.categories.ItemClassEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import thebob.assetmanager.managers.ItemManager;
import thebob.assetmanager.managers.items.Item;
import thebob.assetmanager.managers.items.categories.ItemCategory;
import thebob.assetmanager.managers.items.categories.ItemClassEnum;

/**
 *
 * @author starcatter
 */
public class ItemAutoMapper {

	protected final ItemManager source;
	protected final ItemManager target;
	protected Map<Item, Item> mapping = null;

	public ItemAutoMapper(ItemManager source, ItemManager target) {
		this.source = source;
		this.target = target;
	}

	Map<Item, Item> getMapping() {
		if (mapping == null) {
			createMapping();
		}

		return mapping;
	}

	private void createMapping() {
		mapping = new HashMap<>();

		ItemCategory rootNode = source.getCategories().getRootNode();
		mapCategory(rootNode);
	}

	private void mapCategory(ItemCategory node) {
		// System.out.println("mapCategory(): " + node.getName());
		node.categoryIterator().forEachRemaining(c -> mapCategory(c));
		node.itemIterator().forEachRemaining(i -> mapItem(i));
	}

	double getRatio(Number a, Number b, double p) {
		if (a == b) {
			return 1d;
		}

		if (a == null || b == null) {
			return 0d;
		}

		if (  Double.isNaN(a.doubleValue()) || Double.isNaN(b.doubleValue()) || a.doubleValue() == 0d) {
			return 0d;
		}

		double ratio = Math.pow(1d - abs(a.doubleValue() - b.doubleValue()) / a.doubleValue(), p);

		return max(0d, min(1d, ratio));
	}

	private void mapItem(Item sourceItem) {
		//System.out.println("mapItem() " + sourceItem.getName() + ":");

		Map<Item, Double> scoreMap = getScoreMap(sourceItem);

		List<Map.Entry<Item, Double>> collect = scoreMap.entrySet().stream()
			.sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
			.collect(Collectors.toList());

		//System.out.println("mapItem() :: " + collect.isEmpty());

		if (!collect.isEmpty()) {
			Map.Entry<Item, Double> bestItem = collect.get(0);
			System.out.println("mapItem() " + sourceItem.getName() + " -> " + bestItem.getKey().getName() + " /w score " + bestItem.getValue());
			//System.out.println("\t candidates: " + collect.toString().replace("Item{", "\n\t\t -> Item{"));
			mapping.put(sourceItem, collect.get(0).getKey());
		} else {
			System.err.println("mapItem() " + sourceItem.getName() + " has no candiates!");
			mapping.put(sourceItem, target.getItem(0));
		}
	}

	private Map<Item, Double> getScoreMap(Item sourceItem) {
		ItemCategory targetCat = target.getCategories().getRootNode();

		Map<Item, Double> scoreMap = new HashMap<>();

		targetCat.subCategoryItemIterator().forEachRemaining((Item targetItem) -> {
			if (targetItem == null) {
				//System.out.println("\t = " + sourceItem.getName() + "-> NULL");
				return;
			}

			long targetItemType = sourceItem.getItemType();
			long itemType = targetItem.getItemType();

			// System.out.println("\tscore for " + targetItem.getName());
			// ---
			// handle name similarity, mostly for the case where names are exactly the same
			// if names are almost equal, assume it's a match
			// ---
			double itemLongNameScore = StringUtils.getJaroWinklerDistance(targetItem.getItemDef().getSzLongItemName(), sourceItem.getItemDef().getSzLongItemName());
			double BRNameScore = StringUtils.getJaroWinklerDistance(targetItem.getItemDef().getSzBRName(), sourceItem.getItemDef().getSzBRName());
			double shortNameScore = StringUtils.getJaroWinklerDistance(targetItem.getItemDef().getSzItemName(), sourceItem.getItemDef().getSzItemName());

			double itemNameScore = (shortNameScore + itemLongNameScore + BRNameScore) / 3d;

			if (itemNameScore >= 0.95d) {
				scoreMap.put(targetItem, itemNameScore * 1000d);
				//System.out.println("\t\t itemNameScore = " + itemNameScore);
				return;
			}

			// handle items being different type somehow
			if (targetItemType != itemType) {
				// if target is different type, use further reduced name score
				scoreMap.put(targetItem, itemNameScore / 100d);
				return;
			}

			// ---
			// handle general item similarity
			// ---
			ITEMTYPE targetItemDef = targetItem.getItemDef();
			ITEMTYPE sourceItemDef = sourceItem.getItemDef();

			double[] ratios = new double[]{
				itemNameScore,
				getRatio(targetItem.getCoolness(), sourceItem.getCoolness(), 2d),
				getRatio(targetItem.getItemDef().getUsPrice(), sourceItemDef.getUsPrice(), 1d),
				getRatio(targetItem.getItemDef().getUbWeight(), sourceItemDef.getUbWeight(), 1d),
				targetItemDef.getNotInEditor() == sourceItemDef.getNotInEditor() ? 0d : -10d,
				targetItemDef.getNotBuyable() == sourceItemDef.getNotBuyable() ? 0d : -10d,
				targetItemDef.getAntitankmine() == sourceItemDef.getAntitankmine() ? 0d : -5d,
				targetItemDef.getMine() == sourceItemDef.getMine() ? 0d : -5d,
				targetItemDef.getMedical() == sourceItemDef.getMedical() ? 0d : -5d,
				targetItemDef.getMedicalKit() == sourceItemDef.getMedicalKit() ? 0d : -5d,
				targetItemDef.getAttachment() == sourceItemDef.getAttachment() ? 0d : -5d,
				targetItemDef.getFoodType() == sourceItemDef.getFoodType() ? 0d : -5d,
				targetItemDef.getFlare() == sourceItemDef.getFlare() ? 0d : -5d,};

			double baseScore = Arrays.stream(ratios).sum();

			// System.out.println("\t\t baseScore = "+baseScore);
			ItemClassEnum itemClass = ItemClassEnum.getItemClassMap().get(itemType);

			if (itemClass != null) {

				switch (itemClass) {
					case Gun: {
						WEAPONLIST.WEAPON sourceWeapon = sourceItem.getWeaponData();
						WEAPONLIST.WEAPON targetWeapon = targetItem.getWeaponData();

						// System.out.println("\t\t (as gun) ");
						double[] gunRatios = new double[]{
							pow(StringUtils.getJaroWinklerDistance(sourceWeapon.getSzWeaponName(), targetWeapon.getSzWeaponName()), 4d),
							targetWeapon.getUbWeaponClass() == sourceWeapon.getUbWeaponClass() ? 2d : 0.25d,
							targetWeapon.getUbWeaponType() == sourceWeapon.getUbWeaponType() ? 2d : 0.25d,
							targetWeapon.getNoSemiAuto() == sourceWeapon.getNoSemiAuto() ? 5d : 0d,
							getRatio(targetWeapon.getUbShotsPerBurst(), sourceWeapon.getUbShotsPerBurst(), 3d),
							getRatio(targetWeapon.getBBurstAP(), sourceWeapon.getBBurstAP(), 3d),
							targetWeapon.getUbCalibre() == sourceWeapon.getUbCalibre() ? 5d : 0d,
							getRatio(targetWeapon.getUbDeadliness(), sourceWeapon.getUbDeadliness(), 3d),
							getRatio(targetWeapon.getUsRange(), sourceWeapon.getUsRange(), 2d),
							getRatio(targetWeapon.getUbMagSize(), sourceWeapon.getUbMagSize(), 2d),};

						double score = Arrays.stream(gunRatios).sum();

						scoreMap.put(targetItem, score + baseScore);
					}
					break;

					// ---
					case Tentacle:
					case Blunt_Weapon:
					case Knife: {
						WEAPONLIST.WEAPON sourceWeapon = sourceItem.getWeaponData();
						WEAPONLIST.WEAPON targetWeapon = targetItem.getWeaponData();

						if (sourceWeapon != null && targetWeapon != null) {

							double[] gunRatios = new double[]{
								pow(StringUtils.getJaroWinklerDistance(sourceWeapon.getSzWeaponName(), targetWeapon.getSzWeaponName()), 4d),
								targetWeapon.getUbWeaponClass() == sourceWeapon.getUbWeaponClass() ? 1d : 0.25d,
								getRatio(targetWeapon.getUbImpact(), sourceWeapon.getUbImpact(), 2d),
								getRatio(targetWeapon.getUbDeadliness(), sourceWeapon.getUbDeadliness(), 3d),};

							double score = Arrays.stream(gunRatios).sum();

							scoreMap.put(targetItem, score + baseScore);
						} else {
							scoreMap.put(targetItem, baseScore);
						}
					}
					break;

					// ---
					case Thrown_Weapon:
					case Throwing_Knife: {
						WEAPONLIST.WEAPON sourceWeapon = sourceItem.getWeaponData();
						WEAPONLIST.WEAPON targetWeapon = targetItem.getWeaponData();

						try {
							double nameScore = StringUtils.getJaroWinklerDistance(sourceWeapon.getSzWeaponName(), targetWeapon.getSzWeaponName());
							double scoreDeadliness = targetWeapon.getUbDeadliness() == sourceWeapon.getUbDeadliness() ? 1d : 0.5d;
							double scoreRange = targetWeapon.getUsRange() == sourceWeapon.getUsRange() ? 1d : 0.5d;

							double score = (nameScore + scoreDeadliness + scoreRange) / 3d;
							scoreMap.put(targetItem, (score + baseScore) / 2d);
						} catch (Exception e) {
							// System.out.println("\t\t" + e.getLocalizedMessage());
							scoreMap.put(targetItem, baseScore);
						}
					}
					break;

					case Launcher:
						scoreMap.put(targetItem, baseScore);
						break;

					// ---
					case Grenade:
						scoreMap.put(targetItem, baseScore);
						break;

					case Bomb:
						scoreMap.put(targetItem, baseScore);
						break;

					// ---
					case Ammo:
						AMMOTYPELIST.AMMOTYPE sourceAmmo = sourceItem.getAmmoTypeData();
						AMMOTYPELIST.AMMOTYPE targetAmmo = targetItem.getAmmoTypeData();

						MAGAZINELIST.MAGAZINE sourceMag = sourceItem.getMagazineData();
						MAGAZINELIST.MAGAZINE targetMag = targetItem.getMagazineData();

						double magSizeRatio = getRatio(sourceMag.getUbMagSize(), targetMag.getUbMagSize(), 1d);

						double[] magRatios = new double[]{
							sourceMag.getUbCalibre() == targetMag.getUbCalibre() ? 5d : 0d,
							sourceMag.getUbMagType() == targetMag.getUbMagType() ? 0d : -2.5d,};

						double magScore = Arrays.stream(magRatios).sum();

						double[] ammoRatios = new double[]{
							getRatio(sourceAmmo.getNumberOfBullets(), targetAmmo.getNumberOfBullets(), 4d),
							getRatio(sourceAmmo.getLockBustingPower(), targetAmmo.getLockBustingPower(), 5d),
							getRatio(sourceAmmo.getDDamageModifierBreath(), targetAmmo.getDDamageModifierBreath(), 3d),
							getRatio(sourceAmmo.getDDamageModifierLife(), targetAmmo.getDDamageModifierLife(), 3d),
							getRatio(sourceAmmo.getBeforeArmourDamageMultiplier(), targetAmmo.getBeforeArmourDamageMultiplier(), 3d),
							getRatio(sourceAmmo.getBeforeArmourDamageDivisor(), targetAmmo.getBeforeArmourDamageDivisor(), 3d),
							getRatio(sourceAmmo.getAfterArmourDamageMultiplier(), targetAmmo.getAfterArmourDamageMultiplier(), 3d),
							getRatio(sourceAmmo.getAfterArmourDamageDivisor(), targetAmmo.getAfterArmourDamageDivisor(), 3d),
							getRatio(sourceAmmo.getArmourImpactReductionMultiplier(), targetAmmo.getArmourImpactReductionMultiplier(), 3d),
							getRatio(sourceAmmo.getArmourImpactReductionDivisor(), targetAmmo.getArmourImpactReductionDivisor(), 3d),
							sourceAmmo.getAntiTank() == targetAmmo.getAntiTank() ? 0d : -5d,
							sourceAmmo.getCanGoThrough() == targetAmmo.getCanGoThrough() ? 0d : -5d,
							sourceAmmo.getMonsterSpit() == targetAmmo.getMonsterSpit() ? 0d : -5d
						};

						double ammoScore = Arrays.stream(ammoRatios).sum();

						scoreMap.put(targetItem, baseScore * (magSizeRatio * 5d) * (magScore) * ammoScore);
						break;

					// ---
					case Armour:
						scoreMap.put(targetItem, baseScore);
						break;

					// ---	
					case Medkit:
						scoreMap.put(targetItem, baseScore);
						break;
						
					case Kit:
						scoreMap.put(targetItem, baseScore);
						break;
						
					case Face_Item:
						scoreMap.put(targetItem, baseScore);
						break;
						
					case Key:
						scoreMap.put(targetItem, baseScore);
						break;
						
					case Load_Bearing_Equipment:
						scoreMap.put(targetItem, baseScore);
						break;

					case Misc:
						scoreMap.put(targetItem, baseScore);
						break;
						
					case Money:
						scoreMap.put(targetItem, baseScore);
						break;
						
					case Random_Item:
						scoreMap.put(targetItem, baseScore);
						break;
						
					default:
						throw new AssertionError(itemClass.name());
				}
			}
		});

		return scoreMap;
	}
	private static final Logger LOG = Logger.getLogger(ItemAutoMapper.class.getName());

}
