package svenhjol.charm.enchanting.feature;

import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.enchanting.enchantment.EnchantmentSalvage;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.EnchantmentHelper;
import svenhjol.meson.helper.SoundHelper;

public class Salvage extends Feature {
   public static EnchantmentSalvage enchantment;
   public static int minEnchantability;
   public static Map<EaglercraftUUID, ItemStack> ignoreEquipped = new HashMap();

   public Salvage() {
   }

   public String getDescription() {
      return "An item with the Salvage enchantment does not disappear when its durability is depleted, giving you a chance to get it repaired.\nIf the item runs out of durability the player will drop it and must be picked up again.  Watch out for lava.";
   }

   public void configure() {
      super.configure();
      minEnchantability = 5;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      enchantment = new EnchantmentSalvage();
   }

   @SubscribeEvent
   public void onItemRightClick(RightClickItem event) {
      if (!event.getWorld().isRemote && event.getEntityPlayer() != null && event.getItemStack().getItem() instanceof ItemArmor && EnchantmentHelper.hasEnchantment(enchantment, event.getItemStack())) {
         ignoreEquipped.put(event.getEntityPlayer().getUniqueID(), event.getItemStack().copy());
      }

   }

   @SubscribeEvent
   public void onDestroy(PlayerDestroyItemEvent event) {
      if (!event.getEntityPlayer().world.isRemote && EnchantmentHelper.hasEnchantment(enchantment, event.getOriginal())) {
         EntityPlayer player = event.getEntityPlayer();
         EaglercraftUUID playerId = player.getUniqueID();
         ItemStack item = event.getOriginal();
         if (ignoreEquipped.containsKey(playerId)) {
            ItemStack ignored = (ItemStack)ignoreEquipped.get(playerId);
            ignoreEquipped.remove(playerId);
            if (ItemStack.areItemStacksEqual(ignored, item)) {
               return;
            }
         }

         dropItem(player, item);
      }

   }

   public static void dropItem(EntityPlayer player, ItemStack stack) {
      stack.setItemDamage(stack.getMaxDamage());
      player.dropItem(stack, false);
      SoundHelper.playerSound(player, SoundEvents.BLOCK_ANVIL_LAND, 0.5F, 1.5F, 0.15F, (SoundCategory)null);
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
