package Version;

public class VersionFactory {
	public static IVersion parse(int i)
{
		VersionType a = VersionType.values()[i];
		switch(a)
{
		case Stock:
			return new StockVersion();
		default:
			return null;
		}
	}
}
