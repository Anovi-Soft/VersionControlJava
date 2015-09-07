package Version;

import java.util.ArrayList;

public class StockVersion implements IVersion {
	private ArrayList<Integer> levels = new ArrayList<Integer>();
	public StockVersion()
{
		levels.add(0);
		levels.add(0);
	}
	public StockVersion(String version)
	{
		this.levels = ((StockVersion)parse(version)).levels;
	}

	@Override
	public IVersion parse(String data) {
		StockVersion tmp = new StockVersion();
		tmp.levels.clear();
		for (String part : data.split("\\."))
{
			try{
				Integer ipart = Integer.parseInt(part);
				tmp.levels.add(ipart);
			}catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (tmp.levels.isEmpty())
			return null;
		return tmp;
	}

	@Override
	public IVersion update() {
		StockVersion result = (StockVersion)this.clone();
		int tmp = result.levels.get(result.levels.size()-1);
		result.levels.set(result.levels.size()-1, tmp+1);
		return result;
	}

	@Override
	public IVersion updateTo(int level) {
		if (level+1 == levels.size())
			return update();
		StockVersion result;
		result = (StockVersion)this.clone();
		if (level+1 > levels.size())
{
			for (int i = 0; i < level+1-levels.size(); i++)
				result.levels.add(0);
			return result.update();
		}
		else{
			for (int i = level+1 ; i < levels.size(); i++)
				result.levels.set(i, 0);
			int tmp = result.levels.get(level);
			result.levels.set(level, tmp+1);
			return result;
		}
		
	}
	
	@Override
	public String toString()
{
		String result = "";
		for (int i:levels)
			result+=i+".";
		return result.substring(0, result.length()-1);
	}
	
	@Override
	public boolean equals(Object anObject)
{
		if (anObject instanceof StockVersion)
{
			return levels.equals(((StockVersion)anObject).levels);
		}
		else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone()
{
		StockVersion result = new StockVersion();
		result.levels = (ArrayList<Integer>) this.levels.clone();
		return result;
	}
	
	public VersionType type()
{
		return VersionType.Stock;
	}

	@Override
	public int compareTo(IVersion _arg) {
		if (!(_arg instanceof StockVersion))
			return 0;
		int i = 0;
		StockVersion arg = (StockVersion)_arg;
		while (i<this.levels.size() && i<arg.levels.size())
{
			if (this.levels.get(i)!=arg.levels.get(i))
				return this.levels.get(i)-arg.levels.get(i);
			i++;
		}
		if (this.levels.size()!=arg.levels.size())
			return this.levels.size() - arg.levels.size();
		return 0;
	}
}
