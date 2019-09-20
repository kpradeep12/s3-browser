package net.thetechstack.service;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.thetechstack.models.AWSObject;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class AWSObjectService {
    S3Client s3Client = S3Client.builder().region(Region.US_EAST_1).build();
    String FILE_DELIMITER = "/";
    Image image = new Image(getClass().getResourceAsStream("/icons/folder.png"), 15, 15, true, true);
    ImageView folderIcon = new ImageView(image);

    public AWSObjectService(){
        //folderIcon.setCache(true);
    }

    public List<String> listBuckets(){
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
        return listBucketsResponse.buckets().stream()
                .map(Bucket::name)
                .collect(Collectors.toList());
        /*List<String> list = new ArrayList<>();
        list.add("test");
        list.add("abs");
        return list;*/
    }

    public List<Region> regions() {
        return software.amazon.awssdk.regions.Region.regions();
    }

    public List<AWSObject> listKeys(String bucket){
        List<AWSObject> objects = new ArrayList<>();

        ListObjectsRequest listObjectsRequest =
                ListObjectsRequest.builder().bucket(bucket).build();

        ListObjectsResponse listObjectsResponse = s3Client
                .listObjects(listObjectsRequest);

        listObjectsResponse.contents()
                .forEach(x -> objects.add(new AWSObject(bucket, x.key(), x.key(), x.lastModified(), x.size(), x.owner().displayName())));

        return objects;
    }
    public TreeItem<AWSObject> getSubPathsInS3Prefix(String bucketName, String prefix) {
        //if (!prefix.endsWith(FILE_DELIMITER)) {
          //  prefix += FILE_DELIMITER;
        //}
        List<String> paths = new ArrayList<String>();
        ListObjectsRequest listObjectsRequest  = ListObjectsRequest.builder()
                .bucket(bucketName).build();

        ListObjectsResponse currentListing = s3Client.listObjects(listObjectsRequest);
        //currentListing.contents().forEach(path -> paths.add(path.key()));
        //final ObjectTree tree = new ObjectTree();
        Map<String, TreeItem<AWSObject>> map = new HashMap<>();
        final TreeItem<AWSObject> tree = new TreeItem<>(new AWSObject(bucketName, bucketName));

        List<software.amazon.awssdk.services.s3.model.S3Object> objectList = new ArrayList<>();
        objectList.add(software.amazon.awssdk.services.s3.model.S3Object.builder()
            .key("serverless/resources/").lastModified(Instant.now()).size(123l).owner(Owner.builder().displayName("pradeep").build()).build());
        objectList.add(software.amazon.awssdk.services.s3.model.S3Object.builder()
                .key("serverless/src/main/net/Hello.java").lastModified(Instant.now()).size(4534l).owner(Owner.builder().displayName("pradeep").build()).build());
        objectList.add(software.amazon.awssdk.services.s3.model.S3Object.builder()
              .key("serverless/src/main/net/Algorithms.java").lastModified(Instant.now()).size(34l).owner(Owner.builder().displayName("pradeep").build()).build());
        objectList.add(software.amazon.awssdk.services.s3.model.S3Object.builder()
                .key("src/main/net/Algorithms.java").lastModified(Instant.now()).size(34l).owner(Owner.builder().displayName("pradeep").build()).build());
        objectList.add(software.amazon.awssdk.services.s3.model.S3Object.builder()
            .key("pom.xml").lastModified(Instant.now()).size(123l).owner(Owner.builder().displayName("pradeep").build()).build());
        objectList.add(software.amazon.awssdk.services.s3.model.S3Object.builder()
              .key(".github").lastModified(Instant.now()).size(44l).owner(Owner.builder().displayName("pradeep").build()).build());

        for(software.amazon.awssdk.services.s3.model.S3Object object: currentListing.contents()){
            String[] folders = object.key().split("/");
            StringBuilder key = new StringBuilder();
            //tree.getChildren().add(currentLevel);
            if(folders.length == 1) {
                tree.getChildren().add(new TreeItem<>(new AWSObject(bucketName, folders[0], object.key(), object.lastModified(), object.size(), object.owner().displayName())));
            }else{
                //tree.getChildren().indexOf(new TreeItem<>(new AWSObject(folders[0])));
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
        /*while (currentListing == null || currentListing.isTruncated()) {
            currentListing = s3Client.listNextBatchOfObjects(currentListing);
            paths.addAll(currentListing.getCommonPrefixes());
        }*/
        return tree;
    }

    public void downloadObject(String bucket, String fullKey, String key){
        String home = System.getProperty("user.home");
        /*s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(fullKey).build(),
                ResponseTransformer.toFile(Paths.get(home+"/Downloads/" + key)));*/
        Task downloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(fullKey).build(),
                        ResponseTransformer.toFile(Paths.get(home+"/Downloads/" + key)));
                return null;
            }
        };
        new Thread(downloadTask).start();
    }

    private TreeItem<AWSObject> getTreeViewItem(TreeItem<AWSObject> item , AWSObject value)
    {
        /*if (item != null && item.getValue().equals(value))
            return  item;
        for (TreeItem<AWSObject> child : item.getChildren()){
            TreeItem<AWSObject> s=getTreeViewItem(child, value);
            if(s!=null)
                return s;
        }
        return null;*/
        for (TreeItem<AWSObject> child : item.getChildren()){
            if(child.getValue().equals(value))
                return child;
        }
        return null;
    }


    /*private TreeItem<AWSObject> newTreeItem(int pathIndex, int totalPaths, String key, software.amazon.awssdk.services.s3.model.S3Object s3Object){
        if(pathIndex == 0){
            if((pathIndex+1) == totalPaths){
                return new TreeItem<>(new AWSObject(key, s3Object.lastModified(), s3Object.size(), s3Object.owner().displayName()));
            }else{
                return new TreeItem<>(new AWSObject(key));
            }
        }else{
            return new TreeItem<>();
        }
    }*/
    public static void main(String[] args) {
        AWSObjectService store = new AWSObjectService();
        store.listBuckets().forEach(System.out::println);
    }
}
