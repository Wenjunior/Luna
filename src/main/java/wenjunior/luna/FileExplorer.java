package wenjunior.luna;

import java.io.*;
import java.util.*;
import javafx.scene.image.*;
import javafx.scene.control.*;

public class FileExplorer extends TreeView<String> {
	private TabPane tabs;

	public FileExplorer(TabPane tabs) {
		this.tabs = tabs;

		var lunaProjects = System.getProperty("user.home") + "/LunaProjects";

		var folder = new File(lunaProjects);

		if (!folder.exists()) {
			folder.mkdir();
		}

		var root = new TreeItem<String>(folder.getName());

		root.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png"))));

		setRoot(root);

		var thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					update(root, lunaProjects);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
			}
		});

		thread.setDaemon(true);

		thread.start();

		getSelectionModel().selectedItemProperty().addListener(action -> {
			getPath(lunaProjects);
		});
	}

	private void update(TreeItem<String> mother, String path) {
		var folder = new File(path);

		if (folder.isHidden()) {
			return;
		}

		var files = folder.listFiles();

		Arrays.sort(files, (file1, file2) -> {
			if (file1.isDirectory() && !file2.isDirectory()) {
				return -1;
			}

			if (!file1.isDirectory() && file2.isDirectory()) {
				return 1;
			}

			return file1.getName().compareTo(file2.getName());
		});

		for (var file : files) {
			if (!file.isDirectory() | !file.getName().equals(".git")) {
				var iterator = mother.getChildren().iterator();

				var filename = file.getName();

				var hasChildren = false;

				while (iterator.hasNext()) {
					var children = (TreeItem<String>) iterator.next();

					var fullName = children.getValue();

					if (filename.equals(fullName)) {
						hasChildren = true;
					}
				}

				if (!hasChildren) {
					var children = new TreeItem<String>(filename);

					if (file.isDirectory()) {
						update(children, String.format("%s/%s", path, filename));

						children.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png"))));
					} else {
						children.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/file.png"))));
					}

					mother.getChildren().add(children);
				}
			}
		}

		var iterator = mother.getChildren().iterator();

		ArrayList<TreeItem<String>> removeTheseChildrens = new ArrayList<>();

		while (iterator.hasNext()) {
			var children = (TreeItem<String>) iterator.next();

			var fullName = children.getValue();

			var file = new File(String.format("%s/%s", path, fullName));

			if (!file.exists()) {
				removeTheseChildrens.add(children);
			}
		}

		for (var children : removeTheseChildrens) {
			mother.getChildren().remove(children);
		}
	}

	private void getPath(String home) {
		var path = new ArrayList<String>();

		var item = (TreeItem<String>) getSelectionModel().getSelectedItem();

		path.add((String) item.getValue());

		var oldParent = item.getParent();

		if (oldParent == null) {
			return;
		}

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

		path.add(home);

		Collections.reverse(path);

		var filePath = String.join("/", path);

		var file = new File(filePath);

		if (file.isDirectory()) {
			return;
		}

		openFile(filePath);
	}

	private void openFile(String path) {
		var file = new File(path);

		Scanner scanner;

		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			MsgBox.show("Open File...", "The selected file was not found.");

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
}