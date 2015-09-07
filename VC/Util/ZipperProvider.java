package Util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import Provider.IDataProvider;
import Provider.ProFile;
import Version.IVersion;

public class ZipperProvider {
	public void zip(HashMap<String,ProFile> lst, String srcZip) throws IOException {
		FileOutputStream fos = new FileOutputStream(srcZip);
		ZipOutputStream zip = new ZipOutputStream(fos);
		for (Entry<String, ProFile> a : lst.entrySet())
			addFileToZip(a.getKey(), a.getValue().getIS(), zip);
	    zip.flush();
	    zip.close();
		}

	private void addFileToZip(String path, InputStream in , ZipOutputStream zip) throws IOException{
		byte[] buf = new byte[1024];
		int len;
		zip.putNextEntry(new ZipEntry(path));
		while ((len = in.read(buf)) > 0) {
			zip.write(buf, 0, len);
		}
		in.close();
	}
	
	public void unzip(String path, IDataProvider provider, String project_name, IVersion version) throws IOException { 
		ZipFile zip = new ZipFile(path); 
	    Enumeration<? extends ZipEntry> entries = zip.entries(); 
	    LinkedList<ZipEntry> zfiles = new LinkedList();
	    while (entries.hasMoreElements()) { 
	      ZipEntry entry = entries.nextElement();
	      if (!(entry.isDirectory())) 
	        zfiles.add(entry);
	    } 
	    
	    ArrayList<String> tmp = new ArrayList();
	    for (ZipEntry entry : zfiles)
	    	tmp.add(entry.getName());
	    provider.createEmptyVersion(project_name, version, tmp);
	    HashMap <String, ProFile> map = provider.getListOfStreams(project_name, version);
	    
	    for (ZipEntry entry : zfiles) { 
	      InputStream in = zip.getInputStream(entry);
		  String h = entry.toString().replace("/", "\\");
	      OutputStream out = map.get(h).getOS();
	      byte[] buffer = new byte[1024]; 
	      int len; 
	      while ((len = in.read(buffer)) >= 0) 
	        out.write(buffer, 0, len); 
	      in.close(); 
	      out.close(); 
	      } 
	    zip.close(); 
	  } 
		  
}
