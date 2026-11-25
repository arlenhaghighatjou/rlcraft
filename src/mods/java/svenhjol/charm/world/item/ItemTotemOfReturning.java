package svenhjol.charm.world.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import svenhjol.charm.world.feature.TotemOfReturning;
import svenhjol.meson.helper.ItemNBTHelper;
import svenhjol.meson.helper.PlayerHelper;
import svenhjol.meson.helper.SoundHelper;
import svenhjol.meson.iface.IMesonItem;

public class ItemTotemOfReturning extends Item implements IMesonItem {
   private static final String POS = "pos";
   private static final String DIM = "dim";
   private static final String USES = "uses";

   public ItemTotemOfReturning() {
      this.register("totem_of_returning");
      this.setCreativeTab(CreativeTabs.TRANSPORTATION);
      this.setMaxStackSize(1);
   }

   public String getModId() {
      return "charm";
   }

   public boolean hasEffect(ItemStack stack) {
      return getPos(stack) != null;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      BlockPos pos = getPos(stack);
      int dim = getDim(stack);
      int uses = getUses(stack);
      if (pos != null && !player.isSneaking()) {
         PlayerHelper.teleportPlayer(player, pos, dim);
         if (uses > 1) {
            setUses(stack, uses - 1);
            SoundHelper.playSoundAtPos(world, pos, SoundEvents.ENTITY_SHULKER_TELEPORT, 0.8F, 1.0F);
         } else {
            stack.shrink(1);
            SoundHelper.playSoundAtPos(world, pos, SoundEvents.ITEM_TOTEM_USE, 0.8F, 1.0F);
         }

         return super.onItemRightClick(world, player, hand);
      } else {
         BlockPos playerPos = player.getPosition();
         int playerDim = player.dimension;
         setPos(stack, playerPos);
         setDim(stack, playerDim);
         SoundHelper.playSoundAtPos(world, playerPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.8F);
         return super.onItemRightClick(world, player, hand);
      }
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      int uses = getUses(stack);
      TextFormatting color = uses == 1 ? TextFormatting.RED : TextFormatting.GREEN;
      tooltip.add(color + I18n.format("number_of_uses", new Object[0]) + ": " + uses);
      BlockPos pos = getPos(stack);
      if (pos != null) {
         String x = String.valueOf(pos.getX());
         String y = String.valueOf(pos.getY());
         String z = String.valueOf(pos.getZ());
         String dim = String.valueOf(getDim(stack));
         tooltip.add(TextFormatting.BLUE + x + " " + y + " " + z + ", " + I18n.format("dimension", new Object[0]) + " " + dim);
      }

   }

   public static void setPos(ItemStack stack, BlockPos pos) {
      ItemNBTHelper.setLong(stack, "pos", pos.toLong());
   }

   public static void setDim(ItemStack stack, int dim) {
      ItemNBTHelper.setInt(stack, "dim", dim);
   }

   public static void setUses(ItemStack stack, int uses) {
      ItemNBTHelper.setInt(stack, "uses", uses);
   }

   public static BlockPos getPos(ItemStack stack) {
      long pos = ItemNBTHelper.getLong(stack, "pos", 0L);
      return pos == 0L ? null : BlockPos.fromLong(pos);
   }

   public static int getDim(ItemStack stack) {
      return ItemNBTHelper.getInt(stack, "dim", 0);
   }

   public static int getUses(ItemStack stack) {
      return ItemNBTHelper.getInt(stack, "uses", TotemOfReturning.numberOfUses);
   }
}
