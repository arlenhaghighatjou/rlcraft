package nightkosh.advanced_fishing.core;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nightkosh.advanced_fishing.entity.projectile.EntityCustomFishHook;

public class EventsHandler {
   public EventsHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void entityJoinWorldEvent(EntityJoinWorldEvent event) {
      net.minecraft.entity.Entity entity = event.getEntity();
      if (Config.overrideVanillaFishing && entity instanceof EntityFishHook && entity.getClass().equals(EntityFishHook.class)) {
         World world = event.getWorld();
         if (!world.isRemote) {
            EntityPlayer player = ((EntityFishHook)entity).getAngler();
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (stack.getItem() != net.minecraft.init.Items.FISHING_ROD) {
               stack = player.getHeldItem(EnumHand.OFF_HAND);
            }

            entity.setDead();
            EntityCustomFishHook hook = new EntityCustomFishHook(world, player);
            int speed = EnchantmentHelper.getFishingSpeedBonus(stack);
            if (speed > 0) {
               hook.setLureSpeed(speed);
            }

            int luck = EnchantmentHelper.getFishingLuckBonus(stack);
            if (luck > 0) {
               hook.setLuck(luck);
            }

            world.spawnEntity(hook);
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void lootLoad(LootTableLoadEvent event) {
      LootTables.inject(event);
   }
}
