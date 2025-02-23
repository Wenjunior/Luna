package com.wenderson.luna;

import java.io.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.util.stream.IntStream;
import org.fxmisc.richtext.InlineCssTextArea;

public class CustomTab extends Tab {
    InlineCssTextArea text_area = new InlineCssTextArea();
    
    boolean was_saved = true;
    
    String path = null;
    
    int count = 0;
    
    CustomTab() {
        setText("Untitled");
        
        var scroll_pane = new ScrollPane();
        
        scroll_pane.fitToWidthProperty().set(true);
        
        scroll_pane.fitToHeightProperty().set(true);
        
        text_area.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            was_saved = false;
            
            text_area.clearStyle(0, text_area.getText().length());
            
            if (key.getCode() == KeyCode.TAB) {
                count += 1;
                
                text_area.insertText(text_area.getCaretPosition(), "\t");
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.ENTER) {
                var tab_builder = new StringBuilder("\n");
                
                IntStream.range(0, count).forEachOrdered(i -> {
                    tab_builder.append("\t");
                });
                
                text_area.insertText(text_area.getCaretPosition(), tab_builder.toString());
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.BACK_SPACE) {
                var text = text_area.getSelectedText();
                
                if (text.isEmpty()) {
                    var caret = text_area.getCaretPosition();
                    
                    if (caret > 0) {
                        var ch = text_area.getText(caret - 1, caret);
                        
                        if (ch.equals("\t")) {
                            count -= 1;
                        }
                        
                        text_area.deleteText(caret - 1, caret);
                    }
                } else {
                    var index_of_tab = text.indexOf("\n");
                    
                    while (index_of_tab != -1) {
                        count -= 1;
                        
                        index_of_tab = text.indexOf("\n", index_of_tab + 1);
                    }
                    
                    text_area.deleteText(text_area.getSelection());
                }
                
                key.consume();
            }
        });
        
        text_area.setStyle("-fx-font-family: Consolas; -fx-font-size: 120%;");
        
        scroll_pane.setContent(text_area);
        
        setContent(scroll_pane);
    }
    
    CustomTab(String name, String content) {
        setText(name);
        
        var scroll_pane = new ScrollPane();
        
        scroll_pane.fitToWidthProperty().set(true);
        
        scroll_pane.fitToHeightProperty().set(true);
        
        text_area.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            was_saved = false;
            
            text_area.clearStyle(0, text_area.getText().length());
            
            if (key.getCode() == KeyCode.TAB) {
                count += 1;
                
                text_area.insertText(text_area.getCaretPosition(), "\t");
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.ENTER) {
                var tab_builder = new StringBuilder("\n");
                
                IntStream.range(0, count).forEachOrdered(i -> {
                    tab_builder.append("\t");
                });
                
                text_area.insertText(text_area.getCaretPosition(), tab_builder.toString());
                
                key.consume();
            }
            
            if (key.getCode() == KeyCode.BACK_SPACE) {
                var text = text_area.getSelectedText();
                
                if (text.isEmpty()) {
                    var caret = text_area.getCaretPosition();
                    
                    if (caret > 0) {
                        var ch = text_area.getText(caret - 1, caret);
                        
                        if (ch.equals("\t")) {
                            count -= 1;
                        }
                        
                        text_area.deleteText(caret - 1, caret);
                    }
                } else {
                    var index_of_tab = text.indexOf("\n");
                    
                    while (index_of_tab != -1) {
                        count -= 1;
                        
                        index_of_tab = text.indexOf("\n", index_of_tab + 1);
                    }
                    
                    text_area.deleteText(text_area.getSelection());
                }
                
                key.consume();
            }
        });
        
        text_area.setStyle("-fx-font-family: Consolas; -fx-font-size: 120%;");
        
        text_area.appendText(content);
        
        scroll_pane.setContent(text_area);
        
        setContent(scroll_pane);
    }
    
    public void save() {
        if (path == null) {
            save_as();
            
            return;
        }
        
        if (was_saved) {
            return;
        }
        
        try (var writer = new FileWriter(path)) {
            writer.write(text_area.getText());
        } catch (IOException e) {
            MsgBox.show("Save", "An error occurred while trying to save the file.");
        }
    }
    
    public void save_as() {
        var file_chooser = new FileChooser();
        
        file_chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        var file = file_chooser.showSaveDialog(null);
        
        if (file == null) {
            return;
        }
        
        path = file.getPath();
        
        try (var writer = new FileWriter(path)) {
            writer.write(text_area.getText());
        } catch (IOException e) {
            MsgBox.show("Save As...", "An error occurred while saving the file.");
            
            return;
        }
        
        was_saved = true;
    }
    
    public void undo() {
        if (text_area.isUndoAvailable()) {
            text_area.undo();
        }
    }
    
    public void redo() {
        if (text_area.isRedoAvailable()) {
            text_area.redo();
        }
    }
    
    public void cut() {
        text_area.cut();
    }
    
    public void copy() {
        text_area.copy();
    }
    
    public void paste() {
        text_area.paste();
    }
    
    public void find() {
        var text = text_area.getText();
        
        text_area.clearStyle(0, text.length());
        
        var find_tid = new TextInputDialog(text_area.getSelectedText());
        
        find_tid.setTitle("Find...");
        
        find_tid.setHeaderText("Find:");
        
        find_tid.showAndWait().ifPresent(find -> {
            if (find.isEmpty()) {
                MsgBox.show("Find...", "You need to type what you want to find.");
                
                return;
            }
            
            if (!text.contains(find)) {
                MsgBox.show("Find...", "The text you entered was not found.");
                
                return;
            }
            
            var word_index = text.indexOf(find);
            
            while (word_index != -1) {
                text_area.setStyle(word_index, word_index + text.length(), "-rtfx-background-color: yellow;");
                
                word_index = text.indexOf(find, word_index + find.length());
            }
        });
    }
    
    public void replace() {
        var from_tid = new TextInputDialog(text_area.getSelectedText());
        
        from_tid.setTitle("Replace...");
        
        from_tid.setHeaderText("From:");
        
        from_tid.showAndWait().ifPresent(from -> {
            if (from.isEmpty()) {
                MsgBox.show("Replace...", "Enter the text you wish to replace.");
                
                return;
            }
            
            var text = text_area.getText();
            
            if (!text.contains(from)) {
                MsgBox.show("Replace...", "The text you entered was not found.");
                
                return;
            }
            
            var to_tid = new TextInputDialog();
            
            to_tid.setTitle("Replace...");
            
            to_tid.setHeaderText("To:");
            
            to_tid.showAndWait().ifPresent(to -> {
                var carret = text_area.getCaretPosition();
                
                text_area.clear();
                
                text_area.appendText(text.replaceAll(from, to));
                
                text_area.moveTo(carret);
            });
        });
    }
}