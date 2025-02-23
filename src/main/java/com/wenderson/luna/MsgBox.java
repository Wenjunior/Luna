package com.wenderson.luna;

import javafx.scene.control.Dialog;

public class MsgBox {
    static void show(String title, String msg) {
        var dialog = new Dialog<>();
        
        dialog.setTitle(title);
        
        dialog.setContentText(msg);
        
        dialog.showAndWait();
    }
}