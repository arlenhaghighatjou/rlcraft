package artifacts.common.item;

import artifacts.common.init.ModItems;
import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public abstract class BaubleMagmaStone {
   public BaubleMagmaStone() {
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof EntityDamageSourceIndirect) && !((EntityDamageSource)event.getSource()).getIsThornsDamage() && event.getSource().getTrueSource() instanceof EntityPlayer) {
         EntityPlayer attacker = (EntityPlayer)event.getSource().getTrueSource();
         if ((BaublesApi.isBaubleEquipped(attacker, ModItems.MAGMA_STONE) != -1 || BaublesApi.isBaubleEquipped(attacker, ModItems.FIRE_GAUNTLET) != -1) && !event.getEntity().isImmuneToFire()) {
            event.getEntity().setFire(4);
         }
      }

   }
}
