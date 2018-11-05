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
		
		public List<long> search(String id) {
			return tree.search_by_key(id);
		}
		
		public List<long> searchRange(String min, String max) {
			return tree.search_by_key_range(min, max);
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
			bTrees.get(tablename).setTreeKey(treeKey);
		}
		createTable(tablename);
	}
	
	/** Creates or modifies the "results.txt" file, writing the answer for the select
	 *  query.
	 *
	 * @param colum		The column to be compared of the row to be selected
	 * @param from		The name of the file where the data is stored (example)
	 * @param key		The value expected for the column to search (must be the SAME value)
	 */
	public void select(String src, String column, String id) {
		String srcFilename = manager.getPath() + src + ".txt";
		String targetFilename = manager.getPath() + "results" + String.valueOf(System.nanoTime()) + ".txt";
		RandomAccessFile source = new RandomAccessFile(srcFilename, "r");
		RandomAccessFile target = new RandomAccessFile(targetFilename, "rw");
		Iterator<Long> fp = bTrees.get(srcFilename).search(id).iterator();
		while(fp.hasNext()) {
			source.seek(fp.next());
			target.writeChars(source.readLine());
		}
		source.close();
		target.close();
	}
	
	/** Creates or modifies the "results.txt" file, writing the answer for the select
	 *  query. This works for queries over two or more tables.
	 *
	 * @param src	An array containing the name of every table in the query (size 2)
	 * @param col	An array containing the columns to be compared (one for each table) (size 2)
	 * @param cmp 	What kind of comparison it will be.
	 *				Could be: "<" (if desired output fulfills col[0] < col[1]) or ">" (if col[0] > col[1])
	 */
	public void select(String[] src, String col[], String cmp) {
		String tgtFName = manager.getPath() + "results" + String.valueOf(System.nanoTime()) + ".txt";
		RandomAccessFile target = new RandomAccessFile(tgtFName, "rw");
		int i, l = 2;
		String[] srcFName = new String[l];
		RandomAccessFile[] src = new RandomAccessFile[l];
		for(i = 0; i < l; i++) {
			srcFName[i] = manager.getPath() + src + ".txt";
			src[i] = new RandomAccessFile(srcFName[i], "r");
		}
		Iterator<Long> fp;
		String currentLine = "";
		nodo currentNode;
		String nodeData;
		while(currentLine != null) {
			switch(cmp) {
				case "<":
					currentLine = src[0].readLine();
					currentNode = nodo.strToNode(currentLine);
					nodeData = currentNode.getValueAsString(col[0]);
					fp = bTrees.get(srcFName[1]).searchRange("", nodeData).iterator();
					break;
				case ">":
					currentLine = src[1].readLine();
					currentNode = nodo.strToNode(currentLine);
					nodeData = currentNode.getValueAsString(col[1]);
					fp = bTrees.get(srcFName[0]).searchRange("", nodeData).iterator();
					break;
				default:
					System.out.println("Error ocurred in Database > select.\nWrong comparator");
			}
			while(fp.hasNext()) {
				source.seek(fp.next());
				target.writeChars(currentLine + " -> " + source.readLine());
			}
		}
		target.close();
		for(i = 0; i < l; i++) {
			src[i].close();
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
			bTrees.get(tablename).put(fp);
		}
	}
	
	public String order(String table, String key) {
		String inputfile = manager.getPath() + table + ".txt";
		String outputfile = manager.getPath() + "tmpOrder" + String.valueOf(System.nanoTime()) + ".txt";
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
