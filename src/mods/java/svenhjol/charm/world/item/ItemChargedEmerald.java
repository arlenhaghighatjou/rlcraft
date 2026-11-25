package svenhjol.charm.world.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import svenhjol.charm.world.entity.EntityChargedEmerald;
import svenhjol.meson.iface.IMesonItem;

public class ItemChargedEmerald extends Item implements IMesonItem {
   public ItemChargedEmerald() {
      this.register("charged_emerald");
      this.setMaxStackSize(16);
      this.setCreativeTab(CreativeTabs.MISC);
   }

   public String getModId() {
      return "charm";
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack stack = playerIn.getHeldItem(handIn);
      if (!playerIn.capabilities.isCreativeMode) {
         stack.shrink(1);
      }

      if (!worldIn.isRemote) {
         EntityChargedEmerald emerald = new EntityChargedEmerald(worldIn, playerIn);
         emerald.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
         worldIn.spawnEntity(emerald);
      }

      return new ActionResult(EnumActionResult.SUCCESS, stack);
   }

   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}
