/*
 * FileListSelection.java - Clipboard owner for file list.
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

import java.io.*;
import java.util.*;
import java.awt.datatransfer.*;

import java.io.*;

/**
 * A Transferable which implements the capability required to transfer a
 * List of files.
 *
 * This Transferable properly supports <code>DataFlavor.javaFileListFlavor</code>
 * and all equivalent flavors. No other DataFlavors are supported.
 *
 * @see java.awt.datatransfer.DataFlavor.javaFileListFlavor
 */
public class FileListSelection implements Transferable, ClipboardOwner {

    private static final DataFlavor[] flavors = {DataFlavor.javaFileListFlavor};

    private List data;
						   
    /**
     * Creates a Transferable capable of transferring the specified List.
     */
    public FileListSelection(List data) {
        this.data = data;
    }

    /**
     * Returns an array of flavors in which this Transferable can provide
     * the data. <code>DataFlavor.javaFileListFlavor</code> is properly supported.
     *
     * @return an array of length one, whose element is <code>DataFlavor.javaFileListFlavor</code>.
     */
    public DataFlavor[] getTransferDataFlavors() {
        // returning flavors itself would allow client code to modify our internal behavior
		return (DataFlavor[])flavors.clone();
    }

    /**
     * Returns whether the requested flavor is supported by this Transferable.
     *
     * @param flavor the requested flavor for the data
     * @return true if flavor is equal to <code>DataFlavor.javaFileListFlavor</code>
     *         or <code>DataFlavor.plainTextFlavor</code>, false otherwise.
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < flavors.length; i++) {
		    if (flavors[i].equals(flavor)) {
	           return true;
		    }
	   }
		return false;
    }

    /**
     * Returns the Transferable's data in the requested DataFlavor if
     * possible. If the desired flavor is <code>DataFlavor.javaFileListFlavor</code>,
     * or an equivalent flavor, the List representing the selection is
     * returned.
     *
     * @param flavor the requested flavor for the data
     * @return the data in the requested flavor, as outlined above.
     * @throws UnsupportedFlavorException if the requested data flavor is
     *         not equivalent to either <code>DataFlavor.javaFileListFlavor</code>.
     * @see java.io.Reader
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.equals(flavors[0])) {
			return (Object)data;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}

