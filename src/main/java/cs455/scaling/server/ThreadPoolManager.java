package cs455.scaling.server;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager
{
    protected static LinkedBlockingQueue<workNode> workQueue;
    private WorkerThread[] workerThreadPool;

    public ThreadPoolManager(int threadPoolSize)
    {
        workQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < threadPoolSize; ++i)
        {
            workerThreadPool[i] = new WorkerThread();
        }
    }

    public void newWork(workNode work)
    {
        workQueue.add(work);
        workQueue.notify();
    }
}
