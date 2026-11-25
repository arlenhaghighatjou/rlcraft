package net.silentchaos512.lib.util;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHelper {
   public ItemHelper() {
   }

   /** @deprecated */
   @Deprecated
   public static boolean attemptDamageItem(ItemStack stack, int amount, Random rand) {
      return attemptDamageItem(stack, amount, rand, (EntityPlayer)null);
   }

   /** @deprecated */
   @Deprecated
   public static boolean attemptDamageItem(ItemStack stack, int amount, Random rand, @Nullable EntityPlayer player) {
      EntityPlayerMP playermp = player instanceof EntityPlayerMP ? (EntityPlayerMP)player : null;
      return stack.attemptDamageItem(amount, rand, playermp);
   }

   /** @deprecated */
   @Deprecated
   public static ActionResult<ItemStack> onItemRightClick(@Nonnull Item item, World world, EntityPlayer player, EnumHand hand) {
      return item.onItemRightClick(world, player, hand);
   }

   /** @deprecated */
   @Deprecated
   public static EnumActionResult onItemUse(@Nonnull Item item, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      return item.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
   }

   /** @deprecated */
   @Deprecated
   public static EnumActionResult onItemUseFirst(@Nonnull Item item, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      return item.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
   }

   public static EnumActionResult useItemAsPlayer(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack currentOffhand = player.getHeldItemOffhand();
      player.setHeldItem(EnumHand.OFF_HAND, stack);
      Item item = stack.getItem();
      EnumActionResult result = stack.getItem().onItemUse(player, world, pos, EnumHand.OFF_HAND, side, hitX, hitY, hitZ);
      player.setHeldItem(EnumHand.OFF_HAND, currentOffhand);
      return result;
   }

   /** @deprecated */
   @Deprecated
   public static boolean isInCreativeTab(Item item, CreativeTabs targetTab) {
      for(CreativeTabs tab : item.getCreativeTabs()) {
         if (tab == targetTab) {
            return true;
         }
      }

      CreativeTabs creativetabs = item.getCreativeTab();
      return creativetabs != null && (targetTab == CreativeTabs.SEARCH || targetTab == creativetabs);
   }
}
