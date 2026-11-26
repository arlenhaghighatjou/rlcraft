package atomicstryker.multimine.common;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import atomicstryker.multimine.common.network.NetworkHelper;
import atomicstryker.multimine.common.network.PartialBlockPacket;
import atomicstryker.multimine.common.network.PartialBlockRemovalPacket;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

/**
 * FML superclass causing all of the things to happen. Registers everything,
 * causes the Mod parts to load, keeps the common config file.
 */
@Mod(modid = "multimine", name = "Multi Mine", version = "1.6.2")
public class MultiMine
{
    public void handleModStateEvent(FMLEvent event)
    {
        if (event instanceof FMLConstructionEvent)
        {
            instance = this;
            proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new atomicstryker.multimine.client.ClientProxy() : new atomicstryker.multimine.common.CommonProxy();
        }
        else if (event instanceof FMLPreInitializationEvent)
        {
            this.preInit((FMLPreInitializationEvent)event);
        }
        else if (event instanceof FMLInitializationEvent)
        {
            this.load((FMLInitializationEvent)event);
        }
    }

    private static MultiMine instance;

    private boolean blockRegenEnabled;
    private long initialBlockRegenDelay;
    private long blockRegenInterval;
    private boolean lazyConfigWriteEnabled;

    private boolean debugMode;
    private Logger LOGGER;
    public Configuration config;

    public static CommonProxy proxy;

    public NetworkHelper networkHelper;

    public void preInit(FMLPreInitializationEvent evt)
    {
        networkHelper = new NetworkHelper("AS_MM", PartialBlockPacket::new, PartialBlockRemovalPacket::new);

        config = new Configuration(evt.getSuggestedConfigurationFile());
        config.load();

        blockRegenEnabled = config.get("general", "Block Regeneration Enabled", true).getBoolean(true);
        initialBlockRegenDelay = config.get("general", "Initial Block Regen Delay in ms", 5000).getInt();
        blockRegenInterval = config.get("general", "Block 10 percent Regen Interval in ms", 1000).getInt();
        lazyConfigWriteEnabled = config.get("general", "Config auto-saves unkown blocks - might cause performance issues with mods", true).getBoolean(true);

        debugMode = config.get("general", "debugMode", false, "Tons of debug printing. Only enable if really needed.").getBoolean(false);

        config.save();

        proxy.onPreInit();
        LOGGER = evt.getModLog();
    }

    public void load(FMLInitializationEvent evt)
    {
        proxy.onLoad();
    }

    public static MultiMine instance()
    {
        return instance;
    }

    public boolean getBlockRegenEnabled()
    {
        return blockRegenEnabled;
    }

    public long getInitialBlockRegenDelay()
    {
        return initialBlockRegenDelay;
    }

    public long getBlockRegenInterval()
    {
        return blockRegenInterval;
    }

    public boolean getLazyConfigWriteEnabled()
    {
        return lazyConfigWriteEnabled;
    }

    public void debugPrint(String s, Object... params)
    {
        if (debugMode)
        {
            LOGGER.info(s, params);
        }
    }
}
