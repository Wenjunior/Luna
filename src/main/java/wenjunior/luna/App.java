package wenjunior.luna;

import java.io.File;
import java.util.Scanner;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCode;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import java.io.FileNotFoundException;
import javafx.scene.control.MenuItem;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyCodeCombination;

public class App extends Application {
	private TabPane tabs = new TabPane();

	@Override
	public void start(Stage stage) {
		stage.setTitle("Luna");

		stage.setMaximized(true);

		MenuItem newFile = new MenuItem("New File");

		newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN));

		newFile.setOnAction(action -> {
			newFile();
		});

		MenuItem openFile = new MenuItem("Open File...");

		openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN));

		openFile.setOnAction(action -> {
			openFile();
		});

		MenuItem save = new MenuItem("Save");

		save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN));

		save.setOnAction(action -> {
			actionPerformed("Save");
		});

		MenuItem saveAs = new MenuItem("Save As...");

		saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

		saveAs.setOnAction(action -> {
			actionPerformed("Save As...");
		});

		MenuItem exit = new MenuItem("Exit");

		exit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN));

		exit.setOnAction(action -> {
			stop();
		});

		Menu file = new Menu("File");

		file.getItems().addAll(newFile, openFile, save, saveAs, exit);

		MenuItem cut = new MenuItem("Cut");

		cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN));

		cut.setOnAction(action -> {
			actionPerformed("Cut");
		});

		MenuItem copy = new MenuItem("Copy");

		copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN));

		copy.setOnAction(action -> {
			actionPerformed("Cut");
		});

		MenuItem paste = new MenuItem("Paste");

		paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN));

		paste.setOnAction(action -> {
			actionPerformed("Paste");
		});

		MenuItem undo = new MenuItem("Undo");

		undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN));

		undo.setOnAction(action -> {
			actionPerformed("Undo");
		});

		MenuItem redo = new MenuItem("Redo");

		redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN));

		redo.setOnAction(action -> {
			actionPerformed("Redo");
		});

		MenuItem find = new MenuItem("Find...");

		find.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN));

		find.setOnAction(action -> {
			actionPerformed("Find...");
		});

		MenuItem replace = new MenuItem("Replace...");

		replace.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCodeCombination.CONTROL_DOWN));

		replace.setOnAction(action -> {
			actionPerformed("Replace...");
		});

		Menu edit = new Menu("Edit");

		edit.getItems().addAll(cut, copy, paste, undo, redo, find, replace);

		MenuBar menuBar = new MenuBar();

		menuBar.getMenus().addAll(file, edit);

		BorderPane borderPane = new BorderPane();

		borderPane.setTop(menuBar);

		FileExplorer fileExplorer = new FileExplorer(tabs);

		borderPane.setLeft(fileExplorer);

		borderPane.setCenter(tabs);

		Scene scene = new Scene(borderPane, 1280, 720);

		scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

		scene.getStylesheets().add(getClass().getResource("/css/dracula-theme.css").toExternalForm());

		stage.setScene(scene);

		stage.show();
	}

	private void newFile() {
		CodeTab codeTab = new CodeTab();

		tabs.getTabs().add(codeTab);

		tabs.getSelectionModel().selectLast();
	}

	private void openFile() {
		FileChooser fileChooser = new FileChooser();

		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile == null) {
			return;
		}

		Scanner reader;

		try {
			reader = new Scanner(selectedFile);
		} catch (FileNotFoundException e) {
			MsgBox.show("Open File...", "The selected file was not found.");

			return;
		}

		String line;

		StringBuilder lines = new StringBuilder();

		while (reader.hasNextLine()) {
			line = reader.nextLine();

			lines.append(line);

			if (reader.hasNextLine()) {
				lines.append("\n");
			}
		}

		reader.close();

		CodeTab codeTab = new CodeTab(selectedFile.getName(), lines.toString(), selectedFile.getPath());

		tabs.getTabs().add(codeTab);

		tabs.getSelectionModel().selectLast();
	}

	private void actionPerformed(String action) {
		int selectedIndex = tabs.getSelectionModel().getSelectedIndex();

		if (selectedIndex == -1) {
			return;
		}

		CodeTab codeTab = (CodeTab) tabs.getTabs().get(selectedIndex);

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
		case "Replace...":
			codeTab.replace();

			break;
		}
	}

	@Override
	public void stop() {
		for (Tab tab : tabs.getTabs()) {
			CodeTab codeTab = (CodeTab) tab;

			codeTab.stopAsyncHighlighting();
		}

		System.exit(0);
	}

	public static void main(String[] args) {
		launch();
	}
}