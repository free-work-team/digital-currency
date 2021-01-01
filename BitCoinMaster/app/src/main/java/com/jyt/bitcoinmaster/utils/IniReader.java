package com.jyt.bitcoinmaster.utils;

import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IniReader {

	protected Map<String, Map<String, String>> sections = new LinkedHashMap<String, Map<String, String>>();
	private transient String currentSecion;
	private transient Map<String, String> current;
	
	private String[] notes = new String[]{";", "#", "//"};
	private static IniReader iniReader;
 private IniReader(){
	 
 }
 public static IniReader getIntence(){
	 if(iniReader==null){
		 iniReader=new IniReader();
	 }
	 return iniReader;
 }
	public IniReader(String filename) throws IOException {
		BufferedReader reader = null;
		try {
			File file = new File(filename);
			if(file.exists() && file.isFile()){
				reader = new BufferedReader(new FileReader(file));
				read(reader);
				reader.close();
			}
		}finally{
			if(reader != null){
				reader.close();
			}
		}
	}

	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	protected void parseLine(String line) {
		if(line.contains("[CASHSHUTTERDEV]")){
		}
		line = line.trim();
		int num1=line.indexOf("//");
		if(num1!=-1){
			line = line.substring(0, num1);
		}
		line = line.trim();
		if (line.matches("\\[.*\\]")) {
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			current = new LinkedHashMap<String, String>();
			sections.put(currentSecion.trim(), current);
		} else if (line.matches(".*=.*") && !line.startsWith("#")) {
			if (current != null) {
				//去掉注释
				for(String str : notes){
					int num = line.indexOf(str);
					if(num != -1){
						line = line.substring(0, num);
					}
				}
				if(line.length() > 0 && line.indexOf("=") != -1){
					String[] kv = line.split("=", 2);
					current.put(kv[0].trim(), kv[1].trim());
				}
			}
		}
	}
	  static boolean isInSection = false;

	/**
	 * 修改ini配置文件中变量的�?
	 *
	 *            配置文件的路�?
	 * @param section
	 *            要修改的变量�?��段名�?
	 * @param variable
	 *            要修改的变量名称
	 * @param value
	 *            变量的新�?
	 * @throws IOException
	 *             抛出文件操作可能出现的io异常
	 */
	public boolean setProfileString(String fileName, String section, String variable, String value) {
		BufferedReader reader = null;
		try {
			File file = new File(fileName);
			if (file.exists() && file.isFile()) {
				reader = new BufferedReader(new FileReader(file));
				String fileContent = "", LineStr, line, newLine;
				while ((line = reader.readLine()) != null) {
					LineStr = line.trim();
					line = line.trim();
					int num1 = line.indexOf("//");
					if (num1 != -1) {
						line = line.substring(0, num1);
					}
					line = line.trim();
					if (line.matches("\\[.*\\]")) {
						String currentSecions = line.replaceFirst("\\[(.*)\\]",
								"$1");
						isInSection = TextUtils.equals(currentSecions, section);
						fileContent += LineStr + "\r\n";
						continue;
					}

					if (isInSection && line.matches(".*=.*")
							&& !line.startsWith("#")) {

						// 去掉注释
						for (String str : notes) {
							int num = line.indexOf(str);
							if (num != -1) {
								line = line.substring(0, num);
							}
						}
						if (line.length() > 0 && line.indexOf("=") != -1) {
							String[] kv = line.split("=", 2);
							if (TextUtils.equals(variable, kv[0].trim())) {
								newLine = variable + "=" + value;
								fileContent += newLine + "\r\n";
								while ((line = reader.readLine()) != null) {
									fileContent += line + "\r\n";
								}
								isInSection=false;
								reader.close();
								BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false));
								bufferedWriter.write(fileContent);
								bufferedWriter.flush();
								bufferedWriter.close();
								return true;
							}else{
								fileContent += LineStr + "\r\n";
							}
						}
					} else {
						fileContent += LineStr + "\r\n";
					}
				}
			}
		} catch (Exception e) {
		} 
		return false;
	}

	public String getValue(String section, String name) {
		Map<String, String> sectionmap = sections.get(section);

		if (sectionmap == null) {
			return null;
		}

		String value = sectionmap.get(name);
		return value;
	}
	
	public Set<String> sectionKeys(){
		return sections.keySet();
	}
	
	public Map<String, String> getValues(String section){
		return sections.get(section);
	}

	public boolean containsKey(String section, String key) {
		Map<String, String> m = sections.get(section);
		if(m != null){
			return m.get(key) == null;
		}
		return false;
	}
	
	public Map<String, Map<String, String>> getSections(){
		return sections;
	}

//	public static void main(String[] args) {
//		try {
//			IniReader r = new IniReader("C:/Users/dell/Desktop/aa.ini");
////			String val = r.getValue("SET", "DBNAME");
//			Set<String> keys = r.sectionKeys();
//			for(String k : keys){
//				System.out.println("[" + k + "]");
//				Map<String, String> p = r.getValues(k);
//				for(Map.Entry<String, String> entry : p.entrySet()){
//					System.out.println(entry.getKey() + "==" + entry.getValue());
//				}
//			}
//			
//			Map<String, Map<String, String>> sections = r.getSections();
//			new IniWriter("C:/Users/dell/Desktop/bb.ini", sections);
//			
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}