package cs455.scaling.server;

import java.util.List;

public class workNode
{
    List<Byte[]> dataPacketList;
    int batchSize;
    int batchTime;

    public workNode(int requestedBatchSize, int requestedBathTime)
    {
        this.batchSize = requestedBatchSize;
        this.batchTime = requestedBathTime;
    }
}
