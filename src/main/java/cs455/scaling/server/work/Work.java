package cs455.scaling.server.work;

import cs455.scaling.server.Server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

public abstract class Work implements Runnable
{
    SelectionKey key;
    Server server;

    List<Byte[]> dataPacketList;
    int batchSize;
    int batchTime;

}
