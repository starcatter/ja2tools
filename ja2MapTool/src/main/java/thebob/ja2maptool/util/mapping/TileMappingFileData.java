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
package thebob.ja2maptool.util.mapping;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;

/**
 *
 * @author the_bob
 */
public class TileMappingFileData {

    Map<Integer, TileCategoryMapping> mappings = new HashMap<Integer, TileCategoryMapping>();
    int sourceTilesetId;
    int targetTilesetId;
    int tileCategoryCount;
    String srcConfDir;
    String srcConf;
    String dstConfDir;
    String dstConf;

    public TileMappingFileData() {
    }

    public TileMappingFileData(Map<Integer, TileCategoryMapping> mappings, int srcTilesetId, int dstTilesetId, int tileCategoryCount, String srcConfDir, String srcConf, String dstConfDir, String dstConf) {
	this.mappings = mappings;
	this.sourceTilesetId = srcTilesetId;
	this.targetTilesetId = dstTilesetId;
	this.tileCategoryCount = tileCategoryCount;
	this.srcConfDir = srcConfDir;
	this.srcConf = srcConf;
	this.dstConfDir = dstConfDir;
	this.dstConf = dstConf;
    }

    public Map<Integer, TileCategoryMapping> getMappingList() {
	return mappings;
    }

    public int getSourceTilesetId() {
	return sourceTilesetId;
    }

    public int getTargetTilesetId() {
	return targetTilesetId;
    }

    public int getTileCategoryCount() {
	return tileCategoryCount;
    }

    public String getSrcConfDir() {
	return srcConfDir;
    }

    public String getSrcConf() {
	return srcConf;
    }

    public String getDstConfDir() {
	return dstConfDir;
    }

    public String getDstConf() {
	return dstConf;
    }

    public void setMappings(Map<Integer, TileCategoryMapping> mappings) {
	this.mappings = mappings;
    }

    public void setSourceTilesetId(int sourceTilesetId) {
	this.sourceTilesetId = sourceTilesetId;
    }

    public void setTargetTilesetId(int targetTilesetId) {
	this.targetTilesetId = targetTilesetId;
    }

    public void setTileCategoryCount(int tileCategoryCount) {
	this.tileCategoryCount = tileCategoryCount;
    }

    public void setSrcConfDir(String srcConfDir) {
	this.srcConfDir = srcConfDir;
    }

    public void setSrcConf(String srcConf) {
	this.srcConf = srcConf;
    }

    public void setDstConfDir(String dstConfDir) {
	this.dstConfDir = dstConfDir;
    }

    public void setDstConf(String dstConf) {
	this.dstConf = dstConf;
    }
    
}
