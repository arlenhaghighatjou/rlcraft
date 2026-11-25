package com.lothrazar.playerbosses;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityPlayerBoss extends EntityGiantZombie implements IRangedAttackMob {
   private static final DataParameter<Boolean> ATTACKINGFIRE = EntityDataManager.createKey(EntityGiantZombie.class, DataSerializers.BOOLEAN);
   public static double armor;
   public static double health;
   public static double speed;
   public static double damage;
   public static boolean immuneFire;
   public static int expDropped;
   public static String bossName;
   public static String mainHand = "";
   public static String offHand = "";
   public static SoundEvent SOUND_HURT;
   public static SoundEvent SOUND_AMB;
   public static SoundEvent SOUND_DEATH;
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.GREEN, Overlay.PROGRESS)).setDarkenSky(true);
   private EntityPlayerBoss.EnumAttackType attackType;
   private EntityAIAttackMelee melee;
   private AIFireballAttackGeneric fireball;
   private EntityAIAttackRangedBow bow;
   private EntityAIAvoidEntity runaway;

   public EntityPlayerBoss(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = immuneFire;
      this.experienceValue = expDropped;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(ATTACKINGFIRE, Boolean.valueOf(false));
   }

   public void setAttacking(boolean attacking) {
      this.dataManager.set(ATTACKINGFIRE, Boolean.valueOf(attacking));
   }

   protected boolean canDespawn() {
      return ConfigManager.canDespawn;
   }

   public boolean isNonBoss() {
      return false;
   }

   public String getName() {
      return bossName;
   }

   public void addTrackingPlayer(EntityPlayerMP player) {
      super.addTrackingPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public boolean isHandActive() {
      return this.attackType == EntityPlayerBoss.EnumAttackType.RANGED;
   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public void updateAITasks() {
      super.updateAITasks();
      this.bossInfo.setPercent(this.getHealthPercent());
   }

   private float getHealthPercent() {
      return this.getHealth() / this.getMaxHealth();
   }

   protected ResourceLocation getLootTable() {
      return new ResourceLocation("playerbosses", "entity/player_boss");
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      IEntityLivingData res = super.onInitialSpawn(difficulty, livingdata);
      this.setLeftHanded(false);
      return res;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(armor);
   }

   public AIFireballAttackGeneric getAiFire() {
      if (this.fireball == null) {
         this.fireball = new AIFireballAttackGeneric(this);
      }

      return this.fireball;
   }

   public EntityAIAttackMelee getAiMelee() {
      if (this.melee == null) {
         this.melee = new EntityAIAttackMelee(this, 1.0D, false);
      }

      return this.melee;
   }

   public EntityAIAttackRangedBow getAiBow() {
      if (this.bow == null) {
         this.bow = new EntityAIAttackRangedBow(this, 1.0D, 20, 15.0F);
      }

      return this.bow;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
      this.attackType = EntityPlayerBoss.EnumAttackType.RANGED;
      this.setCombatTask();
   }

   protected SoundEvent getAmbientSound() {
      return SOUND_AMB;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SOUND_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SOUND_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   public void writeEntityToNBT(NBTTagCompound root) {
      super.writeEntityToNBT(root);
      root.setInteger("attackType", this.attackType.ordinal());
   }

   public void readEntityFromNBT(NBTTagCompound root) {
      super.readEntityFromNBT(root);
      int at = root.getInteger("attackType");
      this.attackType = EntityPlayerBoss.EnumAttackType.values()[at];
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if ((double)this.getHealthPercent() < ConfigManager.thirdPhaseHealth && ConfigManager.thirdPhaseHealth > 0.0D) {
         this.attackType = EntityPlayerBoss.EnumAttackType.FIRE;
      } else if ((double)this.getHealthPercent() < ConfigManager.secondPhaseHealth && ConfigManager.secondPhaseHealth > 0.0D) {
         this.attackType = EntityPlayerBoss.EnumAttackType.MELEE;
      } else {
         this.attackType = EntityPlayerBoss.EnumAttackType.RANGED;
      }

      this.setCombatTask();
   }

   public void setCombatTask() {
      switch(this.attackType) {
      case RANGED:
         this.setRangedWeapons();
         this.tasks.addTask(4, this.getAiBow());
         break;
      case MELEE:
         this.tasks.removeTask(this.getAiBow());
         this.setMeleeWeapons();
         this.tasks.addTask(4, this.getAiMelee());
         break;
      case FIRE:
         this.tasks.removeTask(this.getAiMelee());
         this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
         this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
         if (ConfigManager.thirdPhaseRunaway && this.runaway == null) {
            System.out.println("ADD RUNAWAY ");
            this.runaway = new EntityAIAvoidEntity(this, EntityPlayer.class, 4.0F, 0.6D, 0.8D);
            this.tasks.addTask(3, this.runaway);
         }

         this.tasks.addTask(4, this.getAiFire());
      }

   }

   private void setRangedWeapons() {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
      this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.ARROW));
   }

   private void setMeleeWeapons() {
      try {
         if (!mainHand.isEmpty()) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Item.getByNameOrId(mainHand)));
         }

         if (!offHand.isEmpty()) {
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Item.getByNameOrId(offHand)));
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
      EntityArrow entityarrow = this.getArrow(distanceFactor);
      double d0 = target.posX - this.posX;
      double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
      double d2 = target.posZ - this.posZ;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float velocity = 1.6F;
      int inaccuracy = 14 - this.world.getDifficulty().getDifficultyId() * 4;
      entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, velocity, (float)inaccuracy);
      this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
      this.world.spawnEntity(entityarrow);
   }

   private EntityArrow getArrow(float distanceFactor) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
      entitytippedarrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
      if (ConfigManager.arrowPotions) {
         entitytippedarrow.addEffect(new PotionEffect(MobEffects.POISON, 60, 1));
         entitytippedarrow.addEffect(new PotionEffect(MobEffects.UNLUCK, 60, 1));
         entitytippedarrow.addEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 60, 1));
      }

      return entitytippedarrow;
   }

   public void setSwingingArms(boolean swingingArms) {
   }

   public int getItemInUseMaxCount() {
      return this.world.rand.nextDouble() < ConfigManager.arrowChance ? 20 : 0;
   }

   private static enum EnumAttackType {
      MELEE,
      RANGED,
      FIRE;

      private EnumAttackType() {
      }
   }

   public static class Factory implements IRenderFactory<EntityPlayerBoss> {
      public Factory() {
      }

      public Render<? super EntityPlayerBoss> createRenderFor(RenderManager manager) {
         return new RenderPlayerBoss(manager);
      }
   }
}
