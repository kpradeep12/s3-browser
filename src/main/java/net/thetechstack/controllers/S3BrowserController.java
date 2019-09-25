package net.thetechstack.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import net.thetechstack.models.AWSObject;
import net.thetechstack.service.AWSObjectService;

import java.util.List;

public class S3BrowserController {

    @FXML TreeTableView<AWSObject> objectTreeTable = new TreeTableView<>();
    @FXML TreeTableColumn<AWSObject, String> keyCol = new TreeTableColumn<>();
    @FXML TreeTableColumn<AWSObject, String> lastModCol = new TreeTableColumn<>();
    @FXML TreeTableColumn<AWSObject, String> sizeCol = new TreeTableColumn<>();
    @FXML TreeTableColumn<AWSObject, String> ownerCol = new TreeTableColumn<>();

    @FXML VBox bucketVBox = new VBox();
    @FXML ContextMenu objectMenu = new ContextMenu();
    @FXML MenuItem downloadMenu = new MenuItem();
    AWSObjectService store = new AWSObjectService();


    @FXML public void initialize(){

        downloadMenu.setOnAction(event -> {
            if(objectTreeTable.getSelectionModel().getSelectedItem() != null) {
                AWSObject object = objectTreeTable.getSelectionModel().getSelectedItem().getValue();
                if (!object.isFolder())
                    object.setDownload(true);
            }
        });

        Task<List<String>> buckets = store.listBuckets();
        buckets.setOnSucceeded(t ->
                buckets.getValue().forEach(bucket -> {
                Hyperlink link = new Hyperlink();
                link.setText(bucket);
                link.getStyleClass().add("bucket-link");
                link.setOnAction(e -> {
                    Task<TreeItem<AWSObject>> listTask = store.getSubPathsInS3Prefix(bucket, "/");
                    listTask.setOnSucceeded(root -> {
                        objectTreeTable.setRoot(listTask.getValue());
                    });
                });
                bucketVBox.getChildren().add(link);
        }));

        keyCol.setReorderable(false);
        keyCol.prefWidthProperty().bind(objectTreeTable.widthProperty().multiply(0.6));
        keyCol.setCellValueFactory(awsObjectStringCellDataFeatures -> new ReadOnlyObjectWrapper<>(awsObjectStringCellDataFeatures.getValue().getValue().getKey()));
        keyCol.setCellFactory(new Callback<>() {
            @Override
            public TreeTableCell<AWSObject, String> call(TreeTableColumn<AWSObject, String> tableColumn) {
                return new TreeTableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            setText(item);
                            this.getTreeTableRow().getItem().downloadProperty().addListener((observableValue, aBoolean, t1) -> {
                                if (t1) {
                                    if (this.getTreeTableRow().getItem() != null && this.getTreeTableRow().getItem().getFullKey() != null) {
                                        ProgressIndicator progressIndicator = new ProgressIndicator(0);
                                        progressIndicator.setMaxWidth(18);
                                        progressIndicator.setMaxHeight(18);
                                        setGraphic(progressIndicator);
                                        store.downloadObject(this.getTreeTableRow().getItem(), progressIndicator);
                                    }
                                } else {
                                    setGraphic(null);
                                }
                            });
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
            }
        });

        configureTreeTableColumn(lastModCol, "lastModified", 0.2);
        configureTreeTableColumn(sizeCol, "size", 0.1);
        configureTreeTableColumn(ownerCol, "owner", 0.1);
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
        col.setReorderable(false);
    }

    /*@FXML private void handleDownloadAction(ActionEvent event){}*/
}