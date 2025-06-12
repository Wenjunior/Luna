package com.wenderson.luna;

import java.io.*;
import java.util.*;
import javafx.util.Pair;
import java.time.Duration;
import org.fxmisc.richtext.*;
import java.util.concurrent.*;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import java.util.regex.Pattern;
import javafx.stage.FileChooser;
import org.fxmisc.richtext.model.*;
import javafx.scene.layout.GridPane;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class CodeTab extends Tab {
	private String name = "Untitled";

	private CodeArea codeArea = new CodeArea();

	private String path = null;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private Highlighter highlighter = new Highlighter();

	public CodeTab() {
		setText(this.name);

		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		codeArea.textProperty().addListener((obs, oldCode, newCode) -> {
			setText(this.name + " *");
		});

		var scrollPane = new VirtualizedScrollPane<>(codeArea);

		setContent(scrollPane);

		codeArea.multiPlainChanges()
			.successionEnds(Duration.ofMillis(10))
				.retainLatestUntilLater(executorService)
					.supplyTask(this::computeHighlightingAsync)
						.awaitLatest(codeArea.multiPlainChanges())
							.filterMap(task -> {
								if (task.isSuccess()) {
									return Optional.of(task.get());
								}

								return Optional.empty();
							})
								.subscribe(this::applyHighlighting);
	}

	public CodeTab(String name, String code, String path) {
		this.name = name;

		setText(this.name);

		updateHighlighter();

		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		codeArea.replaceText(code);

		codeArea.textProperty().addListener((obs, oldCode, newCode) -> {
			setText(this.name + " *");
		});

		codeArea.setStyleSpans(0, highlighter.highlightSyntax(code));

		var scrollPane = new VirtualizedScrollPane<>(codeArea);

		setContent(scrollPane);

		codeArea.multiPlainChanges()
			.successionEnds(Duration.ofMillis(10))
				.retainLatestUntilLater(executorService)
					.supplyTask(this::computeHighlightingAsync)
						.awaitLatest(codeArea.multiPlainChanges())
							.filterMap(task -> {
								if (task.isSuccess()) {
									return Optional.of(task.get());
								}

								return Optional.empty();
							})
								.subscribe(this::applyHighlighting);

		this.path = path;
	}

	private void updateHighlighter() {
		if (this.name.endsWith(".java")) {
			highlighter.setSyntax("Java");

			return;
		}

		if (this.name.endsWith(".css")) {
			highlighter.setSyntax("CSS");

			return;
		}

		if (this.name.endsWith(".xml")) {
			highlighter.setSyntax("XML");

			return;
		}

		highlighter.setSyntax("Plain Text");
	}

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		var code = codeArea.getText();

		var task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return highlighter.highlightSyntax(code);
			}
		};

		executorService.execute(task);

		return task;
	}

	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		codeArea.setStyleSpans(0, highlighting);
	}

	public void save() {
		if (path == null) {
			saveAs();

			return;
		}

		try (var writer = new FileWriter(path)) {
			writer.write(codeArea.getText());
		} catch (IOException e) {
			MsgBox.show("Save", "An error occurred while saving the file.");

			return;
		}

		setText(this.name);
	}

	public void saveAs() {
		var fileChooser = new FileChooser();

		var selectedFile = fileChooser.showSaveDialog(null);

		if (selectedFile == null) {
			return;
		}

		try (var writer = new FileWriter(selectedFile)) {
			writer.write(codeArea.getText());
		} catch (IOException e) {
			MsgBox.show("Save As...", "An error occurred while saving the file.");

			return;
		}

		this.name = selectedFile.getName();

		setText(this.name);

		this.path = selectedFile.getPath();

		updateHighlighter();
	}

	public void cut() {
		codeArea.cut();

		setText(this.name + " *");

		codeArea.setStyleSpans(0, highlighter.highlightSyntax(codeArea.getText()));
	}

	public void copy() {
		codeArea.copy();
	}

	public void paste() {
		codeArea.paste();

		setText(this.name + " *");

		codeArea.setStyleSpans(0, highlighter.highlightSyntax(codeArea.getText()));
	}

	public void undo() {
		if (codeArea.isUndoAvailable()) {
			codeArea.undo();

			setText(this.name + " *");

			codeArea.setStyleSpans(0, highlighter.highlightSyntax(codeArea.getText()));
		}
	}

	public void redo() {
		if (codeArea.isRedoAvailable()) {
			codeArea.redo();

			setText(this.name + " *");

			codeArea.setStyleSpans(0, highlighter.highlightSyntax(codeArea.getText()));
		}
	}

	public void find() {
		var dialog = new TextInputDialog();

		dialog.setTitle("Find...");

		dialog.setHeaderText(null);

		dialog.setGraphic(null);

		dialog.setContentText("Find:");

		var dialogPane = dialog.getDialogPane();

		dialogPane.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

		dialogPane.getStyleClass().add("text-input-dialog");

		var result = dialog.showAndWait();

		if (result.isPresent()) {
			var code = codeArea.getText();

			var pattern = Pattern.compile(result.get());

			var matcher = pattern.matcher(code);

			var lastKeywordEnd = 0;

			var styleSpansBuilder = new StyleSpansBuilder<Collection<String>>();

			while (matcher.find()) {
				styleSpansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);

				styleSpansBuilder.add(Collections.singleton("yellow"), matcher.end() - matcher.start());

				lastKeywordEnd = matcher.end();
			}

			styleSpansBuilder.add(Collections.emptyList(), code.length() - lastKeywordEnd);

			codeArea.setStyleSpans(0, styleSpansBuilder.create());
		}
	}

	public void replace() {
		var dialog = new Dialog<Pair<String, String>>();

		dialog.setTitle("Replace...");

		dialog.setHeaderText(null);

		dialog.setGraphic(null);

		var grid = new GridPane();

		grid.setHgap(10);

		grid.setVgap(10);

		grid.setPadding(new Insets(20, 150, 10, 10));

		var find = new TextField();

		grid.add(new Label("Find:"), 0, 0);

		grid.add(find, 1, 0);

		var replace = new TextField();

		grid.add(new Label("Replace:"), 0, 1);

		grid.add(replace, 1, 1);

		var dialogPane = dialog.getDialogPane();

		dialogPane.setContent(grid);

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialogPane.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

		dialogPane.getStyleClass().add("text-input-dialog");

		dialog.setResultConverter(pressedButton -> {
			if (pressedButton == ButtonType.OK) {
				return new Pair<>(find.getText(), replace.getText());
			}

			return null;
		});

		var result = dialog.showAndWait();

		result.ifPresent(findReplace -> {
			var code = codeArea.getText();

			code = code.replaceAll(findReplace.getKey(), findReplace.getValue());

			codeArea.replaceText(code);
		});
	}

	public void shutdownExecutorService() {
		executorService.shutdown();
	}
}