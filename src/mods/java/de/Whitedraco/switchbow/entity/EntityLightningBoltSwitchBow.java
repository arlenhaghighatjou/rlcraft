package de.Whitedraco.switchbow.entity;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.proxy.packets.ParticleLightningPacket;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityLightningBoltSwitchBow extends EntityLightningBolt {
   private int lightningState;
   public long boltVertex;
   private int boltLivingTime;
   private final boolean effectOnly;
   private boolean isStorm;

   public EntityLightningBoltSwitchBow(World worldIn, double x, double y, double z, boolean effectOnlyIn, boolean isStorm) {
      super(worldIn, x, y, z, true);
      this.isStorm = isStorm;
      this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
      this.lightningState = 2;
      this.boltVertex = this.rand.nextLong();
      this.boltLivingTime = this.rand.nextInt(3) + 1;
      this.effectOnly = effectOnlyIn;
      BlockPos blockpos = new BlockPos(this);
      if (!effectOnlyIn && !worldIn.isRemote && worldIn.getGameRules().getBoolean("doFireTick") && (worldIn.getDifficulty() == EnumDifficulty.NORMAL || worldIn.getDifficulty() == EnumDifficulty.HARD) && worldIn.isAreaLoaded(blockpos, 10)) {
         if (worldIn.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(worldIn, blockpos) && SwitchBowConfig.spawnFirewithLightning.getBool()) {
            worldIn.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
         }

         if (worldIn.getBlockState(blockpos.up()).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(worldIn, blockpos.up()) && SwitchBowConfig.spawnFirewithLightning.getBool()) {
            worldIn.setBlockState(blockpos.up(), Blocks.FIRE.getDefaultState());
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);
            if (worldIn.getBlockState(blockpos1).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(worldIn, blockpos1) && SwitchBowConfig.spawnFirewithLightning.getBool()) {
               worldIn.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
            }

            if (worldIn.getBlockState(blockpos1.up()).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(worldIn, blockpos1.up()) && SwitchBowConfig.spawnFirewithLightning.getBool()) {
               worldIn.setBlockState(blockpos1.up(), Blocks.FIRE.getDefaultState());
            }
         }
      }

   }

   public void onUpdate() {
      super.onEntityUpdate();
      if (this.lightningState == 2) {
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
         if (this.isStorm) {
            if (!this.world.isRemote) {
               Initial.network.sendToAllAround(new ParticleLightningPacket(this.getPosition()), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 100.0D));
            }

            for(int a = 0; a < SwitchBowConfig.manySoundByThunder_Arrow.getInteger(); ++a) {
               this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.5F);
               this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 3.0F, 0.5F + this.rand.nextFloat() * 0.5F);
            }
         }
      }

      --this.lightningState;
      if (this.lightningState < 0) {
         if (this.boltLivingTime == 0) {
            this.setDead();
         } else if (this.lightningState < -this.rand.nextInt(10)) {
            --this.boltLivingTime;
            this.lightningState = 1;
            if (!this.effectOnly && !this.world.isRemote) {
               this.boltVertex = this.rand.nextLong();
               BlockPos blockpos = new BlockPos(this);
               if (SwitchBowConfig.spawnFirewithLightning.getBool() && this.world.getGameRules().getBoolean("doFireTick") && this.world.isAreaLoaded(blockpos, 10)) {
                  if (this.isStorm) {
                     BlockPos pos = blockpos.add(-2, 0, -2);

                     for(int i = 0; i < 5; ++i) {
                        for(int p = 0; p < 5; ++p) {
                           if (this.world.getBlockState(pos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, pos)) {
                              this.world.setBlockState(pos.add(i, 0, p), Blocks.FIRE.getDefaultState());
                           }

                           if (this.world.getBlockState(pos.add(i, 1, p)).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, pos.add(i, 1, p))) {
                              this.world.setBlockState(pos.add(i, 1, p), Blocks.FIRE.getDefaultState());
                           }
                        }
                     }
                  } else {
                     if (this.world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, blockpos)) {
                        this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
                     }

                     if (this.world.getBlockState(blockpos.up()).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, blockpos.up())) {
                        this.world.setBlockState(blockpos.up(), Blocks.FIRE.getDefaultState());
                     }
                  }
               }
            }
         }
      }

      if (this.lightningState >= 0) {
         if (this.world.isRemote) {
            this.world.setLastLightningBolt(2);
         } else if (!this.effectOnly) {
            double d0 = 3.0D;
            if (this.isStorm) {
               d0 = 7.0D;
            }

            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(this.posX - d0, this.posY - 3.0D, this.posZ - d0, this.posX + d0, this.posY + 6.0D + 3.0D, this.posZ + d0));

            for(int i = 0; i < list.size(); ++i) {
               Entity entity = (Entity)list.get(i);
               if (!ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                  entity.onStruckByLightning(this);
               }
            }
         }
      }

   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
   }
}
