package com.wenderson.luna;

import java.io.*;
import java.util.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class FileExplorer extends TreeView {
	TabPane tabs;

	FileExplorer(TabPane tabs) {
		this.tabs = tabs;

		var home = new File(System.getProperty("user.home"));

		var root = new TreeItem(home.getName());

		root.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png"))));

		var thread = new Thread(new Runnable() {
			public void run() {
				appendItems(home.getPath(), root);
			}
		});

		thread.start();

		getSelectionModel().selectedItemProperty().addListener(action -> {
			var path = new ArrayList<String>();

			var item = (TreeItem) getSelectionModel().getSelectedItem();

			path.add((String) item.getValue());

			var oldParent = item.getParent();

			path.add((String) oldParent.getValue());

			while (true) {
				var parent = oldParent.getParent();

				if (parent == null) {
					break;
				}

				path.add((String) parent.getValue());

				oldParent = parent;
			}

			path.remove(path.size() - 1);

			path.add(home.getPath());

			Collections.reverse(path);

			openFile(String.join("/", path));
		});

		setRoot(root);
	}

	private void openFile(String path) {
		var file = new File(path);

		Scanner scanner;

		try {
			scanner = new Scanner(file);
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

		var codeTab = new CodeTab(file.getName(), stringBuilder.toString(), file.getPath());

		tabs.getTabs().add(codeTab);

		tabs.getSelectionModel().selectLast();
	}

	private void appendItems(String path, TreeItem mother) {
		var dir = new File(path);

		if (dir.isHidden()) {
			return;
		}

		for (var file : dir.listFiles()) {
			if (!file.isHidden()) {
				var children = new TreeItem(file.getName());

				InputStream img;

				if (file.isDirectory()) {
					appendItems(file.getPath(), children);

					img = getClass().getResourceAsStream("/icons/folder.png");
				} else {
					img = getClass().getResourceAsStream("/icons/file.png");
				}

				children.setGraphic(new ImageView(new Image(img)));

				mother.getChildren().add(children);
			}
		}
	}
}