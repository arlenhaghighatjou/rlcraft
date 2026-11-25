package hunternif.mc.atlas.ext.watcher;

import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.registry.MarkerTypes;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathWatcher {
   public DeathWatcher() {
   }

   @SubscribeEvent
   public void onPlayerDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof EntityPlayer && SettingsConfig.gameplay.autoDeathMarker) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         Iterator var3 = AtlasAPI.getPlayerAtlases(player).iterator();

         while(var3.hasNext()) {
            int atlasID = ((Integer)var3.next()).intValue();
            AtlasAPI.markers.putMarker(player.getEntityWorld(), true, atlasID, MarkerTypes.TOMB, "gui.antiqueatlas.marker.tomb " + player.getName(), (int)player.posX, (int)player.posZ);
         }
      }

   }
}
