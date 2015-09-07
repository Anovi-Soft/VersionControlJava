package Auth;

import Exceptions.AuthException;

public interface IServerAuth
{
	boolean Login(String login, String password);
	boolean AddUser(String login, String password) throws AuthException;
}
