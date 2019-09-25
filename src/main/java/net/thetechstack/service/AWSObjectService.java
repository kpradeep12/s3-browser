package net.thetechstack.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import net.thetechstack.models.AWSObject;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class AWSObjectService {
    S3Client s3Client = S3Client.builder().region(Region.US_EAST_1).build();

    public Task<List<String>> listBuckets(){
        Task<List<String>> bucketListTask = new Task<>() {
            @Override
            protected List<String> call() {
                ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
                ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
                return listBucketsResponse.buckets().stream()
                        .map(Bucket::name)
                        .collect(Collectors.toList());
            }
        };
        new Thread(bucketListTask).start();
        return bucketListTask;
    }

    public List<Region> regions() {
        return software.amazon.awssdk.regions.Region.regions();
    }

    public Task<ObservableList<AWSObject>> listKeys(String bucket){
        Task<ObservableList<AWSObject>> keysListTask = new Task<>() {
            @Override
            protected ObservableList<AWSObject> call() {
                ObservableList<AWSObject> objects = FXCollections.observableArrayList();
                ListObjectsRequest listObjectsRequest =
                        ListObjectsRequest.builder().bucket(bucket).build();
                ListObjectsResponse listObjectsResponse = s3Client
                        .listObjects(listObjectsRequest);
                listObjectsResponse.contents()
                        .forEach(x -> objects.add(new AWSObject(bucket, x.key(), x.key(), x.lastModified(), x.size(), x.owner().displayName())));
                return objects;
            }
        };
        new Thread(keysListTask).start();
        return keysListTask;
    }
    public Task<TreeItem<AWSObject>> getSubPathsInS3Prefix(String bucketName, String prefix) {
        Task<TreeItem<AWSObject>> keysListTask = new Task<>() {
            @Override
            protected TreeItem<AWSObject> call() {
                ListObjectsRequest listObjectsRequest  = ListObjectsRequest.builder().bucket(bucketName).build();
                ListObjectsResponse currentListing = s3Client.listObjects(listObjectsRequest);
                final TreeItem<AWSObject> tree = new TreeItem<>(new AWSObject(bucketName, bucketName));

                for(software.amazon.awssdk.services.s3.model.S3Object object: currentListing.contents()){
                    String[] folders = object.key().split("/");
                    StringBuilder key = new StringBuilder();
                    if(folders.length == 1) {
                        tree.getChildren().add(new TreeItem<>(new AWSObject(bucketName, folders[0], object.key(), object.lastModified(), object.size(), object.owner().displayName())));
                    }else{
                        TreeItem<AWSObject> currentLevel = tree;
                        for(int i=0; i<folders.length; i++) {
                            TreeItem<AWSObject> node = getTreeViewItem(currentLevel, new AWSObject(bucketName, folders[i]));
                            if(node == null){
                                TreeItem<AWSObject> newNode =(i+1 == folders.length) ? new TreeItem<>(new AWSObject(bucketName, folders[i], object.key(), object.lastModified(), object.size(), object.owner().displayName())) : new TreeItem<>(new AWSObject(bucketName, folders[i]));
                                currentLevel.getChildren().add(newNode);
                                currentLevel = newNode;
                            }else{
                                currentLevel = node;
                            }
                        }
                    }
                }
                return tree;
            }
        };
        new Thread(keysListTask).start();
        return keysListTask;
    }

    public void downloadObject(AWSObject object, ProgressIndicator progressIndicator){
        String home = System.getProperty("user.home");
        Task downloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long max = object.getSize();
                long currentSize = 0L;
                updateProgress(currentSize, max);
                ResponseInputStream<GetObjectResponse> input = s3Client.getObject(GetObjectRequest.builder().bucket(object.getBucket()).key(object.getFullKey()).build());
                FileOutputStream fos = new FileOutputStream(new File(home+"/Downloads/" + object.getKey()));

                byte[] read_buf = new byte[4096];
                int read_len;
                while ((read_len = input.read(read_buf)) > 0) {
                    fos.write(read_buf, 0, read_len);
                    currentSize = currentSize + read_len;
                    updateProgress(currentSize, max);
                }
                fos.close();
                input.close();
                return null;
            }
            @Override protected void succeeded() {
                object.setDownload(false);
            }
        };
        progressIndicator.progressProperty().bind(downloadTask.progressProperty());
        new Thread(downloadTask).start();
    }

    private TreeItem<AWSObject> getTreeViewItem(TreeItem<AWSObject> item , AWSObject value)
    {
        for (TreeItem<AWSObject> child : item.getChildren()){
            if(child.getValue().equals(value))
                return child;
        }
        return null;
    }
}
