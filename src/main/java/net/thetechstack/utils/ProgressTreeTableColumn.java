package net.thetechstack.utils;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import net.thetechstack.models.AWSObject;

public class ProgressTreeTableColumn extends TreeTableCell<AWSObject, String> {
    final ProgressBar progress = new ProgressBar();

    @Override
    protected void updateItem(String t, boolean empty) {
        super.updateItem(t, empty);
        if (!empty) {
            setGraphic(progress);
        }
    }
}
