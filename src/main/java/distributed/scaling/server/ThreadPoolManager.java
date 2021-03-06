package cs455.scaling.server;

import cs455.scaling.server.work.Work;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolManager
{

    public final LinkedBlockingQueue<ConcurrentLinkedQueue<Work>> workQueue;
    private WorkerThread[] workerThreadPool;
    private final int batchSize;
    private final int batchTime;


    public ThreadPoolManager(int threadPoolSize, int batchSize, int batchTime)
    {
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        workerThreadPool = new WorkerThread[threadPoolSize];
        workQueue = new LinkedBlockingQueue<>();
        for (int i = 0; i < threadPoolSize; ++i)
        {
            workerThreadPool[i] = new WorkerThread(workQueue);
        }
    }

    public void initializeThreadPool()
    {
        for(int i=0; i<workerThreadPool.length; ++i)
        {
            new Thread(workerThreadPool[i]).start();
        }
    }


    public void addWork(ConcurrentLinkedQueue<Work> workList)
    {

        synchronized (workQueue)
        {
            try
            {
                workQueue.put(workList);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            workQueue.notify();
        }
    }

    public ConcurrentLinkedQueue<Work> getWork()
    {
        synchronized (workQueue)
        {
            return workQueue.poll();
        }

    }

}
