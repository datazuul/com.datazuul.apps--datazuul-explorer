package views.imagequickview;

/*
 * ImageQuickView.java - Text plain view for JExplorer
 * Copyright (C) 2000 I�igo Gonz�lez
 * sensei@olemail.com
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.jos.jexplorer.ListNode;
import org.jos.jexplorer.QuickView;

/**
 * this class is a viewer for the JExplorer. Something like a plugin
 */
public class ImageQuickView implements QuickView {

	private static final String APP_NAME = I18n.getString("app_name");
	private static final String APP_VERSION = "1.0";
	private static ImageIcon logo;

	private JLabel lblImage = new JLabel();
	private JPanel main = null;
	private JLabel title = new JLabel();
	private JLabel lblImageInformation = new JLabel();

	public ImageQuickView() {
		logo = I18n.getImageIcon(this, "images/logo.gif");
	}

	/**
	 * Creates the ImageQuickView gui.
	 */
	private void createGUI() {
		main = new JPanel(new BorderLayout());
		JPanel northBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		main.setBackground(Color.white);
		northBar.setBackground(Color.white);
		northBar.add(new JLabel(I18n.getString("file")));
		northBar.add(title);
		main.add(northBar, BorderLayout.NORTH);
		main.add(new JScrollPane(lblImage), BorderLayout.CENTER);
		main.add(lblImageInformation, BorderLayout.SOUTH);
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
			/*
			 * File file = (File)listNode.getObject(); MediaTracker tracker =
			 * new MediaTracker(main); Toolkit tk = main.getToolkit(); Image
			 * image = tk.getImage(file.toURL()); tracker.addImage(image, 0);
			 * tracker.waitForID(0);
			 */

			InputStream is = listNode.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = is.read(b, 0, 1024)) != -1) {
				baos.write(b, 0, len);
			}
			is.close();
			Toolkit tk = main.getToolkit();
			Image image = tk.createImage(baos.toByteArray());
			ImageIcon icon = new ImageIcon(image);
			lblImage.setIcon(icon);
			lblImageInformation.setText(I18n.getString("IMAGE_SIZE") + " "
					+ image.getWidth(null) + ", " + image.getHeight(null));
		} catch (Exception e) {
			System.out.println(e);
			title.setText(title.getText() + " " + I18n.getString("error"));
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
