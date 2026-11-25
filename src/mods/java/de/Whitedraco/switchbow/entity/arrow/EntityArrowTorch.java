package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.BlockInit;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowTorch extends EntityArrowBase implements IThrowableEntity {
   private boolean spawnTorch = true;
   private double damagesetter = ArrowConfig.DamageArrowTorch.getDoub();

   public EntityArrowTorch(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowTorch(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowTorch(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowTorch);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         living.setFire(6);
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote && this.spawnTorch) {
         if (this.BlocksetterFackel(blockpos)) {
            this.spawnTorch = false;
            this.setDead();
         } else {
            this.spawnTorch = false;
         }
      }

   }

   public boolean BlocksetterFackel(BlockPos setTorchPos) {
      int posx = setTorchPos.getX();
      int posy = setTorchPos.getY();
      int posz = setTorchPos.getZ();
      World world = this.world;
      int Richtung = MathHelper.floor((double)(this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      if (!SwitchBowConfig.replaceTorchwithMinilight.getBool()) {
         switch(Richtung) {
         case 0:
            if ((world.getBlockState(new BlockPos(posx, posy, posz - 1)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx, posy, posz - 1)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx, posy, posz - 1)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx, posy, posz - 1))) {
               world.setBlockState(new BlockPos(posx, posy, posz - 1), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
               return true;
            }

            if ((world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx, posy + 1, posz))) {
               world.setBlockState(new BlockPos(posx, posy + 1, posz), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP));
               return true;
            }
            break;
         case 1:
            if ((world.getBlockState(new BlockPos(posx - 1, posy, posz)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx - 1, posy, posz)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx - 1, posy, posz)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx - 1, posy, posz))) {
               world.setBlockState(new BlockPos(posx - 1, posy, posz), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
               return true;
            }

            if ((world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx, posy + 1, posz))) {
               world.setBlockState(new BlockPos(posx, posy + 1, posz), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP));
               return true;
            }
            break;
         case 2:
            if ((world.getBlockState(new BlockPos(posx, posy, posz + 1)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx, posy, posz + 1)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx, posy, posz + 1)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx, posy, posz + 1))) {
               world.setBlockState(new BlockPos(posx, posy, posz + 1), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
               return true;
            }

            if ((world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx, posy + 1, posz))) {
               world.setBlockState(new BlockPos(posx, posy + 1, posz), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP));
               return true;
            }
            break;
         case 3:
            if ((world.getBlockState(new BlockPos(posx + 1, posy, posz)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx + 1, posy, posz)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx + 1, posy, posz)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx + 1, posy, posz))) {
               world.setBlockState(new BlockPos(posx + 1, posy, posz), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
               return true;
            }

            if ((world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.AIR || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.SNOW_LAYER || world.getBlockState(new BlockPos(posx, posy + 1, posz)).getBlock() == Blocks.TALLGRASS) && Blocks.TORCH.canPlaceBlockAt(world, new BlockPos(posx, posy + 1, posz))) {
               world.setBlockState(new BlockPos(posx, posy + 1, posz), Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP));
               return true;
            }
         }
      } else {
         switch(Richtung) {
         case 0:
            if (this.canPlaceLight(world, new BlockPos(posx, posy, posz - 1))) {
               world.setBlockState(new BlockPos(posx, posy, posz - 1), BlockInit.MiniLight.getDefaultState());
               return true;
            }
            break;
         case 1:
            if (this.canPlaceLight(world, new BlockPos(posx - 1, posy, posz))) {
               world.setBlockState(new BlockPos(posx - 1, posy, posz), BlockInit.MiniLight.getDefaultState());
               return true;
            }
            break;
         case 2:
            if (this.canPlaceLight(world, new BlockPos(posx, posy, posz + 1))) {
               world.setBlockState(new BlockPos(posx, posy, posz + 1), BlockInit.MiniLight.getDefaultState());
               return true;
            }
            break;
         case 3:
            if (this.canPlaceLight(world, new BlockPos(posx + 1, posy, posz))) {
               world.setBlockState(new BlockPos(posx + 1, posy, posz), BlockInit.MiniLight.getDefaultState());
               return true;
            }
         }

         if (this.canPlaceLight(world, new BlockPos(posx, posy + 1, posz))) {
            world.setBlockState(new BlockPos(posx, posy + 1, posz), BlockInit.MiniLight.getDefaultState());
            return true;
         }

         if (this.canPlaceLight(world, new BlockPos(posx, posy, posz))) {
            world.setBlockState(new BlockPos(posx, posy, posz), BlockInit.MiniLight.getDefaultState());
            return true;
         }

         if (this.canPlaceLight(world, new BlockPos(posx, posy - 1, posz))) {
            world.setBlockState(new BlockPos(posx, posy - 1, posz), BlockInit.MiniLight.getDefaultState());
            return true;
         }
      }

      return false;
   }

   private boolean canPlaceLight(IBlockAccess worldIn, BlockPos pos) {
      Block blockTest = this.world.getBlockState(pos).getBlock();
      return blockTest.isReplaceable(worldIn, pos) || blockTest == Blocks.AIR || blockTest == Blocks.SNOW_LAYER || blockTest == Blocks.TALLGRASS;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setBoolean("spawnTorch", this.spawnTorch);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.spawnTorch = compound.getBoolean("spawnTorch");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
