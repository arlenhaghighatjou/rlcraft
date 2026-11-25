package rlmixins.handlers.somanyenchantments;

import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CurseOfPossesionHandler {
   public CurseOfPossesionHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (!event.isCanceled() && event.getEntity() != null && event.getEntity() instanceof EntityItem) {
         EntityItem itemEntity = (EntityItem)event.getEntity();
         ItemStack stack = itemEntity.getItem();
         if (EnchantmentHelper.getEnchantmentLevel(Smc_040.CurseofPossession, stack) > 0) {
            EntityPlayer thrower = event.getWorld().getClosestPlayerToEntity(itemEntity, 8.0D);
            if (thrower != null && !thrower.isCreative()) {
               if (thrower.isEntityAlive() && thrower.addItemStackToInventory(stack)) {
                  event.setCanceled(true);
                  return;
               }

               itemEntity.lifespan = 5;
               itemEntity.setPickupDelay(10);
            }
         }

      }
   }
}
