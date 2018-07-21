/* 
 * The MIT License
 *
 * Copyright 2017 starcatter.
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
package thebob.ja2maptool.util.mapping.item;

import thebob.assetmanager.managers.items.Item;

/**
 *
 * @author the_bob
 */
public class ItemMapping implements Mapping{

    Item source;
    Item target;

    public ItemMapping(Item srcItem, Item target) {
	this.source = srcItem;
	this.target = target;
    }

    public Item getSource() {
	return source;
    }

    public Item getTarget() {
	return target;
    }

    public String toString(){
	return source.getName() + " -> " + target.getName();
    }

    @Override
    public Integer getSourceId() {
        return source.getId();
    }

    @Override
    public Integer getTargetId() {
        return target.getId();
    }

    @Override
    public String getSourceName() {
        return source.getName();
    }

    @Override
    public String getTargetName() {
        return target.getName();
    }

    @Override
    public void setSource(Item source) {
        this.source = source;
    }

    @Override
    public void setTarget(Item target) {
        this.target = target;
    }

    // ---

    @Override
    public void setSourceId(Integer sourceId) {
        throw new RuntimeException("Cannot set id for ItemMapping!");
    }

    @Override
    public void setTargetId(Integer sourceId) {
        throw new RuntimeException("Cannot set id for ItemMapping!");
    }

    @Override
    public void setSourceName(String sourceName) {
        throw new RuntimeException("Cannot set name for ItemMapping!");
    }

    @Override
    public void setTargetName(String targetName) {
        throw new RuntimeException("Cannot set name for ItemMapping!");
    }
}
