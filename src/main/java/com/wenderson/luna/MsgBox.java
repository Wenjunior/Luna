package com.wenderson.luna;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MsgBox {
	public static void show(String title, String msg) {
		var msgBox = new Alert(AlertType.WARNING);

		msgBox.setTitle(title);

		msgBox.setHeaderText(null);

		msgBox.setContentText(msg);

		msgBox.getDialogPane().getStylesheets().add(MsgBox.class.getResource("/css/dark-theme.css").toString());

		msgBox.getDialogPane().getStyleClass().add("msg-box");

		msgBox.showAndWait();
	}
}