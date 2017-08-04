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
package thebob.assetloader.map.core.components;

import java.nio.ByteBuffer;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.core.MapData;

public class MapLayers extends MapComponent {

    // layer stats
    byte[][] layerCounts;

    // ?
    byte[] worldLevelDataFlags;

    // layer arrays
    public IndexedElement[][] landLayer = null;
    public IndexedElement[][] objectLayer = null;
    public IndexedElement[][] structLayer = null;
    public IndexedElement[][] shadowLayer = null;
    public IndexedElement[][] roofLayer = null;
    public IndexedElement[][] onRoofLayer = null;

    // layer counts (totals)
    int totalLandLayers = 0;
    int totalObjectLayers = 0;
    int totalStructLayers = 0;
    int totalShadowLayers = 0;
    int totalRoofLayers = 0;
    int totalOnRoofLayers = 0;

    public MapLayers(MapData source) {
	super(source);
    }

    public boolean loadLayerCounts() {
	try {
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayerCounts() start @" + byteBuffer.position());
	    }
	    // Read layer counts
	    layerCounts = new byte[settings().iWorldSize][8];
	    worldLevelDataFlags = new byte[settings().iWorldSize];
	    byte ubCombine;
	    byte ubCombineLow;
	    byte ubCombineHigh;
	    if (MapLoader.logEverything) {
		System.out.println("loadData(): load " + settings().iWorldSize + " * 4 * Byte =\t" + Byte.BYTES * 4 * settings().iWorldSize);
	    }
	    for (int i = 0; i < settings().iWorldSize; i++) {
		// Read combination of land/world flags
		ubCombine = byteBuffer.get();
		// Split
		ubCombineLow = (byte) (ubCombine & 0x0F);
		ubCombineHigh = (byte) ((ubCombine & 0xF0) >> 4);
		layerCounts[i][0] = ubCombineLow;
		worldLevelDataFlags[i] = ubCombineHigh;
		// Read #objects, structs
		ubCombine = byteBuffer.get();
		ubCombineLow = (byte) (ubCombine & 0x0F);
		ubCombineHigh = (byte) ((ubCombine & 0xF0) >> 4);
		layerCounts[i][1] = ubCombineLow;
		layerCounts[i][2] = ubCombineHigh;
		// Read shadows, roof
		ubCombine = byteBuffer.get();
		ubCombineLow = (byte) (ubCombine & 0x0F);
		ubCombineHigh = (byte) ((ubCombine & 0xF0) >> 4);
		layerCounts[i][3] = ubCombineLow;
		layerCounts[i][4] = ubCombineHigh;
		// Read OnRoof, nothing
		ubCombine = byteBuffer.get();
		ubCombineLow = (byte) (ubCombine & 0x0F);
		layerCounts[i][5] = ubCombineLow;
	    }
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayerCounts() end @" + byteBuffer.position());
	    }
	    for (int i = 0; i < settings().iWorldSize; i++) {
		totalLandLayers += layerCounts[i][0] & 0xFF;
		totalObjectLayers += layerCounts[i][1] & 0xFF;
		totalStructLayers += layerCounts[i][2] & 0xFF;
		totalShadowLayers += layerCounts[i][3] & 0xFF;
		totalRoofLayers += layerCounts[i][4] & 0xFF;
		totalOnRoofLayers += layerCounts[i][5] & 0xFF;
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("loadLayerCounts(): " + e.toString());
	}
	return false;
    }

    public boolean loadLayers() {
	try {
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() start @" + byteBuffer.position());
	    }
	    // allocate arrays
	    landLayer = new IndexedElement[settings().iWorldSize][];
	    objectLayer = new IndexedElement[settings().iWorldSize][];
	    structLayer = new IndexedElement[settings().iWorldSize][];
	    shadowLayer = new IndexedElement[settings().iWorldSize][];
	    roofLayer = new IndexedElement[settings().iWorldSize][];
	    onRoofLayer = new IndexedElement[settings().iWorldSize][];

	    // load
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() loadLandLayer @" + byteBuffer.position());
	    }
	    loadLandLayer();
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() loadObjects @" + byteBuffer.position());
	    }
	    loadObjects();
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() loadStructs @" + byteBuffer.position());
	    }
	    loadStructs();
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() loadShadows @" + byteBuffer.position());
	    }
	    loadShadows();
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() loadRoofs @" + byteBuffer.position());
	    }
	    loadRoofs();
	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() loadOnRoof @" + byteBuffer.position());
	    }
	    loadOnRoof();

	    if (MapLoader.logFileIO) {
		System.out.println("loader.core.MapLayers.loadLayers() end @" + byteBuffer.position());
	    }

	    updateLayerCounts();

	    return true;
	} catch (Exception e) {
	    System.out.println("loadLayers(): " + e.toString());
	}
	return false;

    }

    protected boolean loadLandLayer() {
	try {
	    // "Loading land layers..."
	    byte ubType;
	    byte ubSubIndex;

	    for (int i = 0; i < settings().iWorldSize; i++) {
		int layers = layerCounts[i][0] & 0xFF;
		landLayer[i] = new IndexedElement[layers];
		for (int j = 0; j < layers; j++) {
		    ubType = byteBuffer.get();
		    ubSubIndex = byteBuffer.get();

		    landLayer[i][j] = new IndexedElement(ubType, ubSubIndex);
		}
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("loadLandLayer(): " + e.toString());
	}
	return false;
    }

    protected boolean loadObjects() {
	byte ubType;
	short ubTypeSubIndex;
	try {
	    // New load require UINT16 for the type subindex due to the fact that ROADPIECES contain over 300 type subindices.
	    for (int i = 0; i < settings().iWorldSize; i++) {
		int layers = layerCounts[i][1] & 0xFF;
		objectLayer[i] = new IndexedElement[layers];
		for (int j = 0; j < layers; j++) {
		    ubType = byteBuffer.get();
		    ubTypeSubIndex = byteBuffer.getShort();

		    objectLayer[i][j] = new IndexedElement(ubType, ubTypeSubIndex);
		}
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("loadObjects(): " + e.toString());
	}
	return false;
    }

    protected boolean loadStructs() {
	byte ubType;
	byte ubSubIndex;
	try {
	    for (int i = 0; i < settings().iWorldSize; i++) {
		int layers = layerCounts[i][2] & 0xFF;
		structLayer[i] = new IndexedElement[layers];
		for (int j = 0; j < layers; j++) {
		    ubType = byteBuffer.get();
		    ubSubIndex = byteBuffer.get();

		    structLayer[i][j] = new IndexedElement(ubType, ubSubIndex);
		}
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("loadStructs(): " + e.toString());
	}
	return false;
    }

    protected boolean loadShadows() {
	byte ubType;
	byte ubSubIndex;
	try {
	    for (int i = 0; i < settings().iWorldSize; i++) {
		int layers = layerCounts[i][3] & 0xFF;
		shadowLayer[i] = new IndexedElement[layers];
		for (int j = 0; j < layers; j++) {
		    ubType = byteBuffer.get();
		    ubSubIndex = byteBuffer.get();

		    shadowLayer[i][j] = new IndexedElement(ubType, ubSubIndex);
		}
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("loadShadows(): " + e.toString());
	}
	return false;
    }

    protected boolean loadRoofs() {
	byte ubType;
	byte ubSubIndex;
	try {
	    for (int i = 0; i < settings().iWorldSize; i++) {
		int layers = layerCounts[i][4] & 0xFF;
		roofLayer[i] = new IndexedElement[layers];
		for (int j = 0; j < layers; j++) {
		    ubType = byteBuffer.get();
		    ubSubIndex = byteBuffer.get();

		    roofLayer[i][j] = new IndexedElement(ubType, ubSubIndex);
		}
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("loadRoofs(): " + e.toString());
	}
	return false;
    }

    protected boolean loadOnRoof() {
	byte ubType;
	byte ubSubIndex;
	try {
	    for (int i = 0; i < settings().iWorldSize; i++) {
		int layers = layerCounts[i][5] & 0xFF;
		onRoofLayer[i] = new IndexedElement[layers];
		for (int j = 0; j < layers; j++) {
		    ubType = byteBuffer.get();
		    ubSubIndex = byteBuffer.get();

		    onRoofLayer[i][j] = new IndexedElement(ubType, ubSubIndex);
		}
	    }
	    return true;
	} catch (Exception e) {
	    System.out.println("loadOnRoof(): " + e.toString());
	}
	return false;
    }

    @Override
    public String toString() {
	return "\tLayer counts:" + "\n\t\tlandLayers:\t" + totalLandLayers + "\n\t\tobjectLayers:\t" + totalObjectLayers + "\n\t\tstructLayers:\t" + totalStructLayers + "\n\t\tshadowLayers:\t" + totalShadowLayers + "\n\t\troofLayers:\t" + totalRoofLayers + "\n\t\tonRoofLayers:\t" + totalOnRoofLayers + "\n\ttotal layer size:\t" + (totalLandLayers + totalObjectLayers + totalStructLayers + totalShadowLayers + totalRoofLayers + totalOnRoofLayers) * 2 + "B";
    }

    public void updateLayerCounts() {

	System.out.println("Updating layer counts, initial state:");
	System.out.println("\ttotalLandLayers: " + totalLandLayers);
	System.out.println("\ttotalObjectLayers: " + totalObjectLayers);
	System.out.println("\ttotalStructLayers: " + totalStructLayers);
	System.out.println("\ttotalShadowLayers: " + totalShadowLayers);
	System.out.println("\ttotalRoofLayers: " + totalRoofLayers);
	System.out.println("\ttotalOnRoofLayers: " + totalOnRoofLayers);

	totalLandLayers = 0;
	totalObjectLayers = 0;
	totalStructLayers = 0;
	totalShadowLayers = 0;
	totalRoofLayers = 0;
	totalOnRoofLayers = 0;

	for (int i = 0; i < settings().iWorldSize; i++) {
	    layerCounts[i][0] = (byte) landLayer[i].length;
	    totalLandLayers += landLayer[i].length;

	    layerCounts[i][1] = (byte) objectLayer[i].length;
	    totalObjectLayers += objectLayer[i].length;

	    layerCounts[i][2] = (byte) structLayer[i].length;
	    totalStructLayers += structLayer[i].length;

	    layerCounts[i][3] = (byte) shadowLayer[i].length;
	    totalShadowLayers += shadowLayer[i].length;

	    layerCounts[i][4] = (byte) roofLayer[i].length;
	    totalRoofLayers += roofLayer[i].length;

	    layerCounts[i][5] = (byte) onRoofLayer[i].length;
	    totalOnRoofLayers += onRoofLayer[i].length;
	}

	System.out.println("new state:");
	System.out.println("\ttotalLandLayers: " + totalLandLayers);
	System.out.println("\ttotalObjectLayers: " + totalObjectLayers);
	System.out.println("\ttotalStructLayers: " + totalStructLayers);
	System.out.println("\ttotalShadowLayers: " + totalShadowLayers);
	System.out.println("\ttotalRoofLayers: " + totalRoofLayers);
	System.out.println("\ttotalOnRoofLayers: " + totalOnRoofLayers);

    }

    public void saveLayerCounts(ByteBuffer outputBuffer) {
	byte ubCombine;
	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayerCounts() start @" + outputBuffer.position());
	}
	updateLayerCounts();

	for (int i = 0; i < settings().iWorldSize; i++) {
	    // Read combination of land/world flags
	    ubCombine = (byte) (layerCounts[i][0] | (worldLevelDataFlags[i] << 4));
	    outputBuffer.put(ubCombine);
	    // Read #objects, structs

	    ubCombine = (byte) (layerCounts[i][1] | (layerCounts[i][2] << 4));
	    outputBuffer.put(ubCombine);

	    // Read shadows, roof
	    ubCombine = (byte) (layerCounts[i][3] | (layerCounts[i][4] << 4));
	    outputBuffer.put(ubCombine);

	    // Read OnRoof, nothing
	    ubCombine = (byte) layerCounts[i][5];
	    outputBuffer.put(ubCombine);
	}
	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayerCounts() end @" + outputBuffer.position());
	}
    }

    public void saveLayers(ByteBuffer outputBuffer) {
	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayers() start @" + outputBuffer.position());
	}
	for (int i = 0; i < settings().iWorldSize; i++) {
	    int layers = layerCounts[i][0] & 0xFF;
	    for (int j = 0; j < layers; j++) {
		outputBuffer.put((byte) landLayer[i][j].type);
		outputBuffer.put((byte) landLayer[i][j].index);
	    }
	}

	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayers() objectLayer @" + outputBuffer.position());
	}
	for (int i = 0; i < settings().iWorldSize; i++) {
	    int layers = layerCounts[i][1] & 0xFF;
	    for (int j = 0; j < layers; j++) {
		outputBuffer.put((byte) objectLayer[i][j].type);
		outputBuffer.putShort((short) objectLayer[i][j].index);
	    }
	}

	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayers() structLayer @" + outputBuffer.position());
	}
	for (int i = 0; i < settings().iWorldSize; i++) {
	    int layers = layerCounts[i][2] & 0xFF;
	    for (int j = 0; j < layers; j++) {
		outputBuffer.put((byte) structLayer[i][j].type);
		outputBuffer.put((byte) structLayer[i][j].index);
	    }
	}

	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayers() shadowLayer @" + outputBuffer.position());
	}
	for (int i = 0; i < settings().iWorldSize; i++) {
	    int layers = layerCounts[i][3] & 0xFF;
	    for (int j = 0; j < layers; j++) {
		outputBuffer.put((byte) shadowLayer[i][j].type);
		outputBuffer.put((byte) shadowLayer[i][j].index);
	    }
	}

	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayers() roofLayer @" + outputBuffer.position());
	}
	for (int i = 0; i < settings().iWorldSize; i++) {
	    int layers = layerCounts[i][4] & 0xFF;
	    for (int j = 0; j < layers; j++) {
		outputBuffer.put((byte) roofLayer[i][j].type);
		outputBuffer.put((byte) roofLayer[i][j].index);
	    }
	}

	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayers() onRoofLayer @" + outputBuffer.position());
	}
	for (int i = 0; i < settings().iWorldSize; i++) {
	    int layers = layerCounts[i][5] & 0xFF;
	    for (int j = 0; j < layers; j++) {
		outputBuffer.put((byte) onRoofLayer[i][j].type);
		outputBuffer.put((byte) onRoofLayer[i][j].index);
	    }
	}
	if (MapLoader.logFileIO) {
	    System.out.println("loader.core.MapLayers.saveLayers() end @" + outputBuffer.position());
	}
    }
}
