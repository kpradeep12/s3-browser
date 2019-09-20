package net.thetechstack.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.thetechstack.models.AWSObject;
import net.thetechstack.service.AWSObjectService;

public class S3BrowserController {

    @FXML TreeTableView<AWSObject> objectTreeTable = new TreeTableView<>();
    @FXML TreeTableColumn<AWSObject, String> keyCol = new TreeTableColumn<>();
    @FXML TreeTableColumn<AWSObject, String> lastModCol = new TreeTableColumn<>();
    @FXML TreeTableColumn<AWSObject, String> sizeCol = new TreeTableColumn<>();
    @FXML TreeTableColumn<AWSObject, String> ownerCol = new TreeTableColumn<>();
    //@FXML TreeTableColumn<AWSObject, String> actionsCol = new TreeTableColumn<>();
    @FXML VBox bucketVBox = new VBox();
    @FXML ContextMenu objectMenu = new ContextMenu();
    @FXML MenuItem downloadMenu = new MenuItem();
    @FXML MenuItem uploadMenu = new MenuItem();
    AWSObjectService store = new AWSObjectService();


    @FXML public void initialize(){

        downloadMenu.setOnAction(event -> {
            AWSObject object = objectTreeTable.getSelectionModel().getSelectedItem().getValue();
            System.out.println(object.getBucket()+" - "+object.getFullKey());
            store.downloadObject(object.getBucket(), object.getFullKey(), object.getKey());
        });
        uploadMenu.setOnAction(event -> {
            System.out.println(objectTreeTable.getSelectionModel().getSelectedItem().getValue().getKey());
        });

        // Add MenuItem to ContextMenu
        //contextMenu.getItems().addAll(item1, item2);
        /*objectTreeTable.setOnContextMenuRequested(event -> {
            System.out.println(event.getTarget());
            objectMenu.show(objectTreeTable, event.getScreenX(), event.getScreenY());

        });*/
        //System.out.println(getClass().getResource("/icons"));
        //store.getSubPathsInS3Prefix("test", "/");
        //store.getSubPathsInS3Prefix("aws-java-maven-dev-serverlessdeploymentbucket-hht1tg0dpfyr", "/")
          //      .forEach(System.out::println);
        //regionChoiceBox.setItems(FXCollections.observableArrayList(store.regions()));
        //regionChoiceBox.getSelectionModel().select(Region.US_EAST_1);
        store.listBuckets().stream().forEach(bucket -> {
            Hyperlink link = new Hyperlink();
            link.setText(bucket);
            link.getStyleClass().add("bucket-link");
            link.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    //objectTable.setItems(FXCollections.observableList(store.listKeys(bucket)));
                    objectTreeTable.setRoot(store.getSubPathsInS3Prefix(bucket, "/"));
                }
            });
            bucketVBox.getChildren().add(link);
        });
        configureTreeTableColumn(keyCol, "key", 0.5);
        configureTreeTableColumn(lastModCol, "lastModified", 0.2);
        configureTreeTableColumn(sizeCol, "size", 0.1);
        configureTreeTableColumn(ownerCol, "owner", 0.2);
        //configureTreeTableColumn(actionsCol, "", 0.1);
        //keyCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("key"));
        //keyCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.4));
        //lastModCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastModified"));
        //lastModCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.2));
        //sizeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));
        //sizeCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.1));
        //ownerCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("owner"));
        //ownerCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.2));
        /*actionsCol.setCellValueFactory(c -> new SimpleObjectProperty<>(getImageView(
                c.getValue().getValue().isFolder() ? "upload" : "download"
        )));*/
        //actionsCol.setSortable(false);
        //actionsCol.setCellFactory(c -> new SimpleObjectProperty<>(getDownloadButton("")));
        /*actionsCol.setCellFactory(new Callback<>() {
            @Override
            public TreeTableCell<AWSObject, String> call(TreeTableColumn<AWSObject, String> p) {
                TreeTableCell<AWSObject, String> cell = new TreeTableCell<>() {
                    private Button button = new Button();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            button.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/download.png"), 12, 12, true, true)));
                            button.setPadding(new Insets(0, 5, 0, 5));
                            setGraphic(button);
                            button.setOnAction(event -> {
                                System.out.println(objectTreeTable.getSelectionModel().getSelectedItem().getValue().getKey());
                            });
                        }
                    }
                };
                return cell;
            }
        });*/


        //actionsCol.setCellValueFactory(c -> new PropertyValueFactory<>("Test"));
        //actionsCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.1));
        //objectTreeTable.getColumns().addAll(col1, col2, col3, col4, col5);

    }

    private ImageView getImageView(String icon) {
        Image image = new Image(getClass().getResourceAsStream("/icons/" + icon + ".png"), 128, 128, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setCache(true);
        imageView.setStyle("-fx-min-height: 128px;");
        return imageView;
    }
    private Button getDownloadButton(String icon) {
        Image image = new Image(getClass().getResourceAsStream("/icons/download.png"), 12, 12, true, true);
        Button button = new Button();
        button.setGraphic(new ImageView(image));
        button.setPadding(new Insets(0, 5, 0, 5));
        return button;
    }

    private void configureTreeTableColumn(TreeTableColumn col, String name, double width){
        col.setCellValueFactory(new TreeItemPropertyValueFactory<>(name));
        col.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(width));
    }

    /*@FXML private void handleDownloadAction(ActionEvent event){
        System.out.println(event.getSource());

    }*/
}
/*
private class AddPersonCell extends TreeCell<AWSObject> {
    // a button for adding a new person.
    final Button addButton       = new Button("Add");
    // pads and centers the add button in the cell.
    final StackPane paddedButton = new StackPane();
    // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
    final DoubleProperty buttonY = new SimpleDoubleProperty();

    AddPersonCell(final TreeTableView table) {
        paddedButton.setPadding(new Insets(3));
        paddedButton.getChildren().add(addButton);
        addButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                buttonY.set(mouseEvent.getScreenY());
            }
        });
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                //showAddPersonDialog(stage, table, buttonY.get());
                System.out.println("Button clicked");
                //table.getSelectionModel().select(getTableRow().getIndex());
            }
        });
    }

    @Override protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(paddedButton);
        }
    }
}*/