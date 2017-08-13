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
package thebob.ja2maptool.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static thebob.ja2maptool.model.TileCategoryMapping.RemapStatus.None;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;

/**
 *
 * @author the_bob
 */
public class TileCategoryMapping {

    public enum RemapStatus {
	None, // no remapping done
	Same, // automap found same file mapped to category
	Matched, // automap matched a file for this category in the target tileset
	Found, // automap found some similar tiles for this category
	Manual	    // manual remapping needed
    }

    int categoryId;
    String categoryName;
    RemapStatus status;
    ObservableList<TileMapping> mappings = FXCollections.observableArrayList();

    public TileCategoryMapping(int categoryId, ObservableList<TileMapping> mappings) {
	this.categoryId = categoryId;
	this.categoryName = TilesetMappingScope.getTileCategortyName(categoryId);
	this.status = None;
	this.mappings = mappings;
    }

    public TileCategoryMapping(int categoryId, String categoryName, RemapStatus status, ObservableList<TileMapping> mappings) {
	this.categoryId = categoryId;
	this.categoryName = categoryName;
	this.status = status;
	this.mappings = mappings;
    }

    
    
    public String getCategoryName() {
	return categoryName;
    }

    public void setCategoryName(String categoryName) {
	this.categoryName = categoryName;
    }

    public RemapStatus getStatus() {
	return status;
    }

    public void setStatus(RemapStatus status) {
	this.status = status;
    }

    public ObservableList<TileMapping> getMappings() {
	return mappings;
    }    

    public int getCategoryId() {
	return categoryId;
    }
    
    
}
