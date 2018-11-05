package src;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Database {
	private DataManager manager;
	private Map<String, Integer> rowCount;
	private Map<String, KeyedBTree> bTrees;
	private boolean hasBTree;
	private static final int B = 42; // stub
	
	private class KeyedBTree {
		private BTree tree;
		private String key; // Probably could be a better name for this
		
		public KeyedBTree() {
			this.tree = new BTree(B);
		}
		
		public void setTreeKey(String newKey) {
			key = newKey;
		}
		
		public BTree getTree() {
			return tree;
		}
		
		public String getTreeKey() {
			return key;
		}
		
		public void put(long filepointer) {
			tree.put(key, filepointer);
		}
	}
	
	public Database(String path, boolean hasBTree) {
		manager = new TextFile(path);
		rowCount = new HashMap<String, Integer>();
		this.hasBTree = hasBTree;
		if(hasBTree) {
			bTrees = new HashMap<String, KeyedBTree>();
		}
	}

	public boolean usesBTree(){
		return this.hasBTree;
	}
	
	public void createTable(String tablename) {
		// Creates a data storage unit, delegating its type and form to DataManager
		rowCount.put(tablename, 0);
		if(!manager.create(tablename)) {
			System.out.printf("An error ocurring while creating table %s", tablename);
		}
	}
	
	public void createTable(String tablename, String treeKey) {
		if(hasBTree) {
			bTrees.put(tablename, new KeyedBTree());
			bTrees.get("tablename").setTreeKey(treeKey);
		}
		createTable(tablename);
	}
	
	public void insert(nodo data) {
		String tablename = data.getValueAsString("Table");
		int id = rowCount.get(tablename);
		data.put("id", Integer.toString(id));
		id++;
		rowCount.put(tablename, id);
		long fp = manager.insertLine(data);
		if(hasBTree) {
			bTrees.get(tablename).put(fp);
		}
	}
	
	public String order(String table, String key) {
		String inputfile = manager.getPath() + table + ".txt";
		String outputfile = manager.getPath() + String.valueOf(System.nanoTime()) + "_results.txt";
		Comparator<String> comparator = new Comparator<String>() {
            public int compare(String r1, String r2) {
            	// Supposedly r1 and r2 are rows, therefore nodes
            	nodo n1, n2;
            	String v1, v2;
            	n1 = nodo.strToNode(r1);
				v1 = n1.getValueAsString(key);
	            n2 = nodo.strToNode(r2);
	            v2 = n2.getValueAsString(key);
                return v1.compareTo(v2);}};
        List<File> l;
		try {
			l = ExternalSort.sortInBatch(new File(inputfile), comparator);
	        ExternalSort.mergeSortedFiles(l, new File(outputfile), comparator);
		} catch (IOException e) {
			System.out.println("An error has ocurred in src.Database.ordenar by ExternalSort methods");
			e.printStackTrace();
		}
		return outputfile;
	}
}
