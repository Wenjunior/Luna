package com.wenderson.luna;

import java.io.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.util.stream.IntStream;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class CustomTab extends Tab {
    String title = "Untitled";
    
    InlineCssTextArea textArea = new InlineCssTextArea();
    
    String path = "";
    
    int tabCount = 0;
    
    boolean wasSaved = true;
    
    CustomTab() {
        setText(title);
        
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (!key.getCode().isNavigationKey() && !key.isControlDown()&& wasSaved) {
                setText(title + " *");
                
                wasSaved = false;
            }
            
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
        
        var scrollPane = new VirtualizedScrollPane(textArea);
        
        setContent(scrollPane);
    }
    
    CustomTab(String title, String content, String path) {
        this.title = title;
        
        setText(title);
        
        this.path = path;
        
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (!key.getCode().isNavigationKey() && !key.isControlDown() && wasSaved) {
                setText(title + " *");
                
                wasSaved = false;
            }
            
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
        
        var scrollPane = new VirtualizedScrollPane(textArea);
        
        setContent(scrollPane);
    }
    
    void save() {
        if (path.isEmpty()) {
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
        
        wasSaved = true;
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
        
        wasSaved = true;
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