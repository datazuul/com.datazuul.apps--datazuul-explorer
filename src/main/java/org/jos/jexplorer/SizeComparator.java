/*
 * SizeComparator.java - Help us to order listNodes by size
 * Copyright (C) 1999-2000 InigoGonzalez
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
 * Order listNodes by size
 * @see DateComparator
 * @see NameComparator
 * @version 1.0.1
 */
class SizeComparator implements Comparator{
	private static final int IS_GREATER = 1;
	private static final int IS_EQUAL = 0;
	private static final int IS_LESS = -1;

	public int compare(Object file1, Object file2){
		ListNode f1 = (ListNode)file1;
		ListNode f2 = (ListNode)file2;
		if (f1.isDirectory()){
			if (f2.isDirectory()){
				return f1.compareTo(f2); //compare between two directories
			} else { //f1 is a directory and f2 not
				return IS_LESS;
			}
		} else if (f2.isDirectory()){
			return IS_GREATER; //f2 is a directory and f1 not
		} else { //compare between two files
			if (f1.length() > f2.length()){
				return IS_GREATER;
			} else if (f1.length() == f2.length()){
				return IS_EQUAL;
			} else {
				return IS_LESS;
			}
		}
	}

	public boolean equals(Object f1){
		return false;
	}
}
