/*
 * FilmFileView - 
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
import javax.swing.*;
import java.io.*;

/**
 * This class shows an image file like a small screen shot.
 */
class FilmFileView extends JLabel implements FileView {
	public static final int WIDTH = 80;
	public static final int HEIGHT = 80;

	private Color inicialBackground = null;

	/**
	 * The file represented by this class.
 	 * @version 1.0.1
	 */
	private ListNode fileNode;
	
	/**
	 * Constructs a FilmFileView for the specified file.
	 * @param file file to create the view.
	 * @version 1.0.1
	 */
	public FilmFileView(ListNode fileNode){
		super();
		this.fileNode = fileNode;
		setText(fileNode.getFileName());
		setIcon(new Film(fileNode));
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
	}

	/**
	 * Returns the file represented by this view.
	 * @return The file represented.
	 * @version 1.0.1
	 */
	public File getFile(){
		return (File)fileNode.getObject();
	}

	/**
	 * Returns the ListNode represented by this view.
	 * @return the listNode
 	 * @version 1.0.1
	 */
	public ListNode getListNode(){
		return fileNode;
	}

	/**
	 * Set the file view selected or not.
	 * @param selected true if the file view must be selected.
	 */
	public void setSelected(boolean selected){
		if (selected){
			setOpaque(true);
			inicialBackground = getBackground();
			setBackground(Color.orange);
		} else {
			setOpaque(false);
			if (inicialBackground != null) setBackground(inicialBackground);
		}
		repaint();
	}

	class Film implements Icon{
		private ImageIcon imageIcon;

		public Film(ListNode fileNode){
			String name = fileNode.getFileName().toLowerCase();
			if (name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".png")){
				try {
					InputStream is = fileNode.getInputStream();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] b = new byte[1024];
					int len = 0;
					while((len = is.read(b, 0, 1024)) != -1){
						baos.write(b, 0, len);
					}
					is.close();
					imageIcon = new ImageIcon(baos.toByteArray());
					baos.close();
					if (fileNode instanceof ZipListNode) ((ZipListNode)fileNode).closeZipFile();
				} catch(IOException ie){
					imageIcon = (ImageIcon)fileNode.getBigIcon();
				}
			} else {
				imageIcon = (ImageIcon)fileNode.getBigIcon();
			}
		}

		public void paintIcon(Component c, Graphics g, int x, int y){
			//the commented lines draw the film border
			//Color aux = g.getColor();
			//g.setColor(Color.black);
			//g.fillRect(0 ,0 ,WIDTH, HEIGHT);
			//g.setColor(c.getBackground());
			//for(int i=0; i<HEIGHT;i+=15){
			//	g.fillRect(2, i, 6, 6);
			//	g.fillRect(WIDTH - BORDER_WIDTH + 2, i, 6, 6);
			//}
			//g.fillRect(BORDER_WIDTH ,1 ,WIDTH-BORDER_WIDTH*2, HEIGHT-2);
			int imageWidth = imageIcon.getIconWidth();
			int imageHeight = imageIcon.getIconHeight();
			if (imageWidth > WIDTH || imageHeight > HEIGHT){
				//float dw = (float)(WIDTH-BORDER_WIDTH*2) / (float)imageWidth;
				//float dh = (float)HEIGHT / (float)imageHeight;
				float dw = (float)WIDTH / (float)imageWidth;
				float dh = (float)HEIGHT / (float)imageHeight;
				float dd = Math.min(dw, dh);
				int iw = new Float(imageWidth*dd).intValue();
				int ih = new Float(imageHeight*dd).intValue();
				g.drawImage(imageIcon.getImage(), (WIDTH - iw)/2, (HEIGHT - ih)/2, iw, ih, c);
			} else {
				g.drawImage(imageIcon.getImage(), (WIDTH - imageWidth)/2, (HEIGHT - imageHeight)/2, c);
			}
			//g.setColor(aux);
		}

		public int getIconWidth(){
			return WIDTH;
		}

		public int getIconHeight(){
			return HEIGHT;
		}
	}
}
