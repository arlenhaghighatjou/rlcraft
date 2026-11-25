package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModVillagers;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIDefendHive;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAILookAtTradePlayer;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIReEnterHive;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAITradePlayer;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIWanderHiveCenter;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexQueenAIWander;
import com.github.alexthe666.iceandfire.structures.WorldGenMyrmexHive;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class EntityMyrmexQueen extends EntityMyrmexBase {
   private static final ResourceLocation TEXTURE_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_queen.png");
   private static final ResourceLocation TEXTURE_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_queen.png");
   public static final Animation ANIMATION_BITE = Animation.create(15);
   public static final Animation ANIMATION_STING = Animation.create(15);
   public static final Animation ANIMATION_EGG = Animation.create(20);
   public static final Animation ANIMATION_DIGNEST = Animation.create(45);
   private static final DataParameter<Boolean> HASMADEHOME = EntityDataManager.createKey(EntityMyrmexQueen.class, DataSerializers.BOOLEAN);
   private int eggTicks = 0;
   public static final ResourceLocation DESERT_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "myrmex_queen_desert"));
   public static final ResourceLocation JUNGLE_LOOT = LootTableList.register(new ResourceLocation("iceandfire", "myrmex_queen_jungle"));

   @Nullable
   protected ResourceLocation getLootTable() {
      return this.isJungle() ? JUNGLE_LOOT : DESERT_LOOT;
   }

   public EntityMyrmexQueen(World worldIn) {
      super(worldIn);
      this.setSize(2.9F, 1.86F);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HASMADEHOME, Boolean.valueOf(true));
   }

   public void setCustomNameTag(String name) {
      if (!this.getCustomNameTag().equals(name) && this.getHive() != null) {
         this.getHive().colonyName = name;
      }

      super.setCustomNameTag(name);
   }

   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      tag.setInteger("EggTicks", this.eggTicks);
      tag.setBoolean("MadeHome", this.hasMadeHome());
   }

   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      this.eggTicks = tag.getInteger("EggTicks");
      this.setMadeHome(tag.getBoolean("MadeHome"));
   }

   public boolean hasMadeHome() {
      return ((Boolean)this.dataManager.get(HASMADEHOME)).booleanValue();
   }

   public void setMadeHome(boolean madeHome) {
      this.dataManager.set(HASMADEHOME, Boolean.valueOf(madeHome));
   }

   public VillagerProfession getProfessionForge() {
      return this.isJungle() ? ModVillagers.INSTANCE.jungleMyrmexQueen : ModVillagers.INSTANCE.desertMyrmexQueen;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getAnimation() == ANIMATION_DIGNEST) {
         this.spawnGroundEffects(3.0F);
      }

      if (this.getHive() != null) {
         this.getHive().tick(0, this.world);
      }

      if (this.hasMadeHome() && this.getGrowthStage() >= 2 && !this.canSeeSky()) {
         ++this.eggTicks;
      } else if (this.canSeeSky()) {
         this.setAnimation(ANIMATION_DIGNEST);
         if (this.getAnimationTick() == 42) {
            WorldGenMyrmexHive hiveGen = new WorldGenMyrmexHive(true, this.isJungle());
            int down = Math.max(15, this.getPosition().getY() - 20 + this.getRNG().nextInt(10));
            BlockPos genPos = new BlockPos(this.posX, (double)down, this.posZ);
            hiveGen.generate(this.world, this.getRNG(), genPos);
            this.setMadeHome(true);
            this.setLocationAndAngles((double)genPos.getX(), (double)down, (double)genPos.getZ(), 0.0F, 0.0F);
            this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 30));
            this.setHive(hiveGen.hive);

            for(int i = 0; i < 3; ++i) {
               EntityMyrmexWorker worker = new EntityMyrmexWorker(this.world);
               worker.copyLocationAndAnglesFrom(this);
               worker.setHive(this.getHive());
               worker.setJungleVariant(this.isJungle());
               if (!this.world.isRemote) {
                  this.world.spawnEntity(worker);
               }
            }

            return;
         }
      }

      if (!this.world.isRemote && this.eggTicks > IceAndFire.CONFIG.myrmexPregnantTicks && this.getHive() == null || !this.world.isRemote && this.getHive() != null && this.getHive().repopulate() && this.eggTicks > IceAndFire.CONFIG.myrmexPregnantTicks) {
         float radius = -5.25F;
         float angle = 0.017453292F * this.renderYawOffset;
         double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
         double extraZ = (double)(radius * MathHelper.cos(angle));
         BlockPos eggPos = new BlockPos(this.posX + extraX, this.posY + 0.75D, this.posZ + extraZ);
         if (this.world.isAirBlock(eggPos)) {
            this.setAnimation(ANIMATION_EGG);
            if (this.getAnimationTick() == 10) {
               EntityMyrmexEgg egg = new EntityMyrmexEgg(this.world);
               egg.setJungle(this.isJungle());
               int caste = getRandomCaste(this.world, this.getRNG(), this.getHive() == null || this.getHive().reproduces);
               egg.setMyrmexCaste(caste);
               egg.setLocationAndAngles(this.posX + extraX, this.posY + 0.75D, this.posZ + extraZ, 0.0F, 0.0F);
               if (this.getHive() != null) {
                  egg.hiveUUID = this.getHive().hiveUUID;
               }

               if (!this.world.isRemote) {
                  this.world.spawnEntity(egg);
               }

               this.eggTicks = 0;
            }
         }
      }

      if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
         this.playBiteSound();
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 14.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         }
      }

      if (this.getAnimation() == ANIMATION_STING && this.getAnimationTick() == 0) {
         this.playStingSound();
      }

      if (this.getAnimation() == ANIMATION_STING && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 14.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 2));
            this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
            this.getAttackTarget().isAirBorne = true;
            float f = MathHelper.sqrt(0.5D);
            EntityLivingBase var10000 = this.getAttackTarget();
            var10000.motionX /= 2.0D;
            var10000 = this.getAttackTarget();
            var10000.motionZ /= 2.0D;
            var10000 = this.getAttackTarget();
            var10000.motionX -= 0.5D / (double)f * 4.0D;
            var10000 = this.getAttackTarget();
            var10000.motionZ -= 0.5D / (double)f * 4.0D;
            if (this.getAttackTarget().onGround) {
               var10000 = this.getAttackTarget();
               var10000.motionY /= 2.0D;
               var10000 = this.getAttackTarget();
               var10000.motionY += 4.0D;
               if (this.getAttackTarget().motionY > 0.4000000059604645D) {
                  this.getAttackTarget().motionY = 0.4000000059604645D;
               }
            }
         }
      }

   }

   public boolean isEntityInvulnerable(DamageSource source) {
      return super.isEntityInvulnerable(source) || this.getAnimation() == ANIMATION_DIGNEST;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(0, new MyrmexAITradePlayer(this));
      this.tasks.addTask(0, new MyrmexAILookAtTradePlayer(this));
      this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(3, new MyrmexAIReEnterHive(this, 1.0D));
      this.tasks.addTask(4, new MyrmexAIWanderHiveCenter(this, 1.0D));
      this.tasks.addTask(5, new MyrmexQueenAIWander(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new MyrmexAIDefendHive(this));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(3, new MyrmexAIAttackPlayers(this));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, true, true, new Predicate<EntityLiving>() {
         public boolean apply(@Nullable EntityLiving entity) {
            return entity != null && !IMob.VISIBLE_MOB_SELECTOR.apply(entity) && !EntityMyrmexBase.haveSameHive(EntityMyrmexQueen.this, entity) && DragonUtils.isAlive(entity);
         }
      }));
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public boolean shouldMoveThroughHive() {
      return false;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(15.0D);
   }

   public ResourceLocation getAdultTexture() {
      return this.isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
   }

   public float getModelScale() {
      return 1.75F;
   }

   public int getCasteImportance() {
      return 3;
   }

   public boolean shouldLeaveHive() {
      return false;
   }

   public boolean shouldEnterHive() {
      return true;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getGrowthStage() < 2) {
         return false;
      } else if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_BITE) {
         this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_STING : ANIMATION_BITE);
         if (!this.world.isRemote && this.getRNG().nextInt(3) == 0 && this.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY) {
            this.entityDropItem(this.getHeldItem(EnumHand.MAIN_HAND), 0.0F);
            this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
         }

         if (!this.getPassengers().isEmpty()) {
            for(Entity entity : this.getPassengers()) {
               entity.dismountRidingEntity();
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canMove() {
      return super.canMove() && this.hasMadeHome();
   }

   public void spawnGroundEffects(float size) {
      for(int i = 0; (float)i < size * 3.0F; ++i) {
         for(int i1 = 0; i1 < 10; ++i1) {
            double motionX = this.getRNG().nextGaussian() * 0.07D;
            double motionY = this.getRNG().nextGaussian() * 0.07D;
            double motionZ = this.getRNG().nextGaussian() * 0.07D;
            float radius = size * this.rand.nextFloat();
            float angle = 0.017453292F * this.renderYawOffset * 3.14F * this.rand.nextFloat();
            double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
            double extraY = 0.800000011920929D;
            double extraZ = (double)(radius * MathHelper.cos(angle));
            IBlockState iblockstate = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX + extraX), MathHelper.floor(this.posY + extraY) - 1, MathHelper.floor(this.posZ + extraZ)));
            if (iblockstate.getMaterial() != Material.AIR && this.world.isRemote) {
               this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, true, this.posX + extraX, this.posY + extraY, this.posZ + extraZ, motionX, motionY, motionZ, new int[]{Block.getStateId(iblockstate)});
            }
         }
      }

   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_BITE, ANIMATION_STING, ANIMATION_EGG, ANIMATION_DIGNEST};
   }
}
