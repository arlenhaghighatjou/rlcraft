package knightminer.inspirations.tools.entity;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class EntityModArrow extends EntityArrow {
   private int meta;
   public static final String TAG_META = "meta";

   public EntityModArrow(World world) {
      super(world);
   }

   public EntityModArrow(World world, double x, double y, double z, int meta) {
      super(world, x, y, z);
      this.init(meta);
   }

   public EntityModArrow(World world, EntityLivingBase shooter, int meta) {
      super(world, shooter);
      this.init(meta);
   }

   private void init(int meta) {
      this.meta = meta;
      this.setDamage(0.25D);
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(InspirationsTools.arrow, 1, this.meta);
   }

   protected void onHit(RayTraceResult raytrace) {
      if (raytrace.typeOfHit == Type.BLOCK) {
         BlockPos pos = raytrace.getBlockPos().offset(raytrace.sideHit);
         if (!InspirationsTools.redstoneCharge.canPlaceBlockAt(this.world, pos)) {
            pos = pos.offset(raytrace.sideHit);
            if (!InspirationsTools.redstoneCharge.canPlaceBlockAt(this.world, pos)) {
               super.onHit(raytrace);
               return;
            }
         }

         this.world.playSound((EntityPlayer)null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.4F + 0.8F);
         IBlockState state = InspirationsTools.redstoneCharge.getDefaultState().withProperty(BlockRedstoneCharge.FACING, raytrace.sideHit.getOpposite());
         this.world.setBlockState(pos, state, 11);
         this.setDead();
      } else {
         super.onHit(raytrace);
      }
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("meta", this.meta);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.meta = compound.getInteger("meta");
   }
}
