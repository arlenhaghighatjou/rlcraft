package vazkii.quark.automation.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.automation.block.BlockGravisand;
import vazkii.quark.automation.feature.Gravisand;

public class EntityGravisand extends EntityFallingBlock {
   private static final DataParameter<Float> DIRECTION = EntityDataManager.createKey(EntityGravisand.class, DataSerializers.FLOAT);
   private static final String TAG_DIRECTION = "fallDirection";
   private final IBlockState fallTile = Gravisand.gravisand.getDefaultState();

   public EntityGravisand(World worldIn) {
      super(worldIn);
      this.setSize(0.98F, 0.98F);
   }

   public EntityGravisand(World worldIn, double x, double y, double z, IBlockState fallingBlockState, float direction) {
      super(worldIn, x, y, z, fallingBlockState);
      this.dataManager.set(DIRECTION, Float.valueOf(direction));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(DIRECTION, Float.valueOf(0.0F));
   }

   public void onUpdate() {
      Block block = this.fallTile.getBlock();
      if (this.fallTile.getMaterial() != Material.AIR && this.posY <= 300.0D && this.posY >= -50.0D) {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (this.fallTime++ == 0) {
            BlockPos blockpos = new BlockPos(this);
            if (this.world.getBlockState(blockpos).getBlock() == block) {
               this.world.setBlockToAir(blockpos);
            } else if (!this.world.isRemote) {
               this.setDead();
               return;
            }
         }

         float fallDirection = this.getFallDirection();
         this.motionY = (double)(fallDirection * 0.4F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         if (!this.world.isRemote) {
            float off = fallDirection < 0.0F ? 0.5F : 0.0F;
            BlockPos fallTarget = new BlockPos(this.posX, this.posY + (double)fallDirection + (double)off, this.posZ);
            if (this.collidedVertically) {
               BlockPos pos = new BlockPos(this);
               IBlockState iblockstate = this.world.getBlockState(pos);
               this.motionX *= 0.7D;
               this.motionZ *= 0.7D;
               this.motionY *= -0.5D;
               if (iblockstate.getBlock() != Blocks.PISTON_EXTENSION) {
                  this.setDead();
                  if ((!this.world.mayPlace(block, pos, true, fallDirection < 0.0F ? EnumFacing.UP : EnumFacing.DOWN, this) || BlockGravisand.canFallThrough(this.world.getBlockState(fallTarget)) || !this.world.setBlockState(pos, this.fallTile, 3)) && this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
                     this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.fallTile)), 0.0F);
                  }
               }
            }
         }

         this.motionX *= 0.98D;
         this.motionY *= 0.98D;
         this.motionZ *= 0.98D;
      } else {
         this.setDead();
      }
   }

   public void fall(float distance, float damageMultiplier) {
   }

   private float getFallDirection() {
      return ((Float)this.dataManager.get(DIRECTION)).floatValue();
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("fallDirection", this.getFallDirection());
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.dataManager.set(DIRECTION, Float.valueOf(compound.getFloat("fallDirection")));
   }

   public IBlockState getBlock() {
      return this.fallTile;
   }
}
