package src;

import java.io.*;

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
			r = insert(query_args);
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
	
	private String createTable(String[] s) {
		//TODO Create file with table name
		return "";
	}
	
	private String insert(String[] s) {
		/* TODO
		 * 	Open File
		 * 	Insert line
		 */
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
