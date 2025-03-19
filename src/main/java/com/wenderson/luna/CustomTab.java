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
    
    boolean wasSaved = true;
    
    CustomTab(String title, String content, String path) {
        this.title = title;
        
        setText(title);
        
        this.path = path;
        
        var scrollPane = new VirtualizedScrollPane<>(codeArea);
        
        setContent(scrollPane);
        
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (!key.getCode().isNavigationKey() && !key.isControlDown()) {
                changeTitle();
            }
            
            codeArea.clearStyle(0, codeArea.getText().length());
        });
        
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        
        codeArea.replaceText(0, 0, content);
        
        setOnCloseRequest(event -> {
            if (!wasSaved) {
                var dialog = new Dialog<>();
                
                dialog.setTitle("Warning");
                
                dialog.setContentText("O arquivo não foi salvo. Você quer salvar as alterações?");
                
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
            MsgBox.show("Save", "Um erro ocorreu ao salvar o arquivo.");
            
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
            MsgBox.show("Save As...", "Um erro ocorreu ao salvar o arquivo.");
            
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
                MsgBox.show("Find...", "Digite o que você quer encontrar.");
                
                return;
            }
            
            if (!text.contains(word)) {
                MsgBox.show("Find...", "O texto que você digitou não foi encontrado.");
                
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
                MsgBox.show("Replace...", "Digite o que você quer encontrar.");
                
                return;
            }
            
            var text = codeArea.getText();
            
            if (!text.contains(from)) {
                MsgBox.show("Replace...", "O texto que você digitou não foi encontrado.");
                
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