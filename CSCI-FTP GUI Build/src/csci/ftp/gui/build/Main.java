/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csci.ftp.gui.build;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author isiahjohnson
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
    public static final String FINAL_DIR = "/Users/isiahjohnson/Library/Mobile Documents/";
    public static MainGUI mainGUI = new MainGUI();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        run();
    }
    public static synchronized void run() {
        mainGUI.remoteTree.setModel(null);
        refreshTree(FINAL_DIR);
        mainGUI.pack();
        mainGUI.setVisible(true);
    }

    public static void refreshTree(String dir) {
        TreeModel model = new FileTreeModel(new File(dir));
        mainGUI.localTree.setModel(model);
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
            mainGUI.remoteTree.setModel(dtm);
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
    
