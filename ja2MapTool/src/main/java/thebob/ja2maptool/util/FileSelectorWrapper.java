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
package thebob.ja2maptool.util;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author starcatter
 */
public class FileSelectorWrapper {

    public static String saveDialog(String name, String fileTypeName, String fileTypeMask, Window parentWindow) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(name);
        chooser.setInitialDirectory(new File("."));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileTypeName, fileTypeMask));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
        File selectedDirectory = chooser.showSaveDialog(parentWindow);
        if (selectedDirectory != null) {
            return selectedDirectory.getPath();
        } else {
            return null;
        }
    }

    public static String openDialog(String name, String fileTypeName, String fileTypeMask, Window parentWindow) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(name);
        chooser.setInitialDirectory(new File("."));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileTypeName, fileTypeMask));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
        File selectedDirectory = chooser.showOpenDialog(parentWindow);
        if (selectedDirectory != null) {
            return selectedDirectory.getPath();
        } else {
            return null;
        }
    }
}
