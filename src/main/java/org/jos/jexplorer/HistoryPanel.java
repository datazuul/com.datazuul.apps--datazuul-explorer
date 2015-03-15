/*
 * HistoryPanel - Loads the history list from an xml file (Uses JAXP 1.1 API)
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
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class HistoryPanel extends FileComponent {
	/**
	 * Creates the history list from an xml file. the xml file must have this
	 * tags: <history> <item url=".." title=".."/> <item url="..." title=".."/>
	 * <item url=".." title=".."/> </history>
	 * 
	 * @version 1.0
	 */
	private static Icon iconItem = ThemesManager.getImage("smallFile.gif");
	private static Icon iconMenu = ThemesManager.getImage("openFolder.gif");

	private JList list = new FavList();

	private String xmlFile;

	/**
	 * Creates the favorite list from the xml file.
	 */
	public HistoryPanel(String xmlFile) {
		this.xmlFile = xmlFile;
		setLayout(new BorderLayout());
		add(new JScrollPane(list), BorderLayout.CENTER);
		add(createOptions(), BorderLayout.SOUTH);
		list.addListSelectionListener(new FavListener());
		list.setCellRenderer(new FavRenderer());
		// list.setSelectionMode(SINGLE_SELECTION);
		list.setModel(new DefaultListModel());
		refresh();
	}

	/**
	 * Creates the buttoms refresh and delete.
	 * 
	 * @return a panel with the buttons
	 */
	private JPanel createOptions() {
		JPanel panel = new JPanel(new FlowLayout());

		JButton jbRefresh = new JButton(I18n.getString("REFRESH")); // ,
																	// ThemesManager.getImage("refreshAction.gif"));
		jbRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		JButton jbDelete = new JButton(I18n.getString("DELETE"));
		jbDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedElement();
			}
		});
		panel.add(jbRefresh);
		panel.add(jbDelete);
		return panel;
	}

	/**
	 * Creates the list from the xml file
	 */
	public void refresh() {
		try {
			SAXParserFactory saxF = SAXParserFactory.newInstance();
			SAXParser saxP = saxF.newSAXParser();
			DefaultHandler listHandler = new ListHandler();
			File file = new File(xmlFile);
			saxP.parse(file, listHandler);
			// ListHandler mh = (ListHandler)listHandler;
		} catch (FileNotFoundException fnfe) {
			// fnfe.printStackTrace();
			System.err.println("user.dir/" + xmlFile + " not Found");
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Removes the element selected in the list.
	 */
	public void deleteSelectedElement() {
		DefaultListModel model = (DefaultListModel) list.getModel();
		int index = list.getSelectedIndex();
		if (index > -1) {
			model.remove(index);
			save();
		}
	}

	/**
	 * Saves the list in the history file. The file format is: <history> <item
	 * url=".." title=".."/> <item url=".." title=".."/> ... </history>
	 */
	private void save() {
		try {
			File file = new File(HistoryPanel.this.xmlFile);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			ps.println("<favorite>");
			ListModel lm = list.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				ListElement le = (ListElement) lm.getElementAt(i);
				ps.println("<item url='" + le.getURL() + "' title='"
						+ le.getTitle() + "'/>");
			}
			ps.println("</favorite>");
			ps.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	class ListHandler extends DefaultHandler {
		private DefaultListModel model;

		public void startDocument() {
			model = (DefaultListModel) list.getModel();
			model.clear();
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName == "menu") {// now there aren't any menu tag
				AttributesImpl attr = new AttributesImpl(attributes);
				String name = attr.getValue("title");
				ListElement le = new ListElement(name);
				model.addElement(le);
			} else if (qName == "item") {
				AttributesImpl attr = new AttributesImpl(attributes);
				String name = attr.getValue("title");
				String url = attr.getValue("url");
				ListElement le = new ListElement(name, url);
				model.addElement(le);
			}
		}

		public void endDocument() {
			// FavoritesPanel.this.list.setModel(model);
		}
	}

	class ListElement {
		private String title;
		private String url;
		private Icon icon;

		public ListElement(String title) {
			this(title, null);
		}

		public ListElement(String title, String url) {
			this.title = title;
			this.url = url;
			if (url == null) {
				icon = iconMenu;
			} else {
				icon = iconItem;
			}
		}

		public String getTitle() {
			return title;
		}

		public String getURL() {
			return url;
		}

		public Icon getIcon() {
			return icon;
		}

	}

	class FavRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel lbl = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			ListElement le = (ListElement) value;
			lbl.setIcon(le.getIcon());
			lbl.setText(le.getTitle());
			lbl.setToolTipText(le.getURL());
			return lbl;
		}
	}

	class FavListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			int index = list.getSelectedIndex();
			if (index == -1)
				return;
			ListElement le = (ListElement) list.getModel().getElementAt(index);

			if (!e.getValueIsAdjusting()) {
				if (le.getURL() != null) {
					File folder = new File(le.getURL());
					if (folder.exists()) {
						// fileComponent.fireChangeListener(folder);
						fireChangeListener(folder);
					} else {
						JOptionPane.showMessageDialog(null,
								I18n.getString("FAV_ERROR_OLD_FOLDER"),
								I18n.getString("FAV_ERROR_TITLE"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	class FavList extends JList implements DropTargetListener,
			DragSourceListener {
		// To support the Drag&Drop actions
		private DropTarget dropTarget;

		public FavList() {
			dropTarget = new DropTarget(this, this);
		}

		// DropTargetListener interface
		// Called when a drag operation has encountered the DropTarget.
		public void dragEnter(DropTargetDragEvent dtde) {
			dtde.acceptDrag(DnDConstants.ACTION_MOVE);
		}

		// The drag operation has departed the DropTarget without dropping.
		public void dragExit(DropTargetEvent dte) {
		}

		// Called when a drag operation is ongoing on the DropTarget.
		public void dragOver(DropTargetDragEvent dtde) {
		}

		public void drop(DropTargetDropEvent dtde) {
			try {
				Transferable transferable = dtde.getTransferable();
				if (transferable
						.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_MOVE);
					java.util.List fileList = (java.util.List) transferable
							.getTransferData(DataFlavor.javaFileListFlavor);
					File[] files = (File[]) fileList.toArray(new File[1]);
					DefaultListModel model = (DefaultListModel) list.getModel();
					for (int i = 0; i < files.length; i++) {
						model.addElement(new ListElement(files[i].getName(),
								files[i].getPath()));
					}
					dtde.getDropTargetContext().dropComplete(true);
					save();
				} else {
					dtde.rejectDrop();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				dtde.rejectDrop();
			} catch (UnsupportedFlavorException ufe) {
				dtde.rejectDrop();
			}
		}

		// Called if the user has modified the current drop gesture.
		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		// DragSourceListener interface
		// Invoked to signify that the Drag and Drop operation is complete.
		public void dragDropEnd(DragSourceDropEvent dsde) {
		}

		// Called as the hotspot enters a platform dependent drop site.
		public void dragEnter(DragSourceDragEvent dsde) {
		}

		// Called as the hotspot exits a platform dependent drop site.
		public void dragExit(DragSourceEvent dse) {
		}

		// Called as the hotspot moves over a platform dependent drop site.
		public void dragOver(DragSourceDragEvent dsde) {
		}

		// Called when the user has modified the drop gesture.
		public void dropActionChanged(DragSourceDragEvent dsde) {
		}

	}
}