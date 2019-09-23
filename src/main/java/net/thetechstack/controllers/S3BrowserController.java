package net.thetechstack.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.VBox;
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
            object.setDownload(true);
            System.out.println(object);
            //if(object != null && object.getFullKey() != null)
              //  store.downloadObject(object);
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
        //configureTreeTableColumn(keyCol, "key", 0.6);
        keyCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.6));
        //actionsCol.setCellFactory(c -> new SimpleObjectProperty<>(getDownloadButton("")));
        keyCol.setCellValueFactory(awsObjectStringCellDataFeatures ->
                new ReadOnlyObjectWrapper<>(awsObjectStringCellDataFeatures.getValue().getValue().getKey())
        );

        keyCol.setCellFactory(new Callback<>() {
            @Override public TreeTableCell<AWSObject, String> call(TreeTableColumn<AWSObject, String> p) {
                return new TreeTableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {

                            setText(item);
                            this.getTreeTableRow().getItem().downloadProperty().addListener((observableValue, aBoolean, t1) -> {
                                        if (t1) {
                                            ProgressIndicator progressIndicator = new ProgressIndicator(0);
                                            progressIndicator.setMaxWidth(15);
                                            progressIndicator.setMaxHeight(15);
                                            if (this.getTreeTableRow().getItem() != null && this.getTreeTableRow().getItem().getFullKey() != null) {
                                                setGraphic(progressIndicator);
                                                store.downloadObject(this.getTreeTableRow().getItem(), progressIndicator);
                                            }
                                        } else {
                                            setGraphic(null);
                                        }
                                    });
                            //setGraphic(imageView);
                        } else {
                            setText(null);
                            setGraphic(null);
                        }

                        /*if (empty) {
                            setGraphic(null);
                        } else {*/
                            //Label lbl = new Label(this.getTreeTableRow().getItem().getKey());
                            //setGraphic(lbl);
                           //setText(this.getTreeTableRow().getItem().getKey());
                                /*this.getTreeTableRow().getItem().downloadProperty().addListener((observableValue, aBoolean, t1) -> {
                                    if(t1) {
                                        ProgressIndicator progressIndicator = new ProgressIndicator();
                                        progressIndicator.setMaxWidth(15);
                                        progressIndicator.setMaxHeight(15);
                                        if (this.getTreeTableRow().getItem() != null && this.getTreeTableRow().getItem().getFullKey() != null) {
                                            setGraphic(progressIndicator);
                                            store.downloadObject(this.getTreeTableRow().getItem(), progressIndicator);
                                        }
                                    }else{
                                        setGraphic(null);
                                    }
                            });
                            setGraphic(null);*/
                        //}
                    }
                };
            }
        });


        configureTreeTableColumn(lastModCol, "lastModified", 0.2);
        configureTreeTableColumn(sizeCol, "size", 0.1);
        configureTreeTableColumn(ownerCol, "owner", 0.1);
        //configureTreeTableColumn(actionsCol, "", 0.1);
        //keyCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("key"));
        //keyCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.4));
        /*actionsCol.setCellValueFactory(c -> new SimpleObjectProperty<>(getImageView(
                c.getValue().getValue().isFolder() ? "upload" : "download"
        )));*/
    }

    /*private ImageView getImageView(String icon) {
        Image image = new Image(getClass().getResourceAsStream("/icons/" + icon + ".png"), 128, 128, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setCache(true);
        imageView.setStyle("-fx-min-height: 128px;");
        return imageView;
    }*/

    private void configureTreeTableColumn(TreeTableColumn col, String name, double width){
        col.setCellValueFactory(new TreeItemPropertyValueFactory<>(name));
        col.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(width));
    }

    /*@FXML private void handleDownloadAction(ActionEvent event){
        System.out.println(event.getSource());

    }*/
}