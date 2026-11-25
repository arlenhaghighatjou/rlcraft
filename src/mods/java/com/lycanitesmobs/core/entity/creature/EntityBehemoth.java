package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityBehemoth extends TameableCreatureEntity implements IMob {
   protected static final DataParameter<Integer> HELLFIRE_ENERGY = EntityDataManager.createKey(EntityBehemoth.class, DataSerializers.VARINT);
   public int hellfireEnergy = 0;
   public List<EntityHellfireOrb> hellfireOrbs = new ArrayList();

   public EntityBehemoth(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setRange(1.0D).setMaxChaseDistance(8.0F));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(0.0F).setChaseTime(-1));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HELLFIRE_ENERGY, Integer.valueOf(this.hellfireEnergy));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(HELLFIRE_ENERGY, Integer.valueOf(this.hellfireEnergy));
      } else {
         try {
            this.hellfireEnergy = ((Integer)this.dataManager.get(HELLFIRE_ENERGY)).intValue();
         } catch (Exception var7) {
            ;
         }
      }

      if (this.getEntityWorld().isRemote && this.hellfireEnergy > 0) {
         EntityRahovart.updateHellfireOrbs(this, this.updateTick, 3, this.hellfireEnergy, 1.0F, this.hellfireOrbs);
      }

      if (!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
         int trailHeight = 1;
         int trailWidth = 1;
         if (this.isRareVariant()) {
            trailWidth = 3;
         }

         for(int y = 0; y < trailHeight; ++y) {
            Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
            if (block != null && (block == Blocks.AIR || block == Blocks.FIRE || block == Blocks.SNOW || block == Blocks.TALLGRASS || block == ObjectManager.getBlock("frostfire") || block == ObjectManager.getBlock("icefire") || block == ObjectManager.getBlock("scorchfire") || block == ObjectManager.getBlock("doomfire"))) {
               if (trailWidth == 1) {
                  this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), ObjectManager.getBlock("hellfire").getDefaultState());
               } else {
                  for(int x = -(trailWidth / 2); x < trailWidth / 2 + 1; ++x) {
                     for(int z = -(trailWidth / 2); z < trailWidth / 2 + 1; ++z) {
                        this.getEntityWorld().setBlockState(this.getPosition().add(x, y, z), ObjectManager.getBlock("hellfire").getDefaultState());
                     }
                  }
               }
            }
         }
      }

   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return target instanceof EntityBelph ? false : super.canAttackEntity(target);
   }

   public boolean canAttackWithPickup() {
      return true;
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)target;
            if (this.canPickupEntity(entityLivingBase)) {
               this.pickupEntity(entityLivingBase);
            } else if (this.getPickupEntity() == target && this.getRNG().nextBoolean()) {
               this.dropPickupEntity();
               Vec3d throwVelocity = this.getFacingPositionDouble(0.0D, 1.0D, 0.0D, 2.0D, (double)this.rotationYaw);
               target.motionX = throwVelocity.x;
               target.motionY = throwVelocity.y;
               target.motionZ = throwVelocity.z;
            }
         }

         return true;
      }
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("hellfireball", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public double[] getPickupOffset(Entity entity) {
      Vec3d offset = this.getFacingPositionDouble(0.0D, 2.0D, 0.0D, 1.5D, (double)this.rotationYaw);
      return new double[]{offset.x, offset.y, offset.z};
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

   public boolean petControlsEnabled() {
      return true;
   }

   public ResourceLocation getTexture() {
      if (this.hasCustomName() && "Krampus".equals(this.getCustomNameTag())) {
         String textureName = this.getTextureName() + "_krampus";
         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      } else {
         return super.getTexture();
      }
   }
}
