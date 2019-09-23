package net.thetechstack.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AWSObject {
    private SimpleStringProperty bucket;
    private SimpleStringProperty key;
    private SimpleStringProperty fullKey;
    private SimpleStringProperty lastModified;
    private SimpleLongProperty size;
    private SimpleStringProperty owner;
    private SimpleBooleanProperty folder;
    private SimpleBooleanProperty download = new SimpleBooleanProperty(false);

    public AWSObject(String bucket, String key){
        this.bucket = new SimpleStringProperty(bucket);
        this.key = new SimpleStringProperty(key);
        this.folder = new SimpleBooleanProperty(true);
    }

    public AWSObject(String bucket, String key, String fullKey, Instant lastModified, Long size, String owner) {
        this.bucket = new SimpleStringProperty(bucket);
        this.fullKey = new SimpleStringProperty(fullKey);
        this.key = new SimpleStringProperty(key);
        this.lastModified = new SimpleStringProperty(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(lastModified));
        this.size = new SimpleLongProperty(size);
        this.owner = new SimpleStringProperty(owner);
        this.folder = new SimpleBooleanProperty(false);
    }

    public String getKey() {
        return key.get();
    }

    public SimpleStringProperty keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getBucket() {
        return bucket.get();
    }

    public SimpleStringProperty bucketProperty() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket.set(bucket);
    }

    public String getFullKey() {
        return fullKey == null ? null : fullKey.get();
    }

    public SimpleStringProperty fullKeyProperty() {
        return fullKey;
    }

    public void setFullKey(String fullKey) {
        this.fullKey.set(fullKey);
    }

    public boolean isDownload() {
        return download.get();
    }

    public SimpleBooleanProperty downloadProperty() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download.set(download);
    }

    public String getLastModified() {
        return lastModified.get();
    }

    public SimpleStringProperty lastModifiedProperty() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified.set(lastModified);
    }

    public long getSize() {
        return size.get();
    }

    public SimpleLongProperty sizeProperty() {
        return size;
    }

    public void setSize(long size) {
        this.size.set(size);
    }

    public String getOwner() {
        return owner.get();
    }

    public SimpleStringProperty ownerProperty() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public boolean isFolder() {
        return folder.get();
    }

    public SimpleBooleanProperty folderProperty() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder.set(folder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AWSObject s3Object = (AWSObject) o;

        return key.get().equals(s3Object.key.get());
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
