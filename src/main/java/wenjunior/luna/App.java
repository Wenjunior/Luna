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

	private enum Events {SAVE, SAVE_AS, CUT, COPY, PASTE, UNDO, REDO, FIND, REPLACE};

	@Override
	public void start(Stage stage) {
		stage.setTitle("Luna");

		stage.setMaximized(true);

		MenuItem newFile = new MenuItem("New File");

		newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN));

		newFile.setOnAction(event -> {
			newFile();
		});

		MenuItem openFile = new MenuItem("Open File...");

		openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN));

		openFile.setOnAction(event -> {
			openFile();
		});

		MenuItem save = new MenuItem("Save");

		save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN));

		save.setOnAction(event -> {
			actionPerformed(Events.SAVE);
		});

		MenuItem saveAs = new MenuItem("Save As...");

		saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

		saveAs.setOnAction(event -> {
			actionPerformed(Events.SAVE_AS);
		});

		MenuItem exit = new MenuItem("Exit");

		exit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN));

		exit.setOnAction(event -> {
			stop();
		});

		Menu file = new Menu("File");

		file.getItems().addAll(newFile, openFile, save, saveAs, exit);

		MenuItem cut = new MenuItem("Cut");

		cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN));

		cut.setOnAction(event -> {
			actionPerformed(Events.CUT);
		});

		MenuItem copy = new MenuItem("Copy");

		copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN));

		copy.setOnAction(event -> {
			actionPerformed(Events.COPY);
		});

		MenuItem paste = new MenuItem("Paste");

		paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN));

		paste.setOnAction(event -> {
			actionPerformed(Events.PASTE);
		});

		MenuItem undo = new MenuItem("Undo");

		undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN));

		undo.setOnAction(event -> {
			actionPerformed(Events.UNDO);
		});

		MenuItem redo = new MenuItem("Redo");

		redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN));

		redo.setOnAction(event -> {
			actionPerformed(Events.REDO);
		});

		MenuItem find = new MenuItem("Find...");

		find.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN));

		find.setOnAction(event -> {
			actionPerformed(Events.FIND);
		});

		MenuItem replace = new MenuItem("Replace...");

		replace.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCodeCombination.CONTROL_DOWN));

		replace.setOnAction(event -> {
			actionPerformed(Events.REPLACE);
		});

		Menu edit = new Menu("Edit");

		edit.getItems().addAll(cut, copy, paste, undo, redo, find, replace);

		MenuBar menuBar = new MenuBar();

		menuBar.getMenus().addAll(file, edit);

		BorderPane borderPane = new BorderPane();

		borderPane.setTop(menuBar);

		FileExplorer fileExplorer = new FileExplorer(this.tabs);

		borderPane.setLeft(fileExplorer);

		borderPane.setCenter(this.tabs);

		Scene scene = new Scene(borderPane, 1280, 720);

		scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

		scene.getStylesheets().add(getClass().getResource("/css/dracula-color-scheme.css").toExternalForm());

		stage.setScene(scene);

		stage.show();
	}

	private void newFile() {
		CodeTab codeTab = new CodeTab();

		this.tabs.getTabs().add(codeTab);

		this.tabs.getSelectionModel().selectLast();
	}

	private void openFile() {
		FileChooser fileChooser = new FileChooser();

		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile == null) {
			return;
		}

		for (Tab tab : this.tabs.getTabs()) {
			CodeTab codeTab = (CodeTab) tab;

			if (selectedFile.getPath().equals(codeTab.getPath())) {
				int index = this.tabs.getTabs().indexOf(codeTab);

				this.tabs.getSelectionModel().select(index);

				return;
			}
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
				lines.append('\n');
			}
		}

		reader.close();

		CodeTab codeTab = new CodeTab(selectedFile.getName(), lines.toString(), selectedFile.getPath());

		this.tabs.getTabs().add(codeTab);

		this.tabs.getSelectionModel().selectLast();
	}

	private void actionPerformed(Events event) {
		int selectedIndex = this.tabs.getSelectionModel().getSelectedIndex();

		if (selectedIndex == -1) {
			return;
		}

		CodeTab codeTab = (CodeTab) this.tabs.getTabs().get(selectedIndex);

		switch (event) {
		case SAVE:
			codeTab.save();

			break;
		case SAVE_AS:
			codeTab.saveAs();

			break;
		case CUT:
			codeTab.cut();

			break;
		case COPY:
			codeTab.copy();

			break;
		case PASTE:
			codeTab.paste();

			break;
		case UNDO:
			codeTab.undo();

			break;
		case REDO:
			codeTab.redo();

			break;
		case FIND:
			codeTab.find();

			break;
		case REPLACE:
			codeTab.replace();

			break;
		}
	}

	@Override
	public void stop() {
		for (Tab tab : this.tabs.getTabs()) {
			CodeTab codeTab = (CodeTab) tab;

			codeTab.stopAsyncHighlighting();
		}

		System.exit(0);
	}

	public static void main(String[] args) {
		launch();
	}
}