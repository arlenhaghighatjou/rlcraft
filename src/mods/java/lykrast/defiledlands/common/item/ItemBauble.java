package lykrast.defiledlands.common.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import java.util.List;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemBauble extends Item implements IBauble {
   private BaubleType type;

   public ItemBauble(BaubleType type) {
      this.type = type;
      this.setMaxStackSize(1);
   }

   public BaubleType getBaubleType(ItemStack itemstack) {
      return this.type;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      if (!world.isRemote) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

         for(int i = 0; i < baubles.getSlots(); ++i) {
            ItemStack stack = player.getHeldItem(hand);
            if ((baubles.getStackInSlot(i) == null || baubles.getStackInSlot(i).isEmpty()) && baubles.isItemValidForSlot(i, stack, player)) {
               ItemStack equipped = stack.copy();
               baubles.setStackInSlot(i, equipped);
               if (!player.capabilities.isCreativeMode) {
                  stack.shrink(1);
               }

               this.onEquipped(equipped, player);
               break;
            }
         }
      }

      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }

   public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
      player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.75F, 1.9F);
   }

   public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
      player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.75F, 2.0F);
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + I18n.format(super.getUnlocalizedName(stack) + ".tooltip", new Object[0])));
   }
}
