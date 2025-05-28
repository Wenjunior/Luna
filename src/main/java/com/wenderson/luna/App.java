package com.wenderson.luna;

import javafx.stage.*;
import java.util.Scanner;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.control.*;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;

public class App extends Application {
	private TabPane tabs = new TabPane();

	@Override
	public void start(Stage stage) {
		stage.setTitle("Luna");

		stage.setMaximized(true);

		var newFile = new MenuItem("New File");

		newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN));

		newFile.setOnAction(action -> {
			newFile();
		});

		var openFile = new MenuItem("Open File...");

		openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN));

		openFile.setOnAction(action -> {
			openFile();
		});

		var save = new MenuItem("Save");

		save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN));

		save.setOnAction(action -> {
			actionPerformed("Save");
		});

		var saveAs = new MenuItem("Save As...");

		saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

		saveAs.setOnAction(action -> {
			actionPerformed("Save As...");
		});

		var exit = new MenuItem("Exit");

		exit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN));

		exit.setOnAction(action -> {
			System.exit(0);
		});

		var file = new Menu("File");

		file.getItems().addAll(newFile, openFile, save, saveAs, exit);

		var cut = new MenuItem("Cut");

		cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN));

		cut.setOnAction(action -> {
			actionPerformed("Cut");
		});

		var copy = new MenuItem("Copy");

		copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN));

		copy.setOnAction(action -> {
			actionPerformed("Cut");
		});

		var paste = new MenuItem("Paste");

		paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN));

		paste.setOnAction(action -> {
			actionPerformed("Paste");
		});

		var undo = new MenuItem("Undo");

		undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN));

		undo.setOnAction(action -> {
			actionPerformed("Undo");
		});

		var redo = new MenuItem("Redo");

		redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN));

		redo.setOnAction(action -> {
			actionPerformed("Redo");
		});

		var find = new MenuItem("Find...");

		find.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN));

		find.setOnAction(action -> {
			actionPerformed("Find...");
		});

		var edit = new Menu("Edit");

		edit.getItems().addAll(cut, copy, paste, undo, redo, find);

		var menuBar = new MenuBar();

		menuBar.getMenus().addAll(file, edit);

		var borderPane = new BorderPane();

		borderPane.setTop(menuBar);

		var fileExplorer = new FileExplorer(tabs);

		borderPane.setLeft(fileExplorer);

		borderPane.setCenter(tabs);

		var scene = new Scene(borderPane, 1280, 720);

		scene.getStylesheets().add(getClass().getResource("/css/dark-mode.css").toExternalForm());

		scene.getStylesheets().add(getClass().getResource("/css/luna-theme.css").toExternalForm());

		stage.setScene(scene);

		stage.show();
	}

	private void newFile() {
		var codeTab = new CodeTab();

		tabs.getTabs().add(codeTab);

		tabs.getSelectionModel().selectLast();
	}

	private void openFile() {
		var fileChooser = new FileChooser();

		var selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile == null) {
			return;
		}

		Scanner scanner;

		try {
			scanner = new Scanner(selectedFile);
		} catch (FileNotFoundException e) {
			MsgBox.show("Open File...", "O arquivo selecionado não foi encontrado.");

			return;
		}

		String line;

		var stringBuilder = new StringBuilder();

		while (scanner.hasNextLine()) {
			line = scanner.nextLine();

			stringBuilder.append(line);

			if (scanner.hasNextLine()) {
				stringBuilder.append("\n");
			}
		}

		scanner.close();

		var codeTab = new CodeTab(selectedFile.getName(), stringBuilder.toString(), selectedFile.getPath());

		tabs.getTabs().add(codeTab);

		tabs.getSelectionModel().selectLast();
	}

	private void actionPerformed(String action) {
		var selectedIndex = tabs.getSelectionModel().getSelectedIndex();

		if (selectedIndex == -1) {
			return;
		}

		var codeTab = (CodeTab) tabs.getTabs().get(selectedIndex);

		switch (action) {
		case "Save":
			codeTab.save();

			break;
		case "Save As...":
			codeTab.saveAs();

			break;
		case "Cut":
			codeTab.cut();

			break;
		case "Copy":
			codeTab.copy();

			break;
		case "Paste":
			codeTab.paste();

			break;
		case "Undo":
			codeTab.undo();

			break;
		case "Redo":
			codeTab.redo();

			break;
		case "Find...":
			codeTab.find();

			break;
		}
	}

	// Adicionei essa função porque quando a janela principal é fechada, a thread do explorador de arquivos mantém o programa rodando em segundo plano.

	@Override
	public void stop() {
		System.exit(0);
	}

	public static void main(String[] args) {
		launch();
	}
}