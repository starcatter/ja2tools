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
package thebob.ja2maptool.ui.tabs.mapping.tileset;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.inject.Inject;
import thebob.assetloader.tileset.Tile;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;
import thebob.ja2maptool.ui.dialogs.tileselect.TileSelectionDialogView;
import thebob.ja2maptool.ui.dialogs.tileselect.TileSelectionDialogViewModel;
import thebob.ja2maptool.util.DialogHelper;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class TilesetMappingTabView implements FxmlView<TilesetMappingTabViewModel>, Initializable {

    // FXML Bindings
    @FXML
    private ListView<TileCategoryMapping> mapping_list;

    @FXML
    private TableView<TileMapping> mapping_table;

    @FXML
    private Button mapping_save;

    @FXML
    private Button mapping_load;

    @FXML
    void mapping_load_click(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load mapping");
	chooser.setInitialDirectory(new File("."));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("tile mapping files", "*.tilemap"));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
	File selectedDirectory = chooser.showOpenDialog(mapping_save.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.loadMapping(selectedDirectory.getPath());
	}
    }

    @FXML
    void mapping_save_click(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Save mapping as...");
	chooser.setInitialDirectory(new File("."));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("tile mapping files", "*.tilemap"));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
	File selectedDirectory = chooser.showSaveDialog(mapping_save.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.saveMapping(selectedDirectory.getPath());
	}
    }

    @FXML
    void mapping_table_click(MouseEvent event) {

    }

    @FXML
    void mapping_auto(ActionEvent event) {
	viewModel.autoMap(0.2d);    // TODO add some UI settings for this
    }

    // FXML Bindings
    // MVVMFX inject
    @InjectViewModel
    private TilesetMappingTabViewModel viewModel;

    @InjectContext
    private Context context;

    @Inject
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	initCategoryList();
	initMappingTable();

	viewModel.subscribe(viewModel.REBUILD_TABLE, (key, payload) -> {
	    rebuildTable();
	});
	viewModel.subscribe(viewModel.REFRESH_TABLE, (key, payload) -> {
	    mapping_table.refresh();
	});
	viewModel.subscribe(viewModel.REFRESH_LIST, (key, payload) -> {
	    mapping_list.refresh();
	});

    }

    private void initCategoryList() {
	// init tile type list
	mapping_list.setItems(viewModel.getTileCategories());
	mapping_list.getSelectionModel().select(0);
	mapping_list.setOnMouseClicked(event -> {
	    if (event.isControlDown()) {
		viewModel.mapCurrentTypeTo(mapping_list.getSelectionModel().getSelectedIndex(), mapping_table.getSelectionModel().getSelectedIndex());
	    } else {
		viewModel.setSelectedCategory(mapping_list.getSelectionModel().getSelectedIndex());
	    }
	    rebuildTable();
	    mapping_list.refresh();
	});

	mapping_list.setCellFactory(list -> {
	    return new ListCell<TileCategoryMapping>() {
		@Override
		protected void updateItem(TileCategoryMapping item, boolean empty) {
		    super.updateItem(item, empty);
		    if (empty || item == null) {
			setText("err");
			setGraphic(null);
		    } else {
			setText(null);

			Label label = new Label(item.getCategoryName());
			Background b = null;

			switch (item.getStatus()) {
			    case None:
				break;
			    case Same:
				b = new Background(new BackgroundFill(Color.LIGHTCYAN, CornerRadii.EMPTY, Insets.EMPTY));
				break;
			    case Matched:
				b = (new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			    case Found:
				b = (new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			    case Manual:
				b = (new Background(new BackgroundFill(Color.LIGHTPINK, CornerRadii.EMPTY, Insets.EMPTY)));
				break;
			    default:
				b = (new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
			}

			if (item.getCategoryId() == viewModel.getSelectedCategory()) {
			    Border bb = new Border(new BorderStroke(Paint.valueOf("Red"), BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(1d)));
			    setBorder(bb);
			} else {
			    setBorder(Border.EMPTY);
			}

			setBackground(b);
			// label.setBackground(b);
			setGraphic(label);
		    }
		}

		@Override
		public void updateSelected(boolean selected) {
		    super.updateSelected(selected);
		    if (selected) {
			setUserData(getBackground());
			setBackground(new Background(new BackgroundFill(Color.STEELBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		    } else if (getUserData() != null) {
			setBackground((Background) getUserData());
		    } else {
			setBackground(Background.EMPTY);
		    }
		}

	    };
	});
    }

    private void initMappingTable() {
	TableColumn<TileMapping, String> col0 = new TableColumn<TileMapping, String>("Src #");
	TableColumn<TileMapping, TileMapping> col1 = new TableColumn<TileMapping, TileMapping>("Src Tile");
	TableColumn<TileMapping, TileMapping> col2 = new TableColumn<TileMapping, TileMapping>("Target #");
	TableColumn<TileMapping, TileMapping> col3 = new TableColumn<TileMapping, TileMapping>("Target Tile");
	TableColumn<TileMapping, TileMapping> col4 = new TableColumn<TileMapping, TileMapping>("Mapping suggestions");

	// setup source tile ID column	
	col0.setPrefWidth(60);
	col0.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, String>, ObservableValue<String>>() {
	    public ObservableValue<String> call(TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, String> p) {
		return new ReadOnlyObjectWrapper(p.getValue().getSourceType() + "-" + p.getValue().getSourceIndex());
	    }
	});

	// setup source tile column	
	col1.setPrefWidth(120);
	col1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, TileMapping>, ObservableValue<TileMapping>>() {
	    public ObservableValue<TileMapping> call(TableColumn.CellDataFeatures<TileMapping, TileMapping> p) {
		return new ReadOnlyObjectWrapper(p.getValue());
	    }
	});
	col1.setCellFactory(table -> {
	    return new TableCell<TileMapping, TileMapping>() {

		@Override
		protected void updateItem(TileMapping item, boolean empty) {
		    super.updateItem(item, empty);
		    if (empty || item == null) {
			setText(null);
			setGraphic(null);
			setBackground(Background.EMPTY);
		    } else {
			int index = item.getSourceIndex() + 1;
			int type = item.getSourceType();

			Image tile = viewModel.getSourceTileImage(type, index);
			Node cell = getTableCellTileImage(tile, type, index);
			HBox box = new HBox(cell);
			box.setAlignment(Pos.CENTER);

			setGraphic(box);
		    }
		}
	    };
	});

	// setup remapped tile ID column	
	col2.setPrefWidth(60);
	col2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, TileMapping>, ObservableValue<TileMapping>>() {
	    public ObservableValue<TileMapping> call(TableColumn.CellDataFeatures<TileMapping, TileMapping> p) {
		return new ReadOnlyObjectWrapper(p.getValue());
	    }
	});

	col2.setCellFactory(table -> {
	    return new TableCell<TileMapping, TileMapping>() {

		@Override
		protected void updateItem(TileMapping item, boolean empty) {
		    super.updateItem(item, empty);
		    if (empty || item == null) {
			setText(null);
			setGraphic(null);
			setBackground(Background.EMPTY);
		    } else {
			setText(item.getTargetType() + "-" + item.getTargetIndex());
			if (item.getMappingMode() == TileMapping.MappingMode.MatchedFile) {
			    setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
			} else if (item.getMappingMode() == TileMapping.MappingMode.AutoMatched) {
			    setBackground(new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
			} else if (item.getMappingMode() == TileMapping.MappingMode.Manual) {
			    setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
			} else {
			    setBackground(Background.EMPTY);
			}
		    }
		}

	    };
	});

	// setup remapped tile column
	col3.setPrefWidth(120);
	col3.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, TileMapping>, ObservableValue<TileMapping>>() {
	    public ObservableValue<TileMapping> call(TableColumn.CellDataFeatures<TileMapping, TileMapping> p) {
		return new ReadOnlyObjectWrapper(p.getValue());
	    }
	});
	col3.setCellFactory(table -> {
	    return new TableCell<TileMapping, TileMapping>() {

		@Override
		protected void updateItem(TileMapping item, boolean empty) {
		    super.updateItem(item, empty);
		    if (empty || item == null) {
			setText(null);
			setGraphic(null);
			setBackground(Background.EMPTY);
		    } else {
			int s_index = item.getSourceIndex();
			int s_type = item.getSourceType();

			int index = item.getTargetIndex() + 1;
			int type = item.getTargetType();

			Image tile = viewModel.getTargetTileImage(type, index);
			Node cell = getTableCellTileImage(tile, type, index);
			HBox box = new HBox(cell);
			box.setAlignment(Pos.CENTER);
			box.setCursor(Cursor.HAND);

			box.setOnMouseClicked(e -> {
			    showTileSelector(s_type, s_index);
			});
			box.setOnMouseEntered(e -> {
			    box.setBackground(new Background(new BackgroundFill(Color.LIGHTSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
			});
			box.setOnMouseExited(e -> {
			    box.setBackground(Background.EMPTY);
			});

			setGraphic(box);
		    }
		}
	    };
	});

	// setup auto map suggestion column
	col4.setPrefWidth(360);
	col4.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, TileMapping>, ObservableValue<TileMapping>>() {
	    public ObservableValue<TileMapping> call(TableColumn.CellDataFeatures<TileMapping, TileMapping> p) {
		return new ReadOnlyObjectWrapper(p.getValue());
	    }
	});

	col4.setCellFactory(table -> {
	    return new TableCell<TileMapping, TileMapping>() {

		@Override
		protected void updateItem(TileMapping item, boolean empty) {
		    super.updateItem(item, empty);
		    if (empty || item == null) {
			setText(null);
			setGraphic(null);
			setBackground(Background.EMPTY);
		    } else if (item.getAutoMapResult() != null) {
			HBox box = new HBox();

			List<Tile> results = item.getAutoMapResult().getResults();
			for (int i = 0; i < results.size(); i++) {
			    Tile tile = results.get(i);
			    Double tileDistance = item.getAutoMapResult().getResultValues().get(i);

			    VBox vBox = new VBox();
			    vBox.getChildren().add(getTableCellTileImage(tile.getImage(), tile.getType(), tile.getIndex()));
			    vBox.getChildren().add(new Label((int) ((1d - tileDistance) * 100L) + "% match"));
			    vBox.setAlignment(Pos.CENTER);
			    vBox.setCursor(Cursor.HAND);

			    vBox.setOnMouseEntered(e -> {
				vBox.setBackground(new Background(new BackgroundFill(Color.LIGHTSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
			    });
			    vBox.setOnMouseExited(e -> {
				vBox.setBackground(Background.EMPTY);
			    });

			    vBox.setOnMouseClicked(e -> {
				item.setTargetIndex(tile.getIndex());
				item.setTargetType(tile.getType());
				item.setMappingMode(TileMapping.MappingMode.Manual);
				mapping_table.refresh();
			    });

			    box.getChildren().add(vBox);
			    HBox.setMargin(vBox, new Insets(5.0d));
			}
			setGraphic(box);
		    } else {
			setGraphic(null);
		    }
		}

	    };
	});

	mapping_table.getColumns().setAll(
		col0, col1, col2, col3, col4
	);

	// display first mapping type	
	viewModel.initTilesetMappingLists();
	mapping_table.setItems(viewModel.getMappingForType(0));

	/*
	mapping_table.setOnMouseClicked(event -> {
	    showTileSelector(mapping_list.getSelectionModel().getSelectedIndex(), mapping_table.getSelectionModel().getSelectedIndex());
	});
	 */
    }

    private void showTileSelector(int selectedType, int selectedIndex) {
	ViewTuple<TileSelectionDialogView, TileSelectionDialogViewModel> tabTouple = FluentViewLoader.fxmlView(TileSelectionDialogView.class)
		.context(context)
		.providedScopes(viewModel.getMappingScope())
		.load();

	Parent content = tabTouple.getView();
	Stage showDialog = DialogHelper.showDialog(content, primaryStage);

	TileSelectionDialogView selectorView = tabTouple.getCodeBehind();
	selectorView.setDisplayingStage(showDialog);

	TileSelectionDialogViewModel selectorViewModel = tabTouple.getViewModel();
	selectorViewModel.setInitialSelection(selectedType, selectedIndex);
    }

    public static Node getTableCellTileImage(Image tile, int type, int index) {
	if (tile == null) {
	    return new Label("tile missing: " + type + "-" + index);
	}
	ImageView image = new ImageView(tile);
	image.setPreserveRatio(true);
	if (tile.getHeight() > tile.getWidth()) {
	    image.setFitHeight(60);
	} else {
	    image.setFitWidth(80);
	}

	if (TilesetMappingScope.getTileCategortyName(type).contains("SH")) {
	    StackPane sp = new StackPane(image);
	    StackPane.setMargin(image, new Insets(10d));
	    sp.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
	    return sp;
	} else {
	    return image;
	}

    }

    private void rebuildTable() {
	mapping_table.setItems(viewModel.getCurrentMappings());
	mapping_table.refresh();
    }

}
