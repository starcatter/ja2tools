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
package thebob.assetloader.sti;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javolution.io.Struct;
import javolution.io.Union;
import thebob.assetloader.common.AutoLoadingStruct;
import thebob.assetloader.slf.SlfLoader;
import static thebob.assetloader.sti.StiConstants.*;
import static thebob.assetloader.sti.HImageConstants.*;

/**
 *
 *
 * BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
 *
 *
 * @author the_bob
 */
class ETRLEObject extends AutoLoadingStruct {

    Unsigned32 uiDataOffset = new Unsigned32();
    Unsigned32 uiDataLength = new Unsigned32();
    Signed16 sOffsetX = new Signed16();
    Signed16 sOffsetY = new Signed16();
    Unsigned16 usHeight = new Unsigned16();
    Unsigned16 usWidth = new Unsigned16();

    @Override
    public String toString() {
        return "ETRLEObject\n\t"
                + "uiDataOffset: " + uiDataOffset.get() + "\n\t"
                + "uiDataLength: " + uiDataLength.get() + "\n\t"
                + "sOffsetX: " + sOffsetX.get() + "\n\t"
                + "sOffsetY: " + sOffsetY.get() + "\n\t"
                + "usHeight: " + usHeight.get() + "\n\t"
                + "usWidth: " + usWidth.get() + "\n\t"
                + super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

}

class ETRLEData extends AutoLoadingStruct {

    Reference32 pPixData = new Reference32();
    Unsigned32 uiSizePixData = new Unsigned32();
    Reference32<ETRLEObject> pETRLEObject = new Reference32<ETRLEObject>();
    Unsigned16 usNumberOfObjects = new Unsigned16();
}

class STCIPaletteElement extends AutoLoadingStruct {

    Unsigned8 ubRed = new Unsigned8();
    Unsigned8 ubGreen = new Unsigned8();
    Unsigned8 ubBlue = new Unsigned8();

    @Override
    public String toString() {
        return "STCIPaletteElement{" + "ubRed=" + ubRed.get() + ", ubGreen=" + ubGreen.get() + ", ubBlue=" + ubBlue.get() + '}';
    }

}

class STCISubImage extends AutoLoadingStruct {

    Unsigned32 uiDataOffset = new Unsigned32();
    Unsigned32 uiDataLength = new Unsigned32();
    Signed16 sOffsetX = new Signed16();
    Signed16 sOffsetY = new Signed16();
    Unsigned16 usHeight = new Unsigned16();
    Unsigned16 usWidth = new Unsigned16();

}

class STCIHeader extends AutoLoadingStruct {

    Unsigned8[] cID = array(new Unsigned8[STCI_ID_LEN]); //[STCI_ID_LEN]
    Unsigned32 uiOriginalSize = new Unsigned32();
    Unsigned32 uiStoredSize = new Unsigned32(); // equal to uiOriginalSize if data uncompressed
    Unsigned32 uiTransparentValue = new Unsigned32();
    Unsigned32 fFlags = new Unsigned32();
    Unsigned16 usHeight = new Unsigned16();
    Unsigned16 usWidth = new Unsigned16();
    STCIHeaderFormatInfo format = inner(new STCIHeaderFormatInfo());
    Unsigned8 ubDepth = new Unsigned8();	// size in bits of one pixel as stored in the file
    Unsigned32 uiAppDataSize = new Unsigned32();
    Unsigned8[] cUnused = array(new Unsigned8[11]); // was [15] in JA2, shortened to match required 64B size

    @Override
    public String toString() {

        return "STCIHeader (" + size() + ")\n\t"
                + "uiOriginalSize: " + uiOriginalSize.get() + "\n\t"
                + "uiStoredSize: " + uiStoredSize.get() + "\n\t"
                + "uiTransparentValue: " + uiTransparentValue.get() + "\n\t"
                + "fFlags: " + fFlags.get() + "\n\t"
                + "usHeight: " + usHeight.get() + "\n\t"
                + "usWidth: " + usWidth.get() + "\n\t"
                + "format: " + format + "\n\t"
                + "ubDepth: " + ubDepth.get() + "\n\t"
                + "uiAppDataSize: " + uiAppDataSize.get() + "\n\t"
                + (printBinaryBlobs ? super.toString() : "");
    }

}

class STCIHeaderRGBData extends AutoLoadingStruct {

    Unsigned32 uiRedMask = new Unsigned32();
    Unsigned32 uiGreenMask = new Unsigned32();
    Unsigned32 uiBlueMask = new Unsigned32();
    Unsigned32 uiAlphaMask = new Unsigned32();
    Unsigned8 ubRedDepth = new Unsigned8();
    Unsigned8 ubGreenDepth = new Unsigned8();
    Unsigned8 ubBlueDepth = new Unsigned8();
    Unsigned8 ubAlphaDepth = new Unsigned8();

    @Override
    public String toString() {
        return "STCIHeaderRGBData\n\t\t"
                + "uiRedMask: " + uiRedMask.get() + "\n\t\t"
                + "uiGreenMask: " + uiGreenMask.get() + "\n\t\t"
                + "uiBlueMask: " + uiBlueMask.get() + "\n\t\t"
                + "uiAlphaMask: " + uiAlphaMask.get() + "\n\t\t"
                + "ubRedDepth: " + ubRedDepth.get() + "\n\t\t"
                + "ubGreenDepth: " + ubGreenDepth.get() + "\n\t\t"
                + "ubBlueDepth: " + ubBlueDepth.get() + "\n\t\t"
                + "ubAlphaDepth: " + ubAlphaDepth.get() + "\n\t\t"
                + (printBinaryBlobs ? super.toString() : "\n");
    }

}

class STCIHeaderIndexedData extends AutoLoadingStruct {

    Unsigned32 uiNumberOfColours = new Unsigned32();
    Unsigned16 usNumberOfSubImages = new Unsigned16();
    Unsigned8 ubRedDepth = new Unsigned8();
    Unsigned8 ubGreenDepth = new Unsigned8();
    Unsigned8 ubBlueDepth = new Unsigned8();
    Unsigned8[] cIndexedUnused = array(new Unsigned8[11]); //[11];

    @Override
    public String toString() {
        return "STCIHeaderIndexedData\n\t\t"
                + "uiNumberOfColours: " + uiNumberOfColours.get() + "\n\t\t"
                + "usNumberOfSubImages: " + usNumberOfSubImages.get() + "\n\t\t"
                + "ubRedDepth: " + ubRedDepth.get() + "\n\t\t"
                + "ubGreenDepth: " + ubGreenDepth.get() + "\n\t\t"
                + "ubBlueDepth: " + ubBlueDepth.get() + "\n\t\t"
                + (printBinaryBlobs ? super.toString() : "\n");
    }

}

class STCIHeaderFormatInfo extends Union {

    STCIHeaderRGBData RGB = inner(new STCIHeaderRGBData());
    STCIHeaderIndexedData Indexed = inner(new STCIHeaderIndexedData());

    @Override
    public String toString() {
        return "STCIHeaderFormatInfo\n\t"
                + "->RGB: " + RGB + "\n\t"
                + "->Indexed: " + Indexed + "\n\t"
                + (printBinaryBlobs ? super.toString() : "");
    }

}

class HImageConstants {

    public static final int iCOMPRESS_TRANSPARENT = 0x80;
    public static final int iCOMPRESS_RUN_MASK = 0x7F;

// Defines for type of file readers
    public static final int PCX_FILE_READER = 0x1;
    public static final int TGA_FILE_READER = 0x2;
    public static final int STCI_FILE_READER = 0x4;
    public static final int TRLE_FILE_READER = 0x8;
    public static final int PNG_FILE_READER = 0x10;
    public static final int JPC_FILE_READER = 0x20;
    public static final int UNKNOWN_FILE_READER = 0x200;

// Defines for buffer bit depth
    public static final int BUFFER_8BPP = 0x1;
    public static final int BUFFER_16BPP = 0x2;

// Defines for image charactoristics
    public static final int IMAGE_COMPRESSED = 0x0001;
    public static final int IMAGE_TRLECOMPRESSED = 0x0002;
    public static final int IMAGE_PALETTE = 0x0004;
    public static final int IMAGE_BITMAPDATA = 0x0008;
    public static final int IMAGE_APPDATA = 0x0010;
    public static final int IMAGE_ALLIMAGEDATA = 0x000C;
    public static final int IMAGE_ALLDATA = 0x001C;

    public static final int AUX_FULL_TILE = 0x01;
    public static final int AUX_ANIMATED_TILE = 0x02;
    public static final int AUX_DYNAMIC_TILE = 0x04;
    public static final int AUX_SignedERACTIVE_TILE = 0x08;
    public static final int AUX_IGNORES_HEIGHT = 0x10;
    public static final int AUX_USES_LAND_Z = 0x20;
}

class StiConstants {

    public static final boolean printBinaryBlobs = false;

    public static final String STCI_ID_STRING = "STCI";
    public static final int STCI_ID_LEN = 4;

    public static final int STCI_ETRLE_COMPRESSED = 0x0020;
    public static final int STCI_ZLIB_COMPRESSED = 0x0010;
    public static final int STCI_INDEXED = 0x0008;
    public static final int STCI_RGB = 0x0004;
    public static final int STCI_ALPHA = 0x0002;
    public static final int STCI_TRANSPARENT = 0x0001;

// ETRLE defines
    public static final int COMPRESS_TRANSPARENT = 0x80;
    public static final int COMPRESS_NON_TRANSPARENT = 0x00;
    public static final int COMPRESS_RUN_LIMIT = 0x7F;

    public static final int STCI_HEADER_SIZE = 64;
    public static final int STCI_PALETTE_ELEMENT_SIZE = 3;
    public static final int STCI_8BIT_PALETTE_SIZE = 768;
    public static final int STCI_SUBIMAGE_SIZE = 16;

}

public class StiLoader {

    private long fileLength;
    ByteBuffer buffer;

    FileChannel fc;
    private STCIHeader header = null;
    private byte[] imageData = null;
    private byte[] appData = null;
    private STCIPaletteElement[] palette = null;
    private ETRLEObject[] objects = null;

    public int getTransparentValue(){
        return (int) header.uiTransparentValue.get();
    }
    
    public int getImageWidth(int index){
        return objects == null ? header.usWidth.get() : objects[index].usWidth.get();
    }
    
    public int getImageHeight(int index){
        return objects == null ? header.usHeight.get() : objects[index].usHeight.get();
    }
    
    public int getImageOffsetX(int index){
        return objects == null ? 0 : objects[index].sOffsetX.get();
    }
    
    public int getImageOffsetY(int index){
        return objects == null ? 0 : objects[index].sOffsetY.get();
    }
    
    public int getImageCount() {
        if (objects == null) {
            return 1;
        } else {
            return objects.length;
        }
    }

    public byte[] getImage(int index) {
        if (objects == null) {
            return imageData;
        } else {
            return decodeIndex(index);
        }
    }

    public byte[][] getPalette() {
        byte[][] paletteArray = new byte[3][256];

        for (int i = 0; i < 256; i++) {
            paletteArray[0][i] = (byte) palette[i].ubRed.get();
            paletteArray[1][i] = (byte) palette[i].ubGreen.get();
            paletteArray[2][i] = (byte) palette[i].ubBlue.get();
        }

        return paletteArray;
    }

    public boolean loadFile(String fileName) {
        try (final RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            fileLength = file.length();

            fc = file.getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

            loadAsset(byteBuffer);
        } catch (IOException ex) {
            Logger.getLogger(SlfLoader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    public boolean loadAsset(ByteBuffer inputBuffer) {
        buffer = inputBuffer;

        if (buffer.limit() < STCI_HEADER_SIZE) {
            System.out.println("thebob.assetloader.sti.StiLoader.loadAsset(): not a STCI file!");
            return false;
        }

        header = new STCIHeader();
        header.setSource(buffer);

        if (header.cID[0].get() != 'S' || header.cID[1].get() != 'T' || header.cID[2].get() != 'C' || header.cID[3].get() != 'I') {
            System.out.println("thebob.assetloader.sti.StiLoader.loadAsset(): not a STCI file!");
            return false;
        }

        if (buffer.limit() < header.uiStoredSize.get()) {
            System.out.println("thebob.assetloader.sti.StiLoader.loadAsset(): not a STCI file or file too short!");
            return false;
        }

        // System.out.println("thebob.assetloader.sti.StiLoader.loadAsset(): " + header);
        // Determine from the header the data stored in the file. and run the appropriate loader
        if ((header.fFlags.get() & STCI_RGB) > 0) {
            if (!STCILoadRGB()) {
                return (false);
            }
        } else if ((header.fFlags.get() & STCI_INDEXED) > 0) {
            if (!STCILoadIndexed()) {
                return (false);
            }
        } else {	// unsupported type of data, or the right flags weren't set!
            throw new UnsupportedOperationException("Unsupported file type.");  // throw here cause we should've caught the bogus data earlier!
        }

        /*
        if ((header.fFlags.get() & STCI_INDEXED) > 0) {
            processAsset();
        }
        */

        return true;
    }

    private boolean STCILoadRGB() {

        // Allocate memory for the image data and read it in        
        int dataSize = (int) header.uiStoredSize.get();
        byte[] imageData = new byte[dataSize];
        buffer.get(imageData);

        if (header.ubDepth.get() == 16) {
            // ASSUMPTION: file data is 565 R,G,B
            // used to be color correction code here. Who cares about that any more?
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        if (buffer.position() == buffer.limit()) {
            // System.out.println("thebob.assetloader.sti.StiLoader.STCILoadIndexed(): image loaded ok!");
            return true;
        } else {
            System.out.println("thebob.assetloader.sti.StiLoader.STCILoadIndexed(): image size mismatxh! ( " + (buffer.limit() - buffer.position()) + " )");
            return false;
        }
    }

    private boolean STCILoadIndexed() {

        // load palette (has to be there, also has to have 256 colors in it!)
        palette = new STCIPaletteElement[256];
        for (int i = 0; i < 256; i++) {
            STCIPaletteElement el = new STCIPaletteElement();
            el.setSource(buffer);
            palette[i] = el;
        }

        // load image data desc
        if ((header.fFlags.get() & STCI_ETRLE_COMPRESSED) > 0) {
            // load data for the subimage (object) structures
            int usNumberOfObjects = header.format.Indexed.usNumberOfSubImages.get();

            objects = new ETRLEObject[usNumberOfObjects];
            // System.out.print(usNumberOfObjects + " objects; ");

            for (int i = 0; i < usNumberOfObjects; i++) {
                ETRLEObject eo = new ETRLEObject();
                eo.setSource(buffer);
                objects[i] = eo;
            }
        }

        //System.out.println("thebob.assetloader.sti.StiLoader.STCILoadIndexed() load " + dataSize + "B @" + buffer.position() + " / " + buffer.limit() + " buffer pos ( ->" + (buffer.position() + dataSize) + " )");
        // load raw image data
        int dataSize = (int) header.uiStoredSize.get();
        imageData = new byte[dataSize];
        // System.out.print(dataSize + "B of data; ");
        buffer.get(imageData);

        if (header.uiAppDataSize.get() > 0) {
            int appDataSize = (int) header.uiAppDataSize.get();
            appData = new byte[appDataSize];
            // System.out.print(dataSize + "B of APP data; ");
            buffer.get(appData);
        }

        if (buffer.position() == buffer.limit()) {
            // System.out.println("image loaded ok!");
            return true;
        } else {
            // System.out.println("image size mismatxh! ( " + (buffer.limit() - buffer.position()) + " )");
            return false;
        }
    }

    private byte[] decodeImage(ETRLEObject object) {
        int offset = (int) object.uiDataOffset.get();
        int rawLength = (int) object.uiDataLength.get();

        int width = object.usWidth.get();
        int height = object.usHeight.get();

        int uiImageSize = width * height;

        byte[] dataSlice = Arrays.copyOfRange(imageData, offset, offset + rawLength);
        byte[] result = new byte[uiImageSize];
        int pCurrentCnt = 0;
        int uiBufferPos = 0;
        int ubCount = 0;

        while (uiBufferPos < uiImageSize && pCurrentCnt < rawLength) {
            ubCount = dataSlice[pCurrentCnt] & iCOMPRESS_RUN_MASK;
            if ((dataSlice[pCurrentCnt] & iCOMPRESS_TRANSPARENT) > 0) {
                pCurrentCnt++;
                uiBufferPos += ubCount;
            } else {
                pCurrentCnt++;

                for (int i = 0; i < ubCount; i++) {
                    result[uiBufferPos + i] = dataSlice[pCurrentCnt + i];
                }

                pCurrentCnt += ubCount;
                uiBufferPos += ubCount;
            }
        }
        if (uiBufferPos < uiImageSize - 1) {
            System.out.println("thebob.assetloader.sti.StiLoader.processAsset(): error decomposting image");
            return null;
        }
        return result;
    }

    private boolean processAsset() {
        for (ETRLEObject object : objects) {
            if (decodeImage(object) == null) {
                return false;
            }
        }
        return true;
    }

    private byte[] decodeIndex(int index) {
        return decodeImage(objects[index]);
    }

}
