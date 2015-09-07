package Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipperOriginal {
	public void zip(String srcFolder, String srcZip) throws IOException {
		FileOutputStream fos = new FileOutputStream(srcZip);
		ZipOutputStream zip = new ZipOutputStream(fos);
		addFolderToZip("", srcFolder, zip);
	    zip.flush();
	    zip.close();
		}

	private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws IOException {
	    File folder = new File(srcFile);
	    if (folder.isDirectory()) {
	    	addFolderToZip(path, srcFile, zip);
	    } 
	    else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
			in.close();
		}
	}
	  
	private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
		File folder = new File(srcFolder);
		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} 
			else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
	    }
	}
	
	public void unzip(String srcZip, String srcFolder) throws IOException {
		File first_dir = new File(srcFolder);
		if (!first_dir.exists())
			first_dir.mkdirs();
	    ZipFile zip = new ZipFile(srcZip); 
	    Enumeration<? extends ZipEntry> entries = zip.entries(); 
	    LinkedList<ZipEntry> zfiles = new LinkedList();
	    while (entries.hasMoreElements()) { 
	      ZipEntry entry = entries.nextElement();
	      if (entry.isDirectory()) { 
	        new File(srcFolder+"/"+entry.getName()).mkdir(); 
	      } else { 
	        zfiles.add(entry); 
	      } 
	    } 
	    for (ZipEntry entry : zfiles) { 
	      FileHelper.makeFolders(srcFolder+"/"+entry.getName());
	      InputStream in = zip.getInputStream(entry);
		  File tmp = new File(srcFolder+"/"+entry.getName());
		  if (tmp.exists())
			  tmp.delete();
	      OutputStream out = new FileOutputStream(srcFolder+"/"+entry.getName()); 
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
