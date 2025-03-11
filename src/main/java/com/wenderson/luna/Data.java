package com.wenderson.luna;

import java.io.*;
import java.util.Scanner;
import org.json.JSONObject;
import javafx.scene.control.TabPane;

public class Data {
    static void save(TabPane tabs, int selectedIndex, int carretPosition) {
        var tabsData = new JSONObject();
        
        var count = 0;
        
        for (var tab : tabs.getTabs()) {
            var tabData = new JSONObject();
            
            var customTab = (CustomTab) tab;
            
            tabData.put("title", customTab.title);
            
            tabData.put("text", customTab.codeArea.getText());
            
            tabData.put("path", customTab.path);
            
            tabData.put("tabCount", customTab.tabCount);
            
            tabData.put("wasSaved", customTab.wasSaved);
            
            tabsData.put(Integer.toString(count), tabData);
            
            count++;
        }
        
        var data = new JSONObject();
        
        data.put("tabs", tabsData);
        
        data.put("selectedIndex", selectedIndex);
        
        data.put("caretPosition", carretPosition);
        
        var path = String.format("%s/.luna.json", System.getProperty("user.home"));
        
        try (var writer = new FileWriter(path)) {
            writer.write(data.toString());
        } catch (IOException e) {}
    }
    
    static void reopenFiles(TabPane tabs) {
        var path = String.format("%s/.luna.json", System.getProperty("user.home"));
        
        var file = new File(path);
        
        if (!file.exists()) {
            return;
        }
        
        try (var scanner = new Scanner(file)) {
            var data = new JSONObject(scanner.nextLine());
            
            var tabsData = data.getJSONObject("tabs");
            
            for (var id : tabsData.names()) {
                var tabData = tabsData.getJSONObject((String) id);
                
                var customTab = new CustomTab(tabData.getString("title"), tabData.getString("text"), tabData.getString("path"));
                
                customTab.tabCount = tabData.getInt("tabCount");
                
                customTab.wasSaved = tabData.getBoolean("wasSaved");
                
                tabs.getTabs().add(customTab);
                
                customTab.codeArea.moveTo(data.getInt("caretPosition"));
            }
            
            tabs.getSelectionModel().select(data.getInt("selectedIndex"));
        } catch (FileNotFoundException e) {}
    }
}