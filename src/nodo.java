package src;

import java.util.*;
import java.util.Map.Entry;

// I deeply dislike starting a class name with lower case
// but I just do as the sheet says
public class nodo {
	private Map<String, String> dict;
	
	private class Value {
		private Object val;
		private String type;
		
/*		public Value(String v) {
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
		
		public String valToString() {
			if(this.type.equals("String")) {
				return (String) this.val;
			} else if(this.type.equals("Integer")) {
				return Integer.toString((int) this.val);
			} else {
				return "err";
			}
		}
*/	}
	
	public nodo() {
		dict = new HashMap<String, String>();
	}
/*	
	public void put(String key, int value) {
		dict.put(key, new Value(value));
	}
*/
	public void put(String key, String value) {
		dict.put(key, value);
	}
	
	public String getValueAsString(String key) {
		return dict.get(key);
/*		if(value.getType().equals("String")) {
			return value.get().toString();
		} else {
			return "err-notStr"; // Possible errors
		}
*/	}
	
/*	public int getValueAsInt(String key) throws Exception {
		Value value = dict.get(key);
		if (value.getType().equals("Integer")) {
			return (Integer) value.get();
		} else {
			throw new Exception("Value is not an Integer");
		}
	}
*/	
	/** Produces a string from a node
	 * 
	 * @return	a string in format: "table(key1=[type,value1];key2=[type,value2];...)\n"
	 * 								table(key1=value1;key2=value2;...)
	 */
	public String parseToSring() {
		String r = "";
//		String v;
		for (Entry<String, String> entry : dict.entrySet()) {
			if(entry.getKey().equals("Table")) {
//				v = entry.getValue();
//				r += v.valToString() + "(";
				r += entry.getValue() + "(";
			}
			r += entry.getKey() + "=";
//			r += "[";
//			v = entry.getValue();
//			r += v.getType() + ",";
//			r += v.valToString();
			r += entry.getValue();
//			r += "]";
			r += ";";
		}
		r += ")\n";
		return r;
	}
	
	/** Produces a node from a string in a given format
	 * 
	 * @param str			table(key1=[type,value1];key2=[type,value2];...)
	 * 						table(key1=value1;key2=value2;...)
	 * @return				A node from class nodo with the given data
	 * @throws Exception 	When a type other than Integer or String is found
	 */
	public static nodo strToNode(String str) throws Exception {
		nodo r = new nodo();
		int i = 0, j = i + 1;
		char c = str.charAt(0);
		String key = "null", value = "null", type = "null";
		while(c != ')') {
			if(c == '(') {
				key = "Table";
				value = str.substring(0, i);
				j = i + 1;
			} else if(c == '=') {
				// It's a key
				key = str.substring(j, i);
				j = i + 2; // We skip the parenthesis
			} /*else if(c == ',') {
				// It's a type
				type = str.substring(j, i);
				j = i + 1;
			} */else if(c == ';') {
				// It's a value
				value = str.substring(j, i);
				j = i + 1;
			}
			if(!key.equals("null") && !value.equals("null")/* && !type.equals("null")*/) {
/*				switch(type) {
					case "String":
						r.put(key, value);
						break;
					case "Integer":
						r.put(key, Integer.parseInt(value));
						break;
					default:
						throw new Exception("Wrong type at parsing in nodo.strToNode");
				}
*/				r.put(key, value);
				key = "null";
				value = "null";
				type = "null";
			}
			i++;
			c = str.charAt(i);
		}
		return r;
	}
}
