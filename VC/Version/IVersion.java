package Version;

public interface IVersion {
	IVersion parse(String data);
	IVersion update();
	IVersion updateTo(int level);
	int compareTo(IVersion arg);
}
