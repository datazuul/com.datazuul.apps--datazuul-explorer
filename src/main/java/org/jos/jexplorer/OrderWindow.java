/*
 * OrdeWindow.java - 
 * Copyright (C) 2000 Iñigo González
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
package org.jos.jexplorer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Ventana para posibilitar el orden dentro de la lista de ficheros
 * Window to order the file list.
 */
class OrderWindow extends JDialog{

	private boolean cancel;
	private int viewType;
	private boolean inverse;

	JComboBox cboViewType = new JComboBox();	
	JCheckBox chkInverse = new JCheckBox(I18n.getString("ORDER_INVERSE"));

	public OrderWindow(int viewType, boolean inverse){
		super();
		setTitle(I18n.getString("ORDER_TITLE"));
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				close();
			}
		});

		getContentPane().setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		
		JPanel main = new JPanel(new FlowLayout(FlowLayout.LEFT));
		main.add(new JLabel(I18n.getString("ORDER_TYPE")));
		
		cboViewType.addItem(I18n.getString("ORDER_TYPE_NAME"));
		cboViewType.addItem(I18n.getString("ORDER_TYPE_SIZE"));
		cboViewType.addItem(I18n.getString("ORDER_TYPE_DATE"));
		cboViewType.setSelectedIndex(viewType);
		main.add(cboViewType);
		box.add(main);

		chkInverse.setSelected(inverse);
		box.add(chkInverse);

		JPanel buttons = new JPanel(new FlowLayout());
		JButton buttonAcept = new JButton(I18n.getString("OK"));
		buttonAcept.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancel = false;
				close();
			}
		});
		buttons.add(buttonAcept);
		JButton buttonCancel = new JButton(I18n.getString("CANCEL"));
		buttonCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancel = true;
				close();
			}
		});
		buttons.add(buttonCancel);
		box.add(buttons);

		getContentPane().add(box, BorderLayout.CENTER);
		
		setSize(280, 130);
		setResizable(false);
		setVisible(true);
	}

	private void close(){
		viewType = cboViewType.getSelectedIndex();
		inverse = chkInverse.isSelected();
		dispose();
	}

	public boolean isCancelled(){
		return cancel;
	}

	public int getViewType(){
		return viewType;
	}

	public boolean getInverse(){
		return inverse;
	}
}
