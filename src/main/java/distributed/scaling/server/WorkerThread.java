package cs455.scaling.server;

import cs455.scaling.server.work.Work;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

class WorkerThread extends Thread
{
    private final LinkedBlockingQueue<ConcurrentLinkedQueue<Work>> workQueue;
    WorkerThread(LinkedBlockingQueue<ConcurrentLinkedQueue<Work>> workQueue)
    {
        this.workQueue = workQueue;
    }

    public void run()
    {
        ConcurrentLinkedQueue<Work> workList = null;
            while (true)
            {
                synchronized (workQueue)
                {
                    if (workQueue.isEmpty())
                    {
                        try
                        {
                            workQueue.wait();
                            workList = workQueue.poll();
                        }
                        catch (InterruptedException e)
                        {
                            System.out.println("A worker thread was interrupted while waiting for work\n" + e);
                        }
                    }

                }

                try
                {
                    if ( workList != null)
                    {
                        Iterator<Work> workListIterator = workList.iterator();
                        while (workListIterator.hasNext())
                        {
                            if (workList.isEmpty())
                            {
                                continue;
                            }

                            Runnable workUnit;
                            if((workUnit = workList.poll()) != null)
                            Objects.requireNonNull(workUnit).run();
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("A worker thread threw a RuntimeException: " + e + " Size " + workList.size());
                    e.printStackTrace();
                }
            }
    }
}
