package com.wenderson.luna;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MsgBox {
	public static void show(String title, String msg) {
		var alert = new Alert(AlertType.WARNING);

		alert.setTitle(title);

		alert.setHeaderText(null);

		alert.setContentText(msg);

		alert.getDialogPane().getStylesheets().add(MsgBox.class.getResource("/css/dark-theme.css").toString());

		alert.getDialogPane().getStyleClass().add("msg-box");

		alert.showAndWait();
	}
}