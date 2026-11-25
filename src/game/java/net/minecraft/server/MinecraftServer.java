package net.minecraft.server;

import net.lax1dude.eaglercraft.v1_8.ThreadLocalRandom;
import com.google.common.collect.Lists;
import net.lax1dude.eaglercraft.v1_8.futures.Futures;
import net.lax1dude.eaglercraft.v1_8.futures.ListenableFuture;
import net.lax1dude.eaglercraft.v1_8.futures.ListenableFutureTask;
import net.lax1dude.eaglercraft.v1_8.futures.FutureTask;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerIntegratedServerWorker;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.concurrent.Callable;
import net.lax1dude.eaglercraft.v1_8.futures.Executors;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public abstract class MinecraftServer implements ICommandSender, IThreadListener
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final VFile2 USER_CACHE_FILE = new VFile2("usercache.json");
    private final ISaveFormat anvilConverterForAnvilFile;
    private final List<ITickable> tickables = Lists.<ITickable>newArrayList();
    public final ICommandManager commandManager;
    public final Profiler profiler = new Profiler();
    private final ServerStatusResponse statusResponse = new ServerStatusResponse();
    private final EaglercraftRandom random = new EaglercraftRandom();
    private final DataFixer dataFixer;
    @SideOnly(Side.SERVER)
    private String hostname;
    private int serverPort = -1;
    public WorldServer[] worlds = new WorldServer[0];
    private PlayerList playerList;
    protected boolean serverRunning = true;
    private boolean serverStopped;
	private Thread serverThread;
    private int tickCounter;
    public String currentTask;
    public int percentDone;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean canSpawnAnimals;
    private boolean canSpawnNPCs;
    private boolean pvpEnabled;
    private boolean allowFlight;
    private String motd;
    private int buildLimit;
    private int maxPlayerIdleMinutes;
    public final long[] tickTimeArray = new long[100];
    //public long[][] timeOfLastDimensionTick;
    public java.util.Hashtable<Integer, long[]> worldTickTimes = new java.util.Hashtable<Integer, long[]>();
    private KeyPair serverKeyPair;
    private String serverOwner;
    private String folderName;
    @SideOnly(Side.CLIENT)
    private String worldName;
    private boolean isDemo;
    private boolean enableBonusChest;
    private String resourcePackUrl = "";
    private String resourcePackHash = "";
    private boolean serverIsRunning;
    protected long timeOfLastWarning;
    private String userMessage;
    private boolean startProfiling;
    private boolean isGamemodeForced;
    private final PlayerProfileCache profileCache;
    private long nanoTimeSinceStatusRefresh;
    public final Queue < FutureTask<? >> futureTaskQueue = new ArrayDeque<>();
    protected long currentTime = getCurrentTimeMillis();
    private boolean spawnChunksLoaded;
    @SideOnly(Side.CLIENT)

    public MinecraftServer(String worldName)
    {
        this.profileCache = new PlayerProfileCache(USER_CACHE_FILE);
        this.commandManager = this.createCommandManager();
        this.anvilConverterForAnvilFile = new net.lax1dude.eaglercraft.v1_8.sp.server.EaglerSaveFormat(net.lax1dude.eaglercraft.v1_8.sp.server.EaglerSaveFormat.worldsFolder);
        this.dataFixer = DataFixesManager.createFixer();
        this.folderName = worldName;
        this.worldName = worldName;
    }

    public ServerCommandManager createCommandManager()
    {
        return new ServerCommandManager(this);
    }

    protected abstract boolean startServer() throws IOException;

    public void convertMapIfNeeded(String worldNameIn)
    {
        if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn))
        {
            LOGGER.info("Converting map!");
            this.setUserMessage("menu.convertingLevel");
            this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate()
            {
                private long startTime = System.currentTimeMillis();
                public void displaySavingString(String message)
                {
                }
                public void setLoadingProgress(int progress)
                {
                    if (System.currentTimeMillis() - this.startTime >= 1000L)
                    {
                        this.startTime = System.currentTimeMillis();
                        MinecraftServer.LOGGER.info("Converting... {}%", (int)progress);
                    }
                }
                @SideOnly(Side.CLIENT)
                public void resetProgressAndMessage(String message)
                {
                }
                @SideOnly(Side.CLIENT)
                public void setDoneWorking()
                {
                }
                public void displayLoadingString(String message)
                {
                }
            });
        }
    }

    protected synchronized void setUserMessage(String message)
    {
        this.userMessage = message;
    }

    @Nullable
    @SideOnly(Side.CLIENT)

    public synchronized String getUserMessage()
    {
        return this.userMessage;
    }

    public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions)
	{
		WorldSettings settings = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
		settings.setGeneratorOptions(generatorOptions);
		if (this.enableBonusChest) settings.enableBonusChest();
		this.loadAllWorlds(saveName, worldNameIn, settings);
	}

	protected void loadAllWorlds(String saveName, String worldNameIn, WorldSettings settings)
    {
        this.convertMapIfNeeded(saveName);
        this.setUserMessage("menu.loadingLevel");
        ISaveHandler isavehandler = this.anvilConverterForAnvilFile.getSaveLoader(saveName, true);
        this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        WorldSettings worldsettings;

        if (worldinfo == null)
        {
            if (this.isDemo())
            {
                worldsettings = WorldServerDemo.DEMO_WORLD_SETTINGS;
            }
            else
            {
                worldsettings = settings;
            }

            worldinfo = new WorldInfo(worldsettings, worldNameIn);
        }
        else
        {
            worldinfo.setWorldName(worldNameIn);
            worldsettings = new WorldSettings(worldinfo);
        }

        if (false) { //Forge Dead code, reimplemented below
        for (int i = 0; i < this.worlds.length; ++i)
        {
            int j = 0;

            if (i == 1)
            {
                j = -1;
            }

            if (i == 2)
            {
                j = 1;
            }

            if (i == 0)
            {
                if (this.isDemo())
                {
                    this.worlds[i] = (WorldServer)(new WorldServerDemo(this, isavehandler, worldinfo, j, this.profiler)).init();
                }
                else
                {
                    this.worlds[i] = (WorldServer)(new WorldServer(this, isavehandler, worldinfo, j, this.profiler)).init();
                }

                this.worlds[i].initialize(worldsettings);
            }
            else
            {
                this.worlds[i] = (WorldServer)(new WorldServerMulti(this, isavehandler, j, this.worlds[0], this.profiler)).init();
            }

            this.worlds[i].addEventListener(new ServerWorldEventHandler(this, this.worlds[i]));

            if (!this.isSinglePlayer())
            {
                this.worlds[i].getWorldInfo().setGameType(this.getGameType());
            }
        }
        } //Forge: End dead code

        WorldServer overWorld = (WorldServer)(isDemo() ? new WorldServerDemo(this, isavehandler, worldinfo, 0, profiler).init() : new WorldServer(this, isavehandler, worldinfo, 0, profiler).init());
        overWorld.initialize(worldsettings);
        for (int dim : net.minecraftforge.common.DimensionManager.getStaticDimensionIDs())
        {
            WorldServer world = (dim == 0 ? overWorld : (WorldServer)new WorldServerMulti(this, isavehandler, dim, overWorld, profiler).init());
            world.addEventListener(new ServerWorldEventHandler(this, world));

            if (!this.isSinglePlayer())
            {
                world.getWorldInfo().setGameType(this.getGameType());
            }
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(world));
        }

        this.playerList.setPlayerManager(new WorldServer[]{ overWorld });
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.updateSpawnChunks(overWorld);
    }

    private void updateSpawnChunks(WorldServer world)
    {
        boolean keepLoaded = world.getGameRules().getBoolean("loadSpawnChunks");
        if (keepLoaded != this.spawnChunksLoaded)
        {
            if (keepLoaded)
            {
                this.initialWorldChunkLoad();
            }
            else
            {
                BlockPos spawn = world.getSpawnPoint();
                net.minecraft.world.gen.ChunkProviderServer provider = world.getChunkProvider();
                for (int x = -192; x <= 192; x += 16)
                {
                    for (int z = -192; z <= 192; z += 16)
                    {
                        int chunkX = (spawn.getX() + x) >> 4;
                        int chunkZ = (spawn.getZ() + z) >> 4;
                        if (!world.getPlayerChunkMap().contains(chunkX, chunkZ))
                        {
                            net.minecraft.world.chunk.Chunk chunk = provider.getLoadedChunk(chunkX, chunkZ);
                            if (chunk != null)
                            {
                                provider.queueUnload(chunk);
                            }
                        }
                    }
                }
            }
            this.spawnChunksLoaded = keepLoaded;
        }
    }

    public void initialWorldChunkLoad()
    {
        int i = 16;
        int j = 4;
        int k = 192;
        int l = 625;
        int i1 = 0;
        this.setUserMessage("menu.generatingTerrain");
        int j1 = 0;
        LOGGER.info("Preparing start region for level 0");
        WorldServer worldserver = net.minecraftforge.common.DimensionManager.getWorld(j1);
        BlockPos blockpos = worldserver.getSpawnPoint();
        long k1 = getCurrentTimeMillis();

        for (int l1 = -192; l1 <= 192 && this.isServerRunning(); l1 += 16)
        {
            for (int i2 = -192; i2 <= 192 && this.isServerRunning(); i2 += 16)
            {
                long j2 = getCurrentTimeMillis();

                if (j2 - k1 > 1000L)
                {
                    this.outputPercentRemaining("Preparing spawn area", i1 * 100 / 625);
                    k1 = j2;
                }

                ++i1;
                worldserver.getChunkProvider().provideChunk(blockpos.getX() + l1 >> 4, blockpos.getZ() + i2 >> 4);
            }
        }

        this.clearCurrentTask();
    }

    public void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn)
    {
        File file1 = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");

        if (file1.isFile())
        {
            try
            {
                this.setResourcePack("level://" + URLEncoder.encode(worldNameIn, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            }
            catch (UnsupportedEncodingException var5)
            {
                LOGGER.warn("Something went wrong url encoding {}", (Object)worldNameIn);
            }
        }
    }

    public abstract boolean canStructuresSpawn();

    public abstract GameType getGameType();

    public abstract EnumDifficulty getDifficulty();

    public abstract boolean isHardcore();

    public abstract int getOpPermissionLevel();

    public abstract boolean shouldBroadcastRconToOps();

    public abstract boolean shouldBroadcastConsoleToOps();

    protected void outputPercentRemaining(String message, int percent)
    {
        this.currentTask = message;
        this.percentDone = percent;
        LOGGER.info("{}: {}%", message, Integer.valueOf(percent));
        EaglerIntegratedServerWorker.sendProgress("singleplayer.busy.startingIntegratedServer", percent * 0.01F);
    }

    protected void clearCurrentTask()
    {
        this.currentTask = null;
        this.percentDone = 0;
    }

    public void saveAllWorlds(boolean isSilent)
    {
        for (WorldServer worldserver : this.worlds)
        {
            if (worldserver != null)
            {
                if (!isSilent)
                {
                    LOGGER.info("Saving chunks for level '{}'/{}", worldserver.getWorldInfo().getWorldName(), worldserver.provider.getDimensionType().getName());
                }

                try
                {
                    worldserver.saveAllChunks(true, (IProgressUpdate)null);
                }
                catch (MinecraftException minecraftexception)
                {
                    LOGGER.warn(minecraftexception.getMessage());
                }
            }
        }
    }

    public void stopServer()
    {
		if (this.serverStopped) return;
		this.serverRunning = false;
		boolean started = this.serverIsRunning;
		this.serverIsRunning = false;
		try
		{
			if (started) net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerStopping();
        LOGGER.info("Stopping server");

        if (this.playerList != null)
        {
            LOGGER.info("Saving players");
            this.playerList.saveAllPlayerData();
            this.playerList.removeAllPlayers();
        }

        if (this.worlds != null)
        {
            LOGGER.info("Saving worlds");

            for (WorldServer worldserver : this.worlds)
            {
                if (worldserver != null)
                {
                    worldserver.disableLevelSaving = false;
                }
            }

            this.saveAllWorlds(false);

            for (WorldServer worldserver1 : this.worlds)
            {
                if (worldserver1 != null)
                {
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(worldserver1));
                    worldserver1.flush();
                }
            }

            WorldServer[] tmp = worlds;
            for (WorldServer world : tmp)
            {
                net.minecraftforge.common.DimensionManager.setWorld(world.provider.getDimension(), null, this);
            }
        }

		}
		finally
		{
			CommandBase.setCommandListener(null);
			this.serverStopped = true;
			if (started) net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerStopped();
		}
    }

    public boolean isServerRunning()
    {
        return this.serverRunning;
    }

    public void initiateShutdown()
    {
        this.serverRunning = false;
    }


	protected void finishServerStartup()
	{
		this.currentTime = getCurrentTimeMillis();
		this.statusResponse.setServerDescription(new TextComponentString(this.motd));
		this.statusResponse.setVersion(new ServerStatusResponse.Version("1.12.2", 340));
		this.serverIsRunning = true;
		net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerStarted();
	}

	protected void bindServerThread()
	{
		this.serverThread = Thread.currentThread();
	}

    public File getDataDirectory()
    {
        return new File(".");
    }

    public void finalTick(CrashReport report)
    {
    }

    public void systemExitNow()
    {
    }

    public void tick()
    {
        this.updateSpawnChunks(this.worlds[0]);
        long i = System.nanoTime();
        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPreServerTick();
        ++this.tickCounter;

        if (this.startProfiling)
        {
            this.startProfiling = false;
            this.profiler.profilingEnabled = true;
            this.profiler.clearProfiling();
        }

        this.profiler.startSection("root");
        this.updateTimeLightAndEntities();

        if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L)
        {
            this.nanoTimeSinceStatusRefresh = i;
            this.statusResponse.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getCurrentPlayerCount()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int j = MathHelper.getInt(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k)
            {
                agameprofile[k] = ((EntityPlayerMP)this.playerList.getPlayers().get(j + k)).getGameProfile();
            }

            ThreadLocalRandom.current().shuffle(Arrays.asList(agameprofile));
            this.statusResponse.getPlayers().setPlayers(agameprofile);
            this.statusResponse.invalidateJson();
        }

        if (this.tickCounter % 900 == 0)
        {
            this.profiler.startSection("save");
            this.playerList.saveAllPlayerData();
            this.saveAllWorlds(true);
            this.profiler.endSection();
        }

        this.profiler.startSection("tallying");
        this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - i;
        this.profiler.endSection();
        this.profiler.endSection();
        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPostServerTick();
    }

    public void updateTimeLightAndEntities()
    {
        this.profiler.startSection("jobs");

        synchronized (this.futureTaskQueue)
        {
            while (!this.futureTaskQueue.isEmpty())
            {
                Util.runTask(this.futureTaskQueue.poll(), LOGGER);
            }
        }

        this.profiler.endStartSection("levels");

        Integer[] ids = net.minecraftforge.common.DimensionManager.getIDs(this.tickCounter % 200 == 0);
        for (int x = 0; x < ids.length; x++)
        {
            int id = ids[x];
            long i = System.nanoTime();

            if (id == 0 || this.getAllowNether())
            {
                WorldServer worldserver = net.minecraftforge.common.DimensionManager.getWorld(id);
                this.profiler.func_194340_a(() ->
                {
                    return worldserver.getWorldInfo().getWorldName();
                });

                if (this.tickCounter % 20 == 0)
                {
                    this.profiler.startSection("timeSync");
                    this.playerList.sendPacketToAllPlayersInDimension(new SPacketTimeUpdate(worldserver.getTotalWorldTime(), worldserver.getWorldTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")), worldserver.provider.getDimension());
                    this.profiler.endSection();
                }

                this.profiler.startSection("tick");
                net.minecraftforge.fml.common.FMLCommonHandler.instance().onPreWorldTick(worldserver);

                try
                {
                    worldserver.tick();
                }
                catch (Throwable throwable1)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
                    worldserver.addWorldInfoToCrashReport(crashreport);
                    throw new ReportedException(crashreport);
                }

                try
                {
                    worldserver.updateEntities();
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Exception ticking world entities");
                    worldserver.addWorldInfoToCrashReport(crashreport1);
                    throw new ReportedException(crashreport1);
                }

                net.minecraftforge.fml.common.FMLCommonHandler.instance().onPostWorldTick(worldserver);
                this.profiler.endSection();
                this.profiler.startSection("tracker");
                worldserver.getEntityTracker().tick();
                this.profiler.endSection();
                this.profiler.endSection();
            }

            worldTickTimes.get(id)[this.tickCounter % 100] = System.nanoTime() - i;
        }

        this.profiler.endStartSection("dim_unloading");
        net.minecraftforge.common.DimensionManager.unloadWorlds(worldTickTimes);
        this.profiler.endStartSection("connection");
		net.lax1dude.eaglercraft.v1_8.sp.server.EaglerIntegratedServerWorker.tick();
        this.profiler.endStartSection("players");
        this.playerList.onTick();
        this.profiler.endStartSection("commandFunctions");
        this.getFunctionManager().update();
        this.profiler.endStartSection("tickables");

        for (int k = 0; k < this.tickables.size(); ++k)
        {
            ((ITickable)this.tickables.get(k)).update();
        }

        this.profiler.endSection();
    }

    public boolean getAllowNether()
    {
        return true;
    }

    public File getFile(String fileName)
    {
        return new File(this.getDataDirectory(), fileName);
    }

    public void logWarning(String msg)
    {
        LOGGER.warn(msg);
    }

    public WorldServer getWorld(int dimension)
    {
        WorldServer ret = net.minecraftforge.common.DimensionManager.getWorld(dimension, true);
        if (ret == null)
        {
            net.minecraftforge.common.DimensionManager.initDimension(dimension);
            ret = net.minecraftforge.common.DimensionManager.getWorld(dimension);
        }
        return ret;
    }

    public String getMinecraftVersion()
    {
        return "1.12.2";
    }

    public int getCurrentPlayerCount()
    {
        return this.playerList.getCurrentPlayerCount();
    }

    public int getMaxPlayers()
    {
        return this.playerList.getMaxPlayers();
    }

    public String[] getOnlinePlayerNames()
    {
        return this.playerList.getOnlinePlayerNames();
    }

    public GameProfile[] getOnlinePlayerProfiles()
    {
        return this.playerList.getOnlinePlayerProfiles();
    }

    public String getServerModName()
    {
        return net.minecraftforge.fml.common.FMLCommonHandler.instance().getModName();
    }

    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        report.getCategory().addDetail("Profiler Position", new ICrashReportDetail<String>()
        {
            public String call() throws Exception
            {
                return MinecraftServer.this.profiler.profilingEnabled ? MinecraftServer.this.profiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });

        if (this.playerList != null)
        {
            report.getCategory().addDetail("Player Count", new ICrashReportDetail<String>()
            {
                public String call()
                {
                    return MinecraftServer.this.playerList.getCurrentPlayerCount() + " / " + MinecraftServer.this.playerList.getMaxPlayers() + "; " + MinecraftServer.this.playerList.getPlayers();
                }
            });
        }

        return report;
    }

    public List<String> getTabCompletions(ICommandSender sender, String input, @Nullable BlockPos pos, boolean hasTargetBlock)
    {
        List<String> list = Lists.<String>newArrayList();
        boolean flag = input.startsWith("/");

        if (flag)
        {
            input = input.substring(1);
        }

        if (!flag && !hasTargetBlock)
        {
            String[] astring = input.split(" ", -1);
            String s2 = astring[astring.length - 1];

            for (String s1 : this.playerList.getOnlinePlayerNames())
            {
                if (CommandBase.doesStringStartWith(s2, s1))
                {
                    list.add(s1);
                }
            }

            return list;
        }
        else
        {
            boolean flag1 = !input.contains(" ");
            List<String> list1 = this.commandManager.getTabCompletions(sender, input, pos);

            if (!list1.isEmpty())
            {
                for (String s : list1)
                {
                    if (flag1 && !hasTargetBlock)
                    {
                        list.add("/" + s);
                    }
                    else
                    {
                        list.add(s);
                    }
                }
            }

            return list;
        }
    }

    public boolean isAnvilFileSet()
    {
        return this.anvilConverterForAnvilFile != null;
    }

    public String getName()
    {
        return "Server";
    }

    public void sendMessage(ITextComponent component)
    {
        LOGGER.info(component.getUnformattedText());
    }

    public boolean canUseCommand(int permLevel, String commandName)
    {
        return true;
    }

    public ICommandManager getCommandManager()
    {
        return this.commandManager;
    }

    public KeyPair getKeyPair()
    {
        return this.serverKeyPair;
    }

    public String getServerOwner()
    {
        return this.serverOwner;
    }

    public void setServerOwner(String owner)
    {
        this.serverOwner = owner;
    }

    public boolean isSinglePlayer()
    {
        return this.serverOwner != null;
    }

    public String getFolderName()
    {
        return this.folderName;
    }

    public void setFolderName(String name)
    {
        this.folderName = name;
    }

    @SideOnly(Side.CLIENT)
    public void setWorldName(String worldNameIn)
    {
        this.worldName = worldNameIn;
    }

    @SideOnly(Side.CLIENT)
    public String getWorldName()
    {
        return this.worldName;
    }

    public void setKeyPair(KeyPair keyPair)
    {
        this.serverKeyPair = keyPair;
    }

    public void setDifficultyForAllWorlds(EnumDifficulty difficulty)
    {
        for (WorldServer worldserver1 : this.worlds)
        {
            if (worldserver1 != null)
            {
                if (worldserver1.getWorldInfo().isHardcoreModeEnabled())
                {
                    worldserver1.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
                    worldserver1.setAllowedSpawnTypes(true, true);
                }
                else if (this.isSinglePlayer())
                {
                    worldserver1.getWorldInfo().setDifficulty(difficulty);
                    worldserver1.setAllowedSpawnTypes(worldserver1.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                }
                else
                {
                    worldserver1.getWorldInfo().setDifficulty(difficulty);
                    worldserver1.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
                }
            }
        }
    }

    public boolean allowSpawnMonsters()
    {
        return true;
    }

	public void setDifficultyLockedForAllWorlds(boolean locked)
	{
		for (WorldServer world : this.worlds)
		{
			if (world != null) world.getWorldInfo().setDifficultyLocked(locked);
		}
	}

    public boolean isDemo()
    {
        return this.isDemo;
    }

    public void setDemo(boolean demo)
    {
        this.isDemo = demo;
    }

    public void canCreateBonusChest(boolean enable)
    {
        this.enableBonusChest = enable;
    }

    public ISaveFormat getActiveAnvilConverter()
    {
        return this.anvilConverterForAnvilFile;
    }

    public String getResourcePackUrl()
    {
        return this.resourcePackUrl;
    }

    public String getResourcePackHash()
    {
        return this.resourcePackHash;
    }

    public void setResourcePack(String url, String hash)
    {
        this.resourcePackUrl = url;
        this.resourcePackHash = hash;
    }

    public abstract boolean isDedicatedServer();

    public boolean isServerInOnlineMode()
    {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean online)
    {
        this.onlineMode = online;
    }

    public boolean getPreventProxyConnections()
    {
        return this.preventProxyConnections;
    }

    public boolean getCanSpawnAnimals()
    {
        return this.canSpawnAnimals;
    }

    public void setCanSpawnAnimals(boolean spawnAnimals)
    {
        this.canSpawnAnimals = spawnAnimals;
    }

    public boolean getCanSpawnNPCs()
    {
        return this.canSpawnNPCs;
    }

    public abstract boolean shouldUseNativeTransport();

    public void setCanSpawnNPCs(boolean spawnNpcs)
    {
        this.canSpawnNPCs = spawnNpcs;
    }

    public boolean isPVPEnabled()
    {
        return this.pvpEnabled;
    }

    public void setAllowPvp(boolean allowPvp)
    {
        this.pvpEnabled = allowPvp;
    }

    public boolean isFlightAllowed()
    {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean allow)
    {
        this.allowFlight = allow;
    }

    public abstract boolean isCommandBlockEnabled();

    public String getMOTD()
    {
        return this.motd;
    }

    public void setMOTD(String motdIn)
    {
        this.motd = motdIn;
    }

    public int getBuildLimit()
    {
        return this.buildLimit;
    }

    public void setBuildLimit(int maxBuildHeight)
    {
        this.buildLimit = maxBuildHeight;
    }

    public boolean isServerStopped()
    {
        return this.serverStopped;
    }

    public PlayerList getPlayerList()
    {
        return this.playerList;
    }

    public void setPlayerList(PlayerList list)
    {
        this.playerList = list;
    }

    public void setGameType(GameType gameMode)
    {
        for (WorldServer worldserver1 : this.worlds)
        {
            worldserver1.getWorldInfo().setGameType(gameMode);
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean serverIsInRunLoop()
    {
        return this.serverIsRunning;
    }

    public boolean getGuiEnabled()
    {
        return false;
    }

    public abstract String shareToLAN(GameType type, boolean allowCheats);

    public int getTickCounter()
    {
        return this.tickCounter;
    }

    public void enableProfiling()
    {
        this.startProfiling = true;
    }

    public World getEntityWorld()
    {
        return this.worlds[0];
    }

    public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        return false;
    }

    public boolean getForceGamemode()
    {
        return this.isGamemodeForced;
    }

    public static long getCurrentTimeMillis()
    {
        return net.lax1dude.eaglercraft.v1_8.EagRuntime.steadyTimeMillis();
    }

    public int getMaxPlayerIdleMinutes()
    {
        return this.maxPlayerIdleMinutes;
    }

    public void setPlayerIdleTimeout(int idleTimeout)
    {
        this.maxPlayerIdleMinutes = idleTimeout;
    }

    public PlayerProfileCache getPlayerProfileCache()
    {
        return this.profileCache;
    }

    public ServerStatusResponse getServerStatusResponse()
    {
        return this.statusResponse;
    }

    public void refreshStatusNextTick()
    {
        this.nanoTimeSinceStatusRefresh = 0L;
    }

    @Nullable
    public Entity getEntityFromUuid(EaglercraftUUID uuid)
    {
        for (WorldServer worldserver1 : this.worlds)
        {
            if (worldserver1 != null)
            {
                Entity entity = worldserver1.getEntityFromUuid(uuid);

                if (entity != null)
                {
                    return entity;
                }
            }
        }

        return null;
    }

    public boolean sendCommandFeedback()
    {
        return this.worlds[0].getGameRules().getBoolean("sendCommandFeedback");
    }

    public MinecraftServer getServer()
    {
        return this;
    }

    public int getMaxWorldSize()
    {
        return 29999984;
    }

    public <V> ListenableFuture<V> callFromMainThread(Callable<V> callable)
    {
        Validate.notNull(callable);

        if (!this.isCallingFromMinecraftThread() && !this.isServerStopped())
        {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.<V>create(callable);

            synchronized (this.futureTaskQueue)
            {
                this.futureTaskQueue.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
        else
        {
            try
            {
                return Futures.<V>immediateFuture(callable.call());
            }
            catch (Exception exception)
            {
                return Futures.immediateFailedFuture(exception);
            }
        }
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule)
    {
        Validate.notNull(runnableToSchedule);
        return this.<Object>callFromMainThread(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread()
    {
        return Thread.currentThread() == this.serverThread;
    }

    public int getNetworkCompressionThreshold()
    {
        return 256;
    }

    public int getSpawnRadius(@Nullable WorldServer worldIn)
    {
        return worldIn != null ? worldIn.getGameRules().getInt("spawnRadius") : 10;
    }

    public AdvancementManager getAdvancementManager()
    {
        return this.worlds[0].getAdvancementManager();
    }

    public FunctionManager getFunctionManager()
    {
        return this.worlds[0].getFunctionManager();
    }

    public void reload()
    {
        if (this.isCallingFromMinecraftThread())
        {
            this.getPlayerList().saveAllPlayerData();
            this.worlds[0].getLootTableManager().reloadLootTables();
            this.getAdvancementManager().reload();
            this.getFunctionManager().reload();
            this.getPlayerList().reloadResources();
        }
        else
        {
            this.addScheduledTask(this::reload);
        }
    }

    @SideOnly(Side.SERVER)
    public String getServerHostname()
    {
        return this.hostname;
    }

    @SideOnly(Side.SERVER)
    public void setHostname(String host)
    {
        this.hostname = host;
    }

    @SideOnly(Side.SERVER)
    public void registerTickable(ITickable tickable)
    {
        this.tickables.add(tickable);
    }

    @SideOnly(Side.SERVER)
    public void logInfo(String msg)
    {
        LOGGER.info(msg);
    }

    @SideOnly(Side.SERVER)
    public boolean isDebuggingEnabled()
    {
        return false;
    }

    @SideOnly(Side.SERVER)
    public void logSevere(String msg)
    {
        LOGGER.error(msg);
    }

    @SideOnly(Side.SERVER)
    public void logDebug(String msg)
    {
        if (this.isDebuggingEnabled())
        {
            LOGGER.info(msg);
        }
    }

    @SideOnly(Side.SERVER)
    public int getServerPort()
    {
        return this.serverPort;
    }

    @SideOnly(Side.SERVER)
    public void setServerPort(int port)
    {
        this.serverPort = port;
    }

    @SideOnly(Side.SERVER)
    public void setPreventProxyConnections(boolean p_190517_1_)
    {
        this.preventProxyConnections = p_190517_1_;
    }

    @SideOnly(Side.SERVER)
    public int getSpawnProtectionSize()
    {
        return 16;
    }

    @SideOnly(Side.SERVER)
    public void setForceGamemode(boolean force)
    {
        this.isGamemodeForced = force;
    }

    @SideOnly(Side.SERVER)
    public long getCurrentTime()
    {
        return this.currentTime;
    }

    public DataFixer getDataFixer()
    {
        return this.dataFixer;
    }
}
