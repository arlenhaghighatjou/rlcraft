package snownee.snow.hooks;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import snownee.snow.ModConfig;
import snownee.snow.ModSnowBlock;

public class ItemSnowHook {
   public ItemSnowHook() {
   }

   public static EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (player.canPlayerEdit(pos, facing, itemstack) && ModConfig.placeSnowInBlock) {
         IBlockState state = worldIn.getBlockState(pos);
         Block block = state.getBlock();
         BlockPos blockpos = pos;
         if (facing == EnumFacing.UP && !block.isReplaceable(worldIn, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(worldIn, pos.up())) {
            blockpos = pos.up();
            state = worldIn.getBlockState(blockpos);
         }

         if (ModSnowBlock.canContainState(state)) {
            ModSnowBlock.placeLayersOn(worldIn, blockpos, 1, false, true, 3);
            if (player instanceof EntityPlayerMP) {
               CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, itemstack);
            }

            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
         }
      }

      return null;
   }

   public static ActionResult<ItemStack> onItemRightClick(ItemSnow item, World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      EnumActionResult actionResult = EnumActionResult.PASS;
      if (ModConfig.placeSnowInBlock) {
         float f = playerIn.rotationPitch;
         float f1 = playerIn.rotationYaw;
         double d0 = playerIn.posX;
         double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
         double d2 = playerIn.posZ;
         Vec3d vec3d = new Vec3d(d0, d1, d2);
         float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
         float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
         float f4 = -MathHelper.cos(-f * 0.017453292F);
         float f5 = MathHelper.sin(-f * 0.017453292F);
         float f6 = f3 * f4;
         float f7 = f2 * f4;
         double d3 = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
         Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
         RayTraceResult result = worldIn.rayTraceBlocks(vec3d, vec3d1, false, false, false);
         if (result != null && result.typeOfHit == Type.BLOCK && result.sideHit != EnumFacing.DOWN) {
            actionResult = item.onItemUse(playerIn, worldIn, result.getBlockPos(), handIn, result.sideHit, (float)result.hitVec.x, (float)result.hitVec.y, (float)result.hitVec.z);
            if (actionResult == EnumActionResult.SUCCESS) {
               playerIn.swingArm(handIn);
            }
         }
      }

      return new ActionResult(actionResult, playerIn.getHeldItem(handIn));
   }
}
