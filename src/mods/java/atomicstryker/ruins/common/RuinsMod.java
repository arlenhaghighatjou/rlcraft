package atomicstryker.ruins.common;

import net.minecraft.world.chunk.storage.IChunkLoader;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerChunkLoader;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.HashMap;
import java.util.ArrayDeque;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "ruins", name = "Ruins Mod", version = RuinsMod.modversion, dependencies = "after:extrabiomes")
public class RuinsMod
{
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
      } else if (event instanceof FMLInitializationEvent) {
         this.load((FMLInitializationEvent)event);
      } else if (event instanceof FMLServerAboutToStartEvent) {
         this.serverAboutToStart((FMLServerAboutToStartEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverStarted((FMLServerStartingEvent)event);
      }

   }

    static final String modversion = "17.2";

    public static final String TEMPLATE_PATH_MC_EXTRACTED = "config/ruins_config/";
    public static final String TEMPLATE_PATH_JAR = "ruins_config";

    public final static int DIR_NORTH = 0, DIR_EAST = 1, DIR_SOUTH = 2, DIR_WEST = 3;
    public static final String BIOME_ANY = "generic";

    private HashMap<Integer, WorldHandle> generatorMap;

    public void load(FMLInitializationEvent evt)
    {
        GameRegistry.registerWorldGenerator(new RuinsWorldGenerator(), 0);
        this.register(MinecraftForge.EVENT_BUS);

        ConfigFolderPreparator.copyFromJarIfNotPresent(new VFile2(TEMPLATE_PATH_MC_EXTRACTED));
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent evt)
    {
        generatorMap = new HashMap<>();
    }

    public void serverStarted(FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new CommandParseTemplate());
        evt.registerServerCommand(new CommandTestTemplate());
        evt.registerServerCommand(new CommandUndo());
    }

    private long nextInfoTime;

    public void register(EventBus bus) {
       bus.register(BreakSpeed.class, this::onBreakSpeed);
       bus.register(BreakEvent.class, this::onBreak);
       bus.register(WorldEvent.Save.class, this::eventWorldSave);
       bus.register(EntityEvent.EnteringChunk.class, this::onEntityEnteringChunk);
    }

    public void onBreakSpeed(BreakSpeed event)
    {
        WorldHandle wh = getWorldHandle(event.getEntity().getEntityWorld());
        if (wh != null && wh.fileHandle.enableStick)
        {
            ItemStack is = event.getEntityPlayer().getHeldItemMainhand();
            if (is != null && is.getItem() == Items.STICK && System.currentTimeMillis() > nextInfoTime)
            {
                nextInfoTime = System.currentTimeMillis() + 1000L;
                event.getEntityPlayer().sendMessage(new TextComponentTranslation(HString.format("BlockName [%s], blockID [%s], metadata [%d]", event.getState().getBlock().getUnlocalizedName(),
                        event.getState().getBlock().getRegistryName().getResourcePath(), event.getState().getBlock().getMetaFromState(event.getState()))));
            }
        }
    }

    public void onBreak(BreakEvent event)
    {
        if (event.getPlayer() != null && !(event.getPlayer() instanceof FakePlayer))
        {
            WorldHandle wh = getWorldHandle(event.getWorld());
            if (wh != null && wh.fileHandle.enableStick)
            {
                ItemStack is = event.getPlayer().getHeldItemMainhand();
                if (is != null && is.getItem() == Items.STICK && System.currentTimeMillis() > nextInfoTime)
                {
                    nextInfoTime = System.currentTimeMillis() + 1000L;
                    event.getPlayer().sendMessage(new TextComponentTranslation(HString.format("BlockName [%s], blockID [%s], metadata [%d]", event.getState().getBlock().getUnlocalizedName(),
                            event.getState().getBlock().getRegistryName().getResourcePath(), event.getState().getBlock().getMetaFromState(event.getState()))));
                    event.setCanceled(true);
                }
            }
        }
    }

    public void eventWorldSave(WorldEvent.Save evt)
    {
        WorldHandle wh = getWorldHandle(evt.getWorld());
        if (wh != null)
        {
            wh.generator.flushPosFile(evt.getWorld().getWorldInfo().getWorldName());
        }
    }

    public void onEntityEnteringChunk(EntityEvent.EnteringChunk event)
    {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote)
        {
            TileEntityCommandBlock tecb;
            ArrayList<TileEntityCommandBlock> tecblist = new ArrayList<>();

            for (int xoffset = -4; xoffset <= 4; xoffset++)
            {
                for (int zoffset = -4; zoffset <= 4; zoffset++)
                {
                    for (TileEntity teo : event.getEntity().world.getChunkFromChunkCoords(event.getNewChunkX() + xoffset, event.getNewChunkZ() + zoffset).getTileEntityMap().values())
                    {
                        if (teo instanceof TileEntityCommandBlock)
                        {
                            tecb = (TileEntityCommandBlock) teo;
                            if (tecb.getCommandBlockLogic().getCommand().startsWith("RUINSTRIGGER "))
                            {
                                // strip prefix from command
                                tecb.getCommandBlockLogic().setCommand((tecb.getCommandBlockLogic().getCommand()).substring(13));
                                tecblist.add(tecb);
                            }
                        }
                    }
                }
            }

            for (TileEntityCommandBlock tecb2 : tecblist)
            {
                // call command block execution
                tecb2.getCommandBlockLogic().trigger(event.getEntity().world);
                // kill block
                BlockPos pos = tecb2.getPos();
                System.out.println("Ruins executed and killed Command Block at [" + pos + "]");
                event.getEntity().world.setBlockToAir(pos);
            }
        }
    }

    private class RuinsWorldGenerator implements IWorldGenerator
    {
        @Override
        public void generate(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
        {
            if (world.isRemote || !world.getWorldInfo().isMapFeaturesEnabled())
            {
                return;
            }

            final WorldHandle wh = getWorldHandle(world);
            if (wh != null)
            {
                int[] tuple = { chunkX, chunkZ };
                if (wh.currentlyGenerating.contains(tuple))
                {
                    System.err.print("Ruins Mod caught recursive generator call at chunk [" + chunkX + "|" + chunkZ + "]");
                }
                else
                {
                    if (wh.fileHandle.allowsDimension(world.provider.getDimension()) && !wh.chunkLogger.catchChunkBug(chunkX, chunkZ))
                    {
                        wh.currentlyGenerating.add(tuple);
                        if (world.provider instanceof WorldProviderHell)
                        {
                            generateNether(world, random, tuple[0] * 16, tuple[1] * 16);
                        }
                        else
                        // normal world
                        {
                            generateSurface(world, random, tuple[0] * 16, tuple[1] * 16);
                        }
                        wh.currentlyGenerating.remove(tuple);
                    }
                }
            }
        }
    }

    private void generateNether(World world, EaglercraftRandom random, int chunkX, int chunkZ)
    {
        WorldHandle wh = getWorldHandle(world);
        if (wh.fileHandle != null)
        {
            wh.generator.generateNether(world, random, chunkX, chunkZ);
        }
    }

    private void generateSurface(World world, EaglercraftRandom random, int chunkX, int chunkZ)
    {
        WorldHandle wh = getWorldHandle(world);
        if (wh.fileHandle != null)
        {
            wh.generator.generateNormal(world, random, chunkX, chunkZ);
        }
    }

    private class WorldHandle
    {
        FileHandler fileHandle;
        RuinGenerator generator;
        ArrayDeque<int[]> currentlyGenerating;
        ChunkLoggerData chunkLogger;
    }

    private WorldHandle getWorldHandle(World world)
    {
        WorldHandle wh = null;
        if (!world.isRemote)
        {
            if (!generatorMap.containsKey(world.provider.getDimension()))
            {
                wh = new WorldHandle();
                initWorldHandle(wh, world);
                generatorMap.put(world.provider.getDimension(), wh);
            }
            else
            {
                wh = generatorMap.get(world.provider.getDimension());
            }
        }

        return wh;
    }

    private static VFile2 getWorldSaveDir(World world)
    {
        IChunkLoader loader = world.getSaveHandler().getChunkLoader(world.provider);

        if (loader instanceof EaglerChunkLoader)
        {
            return ((EaglerChunkLoader) loader).chunkDirectory;
        }

        return null;
    }

    private void initWorldHandle(WorldHandle worldHandle, World world)
    {
        // load in defaults
        try
        {
            VFile2 worlddir = getWorldSaveDir(world);
            worldHandle.fileHandle = new FileHandler(worlddir, world.provider.getDimension());
            worldHandle.generator = new RuinGenerator(worldHandle.fileHandle, world);
            worldHandle.currentlyGenerating = new ArrayDeque<>();

            worldHandle.chunkLogger = (ChunkLoggerData) world.getPerWorldStorage().getOrLoadData(ChunkLoggerData.class, "ruinschunklogger");
            if (worldHandle.chunkLogger == null)
            {
                worldHandle.chunkLogger = new ChunkLoggerData("ruinschunklogger");
                world.getPerWorldStorage().setData("ruinschunklogger", worldHandle.chunkLogger);
            }
        }
        catch (Exception e)
        {
            System.err.println("There was a problem loading the ruins mod:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}