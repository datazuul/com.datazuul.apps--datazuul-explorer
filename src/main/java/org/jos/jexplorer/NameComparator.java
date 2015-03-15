/* NameComparator.java - Help us to order the FileList's content (ListNode class) by name.
 * Copyright (C) 2000 InigoGonzalez
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

import java.util.*;

/**
 * Compares two listNode. A folder has always priority to a file.
 * @see SizeComparator
 * @see DateComparator
 * @version 1.0.1
 */
class NameComparator implements Comparator{

	private static final int IS_GREATER = 1;
	private static final int IS_EQUAL = 0;
	private static final int IS_LESS = -1;

	/**
	 * Comperes two ListNode. the first will be greater than the second listNode if the first if a folder and the second no or 
	 * if the first listNode name is greater than the second listNode name. No equals names exist.
	 * @return 1 if the first file is a folder and the second file no or if the first name is greater than the second.
	 * @return -1 if the second file is a folder or if the second namme is greater than the first.
	 */
	public int compare(Object file1, Object file2){
		ListNode f1 = (ListNode)file1;
		ListNode f2 = (ListNode)file2;
		if (f1.isDirectory()){
			if (f2.isDirectory()){ //compares between two directories
				return f1.compareTo(f2);
			} else { //f1 is a directory and f2 not
				return IS_LESS;
			}
		} else if (f2.isDirectory()){ //f2 is a directory and f1 not
			return IS_GREATER;
		} else { //compares between two ListNode
			return f1.compareTo(f2);
		}
	}

	/**
	 * No se usa.
	 * Not in use.
	 */
	public boolean equals(Object f1){
		return false;
	}
}
