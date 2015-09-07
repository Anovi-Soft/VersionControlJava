package Version;

public enum VersionType {
	Stock(0);
	private final int value;
	VersionType(int value)
{
		this.value = value;
	}
	public int getValue()
{
		return value;
	}
}
