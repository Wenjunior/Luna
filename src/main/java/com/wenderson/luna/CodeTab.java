package com.wenderson.luna;

import java.io.*;
import java.util.*;
import org.fxmisc.richtext.*;
import javafx.scene.control.*;
import java.util.regex.Pattern;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class CodeTab extends Tab {
	private String name = "Untitled";

	private CodeArea codeArea = new CodeArea();

	private String path = null;

	private Highlighter highlighter = new Highlighter();

	public CodeTab() {
		setText(this.name);

		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		codeArea.textProperty().addListener((obs, oldCode, newCode) -> {
			setText(this.name + " *");

			codeArea.setStyleSpans(0, highlighter.highlightSyntax(newCode));
		});

		var scrollPane = new VirtualizedScrollPane<>(codeArea);

		setContent(scrollPane);
	}

	public CodeTab(String name, String code, String path) {
		this.name = name;

		setText(this.name);

		if (this.name.endsWith(".java")) {
			highlighter.setSyntax("Java");
		}

		if (this.name.endsWith(".css")) {
			highlighter.setSyntax("CSS");
		}

		if (this.name.endsWith(".xml")) {
			highlighter.setSyntax("XML");
		}

		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		codeArea.replaceText(code);

		codeArea.textProperty().addListener((obs, oldCode, newCode) -> {
			setText(this.name + " *");

			codeArea.setStyleSpans(0, highlighter.highlightSyntax(newCode));
		});

		codeArea.setStyleSpans(0, highlighter.highlightSyntax(code));

		var scrollPane = new VirtualizedScrollPane<>(codeArea);

		setContent(scrollPane);

		this.path = path;
	}

	public void save() {
		if (path == null) {
			saveAs();

			return;
		}

		try (var writer = new FileWriter(path)) {
			writer.write(codeArea.getText());
		} catch (IOException e) {
			MsgBox.show("Save", "Um erro ocorreu ao salvar o arquivo.");

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
			MsgBox.show("Save As...", "Um erro ocorreu ao salvar o arquivo.");

			return;
		}

		this.name = selectedFile.getName();

		setText(this.name);

		this.path = selectedFile.getPath();

		highlighter.setSyntax("Plain text");

		if (this.name.endsWith(".java")) {
			highlighter.setSyntax("Java");
		}

		if (this.name.endsWith(".css")) {
			highlighter.setSyntax("CSS");
		}

		if (this.name.endsWith(".xml")) {
			highlighter.setSyntax("XML");
		}
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
		var tid = new TextInputDialog();

		tid.setHeaderText(null);

		tid.setGraphic(null);

		tid.setTitle("Find...");

		tid.setContentText("Encontre:");

		var result = tid.showAndWait();

		if (result.isPresent()) {
			var code = codeArea.getText();

			var pattern = Pattern.compile(result.get());

			var matcher = pattern.matcher(code);

			var lastKeywordEnd = 0;

			var styleSpansBuilder = new StyleSpansBuilder<Collection<String>>();

			while (matcher.find()) {
				styleSpansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);

				styleSpansBuilder.add(Collections.singleton("red"), matcher.end() - matcher.start());

				lastKeywordEnd = matcher.end();
			}

			styleSpansBuilder.add(Collections.emptyList(), code.length() - lastKeywordEnd);

			codeArea.setStyleSpans(0, styleSpansBuilder.create());
		}
	}
}