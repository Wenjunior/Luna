package com.wenderson.luna;

import java.io.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.util.stream.IntStream;
import org.fxmisc.richtext.InlineCssTextArea;

public class CustomTab extends Tab {
    String title = "Untitled";
    
    InlineCssTextArea textArea = new InlineCssTextArea();
    
    String path = null;
    
    int tabCount = 0;
    
    CustomTab() {
        setText(title);
        
        var scrollPane = new ScrollPane();
        
        scrollPane.fitToWidthProperty().set(true);
        
        scrollPane.fitToHeightProperty().set(true);
        
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            setText(title + " *");
            
            textArea.clearStyle(0, textArea.getText().length());
            
            if (key.getCode() == KeyCode.TAB) {
                tabCount += 1;
                
                textArea.insertText(textArea.getCaretPosition(), "\t");
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.ENTER) {
                var tabBuilder = new StringBuilder("\n");
                
                IntStream.range(0, tabCount).forEachOrdered(i -> {
                    tabBuilder.append("\t");
                });
                
                textArea.insertText(textArea.getCaretPosition(), tabBuilder.toString());
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.BACK_SPACE) {
                var text = textArea.getSelectedText();
                
                if (text.isEmpty()) {
                    var caret = textArea.getCaretPosition();
                    
                    if (caret > 0) {
                        var character = textArea.getText(caret - 1, caret);
                        
                        if (character.equals("\t")) {
                            tabCount -= 1;
                        }
                        
                        textArea.deleteText(caret - 1, caret);
                    }
                } else {
                    var indexOfTab = text.indexOf("\n");
                    
                    while (indexOfTab != -1) {
                        tabCount -= 1;
                        
                        indexOfTab = text.indexOf("\n", indexOfTab + 1);
                    }
                    
                    textArea.deleteText(textArea.getSelection());
                }
                
                key.consume();
            }
        });
        
        textArea.setStyle("-fx-font-family: Consolas; -fx-font-size: 120%;");
        
        scrollPane.setContent(textArea);
        
        setContent(scrollPane);
    }
    
    CustomTab(String title, String content) {
        setText(title);
        
        var scrollPane = new ScrollPane();
        
        scrollPane.fitToWidthProperty().set(true);
        
        scrollPane.fitToHeightProperty().set(true);
        
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            setText(title + " *");
            
            textArea.clearStyle(0, textArea.getText().length());
            
            if (key.getCode() == KeyCode.TAB) {
                tabCount += 1;
                
                textArea.insertText(textArea.getCaretPosition(), "\t");
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.ENTER) {
                var tabBuilder = new StringBuilder("\n");
                
                IntStream.range(0, tabCount).forEachOrdered(i -> {
                    tabBuilder.append("\t");
                });
                
                textArea.insertText(textArea.getCaretPosition(), tabBuilder.toString());
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.BACK_SPACE) {
                var text = textArea.getSelectedText();
                
                if (text.isEmpty()) {
                    var caret = textArea.getCaretPosition();
                    
                    if (caret > 0) {
                        var character = textArea.getText(caret - 1, caret);
                        
                        if (character.equals("\t")) {
                            tabCount -= 1;
                        }
                        
                        textArea.deleteText(caret - 1, caret);
                    }
                } else {
                    var indexOfTab = text.indexOf("\n");
                    
                    while (indexOfTab != -1) {
                        tabCount -= 1;
                        
                        indexOfTab = text.indexOf("\n", indexOfTab + 1);
                    }
                    
                    textArea.deleteText(textArea.getSelection());
                }
                
                key.consume();
            }
        });
        
        textArea.setStyle("-fx-font-family: Consolas; -fx-font-size: 120%;");
        
        textArea.appendText(content);
        
        scrollPane.setContent(textArea);
        
        setContent(scrollPane);
    }
    
    void save() {
        if (path == null) {
            saveAs();
            
            return;
        }
        
        try (var writer = new FileWriter(path)) {
            writer.write(textArea.getText());
        } catch (IOException e) {
            MsgBox.show("Save", "An error occurred while trying to save the file.");
            
            return;
        }
        
        title = title.replace(" *", "");
        
        setText(title);
    }
    
    void saveAs() {
        var fileChooser = new FileChooser();
        
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        var file = fileChooser.showSaveDialog(null);
        
        if (file == null) {
            return;
        }
        
        path = file.getPath();
        
        try (var writer = new FileWriter(path)) {
            writer.write(textArea.getText());
        } catch (IOException e) {
            MsgBox.show("Save As...", "An error occurred while saving the file.");
            
            return;
        }
        
        title = file.getName();
        
        setText(title);
    }
    
    void undo() {
        if (textArea.isUndoAvailable()) {
            textArea.undo();
        }
    }
    
    void redo() {
        if (textArea.isRedoAvailable()) {
            textArea.redo();
        }
    }
    
    void cut() {
        textArea.cut();
    }
    
    void copy() {
        textArea.copy();
    }
    
    void paste() {
        textArea.paste();
    }
    
    void find() {
        var text = textArea.getText();
        
        textArea.clearStyle(0, text.length());
        
        var findTID = new TextInputDialog(textArea.getSelectedText());
        
        findTID.setTitle("Find...");
        
        findTID.setHeaderText("Find:");
        
        findTID.showAndWait().ifPresent(find -> {
            if (find.isEmpty()) {
                MsgBox.show("Find...", "You need to type what you want to find.");
                
                return;
            }
            
            if (!text.contains(find)) {
                MsgBox.show("Find...", "The text you entered was not found.");
                
                return;
            }
            
            var wordIndex = text.indexOf(find);
            
            while (wordIndex != -1) {
                textArea.setStyle(wordIndex, wordIndex + text.length(), "-rtfx-background-color: yellow;");
                
                wordIndex = text.indexOf(find, wordIndex + find.length());
            }
        });
    }
    
    void replace() {
        var fromTID = new TextInputDialog(textArea.getSelectedText());
        
        fromTID.setTitle("Replace...");
        
        fromTID.setHeaderText("From:");
        
        fromTID.showAndWait().ifPresent(from -> {
            if (from.isEmpty()) {
                MsgBox.show("Replace...", "Enter the text you wish to replace.");
                
                return;
            }
            
            var text = textArea.getText();
            
            if (!text.contains(from)) {
                MsgBox.show("Replace...", "The text you entered was not found.");
                
                return;
            }
            
            var toTID = new TextInputDialog();
            
            toTID.setTitle("Replace...");
            
            toTID.setHeaderText("To:");
            
            toTID.showAndWait().ifPresent(to -> {
                var carret = textArea.getCaretPosition();
                
                textArea.clear();
                
                textArea.appendText(text.replaceAll(from, to));
                
                textArea.moveTo(carret);
            });
        });
    }
}