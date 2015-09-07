package Exceptions;

public class UnknownCommandException extends VersionControlException {
	public UnknownCommandException()
{
		super("Unknown Command");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
