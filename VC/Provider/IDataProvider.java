package Provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Version.IVersion;
import Version.VersionType;

public interface IDataProvider {
	public boolean existsProject(String project_name);
	public boolean existsVersion(String project_name, IVersion version);
	public IVersion maxProjectVersion(String project_name);
	public ArrayList<IVersion> projectVersions(String project_name);
	public int countOfFiles(String project_name, IVersion version);
	public long projectSize(String project_name, IVersion version);
	public void createEmptyVersion(String project_name, IVersion version, List<String> subPaths);
	public HashMap<String,ProFile> getListOfStreams(String project_name, IVersion version);
	public void createProject(String project_name, VersionType vt);
	
}
