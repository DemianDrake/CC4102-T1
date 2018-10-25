package src;

import java.io.*;
import java.nio.file.*;

public class Database {
	private String path;
	
	public Database(String path) {
		this.path = path;
	}
	
	public String query(String q) throws IOException {
		String r;
		String[] query_args = q.split(" ");
		switch(query_args[0]) {
		case "CREATE":
			if(query_args[1].equals("TABLE")) {
				r = createTable(query_args);
			} else {
				throw new IOException("Invalid query");
			}
			break;
		case "INSERT":
			if(query_args[1].equals("INTO") && query_args[3].equals("VALUES")) {
				r = insert(query_args);
			} else {
				throw new IOException("Invalid query");
			}
			break;
		case "ORDER":
			if(query_args[1].equals("BY")) {
				r = orderBy(query_args);
			} else {
				throw new IOException("Invalid query");
			}
			break;
		default:
			throw new IOException("Invalid query");
		}
		return r;
	}
	
	/** Creates a file with the name of the table, containing as first line the column names
	 * (There's no column type for simplification)
	 * 
	 * @param s	"CREATE TABLE tablename column1 column2 column3 ..."
	 * 			s[0]	s[1]	s[2]	s[3]	s[4]	s[5]
	 * @return
	 */
	private String createTable(String[] s) {
		String filename = path.concat(s[2]);
		try (PrintWriter p = new PrintWriter(new FileOutputStream(filename))) {
			for(int i = 3; i < s.length; i++) {
				p.print(s[i] + " ");
			}
			p.print('\n');
			p.close();
			// I suppose the output stream is also closed with that?
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String insert(String[] s) {
		String filename = path.concat(s[2]);
		try (PrintWriter p = new PrintWriter(new FileOutputStream(filename))) {
			for(int i = 4; i < s.length; i++) {
				// I hope this doesn't overwrite what's in the file...
				p.print(s[i] + " ");
			}
			p.print('\n');
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String orderBy(String[] s) {
		/* TODO
		 * Open File
		 * Order lines
		 */
		return "";
	}
}
