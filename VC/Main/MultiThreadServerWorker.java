package Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Packer.StockPacker;
import Packet.CommandType;
import Packet.PackCommand;
import Threads.WThread;
import Util.ProSocket;
import Version.StockVersion;
import Workers.IWorker;
import Workers.ServerWorkerFactory;

public class MultiThreadServerWorker
{
	private static volatile MultiThreadServerWorker instance;
	private volatile ServerSocket ssocket;
	private ExecutorService thread_pool = Executors.newCachedThreadPool();
	private Map<Integer, Future<?>> status_map = Collections.synchronizedMap(new TreeMap<Integer, Future<?>>());
	
	private MultiThreadServerWorker(short port)
	{
		try 
		{
			
			this.ssocket = new ServerSocket(port);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public static MultiThreadServerWorker getInstance(short port)
	{
		MultiThreadServerWorker local_instance = instance;
		if (local_instance == null) 
		{
			synchronized (MultiThreadServerWorker.class) 
			{
				local_instance = instance;
				if (local_instance == null)
					instance = local_instance = new MultiThreadServerWorker(port);
			}
		}
		return local_instance;
	}
	public void MainLop()
	{
		thread_pool.submit(new Runnable()
			{public void run(){synchronized (this){while (true)
			{
				int shift = 30000; 
				int id = GetFreeId();
				ProSocket socket = new ProSocket(Listen(ssocket), new StockPacker());
				socket.sendPacket(new PackCommand(CommandType.Port, new Integer(id + shift).toString() ));
				
				PackCommand pack = socket.getPackCommand(CommandType.Version);
				StockVersion version = (StockVersion) new StockVersion().parse(pack.getArgs());
				
				IWorker worker = ServerWorkerFactory.get(version);
				if (worker == null)
				{
					socket.sendPacket(new PackCommand(CommandType.Exception, "Unknown Version"));
				}
				else
				{
					socket.sendPacket(new PackCommand(CommandType.OK, ""));					
				}

				socket.close();
				Socket newSocket;
				try {
					ServerSocket ss = new ServerSocket(id + shift);
					newSocket = Listen(ss);
					ss.close();					
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				
				Future<?> future = thread_pool.submit(new WThread(worker, newSocket));
				status_map.put(GetFreeId(), future);
			}}}});
	}
	private int GetFreeId()
	{
		int i = 0;
		for (int value : status_map.keySet())
		{
			if (i!=value)
				return i;
			i++;
		}
		return status_map.size();
	}
	private Socket Listen(ServerSocket ssocket)
	{
		try 
		{
			return ssocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void Close()
	{
		try 
		{
			ssocket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
}
