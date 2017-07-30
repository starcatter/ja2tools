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
package thebob.assetloader.slf;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javolution.io.Struct;
import thebob.assetloader.common.AutoLoadingStruct;
import static thebob.assetloader.common.AutoLoadingStruct.mappedArrayToString;
import static thebob.assetloader.common.AutoLoadingStruct.printArrayAsChars;
import static thebob.assetloader.slf.SLFConstants.*;

/**
 *
 * @author the_bob
 */
class SLFConstants {

    public static final boolean printBinaryBlobs = false;

    public static final int FILE_OK = 0;
    public static final int FILE_DELETED = 0xff;
    public static final int FILE_OLD = 1;
    public static final int FILE_DOESNT_EXIST = 0xfe;

    public static final int FILENAME_SIZE = 256;

    public static final int PATH_SIZE = 80;

    public static final int NUM_FILES_TO_ADD_AT_A_TIME = 20;
    public static final int INITIAL_NUM_HANDLES = 20;

    public static final int REAL_FILE_LIBRARY_ID = 1022;

    public static final int DB_BITS_FOR_LIBRARY = 10;
    public static final int DB_BITS_FOR_FILE_ID = 22;

}

// should be 532B
class LIBHEADER extends AutoLoadingStruct {

    Signed8[] sLibName = array(new Signed8[FILENAME_SIZE]);
    Signed8[] sPathToLibrary = array(new Signed8[FILENAME_SIZE]);
    Signed32 iEntries = new Signed32();
    Signed32 iUsed = new Signed32();
    Unsigned16 iSort = new Unsigned16();
    Unsigned16 iVersion = new Unsigned16();
    Bool fContainsSubDirectories = new Bool();
    Signed32 iReserved = new Signed32();

    @Override
    public String toString() {
        return "LIBHEADER\n\t"
                + "sLibName: " + printArrayAsChars(sLibName) + "\n\t"
                + "sPathToLibrary: " + printArrayAsChars(sPathToLibrary) + "\n\t"
                + "iEntries: " + iEntries.get() + "\n\t"
                + "iUsed: " + iUsed.get() + "\n\t"
                + "iSort: " + iSort.get() + "\n\t"
                + "iVersion: " + iVersion.get() + "\n\t"
                + "fContainsSubDirectories: " + fContainsSubDirectories.get() + "\n\t"
                + "iReserved: " + iReserved.get() + "\n---------\n\t"
                + (printBinaryBlobs ? super.toString() : "");
    }

}

class FILETIME extends AutoLoadingStruct {

    Unsigned32 dwLowDateTime = new Unsigned32();
    Unsigned32 dwHighDateTime = new Unsigned32();
}

// should be 280B
class DIRENTRY extends AutoLoadingStruct {

    Signed8[] sFileName = array(new Signed8[FILENAME_SIZE]);
    Unsigned32 uiOffset = new Unsigned32();
    Unsigned32 uiLength = new Unsigned32();
    Unsigned8 ubState = new Unsigned8();
    Unsigned8 ubReserved = new Unsigned8();
    FILETIME sFileTime = inner(new FILETIME());
    Unsigned16 usReserved2 = new Unsigned16();

    @Override
    public String toString() {
        return "DIRENTRY\n\t"
                + "sFileName: " + printArrayAsChars(sFileName) + "\n\t"
                + "uiOffset: " + uiOffset.get() + "\n\t"
                + "uiLength: " + uiLength.get() + "\n\t"
                + "ubState: " + ubState.get() + "\n\t"
                + "ubReserved: " + ubReserved.get() + "\n\t"
                + "sFileTime: " + sFileTime + "\n\t"
                + "usReserved2: " + usReserved2.get() + "\n---------\n\t"
                + (printBinaryBlobs ? super.toString() : "");
    }

}

public class SlfLoader {

    String fileName;
    MappedByteBuffer byteBuffer;
    FileChannel fc;

    private long fileLength;
    private int usNumEntries;
    private int usNumEntriesFailed;

    private ArrayList<DIRENTRY> fileHeaders;
    private Map<String, DIRENTRY> files;    

    public String getFileName() {
	return fileName;
    }
    
    public boolean loadFile(String fileName) {
        this.fileName = fileName;
        try (final RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            fileLength = file.length();

            fc = file.getChannel();
            byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

            initFile(byteBuffer);
        } catch (IOException ex) {
            Logger.getLogger(SlfLoader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    private void initFile(MappedByteBuffer byteBuffer) {

        // Read in the library header ( at the begining of the library )
        LIBHEADER LibFileHeader = new LIBHEADER();
        // System.out.println("thebob.assetloader.slf.SlfLoader.initFile(): LibFileHeader size=" + LibFileHeader.size());
        LibFileHeader.setSource(byteBuffer);

        // System.out.println("thebob.assetloader.slf.SlfLoader.initFile():\n" + LibFileHeader);

        DIRENTRY DirEntry = new DIRENTRY();
        // System.out.println("thebob.assetloader.slf.SlfLoader.initFile(): DirEntry size=" + DirEntry.size());
        int dirSize = DirEntry.size();

        //place the file pointer at the begining of the file headers ( they are at the end of the file )
        int fileHeaderStartPos = (int) (fileLength - LibFileHeader.iEntries.get() * dirSize);
        // System.out.println("thebob.assetloader.slf.SlfLoader.initFile(): fileHeaderStartPos=" + fileHeaderStartPos);
        byteBuffer.position(fileHeaderStartPos);

        //loop through the library and determine the number of files that are FILE_OK
        //ie.	so we dont load the old or deleted files
        usNumEntries = 0;
        usNumEntriesFailed = 0;
        fileHeaders = new ArrayList<DIRENTRY>();
        files = new HashMap<String, DIRENTRY>();
        int uiLoop;
        int entryState;
        for (uiLoop = 0; uiLoop < LibFileHeader.iEntries.get(); uiLoop++) {
            //read in the file header
            DirEntry = new DIRENTRY();
            DirEntry.setSource(byteBuffer);
            entryState = DirEntry.ubState.get();

            switch (entryState) {
                case FILE_OK:
                    usNumEntries++;
                    fileHeaders.add(DirEntry);
                    files.put( mappedArrayToString(DirEntry.sFileName), DirEntry); 
                    
                    //System.out.println("thebob.assetloader.slf.SlfLoader.initFile(): " + printArrayAsChars(DirEntry.sFileName));
                    break;
                case FILE_DELETED:
                case FILE_OLD:
                case FILE_DOESNT_EXIST:
                    usNumEntriesFailed++;
                    break;
                default:
                    System.out.println("thebob.assetloader.slf.SlfLoader.initFile(): invalid entry state!");
            }

        }

        // System.out.println("thebob.assetloader.slf.SlfLoader.initFile(): " + usNumEntries + " ok, " + usNumEntriesFailed + " failed.");
    }

    protected ByteBuffer grantAccessToAsset(DIRENTRY header){
        byteBuffer.position((int) header.uiOffset.get());

        // System.out.println("thebob.assetloader.slf.SlfLoader.getAsset(): granting asset: " + printArrayAsChars(header.sFileName) + ", " + byteBuffer.position() + "-" + (header.uiLength.get() + byteBuffer.position()));

        ByteBuffer assetBuffer = byteBuffer.slice();
        assetBuffer.limit((int) header.uiLength.get());
        assetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        return assetBuffer;    
    }    
    
    // access by asset number
    public ByteBuffer getAsset(int index) {
        DIRENTRY header = fileHeaders.get(index);
        return grantAccessToAsset(header);
    }

    // get file set
    public final Set<String> getFiles() {
        return files.keySet();
    }

    // access by file name
    public final ByteBuffer getFile(String name) {
        DIRENTRY header = files.get(name);
        return grantAccessToAsset(header);
    }
    
    public int getAssetCount() {
        return usNumEntries;
    }

    @Override
    public String toString() {
        String file = Paths.get(fileName).toAbsolutePath().normalize().toString();
        return "[SLF loader, "+file+", "+ getAssetCount() +" assets]";
    }
    
}
