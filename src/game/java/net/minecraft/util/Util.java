package net.minecraft.util;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.futures.ExecutionException;
import net.lax1dude.eaglercraft.v1_8.futures.FutureTask;

public class Util
{
    @SideOnly(Side.CLIENT)
    public static Util.EnumOS getOSType()
    {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (s.contains("win"))
        {
            return Util.EnumOS.WINDOWS;
        }
        else if (s.contains("mac"))
        {
            return Util.EnumOS.OSX;
        }
        else if (s.contains("solaris"))
        {
            return Util.EnumOS.SOLARIS;
        }
        else if (s.contains("sunos"))
        {
            return Util.EnumOS.SOLARIS;
        }
        else if (s.contains("linux"))
        {
            return Util.EnumOS.LINUX;
        }
        else
        {
            return s.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN;
        }
    }

    @Nullable
    public static <V> V runTask(FutureTask<V> task, Logger logger)
    {
        try
        {
            task.run();
            return task.get();
        }
        catch (ExecutionException executionexception)
        {
            logger.fatal("Error executing task", (Throwable)executionexception);
        }
        catch (InterruptedException interruptedexception)
        {
            logger.fatal("Error executing task", (Throwable)interruptedexception);
        }

        return (V)null;
    }

    public static <T> T getLastElement(List<T> list)
    {
        return list.get(list.size() - 1);
    }

    @SideOnly(Side.CLIENT)
    public static enum EnumOS
    {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN;
    }
}
