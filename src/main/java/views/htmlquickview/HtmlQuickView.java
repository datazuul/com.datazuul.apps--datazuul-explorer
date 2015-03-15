package views.htmlquickview;

/*
 * HtmlQuickView.java - Html view for JExplorer
 * Copyright (C) 2000 I�igo Gonz�lez
 * inigomail@olemail.com
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.text.html.HTMLEditorKit;

import org.jos.jexplorer.FileListNode;
import org.jos.jexplorer.ListNode;
import org.jos.jexplorer.QuickView;

/**
 * this class is a html viewer for the JExplorer. Something like a plugin
 */
public class HtmlQuickView implements QuickView {

	private static final String APP_NAME = I18n.getString("app_name");
	private static final String APP_VERSION = "1.0";
	private static ImageIcon logo;

	private JEditorPane text = new JEditorPane();
	private JPanel main = null;
	private JLabel title = new JLabel("...");

	public HtmlQuickView() {
		logo = I18n.getImageIcon(this, "images/logo.gif");
	}

	/**
	 * Creates the HtmlQuickView gui.
	 */
	private void createGUI() {
		main = new JPanel(new BorderLayout());
		JPanel northBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northBar.add(new JLabel(I18n.getString("file")));
		northBar.add(title);
		main.add(northBar, BorderLayout.NORTH);
		main.add(new JScrollPane(text), BorderLayout.CENTER);
		text.setEditable(false);
	}

	/**
	 * Return the viewer name. This function is used by JExplorer to shown the
	 * 'About as' menu.
	 * 
	 * @return the viewer name
	 */
	public String getName() {
		return APP_NAME;
	}

	/**
	 * Returns the quickview gui where to show the file types. createGUI is not
	 * called in the constructor to get the moost possible number of resources
	 * free.
	 */
	public Component getQuickView() {
		if (main == null) {
			createGUI();
		}
		return main;
	}

	/**
	 * Shows the file in the view.
	 * 
	 * @param file
	 *            the file to show or to edit in the plugin
	 */
	public void showFile(ListNode listNode) {
		try {
			title.setText(listNode.getFileName());
			HTMLEditorKit hek = new HTMLEditorKit();
			text.setEditorKit(hek);
			if (listNode instanceof FileListNode) {
				File file = (File) listNode.getObject();
				FileInputStream fis = new FileInputStream(file);
				hek.read(fis, text.getDocument(), 0);
				fis.close();
			} else {
				hek.read(listNode.getInputStream(), text.getDocument(), 0);
			}
			text.getCaret().setDot(0);
		} catch (Exception e) {
		}
	}

	/**
	 * This function is called when JExplorer ends.
	 * 
	 * @param file
	 *            the file to show or to edit in the plugin
	 */
	public void terminate() {
	}

	public void showAboutWindow(JFrame parent) {
		JDialog dialog = new JDialog(parent, APP_NAME + " " + APP_VERSION, true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setSize(200, 200);
		dialog.getContentPane().add(
				new JLabel(APP_NAME, logo, SwingConstants.LEFT),
				BorderLayout.CENTER);
		dialog.getContentPane().add(new JLabel("www.geocities.com/innigo.geo"),
				BorderLayout.SOUTH);
		dialog.setResizable(false);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(new Point((screen.width - dialog.getWidth()) / 2,
				(screen.height - dialog.getHeight()) / 2));

		dialog.setVisible(true);
	}
}
