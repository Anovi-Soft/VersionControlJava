package Workers;


public interface IWorker {
	public void work(Object args);
	public String name();
	public boolean rightArg(Object args);
}
