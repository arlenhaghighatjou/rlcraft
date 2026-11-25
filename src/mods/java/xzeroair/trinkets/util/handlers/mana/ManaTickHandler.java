package xzeroair.trinkets.util.handlers.mana;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class ManaTickHandler {
   public static ManaTickHandler instance = new ManaTickHandler();

   public ManaTickHandler() {
   }

   @SubscribeEvent
   public void onWorldTick(WorldTickEvent event) {
      if (event.phase != Phase.START) {
         World world = event.world;
         WorldMana.get(world).tick(world);
      }
   }
}
