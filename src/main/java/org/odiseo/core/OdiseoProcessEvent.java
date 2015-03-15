/*
 * OdiseoProcessEvent.java - Used to monitorice the odiseo's processes
 * Copyright (C) 2000-2001 Iñigo González
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

package org.odiseo.core;

import java.util.EventObject;

/**
 * OdiseoProcessEvent has the information about the process addded or killed
 * @see OdiseoProcessListener
 * @version 1.0
 */
public class OdiseoProcessEvent extends EventObject{
	public OdiseoProcessEvent(Object source){
		super(source);
	}
}
