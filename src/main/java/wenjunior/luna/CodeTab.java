package wenjunior.luna;

import java.io.File;
import javafx.util.Pair;
import java.io.FileWriter;
import java.time.Duration;
import java.util.Optional;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import org.fxmisc.richtext.CodeArea;
import java.util.concurrent.Executors;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import java.util.concurrent.ExecutorService;
import javafx.scene.control.TextInputDialog;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class CodeTab extends Tab {
	private String name = "Untitled";

	private CodeArea codeArea = new CodeArea();

	private String path = null;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private Highlighter highlighter = new Highlighter();

	public CodeTab() {
		setText(this.name);

		this.codeArea.setWrapText(true);

		this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

		VirtualizedScrollPane scrollPane = new VirtualizedScrollPane<>(this.codeArea);

		setContent(scrollPane);

		this.codeArea.multiPlainChanges()
			.retainLatestUntilLater(this.executorService)
				.supplyTask(this::computeHighlightingAsync)
					.awaitLatest(this.codeArea.multiPlainChanges())
						.filterMap(task -> {
							if (task.isSuccess()) {
								return Optional.of(task.get());
							}

							return Optional.empty();
						})
							.subscribe(this::applyHighlighting);

		this.codeArea.textProperty().addListener((obs, oldCode, newCode) -> {
			setText(this.name + " *");
		});
	}

	public CodeTab(String name, String code, String path) {
		this.name = name;

		setText(this.name);

		updateHighlighter();

		this.codeArea.setWrapText(true);

		this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

		VirtualizedScrollPane scrollPane = new VirtualizedScrollPane<>(this.codeArea);

		setContent(scrollPane);

		/*
			Evite mudar a ordem das funções a seguir. Elas monitoram mudanças no código fonte, então se
			'multiPlainChanges' estiver depois de 'replaceText', o código não sera realçado porque 'multiPlainChanges'
			ainda não foi definido. E se 'addListener' estiver antes de 'replaceText', ele vai detectar a mudança no
			código fonte e mudar o nome da tab sem necessidade, já que o código fonte não modificado pelo usuário.
		*/

		this.codeArea.multiPlainChanges()
			.retainLatestUntilLater(this.executorService)
				.supplyTask(this::computeHighlightingAsync)
					.awaitLatest(this.codeArea.multiPlainChanges())
						.filterMap(task -> {
							if (task.isSuccess()) {
								return Optional.of(task.get());
							}

							return Optional.empty();
						})
							.subscribe(this::applyHighlighting);

		this.codeArea.replaceText(code);

		this.codeArea.textProperty().addListener((obs, oldCode, newCode) -> {
			setText(this.name + " *");
		});

		this.path = path;
	}

	private void updateHighlighter() {
		if (this.name.endsWith(".java")) {
			this.highlighter.setSyntax(SupportedLanguages.JAVA);

			return;
		}

		if (this.name.endsWith(".xml")) {
			this.highlighter.setSyntax(SupportedLanguages.XML);

			return;
		}

		if (this.name.endsWith(".css")) {
			this.highlighter.setSyntax(SupportedLanguages.CSS);

			return;
		}

		this.highlighter.setSyntax(SupportedLanguages.PLAIN_TEXT);
	}

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String code = this.codeArea.getText();

		Task<StyleSpans<Collection<String>>> task = new Task<>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return highlighter.highlightSyntax(code);
			}
		};

		this.executorService.execute(task);

		return task;
	}

	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		this.codeArea.setStyleSpans(0, highlighting);
	}

	public void save() {
		if (this.path == null) {
			saveAs();

			return;
		}

		try (FileWriter writer = new FileWriter(this.path)) {
			writer.write(this.codeArea.getText());
		} catch (IOException e) {
			MsgBox.show("Save", "An error occurred while saving the file.");

			return;
		}

		setText(this.name);
	}

	public void saveAs() {
		FileChooser fileChooser = new FileChooser();

		File selectedFile = fileChooser.showSaveDialog(null);

		if (selectedFile == null) {
			return;
		}

		try (FileWriter writer = new FileWriter(selectedFile)) {
			writer.write(this.codeArea.getText());
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
		this.codeArea.cut();

		setText(this.name + " *");
	}

	public void copy() {
		this.codeArea.copy();
	}

	public void paste() {
		this.codeArea.paste();

		setText(this.name + " *");
	}

	public void undo() {
		if (this.codeArea.isUndoAvailable()) {
			this.codeArea.undo();

			setText(this.name + " *");
		}
	}

	public void redo() {
		if (this.codeArea.isRedoAvailable()) {
			this.codeArea.redo();

			setText(this.name + " *");
		}
	}

	public void find() {
		TextInputDialog dialog = new TextInputDialog();

		dialog.setTitle("Find...");

		dialog.setHeaderText(null);

		dialog.setGraphic(null);

		dialog.setContentText("Find:");

		DialogPane dialogPane = dialog.getDialogPane();

		dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

		dialogPane.getStyleClass().add("text-input-dialog");

		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			String code = this.codeArea.getText();

			Pattern pattern = Pattern.compile(result.get());

			Matcher matcher = pattern.matcher(code);

			int lastKeywordEnd = 0;

			StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

			while (matcher.find()) {
				styleSpansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);

				styleSpansBuilder.add(Collections.singleton("yellow"), matcher.end() - matcher.start());

				lastKeywordEnd = matcher.end();
			}

			styleSpansBuilder.add(Collections.emptyList(), code.length() - lastKeywordEnd);

			this.codeArea.setStyleSpans(0, styleSpansBuilder.create());
		}
	}

	public void replace() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();

		dialog.setTitle("Replace...");

		dialog.setHeaderText(null);

		dialog.setGraphic(null);

		GridPane grid = new GridPane();

		grid.setHgap(10);

		grid.setVgap(10);

		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField find = new TextField();

		grid.add(new Label("Find:"), 0, 0);

		grid.add(find, 1, 0);

		TextField replace = new TextField();

		grid.add(new Label("Replace:"), 0, 1);

		grid.add(replace, 1, 1);

		DialogPane dialogPane = dialog.getDialogPane();

		dialogPane.setContent(grid);

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialogPane.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

		dialogPane.getStyleClass().add("text-input-dialog");

		dialog.setResultConverter(pressedButton -> {
			if (pressedButton == ButtonType.OK) {
				return new Pair<>(find.getText(), replace.getText());
			}

			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(findReplace -> {
			String code = this.codeArea.getText();

			code = code.replaceAll(findReplace.getKey(), findReplace.getValue());

			this.codeArea.replaceText(code);

			setText(this.name + " *");
		});
	}

	public void stopAsyncHighlighting() {
		this.executorService.shutdown();
	}
}