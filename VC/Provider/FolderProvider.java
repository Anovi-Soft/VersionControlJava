package Provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import Util.FileHelper;
import Version.IVersion;
import Version.VersionFactory;
import Version.VersionType;

public class FolderProvider implements IDataProvider {
	private String path_projects;
	private HashMap<String, ArrayList<IVersion>> info = new HashMap();
	public FolderProvider(String path)
	{
		path_projects = path;
		readData();
	}
	private void readData()
	{
		info.clear();
		File pathFile = new File(path_projects);
		FileHelper.makeFolders(path_projects+"/a");
		for (File file : pathFile.listFiles())
			if(file.isDirectory())
			{
				String path_info = FileHelper.concat(file.getAbsolutePath(), "project.info");
				int vers = Integer.parseInt(FileHelper.readAll(path_info).get(0));
				final IVersion version = VersionFactory.parse(vers);
				ArrayList<IVersion> lstVers = new ArrayList();
				for (File fileVers : file.listFiles()) if (fileVers.isDirectory())
					lstVers.add(version.parse(fileVers.getName()));
				Collections.sort(lstVers, new Comparator<IVersion>() {
			        public int compare(IVersion o1, IVersion o2) {
		                return o1.compareTo(o2);
		        }});
				if (lstVers.isEmpty())
					lstVers.add(version);
				info.put(file.getName(), lstVers );
			}
	}
	@Override
	public boolean existsProject(String project_name)
	{
		readData();
		return info.containsKey(project_name);
	}

	@Override
	public boolean existsVersion(String project_name, IVersion version) {
		readData();
		if (!existsProject(project_name))
			return false;
		return info.get(project_name).contains(version);
	}

	@Override
	public IVersion maxProjectVersion(String project_name) {
		readData();
		if (!existsProject(project_name))
			return null;
		return info.get(project_name).get(info.get(project_name).size() - 1);
	}

	@Override
	public ArrayList<IVersion> projectVersions(String project_name) {
		return info.get(project_name);
	}

	@Override
	public int countOfFiles(String project_name, IVersion version) {
		if (!existsVersion(project_name, version))
			return -1;
		String subdir = FileHelper.concat(project_name, version.toString());
		subdir = FileHelper.concat(path_projects, subdir);
		return FileHelper.files_count(subdir);
	}

	@Override
	public long projectSize(String project_name, IVersion version) {
		if (!existsVersion(project_name, version))
			return -1;
		String subdir = FileHelper.concat(project_name, version.toString());
		subdir = FileHelper.concat(path_projects, subdir);
		return FileHelper.getSize(subdir);
	}

	@Override
	public HashMap<String,ProFile> getListOfStreams(String project_name,
			IVersion version) {
		if (!existsVersion(project_name, version))
			return null;
		HashMap<String,ProFile> result = new HashMap();
		String folder_name = FileHelper.concat(new File(path_projects).getAbsolutePath(),
				FileHelper.concat(project_name,
				version.toString())).replace("/","\\");
		ArrayList<File> paths = FileHelper.pathsGet(folder_name);
		for (File file : paths)if(file.isFile())
		{
			String key = file.getAbsolutePath().replace(folder_name, "");
			if (key.startsWith("\\"))
				key = key.substring(1);
			try {
				FileInputStream is = new FileInputStream(file.getAbsolutePath());
				FileOutputStream os = new FileOutputStream(file.getAbsolutePath());
				ProFile value = new ProFile(is, os);
				result.put(key, value);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	@Override
	public void createEmptyVersion(String project_name, IVersion version, List<String> subPaths) {
		if (existsVersion(project_name, version))
			return;
		String subdir = FileHelper.concat(project_name, version.toString());
		info.get(project_name).add(version);
		subdir = FileHelper.concat(path_projects, subdir);
		for (String end : subPaths)
			try {
				String file = FileHelper.concat(subdir, end);
				FileHelper.makeFolders(file);
				new File(file).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	@Override
	public void createProject(String project_name, VersionType vt) {
		String dir = FileHelper.concat(path_projects, project_name);
		FileHelper.makeFolders(dir + "/" +VersionFactory.parse(vt.getValue()).toString() + "/1");
		File file = new File(dir);
		if (!file.exists())
			file.mkdirs();
		dir = FileHelper.concat(dir, "project.info");
		FileHelper.makeFolders(dir);
		file = new File(dir);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			PrintWriter out = new PrintWriter(dir);
			out.print(vt.getValue());
			out.close();
			readData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
