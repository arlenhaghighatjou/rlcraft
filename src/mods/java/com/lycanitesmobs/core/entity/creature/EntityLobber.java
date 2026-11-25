package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLobber extends BaseCreatureEntity implements IMob {
   public int blockMeltingRadius = 2;

   public EntityLobber(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.isLavaCreature = true;
      this.hasAttackSound = false;
      this.setupMob();
      this.setPathPriority(PathNodeType.LAVA, 0.0F);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
   }

   public void loadCreatureFlags() {
      this.blockMeltingRadius = this.creatureInfo.getFlag("blockMeltingRadius", this.blockMeltingRadius);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
         int trailHeight = 1;
         int trailWidth = 1;
         if (this.isRareVariant()) {
            trailWidth = 3;
         }

         for(int y = 0; y < trailHeight; ++y) {
            Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
            if (block == Blocks.AIR || block == Blocks.FIRE || block == Blocks.SNOW || block == Blocks.TALLGRASS || block == ObjectManager.getBlock("frostfire") || block == ObjectManager.getBlock("icefire")) {
               if (trailWidth == 1) {
                  this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), Blocks.FIRE.getDefaultState());
               } else {
                  for(int x = -(trailWidth / 2); x < trailWidth / 2 + 1; ++x) {
                     for(int z = -(trailWidth / 2); z < trailWidth / 2 + 1; ++z) {
                        this.getEntityWorld().setBlockState(this.getPosition().add(x, y, z), Blocks.FIRE.getDefaultState());
                     }
                  }
               }
            }
         }
      }

      if (!this.getEntityWorld().isRemote && this.isRareVariant() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.blockMeltingRadius > 0 && this.ticksExisted % 10 == 0) {
         int range = this.blockMeltingRadius;

         for(int w = -((int)Math.ceil((double)this.width) + range); (double)w <= Math.ceil((double)this.width) + (double)range; ++w) {
            for(int d = -((int)Math.ceil((double)this.width) + range); (double)d <= Math.ceil((double)this.width) + (double)range; ++d) {
               for(int h = 0; (double)h <= Math.ceil((double)this.height); ++h) {
                  Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
                  if (block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE || block == Blocks.DIRT || block == Blocks.GRAVEL || block == Blocks.SAND) {
                     IBlockState blockState = Blocks.FLOWING_LAVA.getStateFromMeta(11);
                     this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
                  }
               }
            }
         }

         if (this.ticksExisted % 40 == 0) {
            ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("magma");
            if (projectileInfo != null) {
               BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
               projectile.setProjectileScale(2.0F);
               projectile.shoot((double)(2.0F * this.getRNG().nextFloat() - 1.0F), (double)this.getRNG().nextFloat(), (double)(2.0F * this.getRNG().nextFloat() - 1.0F), 1.2F, 6.0F);
               this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
               this.getEntityWorld().spawnEntity(projectile);
            }
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.DRIP_LAVA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         if (this.ticksExisted % 10 == 0) {
            for(int i = 0; i < 2; ++i) {
               this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   public float getAISpeedModifier() {
      return this.lavaContact() ? 2.0F : 1.0F;
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      BlockPos pos = new BlockPos(x, y, z);
      if (this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.LAVA) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.lavaContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("magma", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public boolean isAggressive() {
      return this.getAir() <= -100 ? false : super.isAggressive();
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean canBurn() {
      return false;
   }

   public boolean waterDamage() {
      return !this.isRareVariant();
   }

   public boolean canBreatheUnderlava() {
      return true;
   }

   public boolean canBreatheAir() {
      return true;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 0.0F : super.getDamageModifier(damageSrc);
   }

   public void applyDropEffects(EntityItemCustom entityitem) {
      entityitem.setCanBurn(false);
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
