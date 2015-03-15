/*
 * FileTree.java - This class shows a directory tree
 * Copyright (C) 2000 Iñigo González
 * sensei@hispavista.com
 * http://www.geocities.com/innigo.geo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jos.jexplorer;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;

public class FileTree extends FileComponent implements FileListener, DragGestureListener, DragSourceListener, DropTargetListener{

	//Default icons.
	private final ImageIcon openFolderIcon = ThemesManager.getImage("smallOpenFolder.gif");
	private final ImageIcon closeFolderIcon = ThemesManager.getImage("smallCloseFolder.gif");
	private final ImageIcon openZipFolderIcon = ThemesManager.getImage("smallZipOpenFile.gif");
	private final ImageIcon closeZipFolderIcon = ThemesManager.getImage("smallZipCloseFile.gif");

	private final ImageIcon smallFileIcon = ThemesManager.getImage("smallFile.gif");

	//Default root
	public static String ROOT_NAME = I18n.getString("MY_PC");

	/**
	 * The folder tree
	 */
	private JTree tree = new JTree();

	/**
	 * Allow to select if the control shows the content of zips of jars files
	 */
	private boolean viewZipJarAsFolders = true;

	//To support the Drag&Drop actions
	private DragSource dragSource;

	//To support the Drag&Drop actions
	private DropTarget dropTarget;

	/**
	 * true when the user is making a drag or drop operation. If it's true then select treePath
	 * but don't fire the fireSelectedListener
	 */
	private boolean dragOrDrop = false;

	/**
	 * Creates the folder tree.
	 */
	public FileTree(){
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setSelectionModel(selectionModel);
		tree.setEditable(true);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus){
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				String name = value.toString();
				if (name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar")){
					setIcon((selected) ? openZipFolderIcon : closeZipFolderIcon);
				} else {
					setIcon((selected) ? openFolderIcon : closeFolderIcon);
				}
				return this;
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent e){
				if (!dragOrDrop){
					TreePath newPath = e.getNewLeadSelectionPath();
					if (newPath != null){
						tree.scrollRowToVisible(tree.getRowForPath(newPath));
						Node node = (Node)newPath.getLastPathComponent();
						File file = (File)node.getUserObject();
						fireChangeListener(file);
					}
				}
			}
		});
		tree.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
					JPopupMenu menu = getPopupMenu();
					menu.show(tree, e.getX(), e.getY());
				}
			}
		});
		tree.setScrollsOnExpand(true);
		FileTreeModel treeModel = new FileTreeModel(getNodeRoot());
		tree.setModel(treeModel);

		setLayout(new BorderLayout());
		add(new JScrollPane(tree), BorderLayout.CENTER);

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_MOVE, this);
		dropTarget = new DropTarget(tree, this);
	}

	/**
	 * sets if the zip or jar files will be viewing as folders or not
	 */
	public void setViewZipJarAsFolders(boolean b){
		viewZipJarAsFolders = b;
	}

	/**
	 * Returns if the zip or jar files will be viewing as folders or not
	 */
	public boolean getViewZipJarAsFolders(){
		return viewZipJarAsFolders;
	}

	/**
	 * Selects the node that represents the folder folder.
	 * @param folder the folder to find.
	 */
	public void goToFolder(File folder){
		Node node = getNodeFromFile(folder);
		if (node != null){
			TreeNode[] nodes = node.getPath();
			TreePath tp = new TreePath(nodes);
			activateListener = false;
			tree.expandPath(tp);
			tree.setSelectionPath(tp);
			tree.makeVisible(tp);
			activateListener = true;
		}
	}

	/**
	 * Returns the Node from an specific folder
	 * @param folder the folder to look for
	 */
	private Node getNodeFromFile(File folder){
		if (folder != null){
			Stack stack = new Stack();
			File file = folder;

			while (file.getParent() != null){
				stack.push(file);
				file = file.getParentFile();
			}
			stack.push(file);
			boolean exit = false;
			Node node = (Node)tree.getModel().getRoot();
			boolean isFound = false;

			while (!exit){
				if (!stack.isEmpty()){
					File fileToFound = (File)stack.pop();
					isFound = false;
					if (node.getChildCount()>0){
						for(Enumeration e = node.children(); e.hasMoreElements();){
							Node n = (Node)e.nextElement();
							file = (File)n.getUserObject();
							if (fileToFound.equals(file)){
								isFound = true;
								node = n;
								break;
							}
						}
					} else {
						exit = true;
					}
				} else {
					exit = true;
				}
			}
			if (isFound){
				return node;
			} else {
				return null;
			}
		} else {
		  	return null;
		}
	}

	/**
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileListener
	 */
	public void folderChange(File folder){
		goToFolder(folder);
	}

	/**
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileListener
	 * @version 1.0.1
	 */
	public void folderContentChange(File folder){
		Node node = getNodeFromFile(folder);
		if (node!= null){
			node.refreshChildren();
			FileTreeModel ftm = (FileTreeModel)tree.getModel();
			ftm.nodeStructureChanged((TreeNode)node);
		}
	}

	public File getSelectionFile(){
		File file = null;
		TreePath selPath = tree.getSelectionPath();
		if (selPath != null){
			Node node = (Node)selPath.getLastPathComponent();
			if (node != null) {
				file = (File)node.getUserObject();
			}
		}
		return file;
	}

	public void fileSelected(ListNode listNode){}

	public void endLoadingProcess(){}

	public void fileDoubleClickSelected(ListNode listNode){}

	//DragGestureListener interface
	public void dragGestureRecognized(DragGestureEvent dge){
		Point point = dge.getDragOrigin();
		TreePath treePath = tree.getPathForLocation(point.x, point.y);
		if (treePath != null){
			Node node = (Node)treePath.getLastPathComponent();
			if (node != null) {
				File file = (File)node.getUserObject();
				if (file != null && file.equals(getSelectionFile())){
					File[] files = new File[1];
					files[0] = file;
					FileListSelection fileList = new FileListSelection(Arrays.asList(files));
					dragSource.startDrag(dge, DragSource.DefaultMoveDrop, fileList, this);
					dragOrDrop = true;
				}
			}
		}
	}

	//DragSourceListener interface
	//Invoked to signify that the Drag and Drop operation is complete.
	public void dragDropEnd(DragSourceDropEvent dsde){
	   dragOrDrop = false;
	}

	//Called as the hotspot enters a platform dependent drop site.
	public void dragEnter(DragSourceDragEvent dsde){
		dragOrDrop = true;
	}
	
	//Called as the hotspot exits a platform dependent drop site.
	public void dragExit(DragSourceEvent dse){
		dragOrDrop = false;
	}
	
	//Called as the hotspot moves over a platform dependent drop site.
	public void dragOver(DragSourceDragEvent dsde){}
	
	//Called when the user has modified the drop gesture.
	public void dropActionChanged(DragSourceDragEvent dsde){}

	//DropTargetListener interface
	//Called when a drag operation has encountered the DropTarget.
	public void dragEnter(DropTargetDragEvent dtde){
		DataFlavor[] flavours = dtde.getCurrentDataFlavors();
		for(int i = 0; i<flavours.length; i++){
			if (flavours[i].isFlavorJavaFileListType()){
				dtde.acceptDrag(DnDConstants.ACTION_MOVE);
				dragOrDrop = true;
				return;
			}
		}
	}

	//The drag operation has departed the DropTarget without dropping.
	public void dragExit(DropTargetEvent dte){
		dragOrDrop = false;
	}

	//Called when a drag operation is ongoing on the DropTarget.
	public void dragOver(DropTargetDragEvent dtde){
		Point point = dtde.getLocation();
		TreePath treePath = tree.getClosestPathForLocation(point.x, point.y);
		tree.setSelectionPath(treePath);
	}

	//The drag operation has terminated with a drop on this DropTarget.
	public void drop(DropTargetDropEvent dtde){
		try {
			Transferable transferable = dtde.getTransferable();
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
				dtde.acceptDrop(DnDConstants.ACTION_MOVE);
				java.util.List fileList = (java.util.List)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				File[] files = (File[])fileList.toArray(new File[1]);
				File destination = getSelectionFile();
				if (destination != null) {
					JFileCopy fileCopy = new JFileCopy(files, destination);
					fileCopy.setCopyListener(new JFileCopy.CopyListener(){
						public void endProcess(){
							TreePath selPath = tree.getSelectionPath();
							if (selPath != null){
								Node node = (Node)selPath.getLastPathComponent();
								if (node != null) {
									refresh(node);
									fireChangeListener((File)node.getUserObject());
								}
							}
						}
						public void cancelProcess(){
							endProcess();
						}
					});
				}
				dtde.getDropTargetContext().dropComplete(true);
				dragOrDrop = false;
			} else{
				dtde.rejectDrop();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			dtde.rejectDrop();
		} catch(UnsupportedFlavorException ufe){
			dtde.rejectDrop();
		}
	}

	//Called if the user has modified the current drop gesture.
	public void dropActionChanged(DropTargetDragEvent dtde){}

	/**
	 * Creates the root node. The root node hasn't parent and no file
	 * @return the root node.
	 */
	public Node getNodeRoot(){
		return new Node(null, null);
	}

	/**
	 * Inner class that represents a tree node.
	 */
	class Node extends DefaultMutableTreeNode{
		private boolean isNodeRoot = false;
		private boolean isNodeLeaf = false;

		public Node(TreeNode parent, File file){
			if ((parent == null) && (file == null)){
				isNodeRoot = true;
				userObject = null;
			} else {
				setParent((MutableTreeNode)parent);
				userObject = file;
				//isNodeLeaf = !file.isDirectory();
			}
			allowsChildren = true;
		}

		/**
		 * Returns the number of children that the file represented by this node has. This children
		 * are file and forlder if the userObject is a folder or Zipentries if the parent file is
		 * a Zip or Jar file.
		 * This function refresh the DefaultMutableTreeNode's children vector.
		 * @return the number of children
		 */
		public int getChildCount(){
			if (children == null){
				refreshChildren();
			}
			if (children != null){
				return children.size();
			} else {
				return 0;
			}
		}

		public void refreshChildren(){
			File[] files;
			if (isNodeRoot){
				files = File.listRoots();
			} else {
				File file = (File)userObject;
				files = file.listFiles(new FileFilter(){
					public boolean accept(File pathName){
						String path = pathName.getPath().toLowerCase();
						if (viewZipJarAsFolders){
							return pathName.isDirectory() || path.endsWith("zip") || path.endsWith("jar");
						} else {
							return pathName.isDirectory();
						}
					}
				});
			}
			if (files == null){
				children = null;
			} else if (files.length > 0){
				if (isNodeRoot){
					Arrays.sort(files);
				} else {
					java.util.List fileTree = Arrays.asList(files);
					Collections.sort(fileTree, new NameComparator());
				}
				children = new Vector(files.length);
				for(int i = 0; i < files.length; i++){
					children.add(new Node(this, files[i]));
				}
			} else {
				children = null;
			}
		}

		public boolean isLeaf(){
			return isNodeLeaf;
			//return false;
		}

		public String toString(){
			if (isNodeRoot){
				return FileTree.ROOT_NAME;
			} else {
				File file = (File)getUserObject();
				String fileName = file.getName();
				if (fileName.length() == 0){
					fileName = file.getPath();
				}
				return fileName;
			}
		}
	}

	/**
	 * A tree model for the file Tree.
	 * @version 1.0.1
	 */
	class FileTreeModel extends DefaultTreeModel{
		public FileTreeModel(TreeNode node){
			super(node);
		}

		/**
		 * This function is fired when the user is renaming a file in the file Tree.
		 * @param path the treePath edited
		 * @param newValue the new name for the file
		 * @version 1.0.1
		 */
		public void valueForPathChanged(TreePath path, Object newValue){
			Node node = (Node)path.getLastPathComponent();
			File oldFile = (File)node.getUserObject();
			File newFile = new File(oldFile.getParentFile(), (String)newValue);
			if (!newFile.exists()){
				if (oldFile.renameTo(newFile)){
					node.setUserObject(newFile);
					nodeChanged(node);
					refresh(node);
					fireChangeListener(newFile);
				} else {
			 	   JOptionPane.showMessageDialog(null, I18n.getString("RenameWarning2"));
				}
			} else {
			 	JOptionPane.showMessageDialog(null, I18n.getString("RenameWarning1"));
			}
		}
	}

	/**
	 * Refreshs the tree
	 * @param node Identifies the node to refresh
	 */
	private void refresh(Node node){
		node.refreshChildren();

		FileTreeModel ftm = (FileTreeModel)tree.getModel();
		ftm.nodeStructureChanged((TreeNode)node);
	}

	/**
	 * This method creates a new folder into the actual folder.
	 * It shows a dialog where the user can write the name for the new folder.
	 * If the name is in use, a error message will be showed.
	 * @version 1.0.1
	 */
	public void newFolder(){
		TreePath selPath = tree.getSelectionPath();
		if (selPath != null){
			Node node = (Node)selPath.getLastPathComponent();
			if (node != null) {
				File file = (File)node.getUserObject();
				if (file != null){
					String folderName = JOptionPane.showInputDialog(this, I18n.getString("NEW_FOLDER_QUESTION"), I18n.getString("NEW_FOLDER_TITLE"), JOptionPane.PLAIN_MESSAGE);
					if (folderName != null){
						File newFolder = new File(file, folderName);
						if (newFolder.mkdir()){
							refresh(node);
							fireContentChangeListener(newFolder);
						} else {
							JOptionPane.showMessageDialog(this, I18n.getString("NEW_FOLDER_WARNING"), I18n.getString("NEW_FOLDER_TITLE"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}

	/**
	 * Copies the selected files to the system clipboard
	 * @param file the file to copy.
	 */
	public void copy(File file){
		Clipboard clipboard = getToolkit().getSystemClipboard();
		File[] files = new File[1];
		files[0] = file;
		FileListSelection fileListSelection = new FileListSelection(Arrays.asList(files));
		clipboard.setContents(fileListSelection, fileListSelection);
	}

	/**
	 * Cuts the selected files to the system clipboard
 	 * @param file the file to copy.
	 */
	public void cut(File file){
		copy(file);
	}

	/**
	 * Returns if the Paste operation can be execute.
	 * It's used by the getPopupMenu.
	 * @return if the clipboard has the content of the fileList type.
	 */
	private boolean canPaste(){
		Clipboard clipboard = getToolkit().getSystemClipboard();
		try {
			Transferable td = clipboard.getContents(null);
			if (td != null) {
				Object obj = td.getTransferData(DataFlavor.javaFileListFlavor);
				return (obj != null);
			} else {
				return false;
			}
		} catch(UnsupportedFlavorException ufe){
			return false;
		} catch(IOException ioe){
			return false;
		}
	}

	private JPopupMenu getPopupMenu(){
		JPopupMenu menu = new JPopupMenu();
	  	JMenu mnuNew = new JMenu(I18n.getString("mnuFNew"));
	  	mnuNew.add(actions[NEW_FOLDER_ACTION]);
		menu.add(mnuNew);
		menu.add(actions[DELETE_FOLDER_ACTION]);
		menu.add(actions[EDIT_FOLDER_ACTION]);
		menu.addSeparator();
		menu.add(actions[REFRESH_FOLDER_ACTION]);
		menu.add(actions[EXPLORER_ACTION]);
		menu.addSeparator();
		menu.add(actions[COPY_ACTION]);
		menu.add(actions[CUT_ACTION]);
		if (canPaste()) menu.add(actions[PASTE_ACTION]);
		return menu;
	}

	private static int NEW_FOLDER_ACTION = 0;
	private static int DELETE_FOLDER_ACTION = 1;
	private static int EDIT_FOLDER_ACTION = 2;
	private static int REFRESH_FOLDER_ACTION = 3;
	private static int EXPLORER_ACTION = 4;
	private static int COPY_ACTION = 5;
	private static int CUT_ACTION = 6;
	private static int PASTE_ACTION = 7;

	/**
	 * Actions array
	 * @version 1.0.1
	 */	
	private Action[] actions = {
		new NewFolderAction(),
		new DeleteFolderAction(),
		new EditFolderAction(),
		new RefreshFolderAction(),
		new ExplorerAction(),
		new CopyAction(),
		new CutAction(),
		new PasteAction()
	};

	/**
	 * Actions
	 * @version 1.0.1
	 */
	class NewFolderAction extends AbstractAction{
		public NewFolderAction(){
			super(I18n.getString("mnuFNewFolder"));
		}
		public void actionPerformed(ActionEvent e){
			newFolder();
		}
	}

	class DeleteFolderAction extends AbstractAction implements JFileCopy.CopyListener{

		private Node node = null;

		public DeleteFolderAction(){
			super(I18n.getString("mnuFDelete"));
		}
		public void actionPerformed(ActionEvent e){
			TreePath selPath = tree.getSelectionPath();
			if (selPath != null){
				node = (Node)selPath.getLastPathComponent();
				if (node != null){
					File file = (File)node.getUserObject();
					if (file != null){
						if (JOptionPane.showConfirmDialog(null, I18n.getString("DeleteWindow"), I18n.getString("mnuFDelete"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
							tree.setSelectionPath(selPath.getParentPath());
							File[] files = new File[1];
							files[0] = file;
							JFileCopy fileCopy = new JFileCopy(files);
							fileCopy.setCopyListener(this);
						}
					}
				}
			}
		}

		public void endProcess(){
			if (node != null){
				Node parent = (Node)node.getParent();
				if (parent != null) {
					refresh(parent);
					File file = (File)parent.getUserObject();
					fireContentChangeListener((File)parent.getUserObject());
				}
			}
		}

		public void cancelProcess(){
			endProcess();
		}
	}

	class EditFolderAction extends AbstractAction{
		public EditFolderAction(){
			super(I18n.getString("mnuFRename"));
		}
		public void actionPerformed(ActionEvent e){
			tree.startEditingAtPath(tree.getSelectionPath());
		}
	}

	class RefreshFolderAction extends AbstractAction{
		public RefreshFolderAction(){
			super(I18n.getString("mnuERefresh"), ThemesManager.getImage("refreshAction.gif"));
		}
		public void actionPerformed(ActionEvent e){
			TreePath selPath = tree.getSelectionPath();
			if (selPath != null){
				Node node = (Node)selPath.getLastPathComponent();
				refresh(node);
			}
		}
	}

	class ExplorerAction extends AbstractAction{
		public ExplorerAction(){
			super(I18n.getString("mnuExplorer"));
		}
		public void actionPerformed(ActionEvent e){
			File file = getSelectionFile();
			if (file != null) new JExplorer(file.getPath());
		}
	}
	
	class CopyAction extends AbstractAction{
		public CopyAction(){
			super(I18n.getString("mnuFCopy"), ThemesManager.getImage("copyAction.gif"));
		}
		public void actionPerformed(ActionEvent e){
			File file = getSelectionFile();
			if (file != null) copy(file);
		}
	}

	class CutAction extends AbstractAction{
		public CutAction(){
			super(I18n.getString("mnuFCut"), ThemesManager.getImage("cutAction.gif"));
		}
		public void actionPerformed(ActionEvent e){
			File file = getSelectionFile();
			if (file != null) cut(file);
		}
	}

	class PasteAction extends AbstractAction implements JFileCopy.CopyListener{
		public PasteAction(){
			super(I18n.getString("mnuFPaste"), ThemesManager.getImage("pasteAction.gif"));
		}
		public void actionPerformed(ActionEvent e){
			Clipboard clipboard = getToolkit().getSystemClipboard();
			try {
				Transferable td = clipboard.getContents(null);
				if (td != null) {
					Object obj = td.getTransferData(DataFlavor.javaFileListFlavor);
					if (obj != null){
						//where to paste?
						File destination = getSelectionFile();
						if (destination != null){
							if (destination.isDirectory()){
								java.util.List listFiles = (java.util.List)obj;
								File[] files = (File[])listFiles.toArray(new File[1]);
								JFileCopy fileCopy = new JFileCopy(files, destination);
								fileCopy.setCopyListener(this);
							}
						}
					}
				}
			} catch(UnsupportedFlavorException ufe){
			} catch(IOException ioe){
			}
		}

		public void endProcess(){
			TreePath selPath = tree.getSelectionPath();
			if (selPath != null){
				Node node = (Node)selPath.getLastPathComponent();
				Node parent = (Node)node.getParent();
				if (parent != null) {
					refresh(parent);
					fireContentChangeListener((File)parent.getUserObject());
				}
			}
		}

		public void cancelProcess(){
			endProcess();
		}
	}

	/**
	 * Compares two listNode. A folder has always priority to a file.
	 * @see SizeComparator
	 * @see DateComparator
	 * @version 1.0.1
	 */
	class NameComparator implements Comparator{

		private static final int IS_GREATER = 1;
		private static final int IS_EQUAL = 0;
		private static final int IS_LESS = -1;

		/**
		 * Comperes two Files the first will be greater than the second listNode if the first if a folder and the second no or
		 * if the first file name is greater than the second file name. No equals names exist.
		 * @return 1 if the first file is a folder and the second file no or if the first name is greater than the second.
		 * @return -1 if the second file is a folder or if the second namme is greater than the first.
		 */
		public int compare(Object file1, Object file2){
			File f1 = (File)file1;
			File f2 = (File)file2;
			if (f1.isDirectory()){
				if (f2.isDirectory()){ //compares between two directories
					return f1.compareTo(f2);
				} else { //f1 is a directory and f2 not
					return IS_LESS;
				}
			} else if (f2.isDirectory()){ //f2 is a directory and f1 not
				return IS_GREATER;
			} else { //compares between two ListNode
				return f1.compareTo(f2);
			}
		}

		/**
		 * Not in use.
		 */
		public boolean equals(Object f1){
			return false;
		}
	}
}
