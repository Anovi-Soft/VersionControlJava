package Workers;

import Version.StockVersion;


public class ServerWorkerFactory {	
	public static IWorker get(StockVersion version)
	{
		switch(version.toString())
		{
		case "1.0.0": return new ServerWorkerV1();
		default: return null;
		}
	}
}
