package vazkii.quark.management.gamerule;

import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import vazkii.quark.base.handler.DropoffHandler;

public class DropoffGamerule {
   public DropoffGamerule() {
   }

   @SubscribeEvent
   public void worldLoad(Load event) {
      GameRules rules = event.getWorld().getGameRules();
      if (!rules.hasRule("quark_allowDropoff")) {
         rules.addGameRule("quark_allowDropoff", "true", ValueType.BOOLEAN_VALUE);
      }

   }

   @SubscribeEvent
   public void login(PlayerLoggedInEvent event) {
      if (!event.player.getEntityWorld().getGameRules().getBoolean("quark_allowDropoff")) {
         DropoffHandler.disableClientDropoff(event.player);
      }

   }
}
