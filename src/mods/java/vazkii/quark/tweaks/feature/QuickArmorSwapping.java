package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class QuickArmorSwapping extends Feature {
   public static boolean offhandSwapping;

   public QuickArmorSwapping() {
   }

   public void setupConfig() {
      offhandSwapping = this.loadPropBool("Swap off-hand with armor", "", true);
   }

   @SubscribeEvent
   public void onEntityInteractSpecific(EntityInteractSpecific event) {
      EntityPlayer player = event.getEntityPlayer();
      if (!event.getTarget().world.isRemote && !player.isSpectator() && !player.isCreative() && event.getTarget() instanceof EntityArmorStand) {
         if (player.isSneaking()) {
            event.setCanceled(true);
            EntityArmorStand armorStand = (EntityArmorStand)event.getTarget();
            this.swapSlot(player, armorStand, EntityEquipmentSlot.HEAD);
            this.swapSlot(player, armorStand, EntityEquipmentSlot.CHEST);
            this.swapSlot(player, armorStand, EntityEquipmentSlot.LEGS);
            this.swapSlot(player, armorStand, EntityEquipmentSlot.FEET);
            if (offhandSwapping) {
               this.swapSlot(player, armorStand, EntityEquipmentSlot.OFFHAND);
            }
         }

      }
   }

   private void swapSlot(EntityPlayer player, EntityArmorStand armorStand, EntityEquipmentSlot slot) {
      ItemStack playerItem = player.getItemStackFromSlot(slot);
      ItemStack armorStandItem = armorStand.getItemStackFromSlot(slot);
      player.setItemStackToSlot(slot, armorStandItem);
      armorStand.setItemStackToSlot(slot, playerItem);
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"iberia"};
   }
}
