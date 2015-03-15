/*
 * DateComparator.java - Order the fileList's contents by date
 * Copyright (C) 2000 InigoGonzalez
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

import java.util.*;

/**
 * Order listNodes by date, given priority to folders.
 * It's used by the <i>FileList</i> class to ordenar its content.
 * @see SizeComparator
 * @see NameComparator
 * @version 1.0.1
 */
class DateComparator implements Comparator{

	private static final int IS_GREATER = 1;
	private static final int IS_EQUAL = 0;
	private static final int IS_LESS = -1;

	/**
	 * Compares two listNodes. The folders have priority.
	 * @return 0 si los dos ficheros son de la misma fecha y son los dos ficheros o carpetas.
	 * @return -1 si file1 es menor de file2 o file2 es una carpeta y fiel1 no.
	 */
	public int compare(Object file1, Object file2){
		ListNode f1 = (ListNode)file1;
		ListNode f2 = (ListNode)file2;
		if (f1.isDirectory()){
			if (f2.isDirectory()){ //compare between two directories
			 	if (f1.lastModified() < f2.lastModified()){
					return IS_GREATER;
				} else if (f1.lastModified() == f2.lastModified()){
					return IS_EQUAL;
				} else {
					return IS_LESS;
				}
			} else { //f1 is a directory and f2 not
				return IS_LESS;
			}
		} else if (f2.isDirectory()){ //f2 is a directory and f1 not
			return IS_GREATER;
		} else { //compare between two files
			if (f1.lastModified() < f2.lastModified()){
				return IS_GREATER;
			} else if (f1.lastModified() == f2.lastModified()){
				return IS_EQUAL;
			} else {
				return IS_LESS;
			}
		}
	}

	/**
	 * Compara si el objeto dado (un comparador) es igual a este.
	 * Compares if the param (anothor comparator) is equal to this.
	 * @return siempre falso (no se usa)
	 * @return always fale (not in use)
	 */
	public boolean equals(Object f1){
		return false;
	}
}
