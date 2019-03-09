package cs455.scaling.server;

public class WorkerThread extends Thread
{
    public void run()
    {
        Runnable work;
        while (true)
        {
            synchronized (ThreadPoolManager.workQueue)
            {
                if (ThreadPoolManager.workQueue.isEmpty())
                {
                    try
                    {
                        ThreadPoolManager.workQueue.wait();
                    } catch (InterruptedException e)
                    {
                        System.out.println("A worker thread was interrupted while waiting for work\n" + e);
                    }
                }
                work = ThreadPoolManager.workQueue.poll();
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
