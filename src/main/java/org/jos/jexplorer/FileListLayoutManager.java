/*
 * FileListLayoutManager.java - 
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

interface FileListLayoutManager extends LayoutManager{

	public void layoutContainer(Container target);

	public Dimension minimumLayoutSize(Container target);

	public Dimension preferredLayoutSize(Container target);

	public void addLayoutComponent(String name, Component comp);

	public void removeLayoutComponent(Component comp);

	public int getCols();

}
