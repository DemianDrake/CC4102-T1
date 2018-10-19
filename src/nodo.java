package src;

import java.util.*;

public class nodo {
	/*
	 * The line of database table
	 * key -> string
	 * value -> anything
	 */
	private Map<String, Object> line;
	
	public nodo() {
		line = new HashMap<String, Object>();
	}
	
	/**
	 * Constructs a nodo based on a string in nodo's line format.
	 *   
	 * @param str	a string in line format, which should be like
	 *   			TableName("column1":value1;"column2":value2;...)
	 */
	public nodo(String str) {
		readKeyValue(str.substring(str.indexOf('('), str.length() - 1));
	}
	
	private void readKeyValue(String str) {
		int i = str.indexOf(':'), j = str.indexOf(';');
		String key = str.substring(0, i);
		String stringValue = str.substring(i + 1, j);
		try {
			int intValue = Integer.parseInt(stringValue);
			line.put(key, intValue);
		} catch(NumberFormatException e) {
			line.put(key, stringValue);
		}
		String rest = str.substring(j + 1);
		if(!rest.equals("")) {
			readKeyValue(rest);
		}
	}
	
	public Object get(String key) {
		return line.get(key);
	}
}
