package Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class FileHelper {
	public static ArrayList<File> pathsGet(String path)
{
		return pathsGet(new File(path));
	}
	public static ArrayList<File> pathsGet(File path)
{
		ArrayList<File> lst = new ArrayList<File>();
		if(!(path.exists()))
			return lst;
		
		lst.add(path);		
		if (path.isDirectory())
{
			for(File file: path.listFiles())
				lst.addAll(pathsGet(file));
		}
		return lst;
	}
	public static long getSize(File path)
{
		long size = 0;
		for(File file: pathsGet(path))
			if (file.isFile())
				size += file.length();
		return size;
	}
	public static long getSize(String path)
{
		return getSize(new File(path));
	}
	public static int files_count(File path)
{
		int count = 0;
		for (File file : pathsGet(path))
			if (file.isFile())
				count++;
		return count;
	}
	public static int files_count(String path)
{
		return files_count(new File(path));
	}
	public static boolean checkName(String name)
{
		HashSet<String> badCharacters = new HashSet<String>(Arrays.asList("".split(",")));
		for (int i =0; i< name.length(); i++)
			if (badCharacters.contains(name.charAt(i)))
				return false;
		return true;
	}
	public static String concat(String first, String second)
{
		first = first.replace("\\", "/");
		second = second.replace("\\", "/");
		if (first.endsWith("/"))
			first = first.substring(0, first.length()-1);
		if (second.startsWith("/"))
			second = second.substring(1);
		return first + "/" + second;
	}
	public static ArrayList<String> readAll(String path)
{
		ArrayList<String> result = new ArrayList<String>();
		Scanner in;
		try {
			in = new Scanner(new File(path));
			while(in.hasNext())
				result.add(in.nextLine());
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void makeFolders(String path)
	{
		String sub_path = "";
		String[] splt = path.replace("\\","/").split("/");
		for (int i = 0; i< splt.length-1; i++)
		{
			sub_path += "/" + splt[i];
			if (sub_path.startsWith("/"))
				sub_path = sub_path.substring(1);
			File sub_dir = new File(sub_path);
			if (!sub_dir.exists())
				sub_dir.mkdirs();
		}
	}

	public static boolean freeFolder(File path)
	{
		if(path.exists()){
			File[] files = path.listFiles();
			if(null!=files){
				for(int i=0; i<files.length; i++) {
					if(files[i].isDirectory()) {
						freeFolder(files[i]);
					}
					else {
						files[i].delete();
					}
				}
			}
		}
		return(path.delete());
	}
	public static void freeFolder(String path)
	{
		freeFolder(new File(path));
		makeFolders(path+"/1");
	}
}
