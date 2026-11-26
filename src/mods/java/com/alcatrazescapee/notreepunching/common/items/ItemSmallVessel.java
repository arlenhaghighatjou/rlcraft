package com.alcatrazescapee.notreepunching.common.items;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.alcatrazescapee.alcatrazcore.inventory.capability.ItemHandlerItem;
import com.alcatrazescapee.alcatrazcore.item.ItemCore;
import com.alcatrazescapee.notreepunching.NoTreePunching;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@ParametersAreNonnullByDefault
public class ItemSmallVessel extends ItemCore {
   public ItemSmallVessel() {
      this.setMaxStackSize(1);
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack stack = playerIn.getHeldItem(handIn);
      if (!worldIn.isRemote && !playerIn.isSneaking()) {
         playerIn.openGui(NoTreePunching.getInstance(), 1, worldIn, 0, 0, 0);
      }

      return new ActionResult(EnumActionResult.SUCCESS, stack);
   }

   @Nullable
   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
      return new ItemHandlerItem(nbt, 4);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      IItemHandler cap = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (cap != null) {
         boolean flag = true;

         for(int i = 0; i < cap.getSlots(); ++i) {
            ItemStack stack1 = cap.getStackInSlot(i);
            if (!stack1.isEmpty()) {
               if (flag) {
                  tooltip.add(TextFormatting.GREEN + "Contents:");
                  flag = false;
               }

               tooltip.add(HString.format(TextFormatting.GRAY + "%dx %s", stack1.getCount(), stack1.getDisplayName()));
            }
         }
      }

   }
}
