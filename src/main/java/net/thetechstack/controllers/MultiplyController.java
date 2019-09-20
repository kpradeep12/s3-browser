package net.thetechstack.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MultiplyController {
    @FXML
    TextField myTextField;
    @FXML TextField resultTextField;

    @FXML
    protected void handleButton(ActionEvent event){
        int value = Integer.parseInt(myTextField.getText());
        resultTextField.setText(Integer.toString(value * 5));
    }
}
