package xzeroair.trinkets.traits.abilities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;

public class AbilityClimbing extends Ability implements ITickableAbility {
   private float entityHeight;
   private BlockPos entityPos;
   private BlockPos bodyPos;
   private BlockPos headPos;
   private BlockPos headSpacePos;
   private BlockPos frontBodyPos;
   private BlockPos frontHeadPos;
   private IBlockState body;
   private IBlockState head;
   private IBlockState headSpace;
   private IBlockState frontBody;
   private IBlockState frontHead;
   private final String[] climbList;

   public AbilityClimbing() {
      super("block_climbing");
      this.climbList = TrinketsConfig.SERVER.races.fairy.allowedBlocks;
   }

   public void tickAbility(EntityLivingBase entity) {
      boolean flag = this.isCreativePlayer(entity);
      if (!flag && !entity.onGround && entity.collidedHorizontally && this.canClimb(entity)) {
         if (!entity.isSneaking()) {
            entity.motionY = 0.10000000149011612D;
         }

         if (entity.isSneaking()) {
            entity.motionY = 0.0D;
         }

         entity.fallDistance = 0.0F;
      }

   }

   protected boolean movingForward(EntityLivingBase entity, EnumFacing facing) {
      return (double)facing.getDirectionVec().getX() * entity.motionX > 0.0D || (double)facing.getDirectionVec().getZ() * entity.motionZ > 0.0D;
   }

   protected boolean canClimb(EntityLivingBase entity) {
      World world = entity.getEntityWorld();
      this.entityPos = new BlockPos(entity.getPositionVector());
      this.bodyPos = this.entityPos;
      this.frontBodyPos = this.entityPos.offset(entity.getAdjustedHorizontalFacing());
      this.body = world.getBlockState(this.bodyPos);
      this.frontBody = world.getBlockState(this.frontBodyPos);
      RayTraceResult result = world.rayTraceBlocks(entity.getPositionVector(), entity.getPositionVector().addVector(0.0D, (double)entity.height + 0.1D, 0.0D), false, true, false);
      boolean headClear = result != null && result.typeOfHit == Type.BLOCK;
      boolean whitelist = TrinketsConfig.SERVER.races.fairy.whitelistClimbables;

      for(String s : this.climbList) {
         ConfigHelper.ConfigObject object = new ConfigHelper.ConfigObject(s);
         if (object.doesBlockMatchEntry(this.body) || object.doesBlockMatchEntry(this.frontBody)) {
            return whitelist ? !headClear : false;
         }
      }

      return whitelist ? false : !headClear;
   }
}
