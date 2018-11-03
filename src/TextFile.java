package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class TextFile implements DataManager {
	private String path;
	
	public TextFile(String path) {
		this.path = path;
	}

	@Override
	public boolean create(String tablename) {
		String filename = path.concat(tablename) + ".txt";
		File f = new File(filename);
		f.getParentFile().mkdirs();
		try {
			f.createNewFile();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public long insertLine(nodo data) {
		String line = data.parseToSring();
		String table = data.getValueAsString("Table");
		String filename = path.concat(table) + ".txt";
		File f = new File(filename);
		try {
			RandomAccessFile file = new RandomAccessFile(f, "rw");
			file.writeChars(line);
			long fp = file.getFilePointer();
			file.close();
			return fp;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		try (PrintWriter p = new PrintWriter(new FileOutputStream(filename))) {
			p.print(line);
			p.print('\n');
			p.close();
			// I suppose the output stream is also closed with that?
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} */
		return -1;
	}
	
	public String getPath() {
		return path;
	}
}
