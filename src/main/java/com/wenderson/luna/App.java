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
        
        var newFile = new MenuItem("New File");
        
        var newFileShortcut = new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN);
        
        newFile.setAccelerator(newFileShortcut);
        
        newFile.setOnAction((ActionEvent action) -> {
            newFile();
        });
        
        var openFile = new MenuItem("Open File...");
        
        var openFileShortcut = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN);
        
        openFile.setAccelerator(openFileShortcut);
        
        openFile.setOnAction((ActionEvent action) -> {
            openFile();
        });
        
        var save = new MenuItem("Save");
        
        var saveShortcut = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN);
        
        save.setAccelerator(saveShortcut);
        
        save.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Save");
        });
        
        var saveAs = new MenuItem("Save As...");
        
        var saveAsShortcut = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN);
        
        saveAs.setAccelerator(saveAsShortcut);
        
        saveAs.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Save As...");
        });
        
        var exit = new MenuItem("Exit");
        
        var exitShortcut = new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN);
        
        exit.setAccelerator(exitShortcut);
        
        exit.setOnAction((ActionEvent action) -> {
            System.exit(0);
        });
        
        var file = new Menu("File");
        
        file.getItems().addAll(newFile, openFile, save, saveAs, exit);
        
        var undo = new MenuItem("Undo");
        
        var undoShortcut = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN);
        
        undo.setAccelerator(undoShortcut);
        
        undo.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Undo");
        });
        
        var redo = new MenuItem("Redo");
        
        var redoShortcut = new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN);
        
        redo.setAccelerator(redoShortcut);
        
        redo.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Redo");
        });
        
        var cut = new MenuItem("Cut");
        
        var cutShortcut = new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN);
        
        cut.setAccelerator(cutShortcut);
        
        cut.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Cut");
        });
        
        var copy = new MenuItem("Copy");
        
        var copyShortcut = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN);
        
        copy.setAccelerator(copyShortcut);
        
        copy.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Copy");
        });
        
        var paste = new MenuItem("Paste");
        
        var pasteShortcut = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN);
        
        paste.setAccelerator(pasteShortcut);
        
        paste.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Paste");
        });
        
        var find = new MenuItem("Find...");
        
        var findShortcut = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN);
        
        find.setAccelerator(findShortcut);
        
        find.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Find...");
        });
        
        var replace = new MenuItem("Replace...");
        
        var replaceShortcut = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN);
        
        replace.setAccelerator(replaceShortcut);
        
        replace.setOnAction((ActionEvent action) -> {
            tabActionPerformed("Replace...");
        });
        
        var edit = new Menu("Edit");
        
        edit.getItems().addAll(undo, redo, cut, copy, paste, find, replace);
        
        var menubar = new MenuBar();
        
        menubar.getMenus().addAll(file, edit);
        
        var borderPane = new BorderPane();
        
        borderPane.setTop(menubar);
        
        borderPane.setCenter(tabs);
        
        var scene = new Scene(borderPane, 1280, 720);
        
        stage.setScene(scene);
        
        stage.show();
    }
    
    void newFile() {
        var tab = new CustomTab();
        
        tabs.getTabs().add(tab);
        
        tabs.getSelectionModel().selectLast();
    }
    
    void openFile() {
        var fileChooser = new FileChooser();
        
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        var file = fileChooser.showOpenDialog(null);
        
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
            MsgBox.show("Open File...", "An error occurred while trying to open the file.");
            
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
                tab.saveAs();
                
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