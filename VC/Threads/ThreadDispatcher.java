package Threads;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import Workers.IWorker;


public class ThreadDispatcher {
	private static volatile ThreadDispatcher instance;
	private Map<Integer, Thread> thread_map = Collections.synchronizedMap(new TreeMap<Integer,Thread>());
	
	public static ThreadDispatcher getInstance()
{
		ThreadDispatcher local_instance = instance;
		if (local_instance == null) {
			synchronized (ThreadDispatcher.class) {
				local_instance = instance;
				if (local_instance == null)
					instance = local_instance = new ThreadDispatcher();
			}
		}
		return local_instance;
	}
	
	public int addThread(IWorker worker, Object args)
{
		synchronized(this)
{
			final int id = get_free_id();
			WThread tw = new WThread(worker, args);
			thread_map.put(id, new Thread(tw));
			thread_map.get(id).start();
			new Thread(new Runnable()
		    {
		        public void run() 
		        {
		        	try {
						thread_map.get(id).join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			thread_map.remove(id);
        			System.out.println("Поток №"+id+" завершился");
		        }
		    }).start();
			return id;
		}
	}
	private int get_free_id()
{
		int i = 0;
		for (int value : thread_map.keySet())
{
			if (i!=value)
				return i;
			i++;
		}
		return thread_map.size();
	}
	public boolean isAlive(int id)
{
		return thread_map.get(id).isAlive();
	}
	public Collection<Thread> threads()
{
		return thread_map.values();
	}
	public void interrupt(int id)
{
		thread_map.get(id).interrupt();
	}
}
