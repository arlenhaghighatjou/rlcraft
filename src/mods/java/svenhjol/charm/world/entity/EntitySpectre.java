package svenhjol.charm.world.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.world.event.SpectreAttackEvent;
import svenhjol.charm.world.feature.Spectre;
import svenhjol.charm.world.feature.SpectreHaunting;
import svenhjol.charm.world.message.MessageSpectreDespawn;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.EnchantmentHelper;

public class EntitySpectre extends EntityMob {
   public static final ResourceLocation LOOT_TABLE = new ResourceLocation("charm", "entities/spectre");
   public float eyeHeight = 1.75F;

   public EntitySpectre(World world) {
      super(world);
      this.isImmuneToFire = true;
   }

   protected void initEntityAI() {
      this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)Spectre.movementSpeed);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue((double)Spectre.attackDamage);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)Spectre.maxHealth);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)Spectre.trackingRange);
   }

   public boolean attackEntityAsMob(Entity entity) {
      boolean attacked = super.attackEntityAsMob(entity);
      if (attacked && entity instanceof EntityLivingBase) {
         SpectreAttackEvent attackEvent = new SpectreAttackEvent(this, (EntityLivingBase)entity);
         MinecraftForge.EVENT_BUS.post(attackEvent);
         if (attackEvent.getResult() == Result.DENY || attackEvent.isCanceled()) {
            attacked = false;
         }

         if (attacked && entity instanceof EntityPlayer) {
            if (Spectre.applyCurse) {
               EnchantmentHelper.applyRandomCurse((EntityPlayer)entity);
            } else {
               ((EntityPlayer)entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, Spectre.weaknessDuration * 20, Spectre.weaknessAmplifier));
            }

            entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, CharmSounds.SPECTRE_HIT, SoundCategory.HOSTILE, 1.0F, 1.4F - entity.world.rand.nextFloat() / 2.0F);
         }

         this.despawn();
      }

      return attacked;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
   }

   public void onUpdate() {
      super.onUpdate();
      boolean despawn = false;
      BlockPos pos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
      if (!this.world.isRemote) {
         if (this.world.getLightFor(EnumSkyBlock.SKY, pos) > Spectre.despawnLight && this.world.isDaytime()) {
            despawn = true;
         } else if (this.world.getLightFromNeighbors(pos) >= Spectre.despawnLight) {
            despawn = true;
         }
      }

      if (Charm.hasFeature(SpectreHaunting.class) && this.ticksExisted > SpectreHaunting.ticksLiving) {
         despawn = true;
      }

      if (despawn) {
         this.despawn();
      }

   }

   public void onLivingUpdate() {
      if (this.world.isRemote) {
         for(int i = 0; i < 1; ++i) {
            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

      super.onLivingUpdate();
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return source != DamageSource.FALL && source != DamageSource.IN_WALL ? super.attackEntityFrom(source, amount) : false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT_TABLE;
   }

   public boolean getCanSpawnHere() {
      return super.getCanSpawnHere() && this.getEntityBoundingBox().minY <= (double)Spectre.spawnDepth;
   }

   public float getEyeHeight() {
      return this.eyeHeight;
   }

   public void onDeath(DamageSource cause) {
      this.despawn();
   }

   public void despawn() {
      BlockPos pos = this.getPosition();
      if (this.world instanceof WorldServer && pos != null) {
         NetworkHandler.INSTANCE.sendToAll(new MessageSpectreDespawn(pos));
      }

      if (this.world.getBlockState(pos.down()).getBlock() == Blocks.SAND) {
         this.world.setBlockState(pos.down(), Blocks.SOUL_SAND.getDefaultState(), 2);
      }

      this.setDead();
   }

   protected SoundEvent getDeathSound() {
      return CharmSounds.SPECTRE_DEATH;
   }

   protected SoundEvent getAmbientSound() {
      return CharmSounds.SPECTRE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return CharmSounds.SPECTRE_HIT;
   }
}
