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

import thebob.assetmanager.managers.items.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author the_bob
 */
public class ItemCategory {

	public static boolean dumpTreeInToString = false;

	public static class SubCategoryItemIterator implements Iterator<Item> {

		Iterator<ItemCategory> category;
		Iterator<Item> item;

		SubCategoryItemIterator(Iterator<ItemCategory> categoryIter) {
			category = categoryIter;
			item = category.next().subCategoryItemIterator();
		}

		SubCategoryItemIterator(Iterator<ItemCategory> categoryIter, Iterator<Item> itemIter) {
			category = categoryIter;
			item = itemIter;
		}

		@Override
		public boolean hasNext() {
			if (item.hasNext()) {
				return true;
			}

			while (category.hasNext()) {
				item = category.next().subCategoryItemIterator();

				if (item.hasNext()) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Item next() {
			if (item.hasNext()) {
				return item.next();
			} else if ( hasNext() ) { // find the next element
				return next();
			} else {
				return null;
			}
		}
	}

	private final String ucid;

	String name;
	ItemCategory parent;
	List<ItemCategory> children = new ArrayList<ItemCategory>();
	List<Item> items = new ArrayList<Item>();
	private int depth = 0;

	public String getName() {
		return name;
	}

	public String getNameWithPath() {
		return parent != null ?  parent.getNameWithPath() + "->" + name : name;
	}

	public ItemCategory(String name, ItemCategory parent) {
		this.name = name;
		this.parent = parent;
		if (parent != null) {
			depth = parent.depth + 1;
			parent.addSubCategory(this);
			ucid = parent.ucid + "->" + getName();
		} else {
			ucid = getName();
		}
	}

	public int subCategoryCount() {
		return children.size();
	}

	public boolean addSubCategory(ItemCategory e) {
		return children.add(e);
	}

	public ItemCategory getSubCategory(int index) {
		return children.get(index);
	}

	public Iterator<ItemCategory> categoryIterator() {
		return children.iterator();
	}

	public int itemCount() {
		return items.size();
	}

	public int totalItemCount() {
		if (subCategoryCount() == 0) {
			return items.size();
		} else {
			return items.size() + children.stream().collect(Collectors.summingInt(subCat -> subCat.totalItemCount())).intValue();
		}
	}

	public boolean addItem(Item e) {
		e.setParentCategory(this);
		return items.add(e);
	}

	public Item getItem(int index) {
		return items.get(index);
	}

	public Iterator<Item> itemIterator() {
		return items.iterator();
	}

	public Iterator<Item> subCategoryItemIterator() {
		if (children.isEmpty()) {
			return itemIterator();
		} else if (items.isEmpty()) {
			return new SubCategoryItemIterator(categoryIterator());
		} else {
			return new SubCategoryItemIterator(categoryIterator(),itemIterator());
		}
	}

	public String toString() {
		if(dumpTreeInToString){
            StringBuilder sb = new StringBuilder();
            if (subCategoryCount() + totalItemCount() > 0) {
                sb.append('\n');
                for (int i = 0; i < depth; i++) {
                    sb.append('\t');
                }
                sb.append('[');
                sb.append(name);
                sb.append(']');
                if (totalItemCount() > 0) {
                    sb.append(",  ");
                    sb.append(totalItemCount());
                    sb.append(" items: ");

                    for (Iterator<Item> iterator = items.iterator(); iterator.hasNext(); ) {
                        sb.append('\n');
                        for (int i = 0; i < depth + 1; i++) {
                            sb.append('\t');
                        }
                        sb.append(iterator.next().getName());
                    }
                }
                if (subCategoryCount() > 0) {
                    sb.append(",  ");
                    sb.append(subCategoryCount());
                    sb.append(" sub categories: ");
                    for (Iterator<ItemCategory> iterator = children.iterator(); iterator.hasNext(); ) {
                        ItemCategory next = iterator.next();
                        sb.append(next);
                    }
                }
            }
            return sb.toString();
        } else {
            return name;
        }
	}

	/**
	 * Gets Universal Category Id
	 *
	 * @return
	 */
	public String getUcid() {
		return ucid;
	}
}
