package Auth;

import java.io.*;
import java.util.HashMap;

import Exceptions.AuthException;
import Util.FileHelper;

public class StockServerAuth implements IServerAuth {
	private String path;
	private HashMap<String,String> mapLogin = new HashMap();
	public StockServerAuth(String path)
	{
		this.path = path;
		File file = new File(this.path);
		FileHelper.makeFolders(this.path);
		if (!(file.exists()))
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		Refresh();
	}

	private void Refresh()
	{

		synchronized (StockServerAuth.class)
		{
			mapLogin.clear();
			for(String line : FileHelper.readAll(path))
			{
				String[] splt = line.split("/");
				mapLogin.put(splt[0], splt[1]);
			}
		}
	}

	@Override
	public boolean Login(String login, String password) {
		Refresh();
		return mapLogin.containsKey(login) && mapLogin.get(login).equals(password);
	}

	@Override
	public boolean AddUser(String login, String password) throws AuthException{
		Refresh();
		if (login.contains("/") && password.contains("/"))
			throw new AuthException("You can`t use \"/\"");
		if (mapLogin.containsKey(login))
			throw new AuthException("The name is busy");
		mapLogin.put(login, password);
       
		synchronized (StockServerAuth.class)
		{
			File file = new File(this.path);
		    try {
		        if(!file.exists())
				{
		            file.createNewFile();
		        }
				FileOutputStream fos = new FileOutputStream(file, true);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
		        try {
					osw.write(login + "/" + password + "\n");
		        } finally {
		            osw.close();
		        }
		    } catch (IOException e) {
		        throw new RuntimeException(e);
		    }
		}
	    
		return true;
	}

}
