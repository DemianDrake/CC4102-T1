package src;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Database {
	private DataManager manager;
	private BTree btree;
	private Map<String, Integer> rowCount;
	private boolean hasBTree;
	private static final int B = 42; // stub
	
	public Database(String path) {
		manager = new TextFile(path);
		rowCount = new HashMap<String, Integer>();
		hasBTree = false;
	}
	
	public void createTable(String tablename) {
		// Creates a data storage unit, delegating its type and form to DataManager
		rowCount.put(tablename, 0);
		if(!manager.create(tablename)) {
			System.out.printf("An error ocurring while creating table %s", tablename);
		}
		if(hasBTree) {
			btree = new BTree(B);
		}
	}
	
	public void insert(nodo data) {
		String tablename = data.getValueAsString("Table");
		int id = rowCount.get(tablename);
		data.put("id", Integer.toString(id));
		id++;
		rowCount.put(tablename, id);
		long fp = manager.insertLine(data);
		if(hasBTree) {
			btree.put("id", fp);
		}
	}
	
	public void ordenar(String table, String key) {
		String inputfile = manager.getPath() + table + ".txt";
		String outputfile = manager.getPath() + "results.txt";
		Comparator<String> comparator = new Comparator<String>() {
            public int compare(String r1, String r2){
                return r1.compareTo(r2);}};
        List<File> l;
		try {
			l = ExternalSort.sortInBatch(new File(inputfile), comparator);
	        ExternalSort.mergeSortedFiles(l, new File(outputfile), comparator);
		} catch (IOException e) {
			System.out.println("An error has ocurred in src.Database.ordenar by ExternalSort methods");
			e.printStackTrace();
		}
	}
}
