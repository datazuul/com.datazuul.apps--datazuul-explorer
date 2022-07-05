/*
 * ThemesManager.java - 
 * Copyright (C) 2001 Inigo Gonzalez
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

class ThemesManager extends JDialog{

	private static final String FILE_INTO_JAR_PROPS = "theme.prop";
	private static final String FILE_THEMES_PROPS = "themes.properties";
	private static final String SAMPLE_IMAGE = "sample.gif";
	private static final String THEME_KEY = "theme";
	private static final String THEME_DIR = "themes";

	private static String themesPath = System.getProperty("user.dir") + File.separator + THEME_DIR + File.separator;
	private JLabel lblImage = new JLabel();
	private JList list = new JList(); //themes list
	private JList listNote = new JList();

	private static String jarFile; //selected theme file

	//load the actual selected theme. It's storesd in the themes.properties file. This file is  in the themes folder.
	static {
		try {
			File file = new File(themesPath + FILE_THEMES_PROPS);
			FileInputStream fis = new FileInputStream(file);
			PropertyResourceBundle prop = new PropertyResourceBundle(fis);
			jarFile = (String)prop.getString(THEME_KEY);
		} catch (FileNotFoundException fnfe){
			JOptionPane.showMessageDialog(null, "ThemesManager. " + I18n.getString("ERROR_FILETYPE_NOT_FOUND") + ". \nPath=" + themesPath + FILE_THEMES_PROPS, I18n.getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
			fnfe.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		} catch (MissingResourceException mre) {
			mre.printStackTrace();
		}
	};


	/**
	 * Shows the Manager's window where the user can select a new theme.
	 * The new theme will be used by JExplorer in the next execution
	 */
	public ThemesManager(){
		super((Frame)null, I18n.getString("THEME_TITLE"));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createGUI(), BorderLayout.CENTER);
		getContentPane().add(createButtons(), BorderLayout.SOUTH);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(380, 420);
		setSize(frameSize);
		setLocation(new Point((screen.width - frameSize.width) / 2, (screen.height - frameSize.height) / 2));
		setResizable(false);
		setModal(true);
		setVisible(true);
	}

	/**
	 * Load an image from the Theme. JExplorer uses this function all the time.
	 * @param imageName the image name to load
	 * @return the image
	 */
	public static ImageIcon getImage(String imageName){
		return ThemesManager.getImage(themesPath + jarFile, imageName);
	}

	/**
	 * Load an image from a Theme file.
	 * @param fileName theme jar file wher to look for the image
	 * @param imageName the image name to load
	 * @return the image
	 */
	public static ImageIcon getImage(String fileName, String imageName){
		try{
			JarFile file = new JarFile(fileName);
			ZipEntry entry = file.getEntry(imageName);
			if (entry != null){
				InputStream is = file.getInputStream(entry);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				int len = 0;
				while((len = is.read(b, 0, 1024)) != -1){
					baos.write(b, 0, len);
				}
				is.close();
				ImageIcon icon = new ImageIcon(baos.toByteArray());
				return icon;
			} else {
				return null;
			}
		} catch(FileNotFoundException fnfe){
			//fnfe.printStackTrace();
			return null;
		} catch(IOException ioe){
			ioe.printStackTrace();
			return null;
		}
	}

	private Box createGUI(){
		Box box = Box.createVerticalBox();
		box.add(createThemesList());
		box.add(createSample());
		box.add(createThemesNotes());
		return box;
	}

	private JPanel createButtons(){
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton jb = new JButton();
		ButtonAction ba = new ButtonAction(false);
		jb.setText((String)ba.getValue(Action.NAME));
		jb.addActionListener(ba);
		jp.add(jb);

		jb = new JButton();
		ba = new ButtonAction(true);
		jb.setText((String)ba.getValue(Action.NAME));
		jb.addActionListener(ba);
		jp.add(jb);
		return jp;
	}

	private JPanel createThemesList(){
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(new JLabel(I18n.getString("INSTALLED_THEMES")), BorderLayout.NORTH);
		list.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent lse){
				String fileName = (String)list.getSelectedValue();
				readDataFromJarFile(fileName);
			}
		});
		File folder = new File(themesPath);
		if (folder.exists()){
			String[] themes = folder.list(new FilenameFilter(){
				public boolean accept(File dir, String name){
					return name.endsWith(".jar");
				}
			});
			DefaultListModel model = new DefaultListModel();
			for(int i = 0 ; i<themes.length; i++){
				model.addElement(themes[i]);
			}
			list.setModel(model);
			jp.add(new JScrollPane(list), BorderLayout.CENTER);
			int index = model.indexOf(jarFile);
			if (index>-1) list.setSelectedIndex(index);
		} else {
			jp.add(new JLabel(I18n.getString("NO_INSTALLED_THEMES")), BorderLayout.CENTER);
		}
		return jp;
	}

	private JPanel createSample(){
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(new JLabel(I18n.getString("SAMPLE_IMAGE")), BorderLayout.NORTH);
		jp.add(lblImage, BorderLayout.CENTER);
		return jp;
	}

	private JPanel createThemesNotes(){
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(new JLabel(I18n.getString("THEME_INFO")), BorderLayout.NORTH);
		jp.add(new JScrollPane(listNote), BorderLayout.CENTER);
		return jp;
	}

	private void readDataFromJarFile(String fileName){
		ImageIcon image = ThemesManager.getImage(themesPath + fileName, SAMPLE_IMAGE);
		if (image != null){
			lblImage.setText("");
			lblImage.setIcon(image);
			lblImage.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		} else {
			lblImage.setIcon((ImageIcon)null);
			lblImage.setText(I18n.getString("NO_SAMPLE"));
			lblImage.setBorder(null);
		}
		try {
			Properties prop = new Properties();
			JarFile file = new JarFile(themesPath + fileName);
			ZipEntry entry = file.getEntry(FILE_INTO_JAR_PROPS);
			if (entry != null){
				InputStream is = file.getInputStream(entry);
				prop.load(is);
				DefaultListModel model = new DefaultListModel();
				model.addElement(I18n.getString("AUTHOR") + prop.getProperty("author"));
				model.addElement(I18n.getString("DATE") + prop.getProperty("date"));
				model.addElement(I18n.getString("VERSION") + prop.getProperty("version"));
				model.addElement(I18n.getString("DESCRIPTION") + prop.getProperty("description"));
				listNote.setModel(model);
			}
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	/**
	 * Implements the Ok and the Cancel button.
	 */
	class ButtonAction extends AbstractAction{

		private boolean cancel;

		public ButtonAction(boolean cancel){
			this.cancel = cancel;
			if (cancel){
				putValue(Action.NAME, I18n.getString("CANCEL"));
			} else {
				putValue(Action.NAME, I18n.getString("OK"));
			}
		}

		public void actionPerformed(ActionEvent e){
			if (!cancel){ //Ok button
				String jarFile = (String)list.getModel().getElementAt(list.getSelectedIndex());
				Properties prop = new Properties();
				prop.put(THEME_KEY, jarFile);
				try{
					File file = new File(themesPath + FILE_THEMES_PROPS);
					FileOutputStream fos = new FileOutputStream(file);
					prop.store(fos, "JExplorer's themes information");
					fos.close();
				} catch(FileNotFoundException fnfe){
					fnfe.printStackTrace();
				} catch(IOException fnfe){
					fnfe.printStackTrace();
				}
				dispose();
			} else { //cancel button
				dispose();
			}
		}
	}
}