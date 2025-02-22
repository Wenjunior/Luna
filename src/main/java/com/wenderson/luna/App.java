package com.wenderson.luna;

import java.io.*;
import javafx.stage.*;
import java.util.Scanner;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;

public class App extends Application {
    TabPane tabs = new TabPane();
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Luna");
        
        stage.setMaximized(true);
        
        var new_file = new MenuItem("New File");
        
        var new_file_shortcut = new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN);
        
        new_file.setAccelerator(new_file_shortcut);
        
        new_file.setOnAction((ActionEvent action) -> {
            new_file();
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
            tabActionPerformed("Save");
        });
        
        var save_as = new MenuItem("Save As...");
        
        var save_as_shortcut = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN);
        
        save_as.setAccelerator(save_as_shortcut);
        
        save_as.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Save As...");
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
            tabActionPerformed("Undo");
        });
        
        var redo = new MenuItem("Redo");
        
        var redo_shortcut = new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN);
        
        redo.setAccelerator(redo_shortcut);
        
        redo.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Redo");
        });
        
        var cut = new MenuItem("Cut");
        
        var cut_shortcut = new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN);
        
        cut.setAccelerator(cut_shortcut);
        
        cut.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Cut");
        });
        
        var copy = new MenuItem("Copy");
        
        var copy_shortcut = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN);
        
        copy.setAccelerator(copy_shortcut);
        
        copy.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Copy");
        });
        
        var paste = new MenuItem("Paste");
        
        var paste_shortcut = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN);
        
        paste.setAccelerator(paste_shortcut);
        
        paste.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Paste");
        });
        
        var find = new MenuItem("Find...");
        
        var find_shortcut = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN);
        
        find.setAccelerator(find_shortcut);
        
        find.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Find...");
        });
        
        var replace = new MenuItem("Replace...");
        
        var replace_shortcut = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN);
        
        replace.setAccelerator(replace_shortcut);
        
        replace.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Replace...");
        });
        
        var edit = new Menu("Edit");
        
        edit.getItems().addAll(undo, redo, cut, copy, paste, find, replace);
        
        var menubar = new MenuBar();
        
        menubar.getMenus().addAll(file, edit);
        
        var border_pane = new BorderPane();
        
        border_pane.setTop(menubar);
        
        border_pane.setCenter(tabs);
        
        var scene = new Scene(border_pane, 1280, 720);
        
        stage.setScene(scene);
        
        stage.show();
    }
    
    void new_file() {
        var tab = new CustomTab();
        
        tabs.getTabs().add(tab);
        
        tabs.getSelectionModel().selectLast();
    }
    
    void open_file() {
        var file_chooser = new FileChooser();
        
        file_chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        var file = file_chooser.showOpenDialog(null);
        
        if (file == null) {
            return;
        }
        
        Scanner scanner;
        
        var text = new StringBuilder();
        
        try {
            scanner = new Scanner(file);
            
            String line;
            
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                
                text.append(line);
                
                if (scanner.hasNextLine()) {
                    text.append("\n");
                }
            }
        } catch (FileNotFoundException ex) {
            ShowError.show("Um erro ocorreu ao tentar abrir o arquivo.");
            
            return;
        }
        
        scanner.close();
        
        var tab = new CustomTab(file.getName(), text.toString());
        
        tabs.getTabs().add(tab);
        
        tabs.getSelectionModel().selectLast();
    }
    
    void tabActionPerformed(String command) {
        var index = tabs.getSelectionModel().getSelectedIndex();
        
        if (index == -1) {
            return;
        }
        
        var tab = (CustomTab) tabs.getTabs().get(index);
        
        switch (command) {
            case "Save":
                tab.save();
                
                break;
            case "Save As...":
                tab.save_as();
                
                break;
            case "Undo":
                tab.undo();
                
                break;
            case "Redo":
                tab.redo();
                
                break;
            case "Cut":
                tab.cut();
                
                break;
            case "Copy":
                tab.copy();
                
                break;
            case "Paste":
                tab.paste();
                
                break;
            case "Find...":
                tab.find();
                
                break;
            case "Replace...":
                tab.replace();
                
                break;
        }
    }
    
    public static void main(String[] args) {
        launch();
    }
}