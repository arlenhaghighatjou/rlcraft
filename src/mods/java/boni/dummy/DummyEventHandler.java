package boni.dummy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DummyEventHandler {
   public DummyEventHandler() {
   }

   @SubscribeEvent
   public void onPlayerLeftClick(LivingAttackEvent event) {
      if (event.getSource() != null && event.getEntity() != null && event.getEntity() instanceof EntityDummy) {
         if (event.getSource().damageType.equals("player")) {
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)event.getSource().getTrueSource();
               if (player.isSneaking() && player.getHeldItemMainhand().isEmpty()) {
                  ((EntityDummy)event.getEntity()).dismantle();
               }

            }
         }
      }
   }
}
