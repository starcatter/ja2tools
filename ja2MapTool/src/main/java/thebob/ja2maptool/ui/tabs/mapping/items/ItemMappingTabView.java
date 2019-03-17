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
package thebob.ja2maptool.ui.tabs.mapping.items;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import thebob.assetmanager.managers.items.Item;
import thebob.ja2maptool.components.ItemMappingTreeItem;
import thebob.ja2maptool.components.SimplePropertyItem;
import static thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.SELECT_MAPPING_LEFT;
import static thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.SELECT_MAPPING_LIST;
import static thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.SELECT_MAPPING_RIGHT;
import static thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.UPDATE_MAPPING;
import static thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.UPDATE_PROPS_LEFT;
import static thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.UPDATE_PROPS_RIGHT;
import thebob.ja2maptool.util.mapping.item.ItemMapping;
import thebob.ja2maptool.util.mapping.item.Mapping;

public class ItemMappingTabView implements FxmlView<ItemMappingTabViewModel>, Initializable {

	@FXML
	private PropertySheet props_left;

	@FXML
	private PropertySheet props_right;

	@FXML
	private TreeView<String> list_left;

	@FXML
	private TreeView<String> list_right;

	@FXML
	private ListView<Mapping> mapping_list;

	@FXML
	void onAuto(MouseEvent event) {
		viewModel.autoMapping();
	}

	@FXML
	void onLoad(MouseEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Load mapping");
		chooser.setInitialDirectory(new File("."));
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("item mapping files", "*.itemmap"));
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
		File selectedDirectory = chooser.showOpenDialog(list_left.getScene().getWindow());
		if (selectedDirectory != null) {
			viewModel.loadMapping(selectedDirectory.getPath());
		}
	}

	@FXML
	void onSave(MouseEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save mapping as...");
		chooser.setInitialDirectory(new File("."));
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("item mapping files", "*.itemmap"));
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
		File selectedDirectory = chooser.showSaveDialog(list_left.getScene().getWindow());
		if (selectedDirectory != null) {
			viewModel.saveMapping(selectedDirectory.getPath());
		}
	}

	// MVVMFX inject
	@InjectViewModel
	private ItemMappingTabViewModel viewModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// setup main lists
		list_left.setRoot(viewModel.getRootLeft());
		list_right.setRoot(viewModel.getRootRight());

		list_left.setOnMouseClicked(event -> {
			TreeItem<String> selectedNode = list_left.getSelectionModel().getSelectedItem();
			if (selectedNode != null && selectedNode.isLeaf()) {
				ItemMappingTreeItem mappedNode = (ItemMappingTreeItem) selectedNode;
				viewModel.selectedSource(mappedNode.getItem());
			}
		});

		// setup main list events
		list_right.setOnMouseClicked(event -> {
			Item itemLeft = null;
			Item itemRight = null;

			TreeItem<String> selectedNodeLeft = list_left.getSelectionModel().getSelectedItem();
			if (selectedNodeLeft != null && selectedNodeLeft.isLeaf()) {
				ItemMappingTreeItem mappedNode = (ItemMappingTreeItem) selectedNodeLeft;
				itemLeft = mappedNode.getItem();
			}

			TreeItem<String> selectedNodeRight = list_right.getSelectionModel().getSelectedItem();
			if (selectedNodeRight != null && selectedNodeRight.isLeaf()) {
				ItemMappingTreeItem mappedNode = (ItemMappingTreeItem) selectedNodeRight;
				itemRight = mappedNode.getItem();
			}

			if (itemLeft != null && itemRight != null) {
				viewModel.setItemMapping(itemLeft, itemRight, selectedNodeRight);
			}
		});

		list_left.getSelectionModel().selectedIndexProperty().addListener(event -> {
			updateItemPropsLeft();
		});
		list_right.getSelectionModel().selectedIndexProperty().addListener(event -> {
			updateItemPropsRight();
		});

		// setup prop lists
		viewModel.setPropsLeft(props_left.getItems());
		viewModel.setPropsRight(props_right.getItems());

		props_left.setPropertyEditorFactory(item -> {
			return new PropertyEditor() {
				@Override
				public Node getEditor() {
					return new Label(item.getValue().toString());
				}

				@Override
				public Object getValue() {
					return item;
				}

				@Override
				public void setValue(Object value) {

				}
			};
		});

		props_right.setPropertyEditorFactory(item -> {
			return new PropertyEditor() {
				@Override
				public Node getEditor() {
					return new Label(item.getValue().toString());
				}

				@Override
				public Object getValue() {
					return item;
				}

				@Override
				public void setValue(Object value) {

				}
			};
		});

		// setup mapping list
		mapping_list.setItems(viewModel.getMappingList());
		mapping_list.setOnMouseClicked(event -> {
			Mapping selectedMapping = mapping_list.getSelectionModel().getSelectedItem();
			viewModel.showMapping(selectedMapping);
		});

		mapping_list.setOnKeyPressed( event -> {
			if(event.getCode().equals(KeyCode.DELETE)){
				Mapping selectedMapping = mapping_list.getSelectionModel().getSelectedItem();
				mapping_list.getSelectionModel().clearSelection();
				if(selectedMapping != null){
					viewModel.removeMapping(selectedMapping);
					event.consume();
				}
			}
		} );

		// setup events
		viewModel.subscribe(SELECT_MAPPING_LEFT, (key, value) -> {
			TreeItem node = (TreeItem) value[0];
			list_left.getSelectionModel().select(node);

			int index = list_left.getSelectionModel().getSelectedIndex();
			list_left.getFocusModel().focus(index);
			list_left.scrollTo(index - 5);
			list_left.scrollTo(index + 5);
			list_left.scrollTo(index);
		});

		viewModel.subscribe(SELECT_MAPPING_RIGHT, (key, value) -> {
			TreeItem node = (TreeItem) value[0];
			list_right.getSelectionModel().select(node);

			int index = list_right.getSelectionModel().getSelectedIndex();
			list_right.getFocusModel().focus(index);
			list_right.scrollTo(index - 5);
			list_right.scrollTo(index + 5);
			list_right.scrollTo(index);
		});

		viewModel.subscribe(SELECT_MAPPING_LIST, (key, value) -> {
			Mapping mapping = (Mapping) value[0];
			mapping_list.scrollTo(mapping);
			mapping_list.getSelectionModel().select(mapping);
		});

		viewModel.subscribe(UPDATE_MAPPING, (key, value) -> {
			mapping_list.refresh();
		});

		viewModel.subscribe(UPDATE_PROPS_LEFT, (key, value) -> {
			updateItemPropsLeft();
		});
		viewModel.subscribe(UPDATE_PROPS_RIGHT, (key, value) -> {
			updateItemPropsRight();
		});
	}

	private void updateItemPropsLeft() {
		TreeItem<String> selectedNode = list_left.getSelectionModel().getSelectedItem();
		if (selectedNode != null && selectedNode.isLeaf()) {
			Item item = ((ItemMappingTreeItem) list_left.getSelectionModel().getSelectedItem()).getItem();
			updateItemProps(item, props_left.getItems());
		}
	}

	private void updateItemPropsRight() {
		TreeItem<String> selectedNode = list_right.getSelectionModel().getSelectedItem();
		if (selectedNode != null && selectedNode.isLeaf()) {
			Item item = ((ItemMappingTreeItem) list_right.getSelectionModel().getSelectedItem()).getItem();
			updateItemProps(item, props_right.getItems());
		}
	}

	private void updateItemProps(Item item, ObservableList<PropertySheet.Item> propList) {
		propList.clear();

		propList.add(new SimplePropertyItem("Name", item.getName(), "General", ""));
		propList.add(new SimplePropertyItem("Coolness", item.getCoolness() + "", "General", ""));
	}
}
