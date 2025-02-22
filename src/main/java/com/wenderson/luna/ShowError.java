package com.wenderson.luna;

import javafx.scene.control.Dialog;

public class ShowError {
    static void show(String msg) {
        var dialog = new Dialog<>();
        
        dialog.setTitle("ERROR");
        
        dialog.setContentText(msg);
        
        dialog.showAndWait();
    }
}