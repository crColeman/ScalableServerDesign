package cs455.scaling.server;

import java.util.Date;

public class ServerStatistics implements Runnable
{
    private final Server server;

    public ServerStatistics(Server server)
    {
        this.server = server;
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {

            System.out.printf("[%s] Server Throughput: %d message/s, " +
                                      "Active Client Connections: %d, " +
                                      "Mean Per-client Throughput: %d messages/s," +
                                      "Std. Dev. Of Per-Client Throughput: %d messages/s\n",
                              new Date().toString(), server.getReplySentCount()/5, server.getClientConnectionCount(),-1,-1);
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
