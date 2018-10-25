package src;

import java.util.*;
import java.util.Map.Entry;

// I deeply dislike starting a class name with lower case
// but I just do as the sheet says
public class nodo {
	private Map<String, Value> dict;
	
	private class Value {
		private Object val;
		private String type;
		
		public Value(String v) {
			val = v;
			type = "String";
		}
		
		public Value(int v) {
			val = v;
			type = "Integer";
		}
		
		public Object get() {
			return val;
		}
		
		public String getType() {
			return type;
		}
	}
	
	public nodo() {
		dict = new HashMap<String, Value>();
	}
	
	public void put(String key, int value) {
		dict.put(key, new Value(value));
	}
	
	public void put(String key, String value) {
		dict.put(key, new Value(value));
	}
	
	public String getAsString(String key) throws Exception {
		Value value = dict.get(key);
		if (value.getType().equals("String")) {
			return value.get().toString();
		} else {
			throw new Exception("Value is not a String");
		}
	}
	
	public int getAsInt(String key) throws Exception {
		Value value = dict.get(key);
		if (value.getType().equals("Integer")) {
			return (Integer) value.get();
		} else {
			throw new Exception("Value is not an Integer");
		}
	}
	
	/** Produces a string from a node
	 * 
	 * @return	a string in format: "(key1=[type,value1];key2=[type,value2];...)\n"
	 */
	public String parseToSring() {
		String r = "(";
		Value v;
		for (Entry<String, Value> entry : dict.entrySet()) {
			r += entry.getKey() + "=[";
			v = entry.getValue();
			r += v.getType() + ",";
			if (v.getType().equals("String")) {
				r += (String) v.get();
			} else if (v.getType().equals("Integer")) {
				r += Integer.toString((int)v.get());
			}
			r += "];";
		}
		r += "\n";
		return r;
	}
	
	/** Produces a node from a string in a given format
	 * 
	 * @param str	(key1=[type,value1];key2=[type,value2];...)
	 * @return		A node from class nodo with the given data
	 */
	public static nodo strToNode(String str) {
		nodo r = new nodo();
		// TODO
		return r;
	}
	
	
	/**
	 * Constructs a nodo based on a string in nodo's line format.
	 *   
	 * @param str	a string in line format, which should be like
	 *   			TableName("column1":value1;"column2":value2;...)
	 */
	public nodo(String str) {
		this();
		readKeyValue(str.substring(str.indexOf('('), str.length() - 1));
	}
	
	private void readKeyValue(String str) {
		int i = str.indexOf(':'), j = str.indexOf(';');
		String key = str.substring(0, i);
		String strValue = str.substring(i + 1, j);
		try {
			int intValue = Integer.parseInt(strValue);
			dict.put(key, new Value(intValue));
		} catch(NumberFormatException e) {
			dict.put(key, new Value(strValue));
		}
		String rest = str.substring(j + 1);
		if(!rest.equals("")) {
			readKeyValue(rest);
		}
	}
}
