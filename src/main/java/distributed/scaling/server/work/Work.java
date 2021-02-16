package cs455.scaling.server.work;

import cs455.scaling.server.Server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Work implements Runnable
{
    SelectionKey key;
    Server server;

    LinkedBlockingQueue<Byte[]> dataPacketList;
    int batchSize;
    int batchTime;

}
