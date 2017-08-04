/*
 * Copyright (C) 2017 the_bob.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
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
