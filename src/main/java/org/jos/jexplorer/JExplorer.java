/*
 * JExplorer.java - File browser for Jos
 * Copyright (C) 2000-2001 I�igo Gonz�lez
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.odiseo.core.Odiseo;

public class JExplorer extends JFrame implements FileListener {

	private static final String APP_NAME = "JExplorer";
	private static final String APP_VERSION = "1.2.2";
	public static final String USER_HOME = System.getProperty("user.home")
			+ File.separator + ".jexplorer" + File.separator;
	public static final String XML_FILE = USER_HOME + "favorites.xml";

	/**
	 * JExplorer has those components.
	 */
	private LocationBar locationBar = null;
	private FileTree fileTree = null;
	private FileList fileList = null;
	private FileViewer fileViewer = null;
	private StatusBar statusBar = null;
	// private Favorites favoritesMenu = null;
	private FavoritesPanel favPanel = null;

	/**
	 * true if the quick viewers are activated
	 */
	private boolean isViewActivated = false;

	/**
	 * Instance number. It's used to know when to make a exit(0) when there are
	 * more than one Jexplorer running
	 */
	private static int numInstances = 0;

	/**
	 * The initial progress window. It's only used if the numInstances is equal
	 * to 1.
	 * 
	 * @version 1.0.1
	 */
	private AboutWindow pf = null;

	/**
	 * This split is used to show the FileList and the viewers
	 */
	private JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	static {
		// Crea la carpeta .jexplorer para la aplicaci�n
		File jexplorerUserFolder = new File(USER_HOME);
		if (!jexplorerUserFolder.exists())
			jexplorerUserFolder.mkdir();
	}

	/**
	 * Construct a JExplorer. Creates the graphical interface and add one to the
	 * instances number.
	 */
	public JExplorer() {
		this("");
	}

	/**
	 * Construye un JExplorer con una ruta como la inicial. Constructs a
	 * JExplorer showing a path. El JExplorer creado muestra inicialmente el
	 * path que se le ha dado. Crea la interface gr�fica y aumenta en uno el
	 * n�mero de instancias de JExplorers.
	 * 
	 * @param path
	 *            Camino a mostrar inicialmente
	 * @param path
	 *            path to shows initialy.
	 */
	public JExplorer(String path) {
		super(APP_NAME + " v" + APP_VERSION);
		numInstances++;
		initProgressFrame(APP_NAME + " v" + APP_VERSION, 8);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				unloadMe();
			}
		});

		nextProgressFrame(I18n.getString("BUILD_TREE_LIST"));
		fileTree = new FileTree();
		fileList = new FileList();

		rightSplit.setLeftComponent(fileList);
		rightSplit.setOneTouchExpandable(true);

		JTabbedPane tab = new JTabbedPane();
		tab.add(I18n.getString("TREE_TITLE"), fileTree);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tab,
				rightSplit);
		split.setOneTouchExpandable(true);

		JPanel main = new JPanel(new BorderLayout());
		main.add(split, BorderLayout.CENTER);

		nextProgressFrame(I18n.getString("LOADING_VIEWS"));
		fileViewer = new FileViewer();
		fileViewer.loadViews();
		rightSplit.setDividerLocation(0);

		nextProgressFrame(I18n.getString("BUILD_TOOL"));
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(createToolBar(), BorderLayout.NORTH);

		nextProgressFrame(I18n.getString("BUILD_LOCATION"));
		locationBar = new LocationBar();
		main.add(locationBar, BorderLayout.NORTH);

		nextProgressFrame(I18n.getString("BUILD_FAVORITES_MENU"));
		// favoritesMenu = new Favorites(XML_FILE);
		favPanel = new FavoritesPanel(XML_FILE);
		tab.add(I18n.getString("FAV_TITLE"), favPanel);

		nextProgressFrame(I18n.getString("BUILD_MENU"));
		setJMenuBar(createMenuBar());

		nextProgressFrame(I18n.getString("LINKING_COMPONENTS"));
		statusBar = new StatusBar();
		statusBar.setLength(fileList.getLength());
		main.add(statusBar, BorderLayout.SOUTH);

		locationBar.addFileListener(fileTree);
		locationBar.addFileListener(fileList);
		locationBar.addFileListener(this); // This add must be the last, because
											// it must wait the execution of the
											// other controls (everything is
											// made in the same thread) (not
											// necessary now)
		fileTree.addFileListener(locationBar);
		fileTree.addFileListener(fileList);
		fileTree.addFileListener(this); // This add must be the last
		fileList.addFileListener(locationBar);
		fileList.addFileListener(fileTree);
		fileList.addFileListener(this); // This add must be the last
		// favoritesMenu.addFileListener(fileTree);
		// favoritesMenu.addFileListener(locationBar);
		// favoritesMenu.addFileListener(fileList);
		// favoritesMenu.addFileListener(this); //This add must be the last
		favPanel.addFileListener(fileTree);
		favPanel.addFileListener(locationBar);
		favPanel.addFileListener(fileList);
		favPanel.addFileListener(this);
		nextProgressFrame(I18n.getString("BUILD_JEXPLORER"));
		getContentPane().add(main, BorderLayout.CENTER);
		ImageIcon icon = ThemesManager.getImage("jexplorerIcon.gif");
		if (icon != null) {
			setIconImage(icon.getImage());
		}
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(640, 480);
		setSize(frameSize);
		setLocation(new Point((screen.width - frameSize.width) / 2,
				(screen.height - frameSize.height) / 2));

		if (path.length() > 0) {
			File file = new File(path);
			if (file.exists()) {
				setFolder(file);
			}
		}
		endProgressFrame();
		setVisible(true);
		split.setDividerLocation(0.30);
	}

	/**
	 * Sets the file file
	 * 
	 * @param file
	 */
	public void setFolder(File file) {
		fileTree.goToFolder(file);
		fileList.setFolder(file);
		locationBar.setFile(file);
	}

	/**
	 * Invoked when the control jumps to another folder.
	 * 
	 * @param folder
	 *            the new folder that is being showed
	 * @version 1.0.1
	 */
	public void folderChange(File folder) {
	}

	public void endLoadingProcess() {
		statusBar.setLength(fileList.getLength());
	}

	/**
	 * Invoked when a folder changes its content.
	 * 
	 * @param folder
	 *            the folder that is being modified
	 * @version 1.0.1
	 */
	public void folderContentChange(File folder) {
		folderChange(folder);
	}

	/**
	 * Invoked when a file or a folder is selected in a file control.
	 * 
	 * @param listNode
	 *            the file that has been selected
	 * @version 1.0.1
	 */
	public void fileSelected(ListNode listNode) {
		if (isViewActivated) {
			if (!listNode.isDirectory()) {
				if (fileViewer.showFile(listNode)) {
					rightSplit.setRightComponent(fileViewer);
					rightSplit.setDividerLocation(0.5);// getWidth()/3);
				}
			}
		}
	}

	public void fileDoubleClickSelected(ListNode listNode) { // to execute...
	}

	/**
	 * Shows the progress window. This windows is showed only in the first
	 * instance.
	 * 
	 * @param title
	 *            the window's title
	 * @param step
	 *            number of steps(this value is used by the progress bar
	 * @version 1.0.1
	 */
	private void initProgressFrame(String title, int steps) {
		if (numInstances == 1)
			pf = new AboutWindow(this, title, steps);
	}

	/**
	 * Shows the next step in the progress window. This windows is showed only
	 * in the first instance.
	 * 
	 * @param title
	 *            the progressbar's title
	 * @version 1.0.1
	 */
	private void nextProgressFrame(String title) {
		if (numInstances == 1)
			pf.nextStep(title);
	}

	/**
	 * Hide the progress window. This windows is showed only in the first
	 * instance.
	 * 
	 * @version 1.0.1
	 */
	private void endProgressFrame() {
		if (numInstances == 1)
			pf.endProgress();
	}

	/**
	 * Unload the window, or close the program if there are not more JExplorer
	 * running.
	 */
	private void unloadMe() {
		numInstances--;
		dispose();
		if (numInstances == 0) {
			System.exit(0);
		}
	}

	/**
	 * Create the program's main menu.
	 * 
	 * @return the main menu.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar jmb = new JMenuBar();
		JMenu jm = new JMenu(getString("mnuFile"));
		jm.add(actions[CREATE_NEW_WINDOW_ACTION]);
		jm.addSeparator();
		jm.add(new JMenuItem(getString("mnuFOpen")));
		jm.add(new JMenuItem(getString("mnuFMove")));
		jm.addSeparator();
		jm.add(fileList.getAction(FileList.FILE_PROPERTIES));
		jm.add(new JMenuItem(getString("mnuFShowSize")));
		jm.addSeparator();
		jm.add(createLookAndFeelMenu());
		jm.addSeparator();
		jm.add(actions[CLOSE_ACTION]);
		jmb.add(jm);

		jm = new JMenu(getString("mnuEdit"));
		/*
		 * jm.add(fileList.getAction(FileList.COPY_ACTION));
		 * jm.add(fileList.getAction(FileList.CUT_ACTION));
		 * jm.add(fileList.getAction(FileList.PASTE_ACTION)); jm.addSeparator();
		 */
		jm.add(fileList.getAction(FileList.SELECT_ALL_ACTION));
		jm.add(new JMenuItem(getString("mnuESelectFiles")));
		jm.add(new JMenuItem(getString("mnuEInvertSelection")));
		jm.addSeparator();
		jm.add(new JMenuItem(getString("mnuEFind")));
		jm.add(actions[FILTER_WINDOW_ACTION]);
		jm.addSeparator();
		// jm.add(fileList.getAction(FileList.REFRESH_ACTION));
		jm.add(actions[THEMES_ACTION]);
		jmb.add(jm);

		/*
		 * jm = favoritesMenu.getFavoriteMenu(); if (jm != null){
		 * jm.addSeparator(); jm.add(new
		 * JMenuItem(I18n.getString("mnuFAddFavorites")));
		 * //jm.add(actions[FAVORITES_WINDOW_ACTION]); jmb.add(jm); }
		 */
		jm = new JMenu(getString("mnuLayout"));
		jm.add(fileList.getAction(FileList.ORDER_ACTION));
		jm.addSeparator();
		jm.add(fileList.getAction(FileList.ICON_VIEW_ACTION));
		jm.add(fileList.getAction(FileList.BRIEF_VIEW_ACTION));
		jm.add(fileList.getAction(FileList.DETAILED_VIEW_ACTION));
		jm.add(fileList.getAction(FileList.FILM_VIEW_ACTION));
		jm.addSeparator();
		jm.add(getViewsMenu());
		jm.addSeparator();
		jm.add(actions[VIEW_HORIZONTAL_SPLIT_ACTION]);
		jm.add(actions[VIEW_VERTICAL_SPLIT_ACTION]);
		jmb.add(jm);

		jm = new JMenu(getString("mnuHelp"));
		jm.add(actions[ABOUT_WINDOW_ACTION]);
		jm.add(actions[SEE_ODISEO_ABOUT]);
		jm.add(getViewsHelpMenu());
		jmb.add(jm);
		return jmb;
	}

	private JMenu createLookAndFeelMenu() {
		JMenu jmlf = new JMenu(getString("mnuFLookFeel"));
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < info.length; i++) {
			jmlf.add(new LookAndFeelAction(info[i]));
		}
		return jmlf;
	}

	/**
	 * Returns the menu for the loading views - help
	 * 
	 * @version 1.0.1
	 */
	private JMenu getViewsHelpMenu() {
		Vector views = fileViewer.getViews();
		JMenu menu = new JMenu(getString("mnuHViews"));
		if (views != null) {
			for (Enumeration e = views.elements(); e.hasMoreElements();) {
				QuickView quickView = (QuickView) e.nextElement();
				menu.add(new ViewHelpAction(quickView));
			}
		}
		return menu;
	}

	/**
	 * Returns a menu for the loading views, to open a file using a selected
	 * viewer
	 * 
	 * @version 1.0.1
	 */
	private JMenu getViewsMenu() {
		Vector views = fileViewer.getViews();
		JMenu menu = new JMenu(getString("mnuLViews"));
		menu.setIcon(ThemesManager.getImage("viewAction.gif"));
		if (views != null)
			for (Enumeration e = views.elements(); e.hasMoreElements();) {
				QuickView quickView = (QuickView) e.nextElement();
				Class clazz = quickView.getClass();
				menu.add(new ViewShowAction(quickView, clazz.getName()));
			}
		return menu;
	}

	/**
	 * Creates the buttons for the toolbar.
	 * 
	 * @param action
	 *            the action to execute with this button.
	 * @return a button for the toolbar.
	 */
	private JButton getTool(Action action) {
		// JButton jb = new JButton((String)action.getValue(Action.NAME),
		// (Icon)action.getValue(Action.SMALL_ICON));
		ImageIcon imageIcon = (ImageIcon) action.getValue(Action.SMALL_ICON);
		JButton jb = new JButton((Icon) imageIcon);
		jb.setToolTipText((String) action.getValue(Action.SHORT_DESCRIPTION));
		// jb.setHorizontalTextPosition(SwingConstants.CENTER);
		// jb.setVerticalTextPosition(SwingConstants.BOTTOM);
		jb.setBorder(new EmptyBorder(new Insets(1, 1, 2, 2)));
		if (imageIcon != null) {
			jb.setRolloverEnabled(true);
			jb.setRolloverIcon(new RolloverIcon(imageIcon.getImage()));
		}
		jb.addActionListener(action);
		return jb;
	}

	/**
	 * Creates Radio buttons for the toolbar.
	 * 
	 * @param action
	 *            the action to execute with this button.
	 * @return a button for the toolbar.
	 */
	private JToggleButton getToggleButton(Action action) {
		// JButton jb = new JButton((String)action.getValue(Action.NAME),
		// (Icon)action.getValue(Action.SMALL_ICON));
		ImageIcon imageIcon = (ImageIcon) action.getValue(Action.SMALL_ICON);
		JToggleButton jb = new JToggleButton((Icon) imageIcon);
		jb.setToolTipText((String) action.getValue(Action.SHORT_DESCRIPTION));
		// jb.setHorizontalTextPosition(SwingConstants.CENTER);
		// jb.setVerticalTextPosition(SwingConstants.BOTTOM);
		jb.setBorder(new EmptyBorder(new Insets(1, 1, 2, 2)));
		jb.setRolloverEnabled(true);
		jb.setRolloverIcon(new RolloverIcon(imageIcon.getImage()));
		jb.addActionListener(action);
		return jb;
	}

	/**
	 * Creates the program's toolbar.
	 * 
	 * @return the toolbar
	 */
	private JPanel createToolBar() { // JToolBar
		JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

		JPanel jpBack = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		jpBack.add(getTool(fileList.getAction(FileList.BACK_ACTION)));
		jpBack.add(getTool(actions[SEE_BACK_ACTION]));
		toolBar.add(jpBack);

		toolBar.add(getTool(fileList.getAction(FileList.UP_ACTION)));
		JPanel jpForward = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		jpForward.add(getTool(fileList.getAction(FileList.FORWARD_ACTION)));
		jpForward.add(getTool(actions[SEE_FORWARD_ACTION]));
		toolBar.add(jpForward);

		toolBar.add(new JSeparator()); // jtb.addSeparator();
		toolBar.add(getTool(fileList.getAction(FileList.REFRESH_ACTION)));
		toolBar.add(new JSeparator()); // jtb.addSeparator();
		toolBar.add(getTool(fileList.getAction(FileList.HOME_ACTION)));
		toolBar.add(new JSeparator()); // jtb.addSeparator();
		toolBar.add(getTool(fileList.getAction(FileList.STOP_ACTION)));
		toolBar.add(new JSeparator()); // jtb.addSeparator();

		ButtonGroup bg = new ButtonGroup();
		JToggleButton jrb = getToggleButton(fileList
				.getAction(FileList.ICON_VIEW_ACTION));
		bg.add(jrb);
		toolBar.add(jrb);
		jrb = getToggleButton(fileList.getAction(FileList.BRIEF_VIEW_ACTION));
		bg.add(jrb);
		toolBar.add(jrb);
		jrb = getToggleButton(fileList.getAction(FileList.DETAILED_VIEW_ACTION));
		bg.add(jrb);
		toolBar.add(jrb);
		jrb = getToggleButton(fileList.getAction(FileList.FILM_VIEW_ACTION));
		bg.add(jrb);
		toolBar.add(jrb);
		// toolBar.add(getTool(fileList.getAction(FileList.ICON_VIEW_ACTION)));
		// toolBar.add(getTool(fileList.getAction(FileList.BRIEF_VIEW_ACTION)));
		// toolBar.add(getTool(fileList.getAction(FileList.DETAILED_VIEW_ACTION)));
		// toolBar.add(getTool(fileList.getAction(FileList.FILM_VIEW_ACTION)));
		toolBar.add(new JSeparator()); // jtb.addSeparator();

		ImageIcon imageIcon = (ImageIcon) actions[ACTIVATE_VIEW_ACTION]
				.getValue(Action.SMALL_ICON);
		JToggleButton view = new JToggleButton((Icon) imageIcon);
		view.addActionListener(actions[ACTIVATE_VIEW_ACTION]);
		view.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
		view.setRolloverEnabled(true);
		view.setRolloverIcon(new RolloverIcon(imageIcon.getImage()));
		toolBar.add(view);

		// JToolBar jtb = new JToolBar();
		JPanel jtb = new JPanel(new BorderLayout());
		jtb.setLayout(new BorderLayout());
		jtb.add(toolBar, BorderLayout.CENTER);
		jtb.add(fileList.getLogo(), BorderLayout.EAST);
		return jtb;
	}

	// Shows a rollover icon in the toolbar
	class RolloverIcon implements Icon {
		private Image image;

		public RolloverIcon(Image image) {
			this.image = image;
		}

		public int getIconWidth() {
			return image.getWidth(null);
		}

		public int getIconHeight() {
			return image.getHeight(null);
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.drawImage(image, x, y, null);
			g.draw3DRect(x - 1, y - 1, image.getWidth(c) + 2,
					image.getHeight(c) + 2, true);
		}
	}

	/**
	 * Actions array indexes
	 */
	private static final int CLOSE_ACTION = 0;
	private static final int CREATE_NEW_WINDOW_ACTION = 1;
	private static final int ABOUT_WINDOW_ACTION = 2;
	private static final int ACTIVATE_VIEW_ACTION = 3;
	private static final int VIEW_HORIZONTAL_SPLIT_ACTION = 4;
	private static final int VIEW_VERTICAL_SPLIT_ACTION = 5;
	private static final int FILTER_WINDOW_ACTION = 6;
	private static final int FAVORITES_WINDOW_ACTION = 7;
	private static final int THEMES_ACTION = 8;
	private static final int SEE_BACK_ACTION = 9;
	private static final int SEE_FORWARD_ACTION = 10;
	private static final int SEE_ODISEO_ABOUT = 11;

	/**
	 * JExplorer's actions array
	 */
	private Action[] actions = { new CloseAction(),
			new CreateNewWindowAction(), new AboutWindowAction(),
			new ActivateViewAction(), new ViewSplitOrientAtion(true),
			new ViewSplitOrientAtion(false), new FilterWindowAction(),
			new FavoritesOrganizerAction(), new ThemesAction(),
			new SeeBackAction(true), new SeeBackAction(false),
			new AboutOdiseoAction() };

	// Actions

	class CloseAction extends AbstractAction {
		public CloseAction() {
			super(getString("mnuFCloseWindow"));
		}

		public void actionPerformed(ActionEvent e) {
			unloadMe();
		}
	}

	class CreateNewWindowAction extends AbstractAction {
		public CreateNewWindowAction() {
			super(getString("mnuFNewWindow"));
		}

		public void actionPerformed(ActionEvent e) {
			new JExplorer(locationBar.getText());
		}
	}

	class AboutWindowAction extends AbstractAction {
		public AboutWindowAction() {
			// super(getString("mnuHAbout"),
			// getImageIcon("images/aboutAction.gif"));
			super(getString("mnuHAbout"), ThemesManager
					.getImage("aboutAction.gif"));
		}

		public void actionPerformed(ActionEvent e) {
			new AboutWindow(JExplorer.this, JExplorer.APP_VERSION, 0);
		}
	}

	class ActivateViewAction extends AbstractAction {
		public ActivateViewAction() {
			super("", ThemesManager.getImage("viewAction.gif"));

		}

		public void actionPerformed(ActionEvent e) {
			isViewActivated = !isViewActivated;
			if (!isViewActivated) {
				rightSplit.setRightComponent(null);
				fileViewer.clearSelectedView();
			}
		}
	}

	/**
	 * Use to see the views' about window. It's not in the actions array.
	 * 
	 * @version 1.0.1
	 */
	class ViewHelpAction extends AbstractAction {
		private QuickView quickView;

		public ViewHelpAction(QuickView quickView) {
			this.quickView = quickView;
			putValue(Action.NAME, quickView.getName());
		}

		public void actionPerformed(ActionEvent e) {
			quickView.showAboutWindow((JFrame) JExplorer.this);
		}
	}

	/**
	 * Use to see the views' about window. It's not in the actions array.
	 * 
	 * @version 1.0.1
	 */
	class ViewShowAction extends AbstractAction {
		private QuickView quickView;
		private String className;

		public ViewShowAction(QuickView quickView, String className) {
			this.quickView = quickView;
			this.className = className;
			putValue(Action.NAME, quickView.getName());
		}

		public void actionPerformed(ActionEvent e) {
			if (fileViewer.showFile(fileList.getSelectedListNode(), className)) {
				rightSplit.setRightComponent(fileViewer);
				rightSplit.setDividerLocation(0.5);// getWidth()/3);
			}
		}
	}

	class ViewSplitOrientAtion extends AbstractAction {
		private boolean isHorizontal;

		public ViewSplitOrientAtion(boolean isHorizontal) {
			this.isHorizontal = isHorizontal;
			if (isHorizontal) {
				putValue(Action.NAME, I18n.getString("mnuviewhorizontalsplit"));
			} else {
				putValue(Action.NAME, I18n.getString("mnuviewverticalsplit"));
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (isHorizontal) {
				rightSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			} else {
				rightSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
			}
		}
	}

	class FilterWindowAction extends AbstractAction {
		public FilterWindowAction() {
			super(getString("mnuEFilter"));
		}

		public void actionPerformed(ActionEvent e) {
			FilterWindow fw = new FilterWindow(null, fileList.getFilter());
			fileList.setFilter(fw.getSelectedFilter());
			statusBar.setFilter(fw.getSelectedFilter());
		}
	}

	class FavoritesOrganizerAction extends AbstractAction {
		public FavoritesOrganizerAction() {
			super(getString("mnuFavoritesWindow"));
		}

		public void actionPerformed(ActionEvent e) {
			new FavoritesOrganizer("");
		}
	}

	class ThemesAction extends AbstractAction {
		public ThemesAction() {
			super(getString("mnuThemes"));
		}

		public void actionPerformed(ActionEvent e) {
			new ThemesManager();
		}
	}

	class SeeBackAction extends AbstractAction {
		private boolean back;

		public SeeBackAction(boolean back) {
			super("", ThemesManager.getImage("arrowDown.gif"));
			this.back = back;
		}

		public void actionPerformed(ActionEvent e) {
			java.util.List list;
			if (back) {
				list = fileList.getBackList(10); // the last ten elements
			} else {
				list = fileList.getForwardList(10);
			}
			int index = list.size();
			if (index > 0) {
				JPopupMenu popup = new JPopupMenu();
				for (Iterator i = list.iterator(); i.hasNext();) {
					File file = (File) i.next();
					String name = file.getName();
					if (name.length() == 0)
						name = file.getPath();
					JMenuItem jmi = popup.add(name);
					jmi.addActionListener(new PopUpAction(index--, back));
				}
				JButton jb = (JButton) e.getSource();
				popup.show(jb, 0, jb.getHeight());
			}
		}
	}

	class LookAndFeelAction extends AbstractAction {
		private String className;

		public LookAndFeelAction(UIManager.LookAndFeelInfo info) {
			super(info.getName()); // ,
									// ThemesManager.getImage("lookAndFeel.gif"));
			className = info.getClassName();
		}

		public void actionPerformed(ActionEvent e) {
			try {
				UIManager.setLookAndFeel(className);
				SwingUtilities.updateComponentTreeUI(JExplorer.this);
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch (InstantiationException ie) {
				ie.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			} catch (UnsupportedLookAndFeelException ie) {
				ie.printStackTrace();
			}
		}
	}

	/**
	 * This actionListener is executed when an option from the back popup menu
	 * is activated
	 */
	class PopUpAction implements ActionListener {
		private int index;
		private boolean back;

		public PopUpAction(int index, boolean back) {
			this.index = index;
			this.back = back;
		}

		public void actionPerformed(ActionEvent e) {
			if (back) {
				fileList.goBack(index);
			} else {
				fileList.goForward(index);
			}
		}
	}

	class AboutOdiseoAction extends AbstractAction {
		public AboutOdiseoAction() {
			// super(getString("mnuHAbout"),
			// getImageIcon("images/aboutAction.gif"));
			super(getString("mnuHAboutOdiseo"), ThemesManager
					.getImage("aboutAction.gif"));
		}

		public void actionPerformed(ActionEvent e) {
			Odiseo.seeAboutOdiseo();
		}
	}

	/**
	 * Shortcut to the I18n class
	 */
	private String getString(String key) {
		return I18n.getString(key);
	}

	/**
	 * Load an Icon from the file system or from the jar file (if Jexplorer is
	 * in a Jar file)
	 * 
	 * @param name
	 *            path to the icon
	 * @return an ImageIcon
	 */
	private ImageIcon getImageIcon(String name) {
		return I18n.getImageIcon(this, name);
	}

	/**
	 * Execute a new instance of JExplorer
	 * 
	 * @arg argument list. Not in use.
	 */
	public static void main(String[] arg) {
		System.setErr(System.out);
		new JExplorer();
	}
}
