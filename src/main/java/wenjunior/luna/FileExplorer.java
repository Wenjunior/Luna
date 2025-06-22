package wenjunior.luna;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.event.EventHandler;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import java.io.FileNotFoundException;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;

class FileItem extends TreeItem<String> {
	private String path;

	public FileItem(File file) {
		setValue(file.getName());

		setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/file.png"))));

		this.path = file.getPath();
	}

	public String getPath() {
		return this.path;
	}
}

class DirItem extends TreeItem<String> {
	public DirItem(File folder) {
		Task<Void> task = new Task<>() {
			@Override
			public Void call() {
				setValue(folder.getName());

				setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png"))));

				File[] files = folder.listFiles();

				Arrays.sort(files, (file1, file2) -> {
					if (!file1.isDirectory() && file2.isDirectory()) {
						return 1;
					}

					if (file1.isDirectory() && !file2.isDirectory()) {
						return -1;
					}

					return file1.getName().compareTo(file2.getName());
				});

				for (File file : files) {
					if (!file.isDirectory() | !file.getName().equals(".git")) {
						if (file.isDirectory()) {
							DirItem folderItem = new DirItem(file);

							getChildren().add(folderItem);
						} else {
							FileItem fileItem = new FileItem(file);

							getChildren().add(fileItem);
						}
					}
				}

				return null;
			}
		};

		Thread thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();
	}
}

public class FileExplorer extends TreeView<String> {
	private TabPane tabs;

	public FileExplorer(TabPane tabs) {
		this.tabs = tabs;

		File rootDir = new File(System.getProperty("user.home") + "/LunaProjects");

		if (!rootDir.exists() || !rootDir.isDirectory()) {
			rootDir.mkdir();
		}

		DirItem root = new DirItem(rootDir);

		root.setExpanded(true);

		setRoot(root);

		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
					TreeItem<String> selectedItem = getSelectionModel().getSelectedItem();

					if (selectedItem instanceof FileItem) {
						FileItem selectedFile = (FileItem) selectedItem;

						openFile(selectedFile.getPath());

						getSelectionModel().clearSelection();
					}
				}
			}
		});
	}

	private void openFile(String path) {
		File file = new File(path);

		Scanner reader;

		try {
			reader = new Scanner(file);
		} catch (FileNotFoundException e) {
			MsgBox.show("Warning", "The selected file was not found.");

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

		CodeTab codeTab = new CodeTab(file.getName(), lines.toString(), file.getPath());

		this.tabs.getTabs().add(codeTab);

		this.tabs.getSelectionModel().selectLast();
	}
}