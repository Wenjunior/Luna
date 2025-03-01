package com.wenderson.luna;

import java.io.*;
import org.json.*;
import java.util.*;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;

public class App extends Application {
    TabPane tabs = new TabPane();
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Luna");
        
        stage.setMaximized(true);
        
        stage.setOnCloseRequest(event -> {
            exit();
        });
        
        var newFile = new MenuItem("New File");
        
        var newFileShortcut = new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN);
        
        newFile.setAccelerator(newFileShortcut);
        
        newFile.setOnAction(action -> {
            newFile();
        });
        
        var openFile = new MenuItem("Open File...");
        
        var openFileShortcut = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN);
        
        openFile.setAccelerator(openFileShortcut);
        
        openFile.setOnAction(action -> {
            openFile();
        });
        
        var save = new MenuItem("Save");
        
        var saveShortcut = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN);
        
        save.setAccelerator(saveShortcut);
        
        save.setOnAction(action -> {
            tabActionPerformed("Save");
        });
        
        var saveAs = new MenuItem("Save As...");
        
        var saveAsShortcut = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN);
        
        saveAs.setAccelerator(saveAsShortcut);
        
        saveAs.setOnAction(action -> {
            tabActionPerformed("Save As...");
        });
        
        var exit = new MenuItem("Exit");
        
        var exitShortcut = new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN);
        
        exit.setAccelerator(exitShortcut);
        
        exit.setOnAction(action -> {
            exit();
        });
        
        var file = new Menu("File");
        
        file.getItems().addAll(newFile, openFile, save, saveAs, exit);
        
        var undo = new MenuItem("Undo");
        
        var undoShortcut = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_DOWN);
        
        undo.setAccelerator(undoShortcut);
        
        undo.setOnAction(action -> {
            tabActionPerformed("Undo");
        });
        
        var redo = new MenuItem("Redo");
        
        var redoShortcut = new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.CONTROL_DOWN);
        
        redo.setAccelerator(redoShortcut);
        
        redo.setOnAction(action -> {
            tabActionPerformed("Redo");
        });
        
        var cut = new MenuItem("Cut");
        
        var cutShortcut = new KeyCodeCombination(KeyCode.X, KeyCodeCombination.CONTROL_DOWN);
        
        cut.setAccelerator(cutShortcut);
        
        cut.setOnAction(action -> {
            tabActionPerformed("Cut");
        });
        
        var copy = new MenuItem("Copy");
        
        var copyShortcut = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN);
        
        copy.setAccelerator(copyShortcut);
        
        copy.setOnAction(action -> {
            tabActionPerformed("Copy");
        });
        
        var paste = new MenuItem("Paste");
        
        var pasteShortcut = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN);
        
        paste.setAccelerator(pasteShortcut);
        
        paste.setOnAction(action -> {
            tabActionPerformed("Paste");
        });
        
        var find = new MenuItem("Find...");
        
        var findShortcut = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN);
        
        find.setAccelerator(findShortcut);
        
        find.setOnAction(action -> {
            tabActionPerformed("Find...");
        });
        
        var replace = new MenuItem("Replace...");
        
        var replaceShortcut = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN);
        
        replace.setAccelerator(replaceShortcut);
        
        replace.setOnAction(action -> {
            tabActionPerformed("Replace...");
        });
        
        var edit = new Menu("Edit");
        
        edit.getItems().addAll(undo, redo, cut, copy, paste, find, replace);
        
        var menubar = new MenuBar();
        
        menubar.getMenus().addAll(file, edit);
        
        var borderPane = new BorderPane();
        
        borderPane.setTop(menubar);
        
        borderPane.setCenter(tabs);
        
        reopenFiles();
        
        var scene = new Scene(borderPane, 1280, 720);
        
        stage.setScene(scene);
        
        stage.show();
    }
    
    void reopenFiles() {
        var file = new File(String.format("%s/.luna.json", System.getProperty("user.home")));
        
        if (!file.exists()) {
            return;
        }
        
        try (var scanner = new Scanner(file)) {
            var data = new JSONObject(scanner.nextLine());
            
            var tabsData = data.getJSONObject("tabs");
            
            for (var title : tabsData.names()) {
                var tabData = tabsData.getJSONObject((String) title);
                
                var customTab = new CustomTab((String) title, tabData.getString("text"), tabData.getString("path"));
                
                customTab.tabCount = tabData.getInt("tabCount");
                
                customTab.wasSaved = tabData.getBoolean("wasSaved");
                
                tabs.getTabs().add(customTab);
            }
            
            var index = data.getInt("selectedIndex");
            
            tabs.getSelectionModel().select(index);
            
            var currentTab = (CustomTab) tabs.getTabs().get(index);
            
            currentTab.textArea.moveTo(data.getInt("carret"));
        } catch (FileNotFoundException e) {}
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
        
        var tab = new CustomTab(file.getName(), text.toString(), file.getPath());
        
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
    
    void exit() {
        var tabsData = new JSONObject();
        
        for (var tab : tabs.getTabs()) {
            var customTab = (CustomTab) tab;
            
            var tabData = new JSONObject();
            
            tabData.put("text", customTab.textArea.getText());
            
            tabData.put("path", customTab.path);
            
            tabData.put("tabCount", customTab.tabCount);
            
            tabData.put("wasSaved", customTab.wasSaved);
            
            tabsData.put(customTab.title, tabData);
        }
        
        var data = new JSONObject();
        
        data.put("tabs", tabsData);
        
        var index = tabs.getSelectionModel().getSelectedIndex();
        
        if (index != -1) {
            data.put("selectedIndex", index);
            
            var selectedTab = (CustomTab) tabs.getTabs().get(index);
            
            data.put("carret", selectedTab.textArea.getCaretPosition());
        }
        
        try (var writer = new FileWriter(String.format("%s/.luna.json", System.getProperty("user.home")))) {
            writer.write(data.toString());
        } catch (IOException e) {}
        
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch();
    }
}