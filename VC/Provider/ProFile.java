package Provider;

import java.io.InputStream;
import java.io.OutputStream;

public class ProFile {
	private InputStream is;
	private OutputStream os;
	ProFile(InputStream is, OutputStream os)
{
		this.is = is;
		this.os = os;
	}
	public InputStream getIS()
{
		return this.is;
	}
	public OutputStream getOS()
{
		return this.os;
	}
}
