package com.datazuul.apps.jexplorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.SystemUtils;

import com.datazuul.apps.jexplorer.filetable.FileTableModel;
import com.datazuul.apps.jexplorer.filetree.FileTreeCellRenderer;
import com.datazuul.apps.jexplorer.filetree.FileTreeNode;
import com.datazuul.apps.jexplorer.menu.FileMenu;

public class App1 {

    private static final String APP_ICON_PATH = "/images/folder-search-icon-16x16.png";
    private static final String APP_TITLE = "J-Explorer";

    // Provides nice icons and names for files.
    private static FileSystemView fileSystemView;

    // File-system tree. Built Lazily
    private static JTree tree;
    private static DefaultTreeModel treeModel;

    // Directory listing
    private static JTable table;
    private static FileTableModel fileTableModel;
    private static boolean cellSizesSet = false;
    private static int rowIconPadding = 6;

    // Progress bar
    private static JProgressBar progressBar;

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Ask for window decorations provided by the look and feel.
        JFrame.setDefaultLookAndFeelDecorated(true);

        setLookAndFeel();

        // Create and set up the window.
        JFrame frame = new JFrame(APP_TITLE);
        frame.setLayout(new BorderLayout());

        // Exit the application, using System.exit(0).
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the frame icon to an image loaded from a file.
        frame.setIconImage(createFrameIcon());

        // Add Menubar
        frame.setJMenuBar(createMenuBar());

        // Add Toolbar
        JToolBar toolBar = new JToolBar("Still draggable");
        // toolBar.setBorder(new LineBorder(Color.BLACK, 1));
        // toolBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        toolBar.add(new JButton("Test"));
        toolBar.setFloatable(false);
        frame.add(toolBar, BorderLayout.PAGE_START);

        // Add SplitPane with directory tree and file table
        frame.getContentPane().add(createSplitPane());

        // Add a status bar
        frame.add(createStatusBar(), BorderLayout.SOUTH);

        // The pack method sizes the frame so that all its contents are at or
        // above their preferred sizes. An alternative to pack is to establish a
        // frame size explicitly by calling setSize or setBounds (which also
        // sets the frame location). In general, using pack is preferable to
        // calling setSize, since pack leaves the frame layout manager in charge
        // of the frame size, and layout managers are good at adjusting to
        // platform dependencies and other factors that affect component size.
        frame.pack();

		// size and location of window
        // frame.setLocationRelativeTo(null); // *** this will center your app
        // (only if setSize/setMinimumSize is not used) ***
        // frame.setMinimumSize(new Dimension(800, 600));
        // display window in center of screen instead of top left corner (tested
        // under Linux)
        frame.setLocationByPlatform(true);

        frame.setVisible(true);
    }

    private static void setLookAndFeel() {
        try {
            // Set System L&F
            if (SystemUtils.IS_OS_WINDOWS) {
                UIManager
                        .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if (SystemUtils.IS_OS_LINUX) {
                UIManager
                        .setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            System.out.println("Look and Feel: "
                    + UIManager.getLookAndFeel().getDescription());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }
    }

    private static JPanel createStatusBar() {
        // create the status bar panel and shove it down the bottom of the frame
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setLayout(new BorderLayout(3, 3));

        // dummy
        JLabel statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // progress bar
        progressBar = new JProgressBar();
        statusPanel.add(progressBar, BorderLayout.EAST);
        progressBar.setVisible(false);

        return statusPanel;
    }

    private static JSplitPane createSplitPane() {
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        jSplitPane.setOneTouchExpandable(true);
        jSplitPane.setDividerLocation(200);

        // Provide minimum sizes for the two components in the split pane.
        Dimension minimumSize = new Dimension(200, 50);

        // content for left pane
        JScrollPane leftScrollPane = new JScrollPane();
        leftScrollPane.setViewportView(createFileTree());
        leftScrollPane.setMinimumSize(minimumSize);
        // Dimension preferredSize = leftScrollPane.getPreferredSize();
        // Dimension widePreferred = new Dimension(200,
        // (int) preferredSize.getHeight());
        // leftScrollPane.setPreferredSize(widePreferred);
        jSplitPane.setLeftComponent(leftScrollPane);

        // content for right pane
        JScrollPane rightScrollPane = new JScrollPane();
        rightScrollPane.setViewportView(createFileTable());
        rightScrollPane.setMinimumSize(minimumSize);
        jSplitPane.setRightComponent(rightScrollPane);

        return jSplitPane;
    }

    private static JTable createFileTable() {
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);

        if (fileTableModel == null) {
            fileTableModel = new FileTableModel();
            table.setModel(fileTableModel);
        }

        return table;
    }

    private static JTree createFileTree() {
        // the File tree model
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);

        // show the file system roots.
        fileSystemView = FileSystemView.getFileSystemView();
        File[] roots = fileSystemView.getRoots();
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                    fileSystemRoot);
            root.add(node);
            File[] files = fileSystemView.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    FileTreeNode newChild = new FileTreeNode(file);
//                    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(file);
////					{
////						private static final long serialVersionUID = 1L;
////
////						@Override
////						public boolean isLeaf() {
////							File[] children = ((File) getUserObject())
////									.listFiles();
////							boolean hasChildren = (children != null && children.length > 0);
////							return !hasChildren;
////						}
////					};
                    node.add(newChild);
                }
            }
        }

        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setCellRenderer(new FileTreeCellRenderer());
        tree.setVisibleRowCount(15);
        tree.expandRow(0);

        // add event for node selection
        TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse
                        .getPath().getLastPathComponent();
                showChildren(node);
            }
        };
        tree.addTreeSelectionListener(treeSelectionListener);

        return tree;
    }

    protected static void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                FileTreeNode treeNode = (FileTreeNode) node;
                if (treeNode.getchildren() == null) {
                    treeNode.refreshChildren();
                }
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); // !!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new FileTreeNode(child));
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    protected static void setTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel == null) {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                // table.getSelectionModel().removeListSelectionListener(
                // listSelectionListener);
                fileTableModel.setFiles(files);
                // table.getSelectionModel().addListSelectionListener(
                // listSelectionListener);
                if (!cellSizesSet) {
                    Icon icon = fileSystemView.getSystemIcon(files[0]);

                    if (icon != null) { // null for Motif!
                        // size adjustment to better account for icons
                        table.setRowHeight(icon.getIconHeight()
                                + rowIconPadding);

                        // setColumnWidth(0, -1);
                        // setColumnWidth(1, 100);
                        // setColumnWidth(2, 100);
                        // setColumnWidth(3, 60);
                        // table.getColumnModel().getColumn(3).setMaxWidth(120);
                        // setColumnWidth(4, -1);
                        // setColumnWidth(5, -1);
                        // setColumnWidth(6, -1);
                        // setColumnWidth(7, -1);
                        // setColumnWidth(8, -1);
                        // setColumnWidth(9, -1);
                    }

                    cellSizesSet = true;
                }
            }
        });
    }

    private static void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width < 0) {
            // use the preferred width of the header..
            JLabel label = new JLabel((String) tableColumn.getHeaderValue());
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int) preferred.getWidth() + 14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    private static Image createFrameIcon() {
        URL imgURL = App1.class.getResource(APP_ICON_PATH);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            return null;
        }
    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(200, 20));

        menuBar.add(new FileMenu());

        return menuBar;
    }
}
