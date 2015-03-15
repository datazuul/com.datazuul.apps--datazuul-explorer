/*
 * AboutWindow.java - 
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

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

/**
 * The about window for the JExplorer
 */
class AboutWindow extends JDialog{

	private static final String COPYRIGHT = "Copyright (c) 2000-2001 Iñigo González";
	private Font FONT_TEXT = new Font("Arial", Font.PLAIN, 11);
	private JProgressBar progress = null;
	
	/**
	 * Creates the JExporer's 'About as' dialog.
	 * @param version JExplorer version
	 */
	public AboutWindow(JFrame jexplorer, String version){
		this(jexplorer, version, 0);
	}

	/**
	 * Creates the JExporer's 'About as' dialog.
	 * @param version JExplorer version
	 */
	public AboutWindow(JFrame jexplorer, String version, int steps){
		super(jexplorer);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				dispose();
			}
		});
		
		JPanel head = new JPanel(new BorderLayout());
		JLabel icon = new JLabel(ThemesManager.getImage("splash.jpg"));
		icon.setBorder(new BevelBorder(BevelBorder.LOWERED));
		head.add(icon, BorderLayout.CENTER);

		/*body.add(getLabel("This program is free Software and it's"));
		body.add(getLabel("distributed under the terms of the "));
		body.add(getLabel("GNU General Public License."));*/
		Box body;
		JPanel bottom;

		if (steps == 0){
		    setTitle(I18n.getString("ABOUT"));
			body = createBodyAbout();
			bottom = createBottomAbout();
		} else {
		    setTitle(I18n.getString("LOADING"));
			progress = new JProgressBar();
			progress.setMaximum(steps);
			progress.setStringPainted(true);

			body = createBodyProgress();
			bottom = createBottomProgress();
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
		}
	
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(head, BorderLayout.NORTH);
		getContentPane().add(body, BorderLayout.CENTER);
		getContentPane().add(bottom, BorderLayout.SOUTH);
		pack();
		setResizable(false);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		setLocation(new Point((screen.width - frameSize.width)/ 2, (screen.height - frameSize.height) /2));

		setVisible(true);
		if (steps == 0){
			setModal(true);
		}
	}

	private Box createBodyAbout(){
		Box body = Box.createVerticalBox();
		body.add(getLabel("www.geocities.com/innigo.geo"));
		body.add(getLabel(COPYRIGHT));
		return body;
	}

	private JPanel createBottomAbout(){
		JPanel bottom = new JPanel(new FlowLayout());
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		//bottom.add(new JSeparator());
		bottom.add(ok);
		return bottom;
	}

	private Box createBodyProgress(){
		Box body = Box.createVerticalBox();
		body.add(progress);
		return body;
	}

	private JPanel createBottomProgress(){
		JPanel bottom = new JPanel(new FlowLayout());
		bottom.add(getLabel(COPYRIGHT));
		return bottom;
	}
	/**
	 * Creates a JLabel with the specified font.
	 * @param text the text for the label
	 * @return a label with the specified text
	 */
	private JPanel getLabel(String text){
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(FONT_TEXT);
		label.setForeground(Color.black);
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panel.add(label);
		return panel;
	}

	public void nextStep(String stepTitle){
		progress.setValue(progress.getValue()+1);
		progress.setString(stepTitle);
	}
	
	public void endProgress(){
		dispose();
	}
}
