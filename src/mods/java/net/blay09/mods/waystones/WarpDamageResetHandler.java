package net.blay09.mods.waystones;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.blay09.mods.waystones.item.IResetUseOnDamage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class WarpDamageResetHandler {
   public WarpDamageResetHandler() {
   }

   public void register(EventBus bus) {
      bus.register(LivingDamageEvent.class, this::onDamage);
   }

   public void onDamage(LivingDamageEvent event) {
      if (WaystoneConfig.general.resetUseOnDamage && event.getEntity() instanceof EntityPlayer && event.getEntityLiving().getActiveItemStack().getItem() instanceof IResetUseOnDamage) {
         event.getEntityLiving().stopActiveHand();
      }

   }
}
