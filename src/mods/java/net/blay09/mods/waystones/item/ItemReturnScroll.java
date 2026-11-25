package net.blay09.mods.waystones.item;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemReturnScroll extends ItemBoundScroll {
   public static final String name = "return_scroll";
   public static final ResourceLocation registryName = new ResourceLocation("waystones", "return_scroll");

   public ItemReturnScroll() {
      this.setUnlocalizedName(registryName.toString());
   }

   @Nullable
   protected WaystoneEntry getBoundTo(EntityPlayer player, ItemStack itemStack) {
      return PlayerWaystoneHelper.getLastWaystone(player);
   }

   public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
      return EnumActionResult.PASS;
   }
}
