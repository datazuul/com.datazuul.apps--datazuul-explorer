/*
 * BriefLayout - 
 * The control that shows the content of a folder
 * FileTree.java - This class shows a directory tree
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

import java.awt.*;
import javax.swing.*;

/**
 * Implementa el modo de colocar los ficheros cuando estos se representa mediante iconos grandes.
 * Inplements the way to alocate the files when those are represented by big icons.
 */
class BriefLayout implements FileListLayoutManager{

	/**
	 * Espacio vertival entre cada línea
	 */
	private int vgap;

	/**
	 * Viewport donde se encuentra el componente. Esto se hace por que este layout es para 
	 * componentes que están dentro de un JScrollPane.
	 */
	private JViewport view;

	/**
	 * Construye el <i>layout</i> sin espacio entre líneas.
	 * @param viewport donde está metido el control.
	 */
	public BriefLayout(JViewport view){
		this(0, view);
	}

	/**
	 * Construye el <i>layout</i> con el espacio especificado entre líneas.
	 * @param vgap el espacio entre líneas.
	 * @param viewport donde está metido el control.
	 */
	public BriefLayout(int vgap, JViewport view){
		this.vgap = vgap;
		this.view = view;
	}

	/**
	 * Coloca los ficheros en el control 'contenedor'.
	 * @param target El control que posee los ficheros, los cuales deben ser colocados.
	 */
	public void layoutContainer(Container target){
		if (target.getComponentCount()>0){
			int y = vgap;
			//Se supone que todas las filas tienen la misma altura
			int height = target.getComponent(0).getPreferredSize().height;
			int maxWidth = target.getComponent(0).getPreferredSize().width;
			if (height > 0) { 
				for(int i = 0; i < target.getComponentCount(); i++){
					Component component = target.getComponent(i);
					int width = component.getPreferredSize().width;
					component.setBounds(0, y, width, height);
					y += height + vgap;
					maxWidth = Math.max(maxWidth, component.getWidth());
				}
			}
			Dimension viewSize = view.getExtentSize();
			target.setSize(Math.max(maxWidth, viewSize.width), Math.max(y, viewSize.height));
		}
	}

	/**
	 * Returns the minimum dimensions needed to layout the components contained in the specified
	 * target container.
	 * @param target the component which needs to be laid out.
	 * @return the minimum dimensions to lay out the subcomponents of the specified container.
	 */
	public Dimension minimumLayoutSize(Container target){
		return preferredLayoutSize(target);
	}

	/**
	 * Returns the preferred size to lay out all the componentes contained in the specified target container.
	 * @param target The component which needed to be lay out.
 	 * @return the preferred dimensions to lay out the subcomponents of the specified container.
	 */
	public Dimension preferredLayoutSize(Container target){
		if (target.getComponentCount()>0){
			int maxWidth = 0;
			for(int i = 0; i < target.getComponentCount(); i++){
				Component component = target.getComponent(i);
				int width = component.getPreferredSize().width;
				maxWidth = Math.max(maxWidth, width);
			}
			//Se supone que todas las filas tienen la misma altura
			int height = (target.getComponentCount() + vgap) * target.getComponent(0).getPreferredSize().height + vgap;
			return new Dimension(maxWidth, height);
		} else {
			return new Dimension(0, 0);
		}
	}

	public int getCols(){
		return 1;
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
