package wenjunior.luna;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import javafx.concurrent.Task;
import java.nio.file.WatchEvent;
import javafx.scene.image.Image;
import java.nio.file.FileSystems;
import javafx.event.EventHandler;
import java.nio.file.WatchService;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import java.io.FileNotFoundException;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import java.nio.file.StandardWatchEventKinds;

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
	String path;

	public DirItem(File dir) {
		this.path = dir.getPath();

		Task<Void> task = new Task<>() {
			@Override
			public Void call() {
				setValue(dir.getName());

				setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png"))));

				File[] files = dir.listFiles();

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
							DirItem dirItem = new DirItem(file);

							getChildren().add(dirItem);
						} else {
							FileItem fileItem = new FileItem(file);

							getChildren().add(fileItem);
						}
					}
				}

				try {
					WatchService watchService = FileSystems.getDefault().newWatchService();

					Paths.get(path).register(
						watchService,
						StandardWatchEventKinds.ENTRY_CREATE
					);

					while (true) {
						WatchKey key = watchService.take();

						for (WatchEvent<?> event : key.pollEvents()) {
							update(event);
						}
					}
				} catch (Exception e) {}

				return null;
			}
		};

		Thread thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();
	}

	private void update(WatchEvent<?> event) {
		File dof = new File(this.path + "/" + event.context());

		if (dof.isDirectory()) {
			DirItem dirItem = new DirItem(dof);

			getChildren().add(dirItem);

			return;
		}

		FileItem fileItem = new FileItem(dof);

		getChildren().add(fileItem);
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