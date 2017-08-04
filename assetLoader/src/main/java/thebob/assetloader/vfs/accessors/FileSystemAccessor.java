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
package thebob.assetloader.vfs.accessors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.slf.SlfLoader;

/**
 *
 * @author the_bob
 */
public class FileSystemAccessor extends VFSAccessor {

    String filePath;	// file name in file system
    String vfsPath;	// file name in VFS tree
    MappedByteBuffer byteBuffer;
    FileChannel fc;

    public FileSystemAccessor(String filePath,String vfsPath) {
        this.filePath = filePath;
        this.vfsPath = vfsPath;
    }

    @Override
    public ByteBuffer getBytes() {	
	if( byteBuffer == null ){
	    return getBuffer();
	} else {
	    byteBuffer.rewind(); // when accessing via buffer make sure to rewind between use!
	    return byteBuffer;
	}
    }

    @Override
    public FileInputStream getStream() {
        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSystemAccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private ByteBuffer getBuffer() {
        try (final RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            fc = file.getChannel();
            byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            return byteBuffer;
        } catch (IOException ex) {
            Logger.getLogger(SlfLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
	String file = Paths.get(filePath).toAbsolutePath().normalize().toString();
        return "[ FileSystemAccessor: " + file + " ]";
    }

    @Override
    public String getPath() {
        return filePath;
    }

    @Override
    public String getVFSPath() {
	return vfsPath;
    }

}
