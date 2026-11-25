package artifacts.common.item;

import artifacts.common.init.ModItems;
import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public abstract class BaubleCobaltShield {
   public BaubleCobaltShield() {
   }

   @SubscribeEvent
   public static void onLivingKnockback(LivingKnockBackEvent event) {
      if (event.getEntity() instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer)event.getEntity(), ModItems.COBALT_SHIELD) != -1) {
         event.setCanceled(true);
      }

   }
}
