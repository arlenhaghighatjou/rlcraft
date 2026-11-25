package artifacts.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public abstract class RenderHelper {
   public RenderHelper() {
   }

   public static boolean shouldRenderInSlot(EntityPlayer player, EntityEquipmentSlot slot) {
      ItemStack stack = player.getItemStackFromSlot(slot);
      return stack.isEmpty() || stack.getTagCompound() != null && stack.getTagCompound().getBoolean("classy_hat_invisible") && stack.getTagCompound().getCompoundTag("classy_hat_disguise").hasNoTags();
   }

   public static boolean shouldItemStackRender(EntityPlayer player, ItemStack stack) {
      return stack.getTagCompound() == null || !stack.getTagCompound().getBoolean("phantom_thread_invisible");
   }
}
