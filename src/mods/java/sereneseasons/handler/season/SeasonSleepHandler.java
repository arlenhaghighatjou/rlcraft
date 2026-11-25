package sereneseasons.handler.season;

import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import sereneseasons.season.SeasonSavedData;

public class SeasonSleepHandler {
   public SeasonSleepHandler() {
   }

   @SubscribeEvent
   public void onWorldTick(WorldTickEvent event) {
      if (event.phase == Phase.START && event.side == Side.SERVER) {
         WorldServer world = (WorldServer)event.world;
         if (world.areAllPlayersAsleep()) {
            SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(world);
            long timeDiff = 24000L - (world.getWorldInfo().getWorldTime() + 24000L) % 24000L;
            seasonData.seasonCycleTicks = (int)((long)seasonData.seasonCycleTicks + timeDiff);
            seasonData.markDirty();
            SeasonHandler.sendSeasonUpdate(world);
         }
      }

   }
}
