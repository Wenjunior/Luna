package com.wenderson.luna;

import java.io.*;
import javafx.scene.input.*;
import org.fxmisc.richtext.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.util.stream.IntStream;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class CustomTab extends Tab {
    String title;
    
    CodeArea codeArea = new CodeArea();
    
    String path;
    
    int tabCount = 0;
    
    boolean wasSaved = true;
    
    CustomTab(String title, String content, String path) {
        this.title = title;
        
        setText(title);
        
        this.path = path;
        
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (!key.getCode().isNavigationKey() && !key.isControlDown()) {
                changeTitle();
            }
            
            codeArea.clearStyle(0, codeArea.getText().length());
            
            if (key.getCode() == KeyCode.TAB) {
                tabCount += 1;
                
                codeArea.insertText(codeArea.getCaretPosition(), "\t");
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.ENTER) {
                var tabBuilder = new StringBuilder("\n");
                
                IntStream.range(0, tabCount).forEachOrdered(i -> {
                    tabBuilder.append("\t");
                });
                
                codeArea.insertText(codeArea.getCaretPosition(), tabBuilder.toString());
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.BACK_SPACE) {
                var selectedText = codeArea.getSelectedText();
                
                if (selectedText.isEmpty()) {
                    var caret = codeArea.getCaretPosition();
                    
                    if (caret > 0) {
                        var character = codeArea.getText(caret - 1, caret);
                        
                        if (character.equals("\t")) {
                            tabCount -= 1;
                        }
                        
                        codeArea.deleteText(caret - 1, caret);
                    }
                } else {
                    var indexOfTab = selectedText.indexOf("\n");
                    
                    while (indexOfTab != -1) {
                        tabCount -= 1;
                        
                        indexOfTab = selectedText.indexOf("\n", indexOfTab + 1);
                    }
                    
                    codeArea.deleteText(codeArea.getSelection());
                }
                
                key.consume();
            }
        });
        
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        
        codeArea.replaceText(0, 0, content);
        
        var scrollPane = new VirtualizedScrollPane(codeArea);
        
        setContent(scrollPane);
        
        setOnCloseRequest(event -> {
            if (!wasSaved) {
                var dialog = new Dialog<>();
                
                dialog.setTitle("Warning");
                
                dialog.setContentText("The file has not been saved. Do you want to save the changes?");
                
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                
                dialog.showAndWait().ifPresent(action -> {
                    if (action == ButtonType.YES) {
                        save();
                    }
                    
                    if (action == ButtonType.CANCEL) {
                        event.consume();
                    }
                });
            }
        });
    }
    
    void changeTitle() {
        if (wasSaved) {
            this.title = title + " *";
            
            setText(this.title);
            
            wasSaved = false;
        }
    }
    
    void save() {
        if (path.isEmpty()) {
            saveAs();
            
            return;
        }
        
        try (var writer = new FileWriter(path)) {
            writer.write(codeArea.getText());
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
            writer.write(codeArea.getText());
        } catch (IOException e) {
            MsgBox.show("Save As...", "An error occurred while saving the file.");
            
            return;
        }
        
        title = file.getName();
        
        setText(title);
        
        wasSaved = true;
    }
    
    void undo() {
        if (codeArea.isUndoAvailable()) {
            codeArea.undo();
        }
        
        changeTitle();
    }
    
    void redo() {
        if (codeArea.isRedoAvailable()) {
            codeArea.redo();
        }
        
        changeTitle();
    }
    
    void cut() {
        codeArea.cut();
        
        changeTitle();
    }
    
    void copy() {
        codeArea.copy();
        
        changeTitle();
    }
    
    void paste() {
        codeArea.paste();
        
        changeTitle();
    }
    
    void find() {
        var text = codeArea.getText();
        
        codeArea.clearStyle(0, text.length());
        
        var findTID = new TextInputDialog(codeArea.getSelectedText());
        
        findTID.setTitle("Find...");
        
        findTID.setHeaderText("Find:");
        
        findTID.showAndWait().ifPresent(word -> {
            if (word.isEmpty()) {
                MsgBox.show("Find...", "You need to type what you want to find.");
                
                return;
            }
            
            if (!text.contains(word)) {
                MsgBox.show("Find...", "The text you entered was not found.");
                
                return;
            }
            
            var wordIndex = text.indexOf(word);
            
            while (wordIndex != -1) {
                codeArea.setStyleClass(wordIndex, wordIndex + word.length(), "highlight");
                
                wordIndex = text.indexOf(word, wordIndex + word.length());
            }
        });
    }
    
    void replace() {
        var fromTID = new TextInputDialog(codeArea.getSelectedText());
        
        fromTID.setTitle("Replace...");
        
        fromTID.setHeaderText("From:");
        
        fromTID.showAndWait().ifPresent(from -> {
            if (from.isEmpty()) {
                MsgBox.show("Replace...", "Enter the text you wish to replace.");
                
                return;
            }
            
            var text = codeArea.getText();
            
            if (!text.contains(from)) {
                MsgBox.show("Replace...", "The text you entered was not found.");
                
                return;
            }
            
            var toTID = new TextInputDialog();
            
            toTID.setTitle("Replace...");
            
            toTID.setHeaderText("To:");
            
            toTID.showAndWait().ifPresent(to -> {
                var carret = codeArea.getCaretPosition();
                
                codeArea.clear();
                
                codeArea.appendText(text.replaceAll(from, to));
                
                codeArea.moveTo(carret);
            });
        });
        
        changeTitle();
    }
}