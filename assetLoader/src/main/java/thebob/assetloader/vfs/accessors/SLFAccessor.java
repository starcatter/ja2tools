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

import java.nio.ByteBuffer;
import thebob.assetloader.slf.SlfLoader;

/**
 *
 * @author the_bob
 */
public class SLFAccessor extends VFSAccessor {
    
    SlfLoader loader;
    String filePath;	// file name in slf library
    String vfsPath;	// file name in VFS tree

    public SLFAccessor(SlfLoader loader, String filePath, String vfsPath) {
        this.loader = loader;
        this.filePath = filePath;
        this.vfsPath = vfsPath;
    }

    public ByteBuffer getBytes() {
        return loader.getFile(filePath);
    }

    @Override
    public String toString() {
        return "[ SLFAccessor: " + loader.toString() + "::" + filePath + " ]";
    }

    @Override
    public String getPath() {
        return filePath;
    }

    public SlfLoader getLoader() {
	return loader;
    }

    @Override
    public String getVFSPath() {
	return vfsPath;
    }
    
}
