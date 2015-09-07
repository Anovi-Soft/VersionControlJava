package Exceptions;

public class VersionControlException extends Exception {
	String msg;
	public String getMessage()
{
		return msg;
	}
	public VersionControlException()
{
		this.msg = "";
	}
	public VersionControlException(String msg)
{
		this.msg = msg;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
