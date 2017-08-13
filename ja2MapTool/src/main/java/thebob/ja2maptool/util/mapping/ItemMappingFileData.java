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

import java.util.Map;
import javafx.collections.ObservableList;

/**
 *
 * @author the_bob
 */
public class ItemMappingFileData {

    Map<Integer, Integer> mapping;
    String srcConfDir;
    String srcConf;
    String dstConfDir;
    String dstConf;

    public ItemMappingFileData() {
    }

    public ItemMappingFileData(Map<Integer, Integer> mapping, String srcConfDir, String srcConf, String dstConfDir, String dstConf) {
	this.mapping = mapping;
	this.srcConfDir = srcConfDir;
	this.srcConf = srcConf;
	this.dstConfDir = dstConfDir;
	this.dstConf = dstConf;
    }

    public Map<Integer, Integer> getMapping() {
	return mapping;
    }

    public void setMapping(Map<Integer, Integer> mapping) {
	this.mapping = mapping;
    }

    public String getSrcConfDir() {
	return srcConfDir;
    }

    public void setSrcConfDir(String srcConfDir) {
	this.srcConfDir = srcConfDir;
    }

    public String getSrcConf() {
	return srcConf;
    }

    public void setSrcConf(String srcConf) {
	this.srcConf = srcConf;
    }

    public String getDstConfDir() {
	return dstConfDir;
    }

    public void setDstConfDir(String dstConfDir) {
	this.dstConfDir = dstConfDir;
    }

    public String getDstConf() {
	return dstConf;
    }

    public void setDstConf(String dstConf) {
	this.dstConf = dstConf;
    }

}
