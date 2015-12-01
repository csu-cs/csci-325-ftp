package com.csci;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.File;
import java.io.IOException;

/**
 * Created by Shea on 11/27/2015.
 */
public class Main {

    public static String host;
    public static String port;
    public static String userName;
    public static String password;
    public static boolean isConnected = false;
    static JFrame frame = new JFrame("The CSCI325 FTP Client!");
    public static Connect connection = new Connect();
    public static Commands commands = new Commands();
    public static final String FINAL_DIR = "C:/Users/Shea/Desktop/TestClient";
    public static MainGUI mainGUI = new MainGUI();

    public static void main(String[] args){
        run();
    }

    public static synchronized void run() {
        mainGUI.jRemoteTree.setModel(null);
        refreshTree(FINAL_DIR);
        mainGUI.pack();
        mainGUI.setVisible(true);
    }

    public static void refreshTree(String dir) {
        TreeModel model = new FileTreeModel(new File(dir));
        mainGUI.jLocalTree.setModel(model);
    }

    public static void refreshRemoteTree(String dir) {
        try {
            commands.setDir("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] stringArray = dir.split("\n");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("/");
        DefaultMutableTreeNode[] treeNodes = new DefaultMutableTreeNode[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = stringArray[i].substring(stringArray[i].lastIndexOf(" ") + 1);
            if(!stringArray[i].contains(".")){
                stringArray[i] += " (Folder)";
            }
            if (stringArray[i] == "."){
                root = new DefaultMutableTreeNode(".");
            } else {
                treeNodes[i] = new DefaultMutableTreeNode(stringArray[i]);
                root.add(treeNodes[i]);
            }
            DefaultTreeModel dtm = new DefaultTreeModel(root);
            mainGUI.jRemoteTree.setModel(dtm);
        }
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        Main.host = host;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        Main.port = port;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        Main.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Main.password = password;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void setIsConnected(boolean isConnected) {
        Main.isConnected = isConnected;
    }
}
