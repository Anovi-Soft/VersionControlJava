package Threads;


import Workers.IWorker;

public class WThread implements Runnable {
	private Object args;
	private IWorker worker;
	public WThread (IWorker worker, Object args)
{
		this.args = args;
		System.out.println("Поток работающий с "+this.args+" инициализирован" );
		this.worker = worker;
	}
	@Override
	public void run() {
		System.out.println("Поток работающий с "+this.args+" стартовал" );
		this.worker.work(this.args);
		finishing();
	}
	private void finishing() {
		System.out.println("Поток работающий с "+this.args+" завершается" );
	}

}
