package cs455.scaling.server;

import java.util.List;

public class workNode implements Runnable
{
    List<Byte[]> dataPacketList;
    int batchSize;
    int batchTime;

    public void run()
    {
        System.out.printf("Batch Size: %d Batch Time: %d", batchSize, batchTime);
    }

    public workNode(int requestedBatchSize, int requestedBathTime)
    {
        this.batchSize = requestedBatchSize;
        this.batchTime = requestedBathTime;
    }
}
