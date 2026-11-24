package net.minecraft.world.storage;

public class ThreadedFileIOBase
{
    private static final ThreadedFileIOBase INSTANCE = new ThreadedFileIOBase();

    private ThreadedFileIOBase()
    {
    }

    public static ThreadedFileIOBase getThreadedIOInstance()
    {
        return INSTANCE;
    }

    public void queueIO(IThreadedFileIO fileIo)
    {
        while(fileIo.writeNextIO())
        {
        }
    }

    public void waitForFinish() throws InterruptedException
    {
    }
}
