package com.datazuul.apps.jexplorer.filetable;

import java.io.File;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private File[] files;
	private String[] columns = { "Icon", "File", "Path/name", "Size",
			"Last Modified", "R", "W", "E", "D", "F", };
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	public FileTableModel() {
		this(new File[0]);
	}

	FileTableModel(File[] files) {
		this.files = files;
	}

	public int getRowCount() {
		return files.length;
	}

	public int getColumnCount() {
		return columns.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		File file = files[rowIndex];
		switch (columnIndex) {
		case 0:
			return fileSystemView.getSystemIcon(file);
		case 1:
			return fileSystemView.getSystemDisplayName(file);
		case 2:
			return file.getPath();
		case 3:
			return file.length();
		case 4:
			return file.lastModified();
		case 5:
			return file.canRead();
		case 6:
			return file.canWrite();
		case 7:
			return file.canExecute();
		case 8:
			return file.isDirectory();
		case 9:
			return file.isFile();
		default:
			System.err.println("Logic Error");
		}
		return "";
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ImageIcon.class;
		case 3:
			return Long.class;
		case 4:
			return Date.class;
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			return Boolean.class;
		}
		return String.class;
	}
	
	public void setFiles(File[] files) {
		this.files = files;
		fireTableDataChanged();
	}
}
