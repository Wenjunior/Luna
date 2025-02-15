package com.wenderson.luna;

import java.io.*;
import java.util.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.application.Application;
import org.fxmisc.richtext.InlineCssTextArea;
import javafx.scene.control.ButtonBar.ButtonData;

public class App extends Application {
    TabPane tabs;
    
    ArrayList<InlineCssTextArea> text_areas = new ArrayList<>();
    
    ArrayList<String> paths = new ArrayList<>();
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Luna");
        
        stage.setMaximized(true);
        
        var new_file = new MenuItem("New File");
        
        var new_file_shortcut = new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN);
        
        new_file.setAccelerator(new_file_shortcut);
        
        new_file.setOnAction((ActionEvent action) -> {
            new_file("Untitled", "", null);
        });
        
        var open_file = new MenuItem("Open File...");
        
        var open_file_shortcut = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN);
        
        open_file.setAccelerator(open_file_shortcut);
        
        open_file.setOnAction((ActionEvent action) -> {
            open_file();
        });
        
        var save = new MenuItem("Save");
        
        var save_shortcut = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN);
        
        save.setAccelerator(save_shortcut);
        
        save.setOnAction((ActionEvent action) -> {
            save();
        });
        
        var save_as = new MenuItem("Save As...");
        
        var save_as_shortcut = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN);
        
        save_as.setAccelerator(save_as_shortcut);
        
        save_as.setOnAction((ActionEvent action) -> {
            save_as();
        });
        
        var exit = new MenuItem("Exit");
        
        var exit_shortcut = new KeyCodeCombination(KeyCode.E, KeyCodeCombination.CONTROL_DOWN);
        
        exit.setAccelerator(exit_shortcut);
        
        exit.setOnAction((ActionEvent action) -> {
            System.exit(0);
        });
        
        var file = new Menu("File");
        
        file.getItems().addAll(new_file, open_file, save, save_as, exit);
        
        var undo = new MenuItem("Undo");
        
        var undo_shortcut = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN);
        
        undo.setAccelerator(undo_shortcut);
        
        undo.setOnAction((ActionEvent action) -> {
            edit_text("Undo");
        });
        
        var redo = new MenuItem("Redo");
        
        var redo_shortcut = new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN);
        
        redo.setAccelerator(redo_shortcut);
        
        redo.setOnAction((ActionEvent action) -> {
            edit_text("Redo");
        });
        
        var cut = new MenuItem("Cut");
        
        var cut_shortcut = new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN);
        
        cut.setAccelerator(cut_shortcut);
        
        cut.setOnAction((ActionEvent action) -> {
            edit_text("Cut");
        });
        
        var copy = new MenuItem("Copy");
        
        var copy_shortcut = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN);
        
        copy.setAccelerator(copy_shortcut);
        
        copy.setOnAction((ActionEvent action) -> {
            edit_text("Copy");
        });
        
        var paste = new MenuItem("Paste");
        
        var paste_shortcut = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN);
        
        paste.setAccelerator(paste_shortcut);
        
        paste.setOnAction((ActionEvent action) -> {
            edit_text("Paste");
        });
        
        var find = new MenuItem("Find...");
        
        var find_shortcut = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN);
        
        find.setAccelerator(find_shortcut);
        
        find.setOnAction((ActionEvent action) -> {
            find();
        });
        
        var replace = new MenuItem("Replace...");
        
        var replace_shortcut = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN);
        
        replace.setAccelerator(replace_shortcut);
        
        replace.setOnAction((ActionEvent action) -> {
            replace();
        });
        
        var edit = new Menu("Edit");
        
        edit.getItems().addAll(undo, redo, cut, copy, paste, find, replace);
        
        var menubar = new MenuBar();
        
        menubar.getMenus().addAll(file, edit);
        
        tabs = new TabPane();
        
        var border_pane = new BorderPane();
        
        border_pane.setTop(menubar);
        
        border_pane.setCenter(tabs);
        
        var scene = new Scene(border_pane, 1280, 720);
        
        stage.setScene(scene);
        
        stage.show();
    }
    
    void new_file(String title, String text, String path) {
        var tab = new Tab(title);
        
        var selection_model = tabs.getSelectionModel();
        
        tab.setOnCloseRequest((Event event) -> {
            var index = selection_model.getSelectedIndex();
            
            text_areas.remove(index);
            
            paths.remove(index);
        });
        
        var scroll_pane = new ScrollPane();
        
        var text_area = new InlineCssTextArea();
        
        text_area.setStyle("-fx-font-family: Consolas; -fx-font-size: 120%;");
        
        text_area.appendText(text);
        
        text_area.addEventFilter(KeyEvent.ANY, key -> {
            text_area.clearStyle(0, text_area.getText().length());
            
            if (key.isShortcutDown() && key.getCode() == KeyCode.Y) {
                text_area.redo();
                
                key.consume();
            }
        });
        
        text_areas.add(text_area);
        
        scroll_pane.setContent(text_area);
        
        scroll_pane.fitToWidthProperty().set(true);
        
        scroll_pane.fitToHeightProperty().set(true);
        
        tab.setContent(scroll_pane);
        
        tabs.getTabs().add(tab);
        
        paths.add(path);
        
        selection_model.selectLast();
    }
    
    void show_error(String text) {
        var dialog = new Dialog<>();
        
        dialog.setTitle("ERROR");
        
        dialog.setContentText(text);
        
        var ok = new ButtonType("OK", ButtonData.OK_DONE);
        
        dialog.getDialogPane().getButtonTypes().add(ok);
        
        dialog.showAndWait();
    }
    
    void open_file() {
        var file_chooser = new FileChooser();
        
        file_chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        var file = file_chooser.showOpenDialog(null);
        
        if (file == null) {
            return;
        }
        
        var text = new StringBuilder();
        
        try {
            var scanner = new Scanner(file);
            
            String line;
            
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                
                text.append(line);
                
                if (scanner.hasNextLine()) {
                    text.append("\n");
                }
            }
            
            scanner.close();
        } catch (FileNotFoundException error) {
            show_error("The file was not found.");
            
            return;
        }
        
        new_file(file.getName(), text.toString(), file.getPath());
    }
    
    void save_as() {
        var index = tabs.getSelectionModel().getSelectedIndex();
        
        if (index == -1) {
            return;
        }
        
        var file_chooser = new FileChooser();
        
        file_chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        var file = file_chooser.showSaveDialog(null);
        
        if (file == null) {
            return;
        }
        
        try {
            var writer = new FileWriter(file);
            
            writer.write(text_areas.get(index).getText());
            
            writer.close();
        } catch (IOException error) {
            show_error("An error ocorrur while saving the file.");
        }
        
        if (paths.get(index) == null) {
            tabs.getTabs().get(index).setText(file.getName());
            
            paths.add(file.getPath());
        }
    }
    
    void save() {
        var index = tabs.getSelectionModel().getSelectedIndex();
        
        if (index == -1) {
            return;
        }
        
        if (paths.get(index) == null) {
            save_as();
            
            return;
        }
        
        try {
            var writer = new FileWriter(paths.get(index));
            
            writer.write(text_areas.get(index).getText());
            
            writer.close();
        } catch (IOException error) {
            show_error("An error ocorror when saving the file.");
        }
    }
    
    void edit_text(String edit) {
        var index = tabs.getSelectionModel().getSelectedIndex();
        
        if (index == -1) {
            return;
        }
        
        switch (edit) {
            case "Undo":
                text_areas.get(index).undo();
                
                break;
            case "Redo":
                text_areas.get(index).redo();
                
                break;
            case "Cut":
                text_areas.get(index).cut();
                
                break;
            case "Copy":
                text_areas.get(index).copy();
                
                break;
            case "Paste":
                text_areas.get(index).paste();
                
                break;
        }
    }
    
    void find() {
        var index = tabs.getSelectionModel().getSelectedIndex();
        
        if (index == -1) {
            return;
        }
        
        var text_area = text_areas.get(index);
        
        var text = text_area.getText();
        
        text_area.clearStyle(0, text.length());
        
        var find = new TextInputDialog(text_area.getSelectedText());
        
        find.setTitle("Find...");
        
        find.setHeaderText("Find:");
        
        find.showAndWait().ifPresent(word -> {
            if (word.isEmpty()) {
                show_error("You need to type what you want to find.");
                
                return;
            }
            
            var word_index = text.indexOf(word);
            
            while (word_index != -1) {
                text_area.setStyle(word_index, word_index + word.length(), "-rtfx-background-color: yellow;");
                
                word_index = text.indexOf(word, word_index + word.length());
            }
        });
    }
    
    void replace() {
        var index = tabs.getSelectionModel().getSelectedIndex();
        
        if (index == -1) {
            return;
        }
        
        var text_area = text_areas.get(index);
        
        var from_tid = new TextInputDialog(text_area.getSelectedText());
        
        from_tid.setTitle("Replace...");
        
        from_tid.setHeaderText("From:");
        
        from_tid.showAndWait().ifPresent(from -> {
            if (from.isEmpty()) {
                show_error("Enter the text you wish to replace.");
                
                return;
            }
            
            var text = text_area.getText();
            
            if (!text.contains(from)) {
                show_error("The text you entered was not found.");
                
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
    
    public static void main(String[] args) {
        launch();
    }
}