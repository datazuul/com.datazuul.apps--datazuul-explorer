package com.datazuul.apps.jexplorer.filetree;

import java.io.File;
import java.io.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * A tree node representing a file in a filesystem. Feature: lazy loading
 * enabled with special child count.
 *
 * @author ralf
 */
public class FileTreeNode extends DefaultMutableTreeNode {

    int childCount = -1;

    public FileTreeNode(File file) {
        super(file);

        // calculate childCount
        File[] fileChildren = file.listFiles(new FileFilter() {
            public boolean accept(File child) {
                return child.isDirectory() && !child.isHidden();
            }
        });
        if (fileChildren != null) {
            childCount = fileChildren.length;
        }
    }

    @Override
    public TreeNode getChildAt(int index) {
        if (children == null) {
            refreshChildren();
        }
        return (TreeNode) children.elementAt(index);
    }

    @Override
    public int getChildCount() {
        return childCount;
    }

    public Object getchildren() {
        return children;
    }

    public void refreshChildren() {
        childCount = 0;
        File[] fileChildren = ((File) getUserObject()).listFiles(new FileFilter() {
            public boolean accept(File child) {
                return child.isDirectory() && !child.isHidden();
            }
        });
        for (File file : fileChildren) {
            FileTreeNode fileTreeNode = new FileTreeNode(file);
            add(fileTreeNode);
        }
        childCount = children.size();
    }

}
