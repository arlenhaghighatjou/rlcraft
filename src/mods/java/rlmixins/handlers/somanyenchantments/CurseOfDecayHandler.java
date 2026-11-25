package rlmixins.handlers.somanyenchantments;

import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CurseOfDecayHandler {
   public CurseOfDecayHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (!event.isCanceled() && event.getEntity() != null && event.getEntity() instanceof EntityItem) {
         EntityItem itemEntity = (EntityItem)event.getEntity();
         ItemStack stack = itemEntity.getItem();
         if (EnchantmentHelper.getEnchantmentLevel(Smc_040.CurseofDecay, stack) > 0) {
            itemEntity.lifespan = 40;
            itemEntity.setPickupDelay(10);
         }

      }
   }
}
