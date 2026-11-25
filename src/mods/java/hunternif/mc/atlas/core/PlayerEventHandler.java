package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerEventHandler {
   public PlayerEventHandler() {
   }

   @SubscribeEvent
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      World world = player.world;
      int atlasID = player.getUniqueID().hashCode();
      AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
      if (!data.isEmpty()) {
         data.syncOnPlayer(atlasID, player);
      }

      MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
      if (!markers.isEmpty()) {
         markers.syncOnPlayer(atlasID, player);
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(event.player.getUniqueID().hashCode(), event.player.world);
      data.updateMapAroundPlayer(event.player);
   }
}
