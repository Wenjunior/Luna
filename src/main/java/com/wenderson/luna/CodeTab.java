package com.wenderson.luna;

import java.io.*;
import org.fxmisc.richtext.*;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class CodeTab extends Tab {
	private String name = "Untitled";

	private CodeArea codeArea = new CodeArea();

	private String path = null;

	private Highlighter highlighter = new Highlighter();

	public CodeTab() {
		setText(this.name);

		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		codeArea.textProperty().addListener((obs, oldText, newText) -> {
			setText(this.name + " *");

			codeArea.setStyleSpans(0, highlighter.highlightSyntax(newText));
		});

		var scrollPane = new VirtualizedScrollPane<>(codeArea);

		setContent(scrollPane);
	}

	public CodeTab(String name, String text, String path) {
		this.name = name;

		setText(this.name);

		if (this.name.endsWith(".java")) {
			highlighter.setSyntax("Java");
		}

		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		codeArea.replaceText(text);

		codeArea.textProperty().addListener((obs, oldText, newText) -> {
			setText(this.name + " *");

			codeArea.setStyleSpans(0, highlighter.highlightSyntax(newText));
		});

		codeArea.setStyleSpans(0, highlighter.highlightSyntax(text));

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
}