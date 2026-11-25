package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.BlockInit;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowRedstone extends EntityArrowBase implements IThrowableEntity {
   private int cooldown = 1;
   private boolean spawnRedstone;
   private IBlockState block = Blocks.AIR.getDefaultState();
   private double damagesetter = ArrowConfig.DamageArrowRedstone.getDoub();

   public EntityArrowRedstone(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowRedstone(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowRedstone(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowRedstone);
   }

   protected void arrowHit(EntityLivingBase living) {
   }

   protected void arrowHitGround(BlockPos setTorchPos) {
      BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
      IBlockState spawnRedstone = BlockInit.RedstonePiece.getDefaultState();
      Block test = this.world.getBlockState(blockpos).getBlock();
      if ((test.isReplaceable(this.world, blockpos) || test instanceof BlockFlower || test instanceof BlockSnow) && !this.spawnRedstone) {
         this.block = this.world.getBlockState(blockpos);
         this.world.setBlockState(blockpos, spawnRedstone);
         this.spawnRedstone = true;
      }

      if (this.cooldown <= 0) {
         this.world.setBlockState(blockpos, this.block);
         this.setDead();
      }

      if (this.spawnRedstone) {
         --this.cooldown;
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger("cooldown", this.cooldown);
      compound.setBoolean("spawnRedstone", this.spawnRedstone);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.cooldown = compound.getInteger("cooldown");
      this.spawnRedstone = compound.getBoolean("spawnRedstone");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
