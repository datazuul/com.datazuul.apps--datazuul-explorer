/*
 * FileList - This control shows the content of a folder or Zip/Jar file.
 * Copyright (C) 2000-2001 Iñigo González
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

import javax.accessibility.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.border.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.odiseo.core.*;
/**
 * This control allows to see the content of a folder and of a zip/jar file
 * It also allow to navigate through the folders (to parent and into another folder)
 */ 
public class FileList extends FileComponent implements FileListener, Accessible, FocusListener, DragGestureListener, DragSourceListener, DropTargetListener{

	/**
	 * Unit increment for scroll bar
	 */
	private static int UNIT_INCREMENT = 40;

	/**
	 * View types allowed by FileList
	 */
	public final static int ICON_VIEW = 0;
	public final static int BRIEF_VIEW = 1;
	public final static int DETAILED_VIEW = 2;
	public final static int FILM_VIEW = 3;

	/**
	 * File orders types allowed by FileList
	 */
	public final static int ORDER_BY_NAME = 0;
	public final static int ORDER_BY_SIZE = 1;
	public final static int ORDER_BY_DATE = 2;

	/**
	 * La carpeta de inicio por defecto. En un futuro se deberá recogerá de un fichero de propiedades o desaparecer.
	 * The initial folder by default.
	 */
	public final static File DEFAULT_FILE = new File("C:\\"); //new File("\\");

	/**
	 * Folder which content is been showing.
	 */
	private File folder = null;

	/**
	 * Icon view type: ICON_VIEW, BRIEF_VIEW(by default) or DETAILED_VIEW.
	 */
	private int viewType;

	/**
	 * Tipo de ordenación con la que se muestran los ficheros. Sus valores posibles son 
	 * ORDER_BY_NAME (por defecto), ORDER_BY_SIZE u ORDER_BY_DATE.
	 */
	private int orderBy = ORDER_BY_NAME;

	/**
	 * Indica si se debe de ordenar la lista de ficheros de forma inversa.
	 */
	private boolean inverse = false;

	/**
	 * It is used to filter the files.
	 * @version 1.0.1
	 */
	private String filter = "";

	/**
	 * Mantiene el camino que se ha seguido. Lo utiliza la función goBack().
	 */
	private Stack backStack = new Stack();

	/**
	 * Mantiene el camino que se ha seguido. Lo utiliza la función goForward().
	 */
	private Stack forwardStack = new Stack();

	/**
	 * Mantiene los ficheros seleccionados de la vista.
	 */
	private SelectionModel selectionModel = new SelectionModel();

	/**
	 * Panel donde se visualizarán los ficheros.
	 */
	private JPanel main = new JPanel();

	/**
	 * El scroll de la pantalla.
	 */
	private JScrollPane scroll = new JScrollPane();

	/**
	 * FileLoader is the thread that loads the fileList.
	 */
	private FileLoader fileLoader = null;
	private ZipLoader zipLoader = null;
	
	//Those variables are for the animation of the logo
	private ImageIcon stopLogo = ThemesManager.getImage("jos_stop.gif");
	private ImageIcon startLogo = ThemesManager.getImage("jos_start.gif");
	private JLabel lblLogo = new JLabel(stopLogo);

	//To support Accessible interface
	private FileListContext accessibleContext;

	//To support the Drag&Drop actions
	private DragSource dragSource;
	private DropTarget dropTarget;

	/**
	 * Crea un FileList con los valores por defecto. Estos son: la carpeta
	 * raíz del sistema y la vista de Iconos grandes.
	 */
	public FileList(){
		this(DEFAULT_FILE);
	}

	/**
	 * Construye un FileList con vista de iconos grandes pero a partir de la carpeta especificada.
	 * @param folder La nueva carpeta de la cual se mostrará el contenido
	 */
	public FileList(File folder){
		this(folder, ICON_VIEW); //BRIEF_VIEW
	}

	/**
	 * Construye un FileList con una vista y una carpeta especificadas.
	 * @param folder La nueva carpeta de la cual se mostrará el contenido.
	 * @param viewType El tipo de vista. Los valores posibles son ICON_VIEW, BRIEF_VIEW y DETAILED_VIEW.
	 */
	public FileList(File folder, int viewType){
		this.viewType = viewType;
		main.setOpaque(true);
		main.setBackground(Color.white);
		setBackground(Color.white);
		scroll.setViewportView(main);
		scroll.getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT);
		scroll.getHorizontalScrollBar().setUnitIncrement(UNIT_INCREMENT);
		setFolder(folder, false, false);
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);

		lblLogo.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		lblLogo.setBackground(Color.white);
		addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_F5){
					refresh();
				} else if (key == KeyEvent.VK_RIGHT){
					selectNextFile();
				} else if (key == KeyEvent.VK_LEFT){
					selectPreviousFile();
				} else if (key == KeyEvent.VK_UP){
					selectPreviousVerticalFile();
				} else if (key == KeyEvent.VK_DOWN){
					selectNextVerticalFile();
				} else if (key == KeyEvent.VK_BACK_SPACE){
					goUp();
				} else if (key == KeyEvent.VK_HOME){
					selectFirstFile();
				} else if (key == KeyEvent.VK_END){
					selectLastFile();
				} else if (key == KeyEvent.VK_ENTER){
					doubleClickInSelectedFileView();
				}
			}
		});
		addFocusListener(this);
		main.addMouseListener(new MouseAdapter(){
			//public void mousePressed(MouseEvent e){}

			public void mouseClicked(MouseEvent e){
				requestFocus();
				Component component = main.getComponentAt(e.getPoint());
				if (component instanceof FileView){
					FileView fileView = (FileView)component;
					if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0){
						if (e.getClickCount() == 2){
							ListNode listNode = fileView.getListNode();
							doubleClickInAFileView(listNode);
						} else if (e.isControlDown()){
							if (selectionModel.isSelected(fileView)){
								selectionModel.remove(fileView);
							} else {
								selectionModel.add(fileView);
								fireFileSelectedListener(fileView.getListNode());
							}
						} else {
							selectionModel.clear();
							selectionModel.add(fileView);
							fireFileSelectedListener(fileView.getListNode());
						}
					} else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
						ListNode node = fileView.getListNode();
						JPopupMenu menu = null;
						if (selectionModel.isSelected(fileView)){
							if (selectionModel.getSize() == 1){
								menu = node.getSinglePopupMenu(FileList.this);
							} else {
								menu = node.getMultiPopupMenu(FileList.this, selectionModel.getListNodeArray());
							}
						} else {
							selectionModel.clear();
							selectionModel.add(fileView);
							menu = node.getSinglePopupMenu(FileList.this);
						}
						menu.show(main, e.getX(), e.getY());
					} else {
						selectionModel.clear();
						selectionModel.add(fileView);
					}
				} else {
					selectionModel.clear();
					if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
						JPopupMenu menu = getPopupMenu();
						menu.show(main, e.getX(), e.getY());
					}
				}
			}
		});
		
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(main, DnDConstants.ACTION_MOVE, this);
		dropTarget = new DropTarget(main, this);
	}

	/**
	 * Returns the logo animation. This function is usefull to show the logo in 
	 * the frame where the file list is showed.
	 */
	public JLabel getLogo(){
		return lblLogo;
	}

	/**
	 * Returns the number of files listed in the control
	 */
	public int getLength(){
		return main.getComponentCount();
	}

	/**
	 * Returns the cols used to show the files.
	 * In the list view and in the detailed view this value may be one.
	 */
	public int getCols(){
		FileListLayoutManager layout = (FileListLayoutManager)main.getLayout();
		return layout.getCols();
	}

	public boolean isFocusTraversable(){
		return true;
	}

	/**
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileComponent
	 */
	public void folderChange(File folder){
		setFolder(folder, true, false);
	}

	/**
	 * Implements the FileListener interface.
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileListener
	 * @version 1.0.1
	 */
	public void folderContentChange(File folder){
		refresh(true, false);
	}

	/**
	 * Implements the FileListener interface.
	 */
	public void fileSelected(ListNode listNode){}

	public void endLoadingProcess(){}

	public void fileDoubleClickSelected(ListNode listNode){}

	/**
	 * This method call the fireContentChangeListener method defined in the FileComponent class.
	 * Calls of the registers listerners
 	 * @see FileComponent
	 * @version 1.0.1
	 */
	public void fireContentChangeListener(){
		fireContentChangeListener(folder);
	}

	//Accessible interface
	public AccessibleContext getAccessibleContext(){
		if (accessibleContext == null){
			accessibleContext = new FileListContext();
		}
		return accessibleContext;
	}

	//FocusListener Interface
	//Invoked when a component gains the keyboard focus.
	public void focusGained(FocusEvent e){
		//System.out.println("focus gained");
		/*FileView fileView = selectionModel.getSelectedFileView();
		if (fileView != null){
			fileView.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		}*/
	}

	//Invoked when a component loses the keyboard focus.
	public void focusLost(FocusEvent e){
		//System.out.println("focus lost");
	}

	/**
	 * Cambia la carpeta de la cual se está visualizando su contenido. Esta operación hace
	 * que se refresque la pantalla. Este método no lanza el <i>fireChangeListener</i>
	 * @param folder La nueva carpeta a visualizar su contenido.
	 * @param back Si es true la carpeta antigua se añadirá a la pila de vuelta atras.
	 * @param back false en el caso de la función setBack, ya que no se quiere que esta acción se añada en la pila de marcha atras.
	 */
	private void setFolder(File folder, boolean back, boolean fireEvent){
		if (this.folder != null){
			if (back) if (!this.folder.equals(folder)){
				backStack.push(this.folder);
				forwardStack.clear();
			}
		}
		this.folder = folder;
		refresh(true, fireEvent);
	}

	/**
	 * Cambia la carpeta de la cual se está visualizando el contenido. A su vez
	 * hace que se refresque el contenido de la pantalla.
	 * Throws the <i>fireChangeListener</i>.
	 * @param folder La nueva carpeta a visualizar su contenido.
	 */
	public void setFolder(File folder){
		if (this.folder != folder) {
			setFolder(folder, true, true);
		}
	}
	
	/**
	 * Shows the content of the last folder visited.
	 */
	public void goBack(){
		//if (!backStack.empty()){
		//	forwardStack.push(folder);
		//	setFolder((File)backStack.pop(), false, true);
		//}
		goBack(1); // go back only one step
	}

	/**
	 * Shows the content of the steps-last folder visited.
	 * @param steps must be a number greatter than 0
	 */
	public void goBack(int steps){
		if (steps > 0 && backStack.size() >= steps)
			if (!backStack.empty()){
				File file = folder;
				for(int i = 0; i < steps; i++){
					forwardStack.push(file);
					file = (File)backStack.pop();
				}
				setFolder(file, false, true);
			}
	}

	/**
	 * Returns the first n elements from the back stack
	 * @param n the number of elements to return. If the number is greatter than the
	 * @param n size of the back stack then returns all the elements
	 */
	public java.util.List getBackList(int n){
		int last = n;
		if (n > backStack.size()) last = backStack.size();
		return backStack.subList(0, last);
	}

	/**
	 * Shows the content of the last?? folder visited.
	 */
	public void goForward(){
		//if (!forwardStack.empty()){
		//	backStack.push(folder);
		//	setFolder((File)forwardStack.pop(), false, true);
		//}
		goForward(1);
	}

	public void goForward(int steps){
		if (steps > 0 && forwardStack.size() >= steps)
			if (!forwardStack.empty()){
				File file = folder;
				for(int i = 0; i < steps; i++){
					backStack.push(file);
					file = (File)forwardStack.pop();
				}
				setFolder(file, false, true);
			}
	}

	/**
	 * Returns the first n elements from the forward stack
	 * @param n the number of elements to return. If the number is greatter than the
	 * @param n size of the forward stack then returns all the elements
	 */
	public java.util.List getForwardList(int n){
		int last = n;
		if (n > forwardStack.size()) last = forwardStack.size();
		return forwardStack.subList(0, last);
	}

	/**
	 * Visualiza el contenido de la carpeta padre de la actual.
	 * Shows the content of the actual folder's parent.
	 */
	public void goUp(){
		File newFolder = folder.getParentFile();
		if (newFolder != null){
			setFolder(newFolder);
		}
	}

	/**
	 * Visualiza el contenido de la carpeta de inicio( C:\ en windows o \ en Unix).
	 * Shows the content of the initial folder (C:\ in windows or \ in Unix).
	 */
	public void goHome(){
		setFolder(DEFAULT_FILE);
	}

	/**
	 * Devuleve la carpeta de la cual se está visualizando su contenido.
	 * @return la carpeta de la cual se está visualizando el contenido.
	 */
	public File getFolder(){
		return folder;
	}

	/**
	 * Cambia el tipo de vista del contenido de la carpeta actual. Refresca la pantalla.
	 * Los valores posibles son ICON_VIEW, BRIEF_VIEW o DETAILED_VIEW.
	 * @param viewType El modo de visualización que se quiere poner.
	 */
	public void setViewType(int viewType){
		if (this.viewType != viewType) {
			this.viewType = viewType;
			refresh(false, false);
		}
	}

	/**
	 * Devuleve el tipo de vista actual. Los valores posibles son ICON_VIEW, BRIEF_VIEW o DETAILED_VIEW.
	 * @return El modo de visualización actual.
	 */
	public int getViewType(){
		return viewType;
	}

	/**
	 * Cambia el tipo de ordenación del contenido de la carpeta actual. Refresca la pantalla.
	 * Los posibles son ORDER_BY_NAME, ORDER_BY_SIZE u ORDER_BY_DATE.
	 * @param orderBy El modo de ordenación que se quiere poner.
	 */
	public void setOrderBy(int orderBy){
		if (this.orderBy != orderBy) {
			this.orderBy = orderBy;
			refresh(false, false);
		}
	}

	/**
	 * Devuleve el tipo ordenación. Los posibles son ORDER_BY_NAME, ORDER_BY_SIZE u ORDER_BY_DATE.
	 * @return true si la ordenación se hace inversamente.
	 */
	public int getOrderBy(){
		return orderBy;
	}

	/**
	 * Cambia el tipo de ordenación del contenido de la carpeta actual. Refresca la pantalla.
	 * @param orderBy El modo de ordenación que se quiere poner. Los posibles son ORDER_BY_NAME, ORDER_BY_SIZE u ORDER_BY_DATE.
	 * @param inverse será verdadero si la ordenación se ha de invertir.
	 */
	public void setOrderBy(int orderBy, boolean inverse){
		if (this.orderBy != orderBy || this.inverse != inverse){
			this.orderBy = orderBy;
			this.inverse = inverse;
			refresh(false, true);
		}
	}

	/**
	 * Cambia la ordenación, para que sea inversa o no.
	 * @param inverse verdadero para que la ordenación se invierta.
	 */
	 public void setInverse(boolean inverse){
		if (this.inverse != inverse){
			this.inverse = inverse;
			refresh(false, true);
		}
	}

	/**
	 * Returns the order type, inverse or no.
	 * @return true if the file list is been ordered inversed.
	 */
	public boolean getInverse(){
		return inverse;
	}

	/**
	 * Sets the filter to used in the file list
	 * @filter to use.
	 * @version 1.0.1
	 */
	public void setFilter(String filter){
		this.filter = filter;
		refresh(false, true);
	}
	/**
	 * Returns the filter used in the file list.
	 * @return the filter.
	 * @version 1.0.1
	 */
	public String getFilter(){
		return filter;
	}

	/**
	 * Selects the first file in the file list.
	 * This function is called when the home key is pressed
	 */
	public void selectFirstFile(){
		if (main.getComponentCount() > 0){
			FileView selView = (FileView)main.getComponent(0);
			selectionModel.clear();
			selectionModel.add(selView);
		}
	}

	/**
	 * Selects the last file in the file list.
	 * This function is called when the end key is pressed
	 */
	public void selectLastFile(){
		int componentsCount = main.getComponentCount();
		if (componentsCount > 0){
			FileView selView = (FileView)main.getComponent(componentsCount-1);
			selectionModel.clear();
			selectionModel.add(selView);
		}
	}

	/**
	 * Selects the next file in the file list.
	 * This function is called when the right arrow key is pressed
	 */
	public void selectNextFile(){
		selectNextFile(1);
	}

	/**
	 * Selects the next vertical file in the file list.
	 * This function is called when the down arrow key is pressed
	 */
	public void selectNextVerticalFile(){
		selectNextFile(getCols());
	}

	/**
	 * Assigns the layout manager to the main component
	 * this function is necessary to assign a FileListLayoutManager
	 * @param layout the layout to assign (IconLayout, BriefLayout, DetailedLayout)
	 */
	private void setMainLayout(FileListLayoutManager layout){
		main.setLayout(layout);
	}

	/**
	 * Returns the index of a file view in the container
	 * @param fileView the fileview to look for
	 * @return the index of the param fileView. -1 if there are not files.
	 */
	private int getComponentIndex(FileView fileView){
		int componentsCount = main.getComponentCount();
		if (componentsCount == 0) return -1;
		for(int i = 0; i < componentsCount; i++){
			FileView selView = (FileView)main.getComponent(i);
			if (selView.equals(fileView)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Selects a file in the file list.
	 * @param cols the next file will be the actual selected file's index + cols
	 */
	private void selectNextFile(int cols){
		int componentsCount = main.getComponentCount();
		if (componentsCount > 0){
			FileView fileView = selectionModel.getSelectedFileView();
			FileView selView;
			if (fileView == null){
				selView = (FileView)main.getComponent(0);
				if (fileView == null) selectionModel.add(selView);
			} else {
				int i = getComponentIndex(fileView);
				if (i + cols < componentsCount){
					selView = (FileView)main.getComponent(i + cols);
					selectionModel.clear();
					selectionModel.add(selView);
				}
			}
		}
	}

	/**
	 * Selects the previous file in the file list.
	 * This function is called when the left arrow key is pressed
	 */
	public void selectPreviousFile(){
		selectPreviousFile(1);
	}

	/**
	 * Selects the previous vertical file in the file list.
	 * This function is called when the up arrow key is pressed
	 */
	public void selectPreviousVerticalFile(){
		selectPreviousFile(getCols());
	}

	/**
	 * Selects a previous file in the file list.
 	 * @param cols the next file will be the actual selected file's index - cols
	 */
	private void selectPreviousFile(int cols){
		int componentsCount = main.getComponentCount();
		if (componentsCount > 0){
			FileView fileView = selectionModel.getSelectedFileView();
			FileView selView;
			if (fileView == null){
				selView = (FileView)main.getComponent(componentsCount-1);
				if (fileView == null) selectionModel.add(selView);
			} else {
				int i = getComponentIndex(fileView);
				if (i-cols >= 0){
					selView = (FileView)main.getComponent(i-cols);
					selectionModel.clear();
					selectionModel.add(selView);
				}
			}
		}
	}

	/**
	 * Refreshes the files of the actual folder
	 */
	public void refresh(){
		refresh(false, false);
	}

	/**
	 * Refresca el contenido de la pantalla. Mostrará el contenido de la carpeta actual
	 * con el tipo de vista y la ordenación actuales.
	 * @param clearSelectionModel si es <i>true</i> limpia la selección de ficheros
	 * @param fireEvent if it's true then the ChangeListener event will be throw (no the ContentChangeListener).
	 */
	public void refresh(boolean clearSelectionModel, boolean fireEvent){
		if (fileLoader != null) fileLoader.stopLoader();
		fileLoader = new FileLoader(clearSelectionModel, fireEvent);
		fileLoader.start();
	}

	class FileLoader extends Thread {

		private boolean clearSelectionModel;
		private boolean fireEvent;

		private boolean stopLoading;

		public FileLoader(boolean clearSelectionModel, boolean fireEvent){
			this.clearSelectionModel = clearSelectionModel;
			this.fireEvent = fireEvent;
		}

		public void run(){
			lblLogo.setIcon(startLogo);
			stopLoading = false;
			ListNode node;
			if (folder == null){
				node = new FileListNode();
			} else {
				node = new FileListNode(folder);
			}
			ListNode[] nodes = null;
			if (node != null){
				nodes = node.getChildren(filter);//returns FileListNodes or ZipListNodes
			}
			if (stopLoading) nodes = null;
			if (nodes != null){
				java.util.List fileTable = Arrays.asList(nodes);
				if (orderBy == ORDER_BY_NAME){
					Collections.sort(fileTable, new NameComparator());
				} else if (orderBy == ORDER_BY_SIZE){
					Collections.sort(fileTable, new SizeComparator());
				} else {
					Collections.sort(fileTable, new DateComparator());
				}
				if (inverse) {
					Collections.reverse(fileTable);
				}
				nodes = (ListNode[])fileTable.toArray();

				scroll.setViewportView(null);
				main.invalidate();
				//if (clearSelectionModel){
					selectionModel.clear();
				//}
				main.removeAll();
				if (viewType == BRIEF_VIEW){
					setMainLayout(new BriefLayout(scroll.getViewport()));
				} else if (viewType == ICON_VIEW){
					setMainLayout(new IconLayout(scroll.getViewport()));
				} else if (viewType == DETAILED_VIEW){
					setMainLayout(new DetailedLayout(scroll.getViewport()));
				} else {//if (viewType == FILM_VIEW){
					setMainLayout(new IconLayout(5, 5, FilmFileView.WIDTH, FilmFileView.HEIGHT+20, scroll.getViewport()));
				}
				for(int i=0; i<nodes.length; i++){
					FileView fileView;
					if (viewType == BRIEF_VIEW){
						 fileView = new BriefFileView(nodes[i]);
					} else if (viewType == ICON_VIEW){
						 fileView = new IconFileView(nodes[i]);
					} else if (viewType == DETAILED_VIEW){
						 fileView = new DetailedFileView(nodes[i]);
					} else { //if (viewType == FILM_VIEW){
						 fileView = new FilmFileView(nodes[i]);
					}
					main.add((Component)fileView);
					if (stopLoading) break;
					//fileView.setSelected(selectionModel.isSelected(fileView));
				}
				if (!stopLoading){
					scroll.setViewportView(main);
					main.repaint();
					main.validate();
					fireEndLoadingProcess();
					if (fireEvent) fireChangeListener(folder);
				}
			}
			lblLogo.setIcon(stopLogo);
		}

		public void stopLoader(){
			stopLoading = true;
		}
	}

	/**
	 * Selecciona todos los ficheros que se están mostrando en la actualidad.
	 */
	public void selectAll(){
		//FileView[] fileViews = (FileView[])getComponents();
		Component[] components = main.getComponents();
		selectionModel.clear();
		for(int i = 0; i<components.length; i++){
			selectionModel.add((FileView)components[i]);
		}
	}

	/**
	 * Creates a new folder into the actual folder.
	 * It shows a dialog where the user can write the name for the new folder.
	 * If the name is in use, a error message will be showed.
	 */
	public void newFolder(){
		String folderName = JOptionPane.showInputDialog(this, I18n.getString("NEW_FOLDER_QUESTION"), I18n.getString("NEW_FOLDER_TITLE"), JOptionPane.PLAIN_MESSAGE);
		if (folderName != null){
			File newFolder = new File(folder, folderName);
			if (newFolder.mkdir()){
				refresh(true, false);
				fireContentChangeListener(folder);
			} else {
				JOptionPane.showMessageDialog(this, I18n.getString("NEW_FOLDER_WARNING"), I18n.getString("NEW_FOLDER_TITLE"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Opens the file properties window
	 */
	private void showFileProperties(){
		ListNode listNode = selectionModel.getSelectedListNode();
		if (listNode != null){
			FilePropertiesWindow fpw = new FilePropertiesWindow(listNode);
		}
	}

	/**
	 * Returns the selected files.
	 * @return the selected files
	 */
	public File[] getSelectedFiles(){
		return selectionModel.getFileArray();
	}

	/**
	 * Returns the last selected file.
	 * @return the last selected file
	 */
	public File getSelectedFile(){
		return selectionModel.getSelectedFile();
	}

	/**
	 * Returns the selected file list's Nodes.
	 * @return an array of selected List Nodes
	 */
	public ListNode[] getSelectedListNodes(){
		return selectionModel.getListNodeArray();
	}

	/**
	 * Returns the last selected file list's node.
	 * @return the last selected ListNode
	 */
	public ListNode getSelectedListNode(){
		return selectionModel.getSelectedListNode();
	}

	/**
	 * Copies the selected files to the system clipboard
	 */
	public void copy(){
		Clipboard clipboard = getToolkit().getSystemClipboard();
		File[] files = getSelectedFiles();
		FileListSelection fileListSelection = new FileListSelection(Arrays.asList(files));
		clipboard.setContents(fileListSelection, fileListSelection);
	}

	/**
	 * Cuts the selected files to the system clipboard.
	 * At this moment the cut operation will be like the copy operation!!!
	 */
	public void cut(){
		copy();
	}

	/**
	 * This function is called when the user press the enter key.
	 */
	private void doubleClickInSelectedFileView(){
		ListNode listNode = selectionModel.getSelectedListNode();
		if (listNode != null) doubleClickInAFileView(listNode);
	}
	
	/**
	 * When the user click in a file or type an enter.
	 * @param listNode file who recived the double click or the enter key
	 */
	private void doubleClickInAFileView(ListNode listNode){
		if (listNode.isDirectory()){
			Object obj = listNode.getObject();
			if (obj instanceof File){
				File newFolder = (File)listNode.getObject();
				setFolder(newFolder);
			}
		} else {
			String typeName = listNode.getTypeName();
			if (typeName.equals("Zip file")){// || typeName.equals("Jar file")){
				File newFolder = (File)listNode.getObject();
				if (newFolder != null){
					setFolder(newFolder);
				}
			} else {//Executes an external jar file
				if (typeName.equals("Jar file") && OdiseoClassLoader.getMainClassName(listNode.getURL()) == null){
					File newFolder = (File)listNode.getObject();
					if (newFolder != null){
						setFolder(newFolder);
					}
				} else {
					String url;
					String[] args;
					String className;
					if (typeName.equals("Jar file")){
						args = new String[]{""};
						className = "";
						url = listNode.getFullName();
					} else{ //executes the associated file
						url = listNode.getUrl();
						args = new String[]{listNode.getFullName()};
						//System.out.println("url=" + url);
					}
					OdiseoProcess op = OdiseoProcess.createProcess(url, args);
					fireDoubleClickFileSelectedListener(listNode);
				}
			}
		}
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

	/**
	 * Creates a zip file with the selected files
	 */
	class ZipLoader extends Thread{

		private boolean stopLoading;
		
		public void run(){
			lblLogo.setIcon(startLogo);
			stopLoading = false;
			boolean cancel = false;
			File file = getSelectedFile();
			String zipName = file.getPath();
			int last = zipName.lastIndexOf(".");
			if (last == -1){
				zipName += ".zip";
			} else {
				zipName = zipName.substring(0, last) + ".zip";
			}
			try{
				File zip = new File(zipName);
				if (zip.exists()){
					int ans = JOptionPane.showConfirmDialog(FileList.this, I18n.getString("EXTRACT_TITLE_TO") + " " + zipName + "\n" + I18n.getString("EXTRACT_QUESTION"), I18n.getString("EXTRACT_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if (ans == JOptionPane.YES_OPTION){
						if (!zip.delete()){
							JOptionPane.showMessageDialog(FileList.this, I18n.getString("EXTRACT_WARNING"), I18n.getString("EXTRACT_TITLE"), JOptionPane.ERROR_MESSAGE);
							cancel = true;
						}
						zip.createNewFile();
					} else if (ans == JOptionPane.CANCEL_OPTION){
						cancel = true;
					}
				}
				if (!cancel){
					ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
					zos.setComment("Created by JExplorer. http://www.geocities.com/innigo.geo");
					File[] files = getSelectedFiles();
					for(int i = 0; i < files.length; i++){
						if (stopLoading) break;
						zipFile("", zos, files[i]);
					}
					zos.close();
					if (stopLoading){
						zip.delete();
					} else {
						refresh(true, false);
						fireContentChangeListener(folder);
					}
				}
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
			lblLogo.setIcon(stopLogo);
		}
	
		private void zipFolder(String path, ZipOutputStream zos, File file) throws IOException{
			ZipEntry entry = new ZipEntry(path + "/");
			entry.setTime(file.lastModified());
			zos.putNextEntry(entry);
			File[] files = file.listFiles();
			for(int i = 0; i < files.length; i++){
				if (stopLoading) break;
				zipFile(path, zos, files[i]);
			}
		}
	
		private void zipFile(String path, ZipOutputStream zos, File file) throws IOException{
			String name = file.getName();
			if (path.length() > 0) name = path + "/" + name;
			if (file.isDirectory()){
				zipFolder(name, zos, file);
			} else {
				ZipEntry entry = new ZipEntry(name);
				entry.setSize(file.length());
				entry.setTime(file.lastModified());
				zos.putNextEntry(entry);
				FileInputStream fis = new FileInputStream(file);
				int c;
				while ((c = fis.read()) != -1){
					if (stopLoading) break;
					zos.write(c);
				}
				fis.close();
				zos.closeEntry();
			}
		}
		
		public void stopLoader(){
			stopLoading = true;
		}
	}

	private JPopupMenu getPopupMenu(){
		JPopupMenu menu = new JPopupMenu();
	  	JMenu mnuNew = new JMenu(I18n.getString("mnuFNew"));
	  	mnuNew.add(actions[NEW_FOLDER_ACTION]);
		menu.add(mnuNew);
		menu.addSeparator();
		menu.add(actions[BACK_ACTION]);
		menu.add(actions[UP_ACTION]);
		menu.add(actions[FORWARD_ACTION]);
		menu.add(actions[HOME_ACTION]);
		menu.addSeparator();
		JMenu mnuView = new JMenu(I18n.getString("mnuLayout"));
		mnuView.add(actions[ORDER_ACTION]);
		mnuView.addSeparator();
		mnuView.add(actions[ICON_VIEW_ACTION]);
		mnuView.add(actions[BRIEF_VIEW_ACTION]);
		mnuView.add(actions[DETAILED_VIEW_ACTION]);
		mnuView.add(actions[FILM_VIEW_ACTION]);
		menu.add(mnuView);
		menu.addSeparator();
		menu.add(actions[SELECT_ALL_ACTION]);
		if (canPaste()) menu.add(actions[PASTE_ACTION]);
		return menu;
	}

	/**
	 * Return the action by index
 	 * @return an action
	 * @version 1.0.1
	 */
	public Action getAction(int index){
		if (index >= 0 && index < actions.length){
			return actions[index];
		} else {
		  return null;
		}
	}

	/**
	 * FileList's actions constants
	 * @version 1.0.1
	 */
	public static final int BACK_ACTION = 0;
	public static final int UP_ACTION = 1;
	public static final int FORWARD_ACTION = 2;
	public static final int REFRESH_ACTION = 3;
	public static final int HOME_ACTION = 4;
	public static final int ICON_VIEW_ACTION = 5;
	public static final int BRIEF_VIEW_ACTION = 6;
	public static final int DETAILED_VIEW_ACTION = 7;
	public static final int FILM_VIEW_ACTION = 8;
	public static final int ORDER_ACTION = 9;
	public static final int NEW_FOLDER_ACTION = 10;
	public static final int SELECT_ALL_ACTION = 11;
	public static final int FILE_PROPERTIES = 12;
	public static final int COPY_ACTION = 13;
	public static final int CUT_ACTION = 14;
	public static final int PASTE_ACTION = 15;
	public static final int ZIP_ACTION = 16;
	public static final int STOP_ACTION = 17;

	/**
	 * FileList's action array
	 * @version 1.0.1
	 */	
	private Action[] actions = {
		new BackAction(),
		new UpAction(),
		new ForwardAction(),
		new RefreshAction(),
		new HomeAction(),
		new IconViewAction(),
		new BriefViewAction(),
		new DetailedViewAction(),
		new FilmViewAction(),
		new OrderAction(),
		new NewFolderAction(),
		new SelectAllAction(),
		new FilePropertiesAction(),
		new CopyAction(),
		new CutAction(),
		new PasteAction(),
		new ZipAction(),
		new StopAction()
	};

	class BackAction extends AbstractAction{
		public BackAction(){
			super(I18n.getString("BackAction"), ThemesManager.getImage("backAction.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("BackActionToolTip"));
		}
		public void actionPerformed(ActionEvent e){
			goBack();
		}
	}

	class UpAction extends AbstractAction{
		public UpAction(){
			super(I18n.getString("UpAction"), ThemesManager.getImage("upAction.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("UpActionToolTip"));
		}
		public void actionPerformed(ActionEvent e){
			goUp();
		}
	}

	class ForwardAction extends AbstractAction{
		public ForwardAction(){
			super(I18n.getString("ForwardAction"), ThemesManager.getImage("forwardAction.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("ForwardActionToolTip"));
		}
		public void actionPerformed(ActionEvent e){
			goForward();
		}
	}

	class RefreshAction extends AbstractAction{
		public RefreshAction(){
			super(I18n.getString("mnuERefresh"), ThemesManager.getImage("refreshAction.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("mnuERefresh"));
			putValue(Action.ACCELERATOR_KEY, "(released)? VK_F2");
		}
		public void actionPerformed(ActionEvent e){
			refresh(true, false);
			//fireContentChangeListener();
		}
	}

	class HomeAction extends AbstractAction{
		public HomeAction(){
			super(I18n.getString("HomeAction"), ThemesManager.getImage("homeAction.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("HomeActionToolTip"));
		}
		public void actionPerformed(ActionEvent e){
			goHome();
		}
	}

	class IconViewAction extends AbstractAction{
		public IconViewAction(){
			super(I18n.getString("mnuLIcon"), ThemesManager.getImage("iconView.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("mnuLIcon"));
		}
		public void actionPerformed(ActionEvent e){
			setViewType(FileList.ICON_VIEW);
		}
	}

	class BriefViewAction extends AbstractAction{
		public BriefViewAction(){
			super(I18n.getString("mnuLBrief"), ThemesManager.getImage("briefView.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("mnuLBrief"));
		}
		public void actionPerformed(ActionEvent e){
			setViewType(FileList.BRIEF_VIEW);
		}
	}

	class DetailedViewAction extends AbstractAction{
		public DetailedViewAction(){
			super(I18n.getString("mnuLDetailed"), ThemesManager.getImage("detailedView.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("mnuLDetailed"));
		}
		public void actionPerformed(ActionEvent e){
			setViewType(FileList.DETAILED_VIEW);
		}
	}

	class FilmViewAction extends AbstractAction{
		public FilmViewAction(){
			super(I18n.getString("mnuLFilm"), ThemesManager.getImage("filmView.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("mnuLFilm"));
		}
		public void actionPerformed(ActionEvent e){
			setViewType(FileList.FILM_VIEW);
		}
	}

	class OrderAction extends AbstractAction{
		public OrderAction(){
			super(I18n.getString("mnuLOrder"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("mnuLOrder"));
		}
		public void actionPerformed(ActionEvent e){
			OrderWindow orderWindow = new OrderWindow(getOrderBy(), getInverse());
			if (!orderWindow.isCancelled()){
				setOrderBy(orderWindow.getViewType(), orderWindow.getInverse());
			}
		}
	}

	class NewFolderAction extends AbstractAction{
		public NewFolderAction(){
			super(I18n.getString("mnuFNewFolder"));
		}
		public void actionPerformed(ActionEvent e){
			newFolder();
		}
	}

	class SelectAllAction extends AbstractAction{
		public SelectAllAction(){
			super(I18n.getString("mnuESelectAll"));
		}
		public void actionPerformed(ActionEvent e){
			selectAll();
		}
	}

	class FilePropertiesAction extends AbstractAction{
		public FilePropertiesAction(){
			super(I18n.getString("mnuFFilePropertiesWindow"));
		}
		public void actionPerformed(ActionEvent e){
			showFileProperties();
		}
	}

	class CopyAction extends AbstractAction{
		public CopyAction(){
			super(I18n.getString("mnuFCopy"), ThemesManager.getImage("copyAction.gif"));
		}
		public void actionPerformed(ActionEvent e){
			copy();
		}
	}

	class CutAction extends AbstractAction{
		public CutAction(){
			super(I18n.getString("mnuFCut"), ThemesManager.getImage("cutAction.gif"));
		}
		public void actionPerformed(ActionEvent e){
			cut();
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
						File destination = getSelectedFile();
						if (destination != null) {
						   if (!destination.isDirectory()) destination = folder;
						} else {
							destination = folder;
						}
						if (destination != null){
							java.util.List listFiles = (java.util.List)obj;
							File[] files = (File[])listFiles.toArray(new File[1]);
							JFileCopy fileCopy = new JFileCopy(files, folder);
							fileCopy.setCopyListener(this);
						}
					}
				}
			} catch(UnsupportedFlavorException ufe){
			} catch(IOException ioe){
			}
		}

		public void endProcess(){
			refresh(true, true);
			fireContentChangeListener(folder);
		}

		public void cancelProcess(){
			endProcess();
		}
	}

	class ZipAction extends AbstractAction{
		public ZipAction(){
			super(I18n.getString("mnuZip")); //, ThemesManager.getImage("zipAction.gif"));
		}
		public void actionPerformed(ActionEvent e){
			if (zipLoader != null) zipLoader.stopLoader();
			zipLoader = new ZipLoader();
			zipLoader.start();
		}
	}

	/**
	 * Stops all the thread (if they are running).
	 * Stops the loading thread and the zip thread.
	 */
	class StopAction extends AbstractAction{
		public StopAction(){
			super(I18n.getString("StopAction"), ThemesManager.getImage("stopAction.gif"));
			putValue(Action.SHORT_DESCRIPTION, I18n.getString("StopActionTooltip"));
		}
		public void actionPerformed(ActionEvent e){
			if (zipLoader != null) zipLoader.stopLoader();
			if (fileLoader != null) fileLoader.stopLoader();
		}
	}

	protected class FileListContext extends AccessibleJComponent{
	}

	//DragGestureListener interface
	public void dragGestureRecognized(DragGestureEvent dge){
		File[] files = getSelectedFiles();
		if (files != null){
			FileListSelection fileList = new FileListSelection(Arrays.asList(files));
			dragSource.startDrag(dge, DragSource.DefaultMoveDrop, fileList, this);
		}
	}

	//DragSouceListener interface
	//Invoked to signify that the Drag and Drop operation is complete.
	public void dragDropEnd(DragSourceDropEvent dsde){}

	//Called as the hotspot enters a platform dependent drop site.
	public void dragEnter(DragSourceDragEvent dsde){}

	//Called as the hotspot exits a platform dependent drop site.
	public void dragExit(DragSourceEvent dse){}

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
				return;
			}
		}
	}

	//The drag operation has departed the DropTarget without dropping.
	public void dragExit(DropTargetEvent dte){}

	//Called when a drag operation is ongoing on the DropTarget.
	public void dragOver(DropTargetDragEvent dtde){}

	//The drag operation has terminated with a drop on this DropTarget.
	public void drop(DropTargetDropEvent dtde){
		try {
			Transferable transferable = dtde.getTransferable();
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
				dtde.acceptDrop(DnDConstants.ACTION_MOVE);
				java.util.List fileList = (java.util.List)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				File[] files = (File[])fileList.toArray(new File[1]);
				if (folder != null){
					JFileCopy fileCopy = new JFileCopy(files, folder);
					fileCopy.setCopyListener(new JFileCopy.CopyListener(){
						public void endProcess(){
							refresh(true, false);
							fireContentChangeListener(folder);
						}

						public void cancelProcess(){
							endProcess();
						}
					});
				}
				dtde.getDropTargetContext().dropComplete(true);
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
	public void dropActionChanged(DropTargetDragEvent dtde){
		System.out.println("dropActionchanged");
	}
}
