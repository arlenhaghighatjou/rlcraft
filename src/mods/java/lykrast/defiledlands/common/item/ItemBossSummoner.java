package lykrast.defiledlands.common.item;

import java.util.List;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.block.BlockConjuringAltar;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public abstract class ItemBossSummoner extends Item {
   public ItemBossSummoner() {
      this.setMaxStackSize(1);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      Block block = worldIn.getBlockState(pos).getBlock();
      if (!(block instanceof BlockConjuringAltar)) {
         return EnumActionResult.FAIL;
      } else if (((BlockConjuringAltar)block).isActive(worldIn, pos) && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL) {
         if (!worldIn.isRemote) {
            Entity boss = this.getBoss(worldIn);
            boss.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);

            for(EntityPlayerMP entityplayermp : worldIn.getEntitiesWithinAABB(EntityPlayerMP.class, boss.getEntityBoundingBox().grow(50.0D))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(entityplayermp, boss);
            }

            worldIn.spawnEntity(boss);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   protected abstract Entity getBoss(World var1);

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + I18n.format(super.getUnlocalizedName(stack) + ".tooltip", new Object[0])));
   }
}
