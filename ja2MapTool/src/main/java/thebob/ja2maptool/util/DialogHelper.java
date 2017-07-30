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
package thebob.ja2maptool.util;

import java.util.function.Supplier;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class is a helper to create a dialog that will open and close itself
 * according to a boolean property.
 *
 * This way you can define in a ViewModel when and under which conditions a
 * dialog should be shown.
 */
public class DialogHelper {

	/**
	 * Use this method to initialize the show/hide listeners for the dialog.
	 *
	 * @param openProperty the boolean property that defines whether the dialog
	 * should be shown or hidden. Set this property to <code>true</code> to open
	 * the dialog. Set it to <code>false</code> to close the dialog. When the
	 * dialog is closed by the user by clicking on the close-button of the
	 * window, this property will also be set to <code>false</code> by the
	 * dialog.
	 * @param parentStage the Stage that is used as parent to initialize the
	 * ownership of the dialog. This way modal dialogs can be created.
	 * @param rootSupplier a supplier function that is called when the dialog is
	 * made visible for the first time. This function has to return a
	 * {@link Parent} instance that is used as the root node of the dialog
	 * scene.
	 */
	public static void initDialog(BooleanProperty openProperty, final Stage parentStage, Supplier<Parent> rootSupplier) {
		final Stage dialogStage = new Stage(StageStyle.UTILITY);
		dialogStage.initOwner(parentStage);
		dialogStage.initModality(Modality.APPLICATION_MODAL);

		openProperty.addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				// when it is the first time the dialog is made visible (and therefore no scene exists) ...
				if (dialogStage.getScene() == null) {
					// ... we create a new scene and register it in the stage.
					Scene dialogScene = new Scene(rootSupplier.get());
					dialogScene.getStylesheets().add("/contacts.css");
					dialogStage.setScene(dialogScene);
				} else {
					// ... otherwise we simple bring the dialog to front.
					dialogStage.toFront();
				}

				dialogStage.sizeToScene();
				dialogStage.show();
			} else {
				dialogStage.close();
			}
		});

		// when the user clicks on the close button of the dialog window
		// we want to set the property to false
		dialogStage.setOnCloseRequest(event -> openProperty.set(false));
	}

	public static Stage showDialog(Parent view, Stage parentStage, String... sceneStyleSheets) {
		final Stage dialogStage = new Stage(StageStyle.UTILITY);
		dialogStage.initOwner(parentStage);
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		if (dialogStage.getScene() == null) {
			// ... we create a new scene and register it in the stage.
			Scene dialogScene = new Scene(view);
			dialogScene.getStylesheets().addAll(sceneStyleSheets);
			dialogStage.setScene(dialogScene);

			dialogStage.sizeToScene();
			dialogStage.show();
			return dialogStage;
		}
		return null;
	}
}
