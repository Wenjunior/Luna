package com.wenderson.luna;

import javafx.scene.control.*;

public class MsgBox {
    static void show(String title, String msg) {
        var dialog = new Dialog<>();
        
        dialog.setTitle(title);
        
        dialog.setContentText(msg);
        
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        
        dialog.showAndWait();
    }
}