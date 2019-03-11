package cs455.scaling.server;

import java.util.concurrent.LinkedBlockingQueue;

class WorkerThread extends Thread
{
    private final LinkedBlockingQueue<Runnable> workQueue;
    WorkerThread(LinkedBlockingQueue workQueue)
    {
        System.out.println("Worker Thread Constructor");
        this.workQueue = workQueue;
    }

    public void run()
    {
        System.out.println("Worker Thread Run");
        Runnable work;
        while (true)
        {
            synchronized (workQueue)
            {
                if (workQueue.isEmpty())
                {
                    try
                    {
                        workQueue.wait();
                    } catch (InterruptedException e)
                    {
                        System.out.println("A worker thread was interrupted while waiting for work\n" + e);
                    }
                }
                synchronized (workQueue)
                {
                    work = workQueue.poll();
                }
            }

            try
            {
                work.run();
            } catch (RuntimeException e)
            {
                System.out.println("A worker thread threw a RuntimeException: " + e);
            }
        }
    }
}
