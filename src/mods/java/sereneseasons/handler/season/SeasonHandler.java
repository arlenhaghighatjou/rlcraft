package sereneseasons.handler.season;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.HashMap;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.handler.PacketHandler;
import sereneseasons.network.message.MessageSyncSeasonCycle;
import sereneseasons.season.SeasonASMHelper;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class SeasonHandler implements SeasonHelper.ISeasonDataProvider {
   private Season.SubSeason lastSeason = null;
   public static final HashMap<Integer, Integer> clientSeasonCycleTicks = new HashMap();

   public SeasonHandler() {
   }

   public void register(EventBus bus) {
      bus.register(WorldTickEvent.class, this::onWorldTick);
      bus.register(PlayerLoggedInEvent.class, this::onPlayerLogin);
      bus.register(ClientTickEvent.class, this::onClientTick);
      bus.register(Populate.class, this::onPopulateChunk);
   }

   public void onWorldTick(WorldTickEvent event) {
      World world = event.world;
      if (event.phase == Phase.END && !world.isRemote) {
         if (!SyncedConfig.getBooleanValue(SeasonsOption.PROGRESS_SEASON_WHILE_OFFLINE)) {
            MinecraftServer server = world.getMinecraftServer();
            if (server != null && server.getPlayerList().getCurrentPlayerCount() == 0) {
               return;
            }
         }

         SeasonSavedData savedData = getSeasonSavedData(world);
         if (savedData.seasonCycleTicks++ > SeasonTime.ZERO.getCycleDuration()) {
            savedData.seasonCycleTicks = 0;
         }

         if (savedData.seasonCycleTicks % 20 == 0) {
            sendSeasonUpdate(world);
         }

         savedData.markDirty();
      }

   }

   public void onPlayerLogin(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      World world = player.world;
      sendSeasonUpdate(world);
   }

   public static SeasonTime getClientSeasonTime() {
      Integer i = (Integer)clientSeasonCycleTicks.get(Integer.valueOf(0));
      return new SeasonTime(i == null ? 0 : i.intValue());
   }

   public void onClientTick(ClientTickEvent event) {
      if (Minecraft.getMinecraft().player != null) {
         int dimension = Minecraft.getMinecraft().player.dimension;
         if (event.phase == Phase.END && SeasonsConfig.isDimensionWhitelisted(dimension)) {
            clientSeasonCycleTicks.compute(Integer.valueOf(dimension), (k, v) -> {
               return v == null ? 0 : v.intValue() + 1;
            });
            if (((Integer)clientSeasonCycleTicks.get(Integer.valueOf(dimension))).intValue() > SeasonTime.ZERO.getCycleDuration()) {
               clientSeasonCycleTicks.put(Integer.valueOf(dimension), Integer.valueOf(0));
            }

            SeasonTime calendar = new SeasonTime(((Integer)clientSeasonCycleTicks.get(Integer.valueOf(dimension))).intValue());
            if (calendar.getSubSeason() != this.lastSeason) {
               Minecraft.getMinecraft().renderGlobal.loadRenderers();
               this.lastSeason = calendar.getSubSeason();
            }
         }

      }
   }

   public void onPopulateChunk(Populate event) {
      if ((event.getWorld().isRemote || event.getType() == EventType.ICE) && SeasonsConfig.isDimensionWhitelisted(event.getWorld().provider.getDimension())) {
         event.setResult(Result.DENY);
         BlockPos blockpos = (new BlockPos(event.getChunkX() * 16, 0, event.getChunkZ() * 16)).add(8, 0, 8);

         for(int k2 = 0; k2 < 16; ++k2) {
            for(int j3 = 0; j3 < 16; ++j3) {
               BlockPos blockpos1 = event.getWorld().getPrecipitationHeight(blockpos.add(k2, 0, j3));
               BlockPos blockpos2 = blockpos1.down();
               if (SeasonASMHelper.canBlockFreezeInSeason(event.getWorld(), blockpos2, false, SeasonHelper.getSeasonState(event.getWorld()), true)) {
                  event.getWorld().setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
               }

               if (SeasonASMHelper.canSnowAtInSeason(event.getWorld(), blockpos1, true, SeasonHelper.getSeasonState(event.getWorld()), true)) {
                  event.getWorld().setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
               }
            }
         }

      }
   }

   public static void sendSeasonUpdate(World world) {
      if (!world.isRemote) {
         SeasonSavedData savedData = getSeasonSavedData(world);
         PacketHandler.instance.sendToAll(new MessageSyncSeasonCycle(world.provider.getDimension(), savedData.seasonCycleTicks));
      }

   }

   public static SeasonSavedData getSeasonSavedData(World world) {
      MapStorage mapStorage = world.getPerWorldStorage();
      SeasonSavedData savedData = (SeasonSavedData)mapStorage.getOrLoadData(SeasonSavedData.class, "seasons");
      if (savedData == null) {
         savedData = new SeasonSavedData("seasons");
         int startingSeason = SyncedConfig.getIntValue(SeasonsOption.STARTING_SUB_SEASON);
         if (startingSeason == 0) {
            savedData.seasonCycleTicks = world.rand.nextInt(12) * SeasonTime.ZERO.getSubSeasonDuration();
         }

         if (startingSeason > 0) {
            savedData.seasonCycleTicks = (startingSeason - 1) * SeasonTime.ZERO.getSubSeasonDuration();
         }

         mapStorage.setData("seasons", savedData);
         savedData.markDirty();
      }

      return savedData;
   }

   public ISeasonState getServerSeasonState(World world) {
      SeasonSavedData savedData = getSeasonSavedData(world);
      return new SeasonTime(savedData.seasonCycleTicks);
   }

   public ISeasonState getClientSeasonState() {
      Integer i = (Integer)clientSeasonCycleTicks.get(Integer.valueOf(0));
      return new SeasonTime(i == null ? 0 : i.intValue());
   }
}
