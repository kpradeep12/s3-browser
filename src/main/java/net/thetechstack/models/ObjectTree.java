package net.thetechstack.models;

import java.util.ArrayList;

public class ObjectTree {
    private AWSObject object;
    private ArrayList<AWSObject> children;
    public ObjectTree(){
        this.children = new ArrayList<>();
    }
    public ObjectTree(AWSObject object){
        this.object = object;
        this.children = new ArrayList<>();
    }

    public AWSObject getObject() {
        return object;
    }

    public void setObject(AWSObject object) {
        this.object = object;
    }

    public ArrayList<AWSObject> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<AWSObject> children) {
        this.children = children;
    }
}
