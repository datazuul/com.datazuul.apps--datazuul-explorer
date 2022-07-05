/*
 * IconLayout.java - 
 * Copyright (C) 2000-2001 Inigo Gonzalez
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

/**
 * Implementa el modo de colocar los ficheros cuando estos se representa mediante iconos grandes.
 * Este <i>layout</i> esta creado para soportar un container con un JScrollPane.
 * Inplements the way to alocate the files when those are represented by big icons.
 */
class IconLayout implements FileListLayoutManager{
	
	/**
	 * The width of the files representation.
	 */
	private int width;

	/**
	 * The height of the files representation.
	 */
	private int height;
	
	/**
	 * The horisontal space between the files
	 */
	private int hgap;

	/**
	 * The vertical space between the files
	 */
	private int vgap;

	/**
	 * The number of cols
	 */
	private int cols;

	/**
	 * El viewPort con el que se trabaja. Esto se hace aqu� por que este layout es para 
	 * componentes que se encuentran dentro de un JScrollPane.
	 */
	private JViewport view;

	/**
	 * Construye el <i>layout</i> con los valores por defecto. Estos valores son: 
	 * cero de separacion tanto vertical como horizontal y 80 el tamaro tanto
	 * a lo ancho como a lo largo de los iconos.
	 * Construt the layout with the defaults values. This values are: no space
	 * between files horizontally or vertically and a widht and a height of 80.
	 */
	public IconLayout(JViewport view){
		this(0, 0, 80, 80, view);
	}

	/**
	 * Construye el <i>layout</i> con el espacio entre iconos especificado
	 * por los parametros de entrada. El resto de valores seron los determinados por defecto.
	 * @param hgap El espacio horizontal entre los ficheros.
	 * @param vgap El espacio vertical entre los ficheros.
	 * Construct the layout with the columns and the space between files specified by the params.
	 * The rest of values will have the default.
	 * @param hgap The horizonal space between the files.
	 * @param hgap The vertical space between the files.
 	 * @param view el viewport donde se esta mostrando el componente.
	 */
	public IconLayout(int hgap, int vgap, JViewport view){
		this(hgap, vgap, 80, 80, view);
	}

	/**
	 * Construye el <i>layout</i> con todos los valores especificados por los parametros.
	 * @param hgap El espacio horizontal entre los ficheros.
	 * @param vgap El espacio vertical entre los ficheros.
	 * @param width La anchura de la representacion de los ficheros.
	 * @param height La altura de la representacion de los ficheros.
	 * @param view el viewport donde se esto mostrando el componente.
	 */
	public IconLayout(int hgap, int vgap, int width, int height, JViewport view){
		this.hgap = hgap;
		this.vgap = vgap;
		this.width = width;
		this.height = height;
		this.view = view;
	}

	/**
	 * Coloca los ficheros en el control 'contenedor'.
	 * @param target El control que posee los ficheros, los cuales deben ser colocados.
	 */
	public void layoutContainer(Container target){
	synchronized (target.getTreeLock()){
		Insets insets = target.getInsets();
		int x = hgap + insets.left;
		int y = vgap + insets.top;
		Dimension viewSize = view.getExtentSize();

		int maxWidth = viewSize.width - (insets.left + insets.right + hgap*2);
		cols = maxWidth / (width + hgap);
		int col = 0;
		for(int i = 0; i<target.getComponentCount(); i++){
			target.getComponent(i).setBounds(x, y, width, height);
			col++;
			if (col >= cols){
				x = hgap + insets.left;
				y += height + vgap;
				col = 0;
			} else {
				x += width + hgap;
			}
		}
		if (col == 0){
			target.setSize(viewSize.width, Math.max(y + insets.bottom, viewSize.height));
		} else {
			target.setSize(viewSize.width, Math.max(y + height + vgap + insets.bottom, viewSize.height));
		}
	}
	}

	/**
	 * Devuelve el tamaro minimo necesario para colocar los componentes contenidos en el control especificado.
	 * @param target El componentes el cual necesita ser colocado (sus contenidos).
	 * @param returns El tamaro minimo necesario para colocar los subcomponentes del contenedor especificado.
	 * Returns the minimum dimensions needed to layout the components contained in the specified
	 * target container.
	 * @param target the component which needs to be laid out.
	 * @return the minimum dimensions to lay out the subcomponents of the specified container.
	 */
	public Dimension minimumLayoutSize(Container target){
		return preferredLayoutSize(target);
	}

	/**
	 * Devuelve el tamaro preferido para colocar los subcomponentes del componente especificado.
	 * @param target El contenedor que necesita ser recolocado (sus contenidos).
	 * @return el tamaro preferido para colocar los subcomponentes del componente especificado.
	 * Retuen the preferred size to lay out all the componentes contained in the specified target container.
	 * @param target The component which needed to be lay out.
 	 * @return the preferred dimensions to lay out the subcomponents of the specified container.
	 */
	public Dimension preferredLayoutSize(Container target){
	synchronized (target.getTreeLock()){
		Insets insets = target.getInsets();

		//target.setSize(view.getExtentSize().width, target.getHeight());
		int maxWidth = view.getExtentSize().width - (insets.left + insets.right + hgap*2);
		cols = maxWidth / (width + hgap);
		if (cols == 0){
			cols = 1;
		}
		int rows = target.getComponentCount() / cols;
		return new Dimension(maxWidth, vgap*2 + insets.top + insets.bottom + (height + vgap) * (rows+1));
	}
	}

	public int getCols(){
		return cols;
	}

	/**
	 * No se usa.
	 * Not used.
	 */
	public void addLayoutComponent(String name, Component comp){ }
	
	/**
	 * No se usa
	 * Not used
	 */
	public void removeLayoutComponent(Component comp){ }
}
