package org.jos.jexplorer;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

class FilterWindow extends JDialog{

	private JTextField txtFilter = new JTextField(20);
	private String selectedFilter;

	public FilterWindow(JFrame owner, String filter){
		super(owner, I18n.getString("FILTER_WINDOW_TITLE"), true);
		//setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				selectedFilter = "";
				dispose();
			}
		});

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createGUI());
		txtFilter.setText(filter);
 		//setSize(300, 200);
		pack();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(new Point((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2));

		setVisible(true);
	}

	public String getSelectedFilter(){
		return selectedFilter;
	}

	private JPanel createGUI(){
		JPanel jp = new JPanel(new BorderLayout());

		JPanel jpTxt = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jpTxt.add(new JLabel(I18n.getString("FILTER_WINDOW_NAME")));
		jpTxt.add(txtFilter);
		jp.add(jpTxt, BorderLayout.NORTH);

		JPanel jpButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		Action ac = new ButtonAction(false);
		JButton jb = new JButton((String)ac.getValue(Action.NAME));
		jb.addActionListener(ac);
		jpButtons.add(jb);

		ac = new ButtonAction(true);
		jb = new JButton((String)ac.getValue(Action.NAME));
		jb.addActionListener(ac);
		jpButtons.add(jb);

		jp.add(jpButtons, BorderLayout.SOUTH);
		jp.add(new JLabel(I18n.getString("FILTER_WINDOW_TIP")), BorderLayout.CENTER);
		return jp;
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
				selectedFilter = txtFilter.getText();
				dispose();
			} else { //cancel button
				selectedFilter = "";
				dispose();
			}
		}
	}
}