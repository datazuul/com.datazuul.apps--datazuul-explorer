/*
 * OdiseoProcessListener.java - Used to monitorice the Odiseo's processes
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

package org.odiseo.core;

/**
 * Any applications that wants to monitorice the Odiseo's processes must implements this
 * interface and must be added to the OdiseoProcess
 * @version 1.0
 */
public interface OdiseoProcessListener{
	/**
	 * Is invoked when a process has been added to Odiseo
 	 * @param odiseoProcessEvent
	 * @version 1.0
	 */
	public void odiseoProcessAdded(OdiseoProcessEvent odiseoProcessEvent);

	/**
	 * Is invoked when a process has been killed to Odiseo
	 * @param odiseoProcessEvent
	 * @version 1.0
	 */	
	public void odiseoProcessKilled(OdiseoProcessEvent odiseoProcessEvent);
}
