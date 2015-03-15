/*
 * FilePropertiesWindow.java - Interface impelmented by FileList's components
 * Copyright (C) 2001 Iñigo González
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
import javax.swing.*;
import javax.swing.event.*;
import java.util.Date;

class FilePropertiesWindow extends JDialog{
	
	public FilePropertiesWindow(ListNode listNode){
		setTitle(I18n.getString("PW_TITLE"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				close();
			}
		});
		Box main = Box.createVerticalBox();

		main.add(new JLabel((Icon)listNode.getBigIcon()));

		main.add(new JLabel(I18n.getString("PW_FULL_NAME") + ": " + listNode.getFullName()));
		main.add(new JLabel(I18n.getString("PW_FILE_NAME") + ": " + listNode.getFileName()));
		main.add(new JSeparator());

		main.add(new JLabel(I18n.getString("PW_FILE_TYPE") + ": " + listNode.getTypeName()));		
		main.add(new JLabel(I18n.getString("PW_FILE_SIZE") + ": " + listNode.length() + " b"));
		main.add(new JSeparator());

		main.add(new JLabel(I18n.getString("PW_FILE_CREATED_ON")));
		Date date = new Date(listNode.lastModified());
		main.add(new JLabel(I18n.getString("PW_LAST_MODIFIED") + ": " + date.toString()));

		getContentPane().setLayout(new BorderLayout(5, 5));
		JTabbedPane tb = new JTabbedPane(SwingConstants.TOP);
		tb.addTab(I18n.getString("PW_PROPERTIES"), main);
		getContentPane().add(tb, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel.add(createButtonOk());
		//buttonsPanel.add(createButtonCancel());
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		setLocation(new Point((screen.width - frameSize.width)/ 2, (screen.height - frameSize.height) /2));

		setModal(true);
		setResizable(false);
		setVisible(true);
	}

	private JButton createButtonOk(){
		JButton b = new JButton(I18n.getString("OK"));
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				close();
			}
		});
		return b;
	}

	private void close(){  
		dispose();
	}

	/*private createButtonCancel(){
		JButton b = new JButton(" Cancel ");
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		return b;
	}*/
}
