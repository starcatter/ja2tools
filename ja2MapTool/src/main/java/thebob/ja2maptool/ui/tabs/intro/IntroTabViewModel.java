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
package thebob.ja2maptool.ui.tabs.convert;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import thebob.ja2maptool.scopes.MainScope;
import static thebob.ja2maptool.scopes.MainScope.TabTypes.TAB_CONVERT;
import static thebob.ja2maptool.scopes.MainScope.TabTypes.TAB_MAPPING_SETUP;
import static thebob.ja2maptool.scopes.MainScope.TabTypes.TAB_VFS_SETUP;

public class IntroTabViewModel implements ViewModel {
    @InjectScope
    MainScope mainScreen;
    
    public void goToConfigSetupTab() {
	mainScreen.publish(mainScreen.GOTO_TAB, TAB_VFS_SETUP);
    }

    public void goToConvertSetupTab() {
	mainScreen.publish(mainScreen.GOTO_TAB, TAB_CONVERT);
    }

    public void goToMapSetupTab() {
	mainScreen.publish(mainScreen.GOTO_TAB, TAB_MAPPING_SETUP);
    }

}