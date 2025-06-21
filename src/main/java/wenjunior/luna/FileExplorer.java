package wenjunior.luna;

import java.io.*;
import java.util.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.event.EventHandler;

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

class FolderItem extends TreeItem<String> {
	public FolderItem(File folder) {
		setValue(folder.getName());

		setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png"))));

		var task = new Task<Void>() {
			@Override
			public Void call() {
				var files = folder.listFiles();

				Arrays.sort(files, (file1, file2) -> {
					if (!file1.isDirectory() && file2.isDirectory()) {
						return 1;
					}

					if (file1.isDirectory() && !file2.isDirectory()) {
						return -1;
					}

					return file1.getName().compareTo(file2.getName());
				});

				for (var file : files) {
					if (!file.isDirectory() | !file.getName().equals(".git")) {
						TreeItem<String> item;

						if (file.isDirectory()) {
							item = new FolderItem(file);
						} else {
							item = new FileItem(file);
						}

						getChildren().add(item);
					}
				}

				return null;
			}
		};

		var thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();
	}
}

public class FileExplorer extends TreeView<String> {
	private TabPane tabs;

	public FileExplorer(TabPane tabs) {
		this.tabs = tabs;

		var rootFolder = new File(System.getProperty("user.home") + "/LunaProjects");

		if (!rootFolder.exists() || !rootFolder.isDirectory()) {
			rootFolder.mkdir();
		}

		var root = new FolderItem(rootFolder);

		root.setExpanded(true);

		setRoot(root);

		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
					var selectedItem = getSelectionModel().getSelectedItem();

					if (selectedItem instanceof FileItem) {
						var selectedFile = (FileItem) selectedItem;

						openFile(selectedFile.getPath());

						/*
							A linha de código a seguir evita que logo após selecionar um arquivo e abrir uma tab,
							uma nova tab seja desnecessariamente aberta quando o usuário não clicar no arquivo.
							Isso acontece porque a função setOnMouseClicked não consegue detectar cliques somente
							quando um arquivo for selecionado.
						*/

						getSelectionModel().clearSelection();
					}
				}
			}
		});
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

		this.tabs.getTabs().add(codeTab);

		this.tabs.getSelectionModel().selectLast();
	}
}