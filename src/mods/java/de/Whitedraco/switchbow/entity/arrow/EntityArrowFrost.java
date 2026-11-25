package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.PotionInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowFrost extends EntityArrowBase implements IThrowableEntity {
   private boolean spawnOlaf = true;
   private boolean spawnSnow = true;
   private double damagesetter = ArrowConfig.DamageArrowFrost.getDoub();

   public EntityArrowFrost(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowFrost(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowFrost(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowFrost);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         living.addPotionEffect(new PotionEffect(PotionInit.PotionFreeze, 100, 0, true, true));
      }

      if (this.spawnSnow) {
         this.setBlock(living, living.posX, living.posY, living.posZ);
         this.spawnSnow = false;
      }

   }

   private void setBlock(EntityLivingBase entitylivingbase, double posx, double posy, double posz) {
      float Mobradius = entitylivingbase.width / 2.0F;
      int minx = (int)Math.floor(posx - (double)Mobradius);
      int maxx = (int)Math.floor(posx + (double)Mobradius);
      int miny = (int)posy;
      int minz = (int)Math.floor(posz - (double)Mobradius);
      int maxz = (int)Math.floor(posz + (double)Mobradius);

      for(int x = minx; x <= maxx; ++x) {
         for(int z = minz; z <= maxz; ++z) {
            if (entitylivingbase.world.getBlockState(new BlockPos((double)x, posy, (double)z)).getBlock() == Blocks.AIR && Blocks.SNOW_LAYER.canPlaceBlockAt(entitylivingbase.world, new BlockPos((double)x, posy, (double)z))) {
               this.world.setBlockState(new BlockPos((double)x, posy, (double)z), Blocks.SNOW_LAYER.getDefaultState(), 3);
            }
         }
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      Block block = this.world.getBlockState(blockpos).getBlock();
      if (!this.world.isRemote) {
         if (!this.world.getBlockState(blockpos.up()).isFullCube() && !this.world.getBlockState(blockpos.up(2)).isFullCube() && this.rand.nextInt(40) == 1 && this.spawnOlaf) {
            EntitySnowman Snowman = new EntitySnowman(this.world);
            Snowman.setCustomNameTag("Olaf");
            Snowman.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
            Snowman.heal(20.0F);
            Snowman.setPumpkinEquipped(false);
            Snowman.setPosition((double)blockpos.getX(), (double)(blockpos.getY() + 1), (double)blockpos.getZ());
            this.world.spawnEntity(Snowman);
         }

         this.spawnOlaf = false;
         if (this.world.getBlockState(blockpos.up()).getBlock() == Blocks.AIR && Blocks.SNOW_LAYER.canPlaceBlockAt(this.world, new BlockPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ())) && this.spawnSnow) {
            this.world.setBlockState(new BlockPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ()), Blocks.SNOW_LAYER.getDefaultState(), 3);
            this.spawnSnow = false;
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setBoolean("spawnSnow", this.spawnSnow);
      compound.setBoolean("spawnOlaf", this.spawnOlaf);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.spawnSnow = compound.getBoolean("spawnSnow");
      this.spawnOlaf = compound.getBoolean("spawnOlaf");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }

   protected void particleOnFlyArrow() {
      for(int k = 0; k < 4; ++k) {
         double d0 = 0.0D;
         double d1 = 0.0D;
         double d2 = 1.0D;
         this.world.spawnParticle(EnumParticleTypes.SPELL_INSTANT, this.posX + this.motionX * (double)k / 4.0D, this.posY + this.motionY * (double)k / 4.0D, this.posZ + this.motionZ * (double)k / 4.0D, d0, d1, d2, new int[0]);
      }

   }

   public void detectinAir() {
      int level = 0;
      BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
      float f = (float)Math.min(16, 2 + level);
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos(0, 0, 0);

      for(MutableBlockPos blockpos$mutableblockpos1 : BlockPos.getAllInBoxMutable(pos.add((double)(-f), -1.0D, (double)(-f)), pos.add((double)f, -1.0D, (double)f))) {
         if (blockpos$mutableblockpos1.distanceSqToCenter(this.posX, this.posY, this.posZ) <= (double)(f * f)) {
            blockpos$mutableblockpos.setPos(blockpos$mutableblockpos1.getX(), blockpos$mutableblockpos1.getY() + 1, blockpos$mutableblockpos1.getZ());
            IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos);
            if (iblockstate.getMaterial() == Material.AIR) {
               IBlockState iblockstate1 = this.world.getBlockState(blockpos$mutableblockpos1);
               if (iblockstate1.getMaterial() == Material.WATER && ((Integer)iblockstate1.getValue(BlockLiquid.LEVEL)).intValue() == 0 && this.world.mayPlace(Blocks.FROSTED_ICE, blockpos$mutableblockpos1, false, EnumFacing.DOWN, (Entity)null)) {
                  this.world.setBlockState(blockpos$mutableblockpos1, Blocks.FROSTED_ICE.getDefaultState());
                  this.world.scheduleUpdate(blockpos$mutableblockpos1.toImmutable(), Blocks.FROSTED_ICE, MathHelper.getInt(this.rand, 60, 120));
               }
            }
         }
      }

   }
}
