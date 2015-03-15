/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.File;
import javax.swing.border.*;

public class Explorer extends Frame {

    public Explorer() {
        try {
// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Error loading L&F: " + exc);
        }
        MenuBar mbar = new MenuBar();
        setMenuBar(mbar);
        Menu file = new Menu("File");
        MenuItem item1, item2, item3, item4;
        Menu subf = new Menu("New....", true);
        MenuItem item11, item12;
        subf.add(item11 = new MenuItem("Folder"));
        subf.add(item12 = new MenuItem("Text Document"));
        file.add(subf);
        item2 = new MenuItem("Open");
        file.add(item2);
        item3 = new MenuItem("Close");
        file.add(item3);
        item4 = new MenuItem("Quit....");
//file.add(item4 = new MenuItem("Quit...."));
        file.add(item4);
        mbar.add(file);
        Menu edit = new Menu("Edit");
        MenuItem item5, item6, item7, item8, item9;
        edit.add(item5 = new MenuItem("Undo"));
        edit.add(item6 = new MenuItem("Cut"));
        edit.add(item7 = new MenuItem("Copy"));
        edit.add(item8 = new MenuItem("Paste"));
        edit.add(new MenuItem("-"));
        edit.add(item9 = new MenuItem("Select All"));
        mbar.add(edit);
        Menu view = new Menu("View");
        MenuItem item20, item14, item15, item16, item17;
        Menu subv = new Menu("Explorer Bar.....", true);
        MenuItem item10, item13;
        subv.add(item10 = new MenuItem("History"));
        subv.add(item13 = new MenuItem("Folders"));
        view.add(subv);
        view.add(new MenuItem("-"));
        view.add(item14 = new MenuItem("Large Icons"));
        view.add(item15 = new MenuItem("Small Icons"));
        view.add(item16 = new MenuItem("List"));
        view.add(item17 = new MenuItem("Details"));
        view.add(new MenuItem("-"));
        Menu subv1 = new Menu("Arrange icons.....", true);
        MenuItem item18, item19;
        subv1.add(item18 = new MenuItem("by Name"));
        subv1.add(item19 = new MenuItem("by Type"));
        view.add(subv1);
        view.add(new MenuItem("-"));
        view.add(item20 = new MenuItem("Refresh"));
        mbar.add(view);
        Menu go = new Menu("Go");
        MenuItem item21, item22, item23;
        go.add(item21 = new MenuItem("Back"));
        go.add(item22 = new MenuItem("Forward"));
        go.add(new MenuItem("-"));
        go.add(item23 = new MenuItem("My Computer"));
        mbar.add(go);
        Menu tools = new Menu("Tools");
        MenuItem item24;
        tools.add(item24 = new MenuItem("Find(files/folders)"));
        mbar.add(tools);
        Menu help = new Menu("Help");
        MenuItem item25, item26;
        help.add(item25 = new MenuItem("Help Topic"));
        mbar.add(help);
        MyMenuHandler handler = new MyMenuHandler(this);
//item1.addActionListener(handler);
        item2.addActionListener(handler);
        item3.addActionListener(handler);
        item4.addActionListener(handler);
        item5.addActionListener(handler);
        item6.addActionListener(handler);
        item7.addActionListener(handler);
        item8.addActionListener(handler);
        item9.addActionListener(handler);
        item10.addActionListener(handler);
        item11.addActionListener(handler);
        item12.addActionListener(handler);
        item13.addActionListener(handler);
        item14.addActionListener(handler);
        item15.addActionListener(handler);
        item16.addActionListener(handler);
        item17.addActionListener(handler);
        item18.addActionListener(handler);
        item19.addActionListener(handler);
        item20.addActionListener(handler);

        final JTree tree = new JTree(createTreeModel());
        JScrollPane scrollPane = new JScrollPane(tree);
        FileNodeRenderer renderer = new FileNodeRenderer();
        AbstractBorder border = new EtchedBorder();
        tree.setEditable(true);
        tree.setCellRenderer(renderer);
// getContentPane().add(scrollPane, BorderLayout.CENTER);
        setLayout(new GridLayout(1, 2));
        JPanel contentPane = new JPanel();
        add(contentPane);
        contentPane.setBorder(border);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        JPanel contentPane1 = new JPanel();
        add(contentPane1);
        contentPane1.setBorder(border);
        MyWindowAdapter adapter = new MyWindowAdapter(this);
        addWindowListener(adapter);

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeCollapsed(TreeExpansionEvent e) {
            }

            public void treeExpanded(TreeExpansionEvent e) {
                TreePath path = e.getPath();
                FileNode node = (FileNode) path.getLastPathComponent();
                if (!node.isExplored()) {
                    DefaultTreeModel model
                            = (DefaultTreeModel) tree.getModel();
                    node.explore();
                    model.nodeStructureChanged(node);
                }
            }
        });
    }

    private DefaultTreeModel createTreeModel() {
        String dirname[] = {"A:/..", "C:/..", "D:/..", "E:/..", "F:/..", "G:/.."};
        File root = new File("C:/");
        FileNode rootNode = new FileNode(root), node;
        rootNode.explore();
        return new DefaultTreeModel(rootNode);
    }

    public static void main(String args[]) {
        Explorer f = new Explorer();
        f.setSize(new Dimension(400, 360));
        f.setVisible(true);
        /* GJApp.launch(new Explorer(),"JTree File Explorer",
         300,300,450,400);*/
    }
}

class MyWindowAdapter extends WindowAdapter {

    Explorer exp;

    public MyWindowAdapter() {
    }

    public MyWindowAdapter(Explorer exp) {
        this.exp = exp;
    }

    public void windowClosing(WindowEvent we) {
        exp.dispose();
        System.exit(0);
    }
}

class MyMenuHandler implements ActionListener, ItemListener {

    Explorer exp1;

    public MyMenuHandler(Explorer exp1) {
        this.exp1 = exp1;
    }

    public void actionPerformed(ActionEvent ae) {
    }

    public void itemStateChanged(ItemEvent ie) {
    }
}

class FileNode extends DefaultMutableTreeNode {

    private boolean explored = false, selected = false;

    public FileNode(File file) {
        setUserObject(file);
    }

    public boolean getAllowsChildren() {
        return isDirectory();
    }

    public boolean isLeaf() {
        return !isDirectory();
    }

    public File getFile() {
        return (File) getUserObject();
    }

    public void explore() {
        explore(false);
    }

    public boolean isExplored() {
        return explored;
    }

    public void setSelected(boolean s) {
        selected = s;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isDirectory() {
        File file = (File) getUserObject();
        return file.isDirectory();
    }

    public String toString() {
        File file = (File) getUserObject();
        String filename = file.toString();
        int index = filename.lastIndexOf("\\");
        return (index != -1 && index != filename.length() - 1)
                ? filename.substring(index + 1)
                : filename;
    }

    public void explore(boolean force) {
        if (!isExplored() || force) {
            File file = getFile();
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; ++i) {
                add(new FileNode(children[i]));
            }
            explored = true;
        }
    }
}

class FileNodeRenderer extends DefaultTreeCellRenderer {
// protected JCheckBox checkBox = new JCheckBox("backup");
// private Component strut = Box.createHorizontalStrut(8);

    private JPanel panel = new JPanel();

    public FileNodeRenderer() {
        panel.setBackground(
                UIManager.getColor("Tree.textBackground"));
        setOpaque(false);
// checkBox.setOpaque(false);
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.add(this);
// panel.add(strut);
// panel.add(checkBox);
    }

    public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean selected, boolean expanded,
            boolean leaf, int row,
            boolean hasFocus) {
        FileNode node = (FileNode) value;
        String s = tree.convertValueToText(value, selected,
                expanded, leaf, row, hasFocus);
        super.getTreeCellRendererComponent(
                tree, value, selected, expanded,
                leaf, row, hasFocus);
// checkBox.setVisible(node.isDirectory());
// checkBox.setSelected(node.isSelected());
        return panel;
    }
}
