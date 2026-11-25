package lykrast.defiledlands.common.entity.passive;

import com.google.common.collect.Sets;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.entity.IEntityDefiled;
import lykrast.defiledlands.common.init.ModBlocks;
import lykrast.defiledlands.common.init.ModItems;
import lykrast.defiledlands.common.init.ModSounds;
import lykrast.defiledlands.common.util.Config;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBookWyrm extends EntityAnimal implements IEntityDefiled, IEntityAdditionalSpawnData {
   public static final ResourceLocation LOOT = new ResourceLocation("defiledlands", "entities/bookwyrm/normal");
   public static final ResourceLocation LOOT_GOLDEN = new ResourceLocation("defiledlands", "entities/bookwyrm/golden");
   private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(new Item[]{Items.ENCHANTED_BOOK, ModItems.foulCandy});
   private static final DataParameter<Boolean> GOLDEN = EntityDataManager.createKey(EntityBookWyrm.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> DIGEST_TIME = EntityDataManager.createKey(EntityBookWyrm.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> MAX_LEVEL = EntityDataManager.createKey(EntityBookWyrm.class, DataSerializers.VARINT);
   public int digested;
   public int digesting;
   public int digestTimer;

   public EntityBookWyrm(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 0.9F);
      this.spawnableBlock = ModBlocks.grassDefiled;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(GOLDEN, Boolean.valueOf(false));
      this.dataManager.register(DIGEST_TIME, Integer.valueOf(200));
      this.dataManager.register(MAX_LEVEL, Integer.valueOf(3));
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityBookWyrm.AIPanic(1.2D));
      this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.2D, false));
      this.tasks.addTask(4, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(5, new EntityAITempt(this, 1.2D, false, TEMPTATION_ITEMS));
      this.tasks.addTask(6, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityBookWyrm.AIHurtByTarget());
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 5.0F);
   }

   protected void updateAITasks() {
      if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         if (this.getAttackTarget() != null) {
            this.setAttackTarget((EntityLivingBase)null);
         }

         if (this.getRevengeTarget() != null) {
            this.setRevengeTarget((EntityLivingBase)null);
         }
      }

      super.updateAITasks();
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.digesting > 0 && this.digestTimer > 0) {
         --this.digestTimer;
         if (this.world.isRemote) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.25D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
         }

         if (this.digestTimer <= 0) {
            ++this.digested;
            --this.digesting;
            this.playDigestEffect(false);
            if (this.digesting > 0) {
               this.digestTimer = this.getDigestTime();
            }
         }
      }

      if (this.digested >= this.getMaxLevel()) {
         this.digested -= this.getMaxLevel();
         this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
         this.playDigestEffect(true);
         if (!this.world.isRemote) {
            List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, new ItemStack(Items.BOOK), this.getMaxLevel(), this.isGolden());
            if (!list.isEmpty()) {
               Collections.sort(list, Comparator.comparingInt((ex) -> {
                  return -e.enchantment.getMinEnchantability(e.enchantmentLevel);
               }));
               int remaining = (int)((float)this.getMaxLevel() / Config.conversionRate);

               for(EnchantmentData e : list) {
                  int value = (int)((float)e.enchantment.getMinEnchantability(e.enchantmentLevel) * Config.conversionRate);
                  if (remaining >= value) {
                     remaining -= value;
                     ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                     ItemEnchantedBook.addEnchantment(book, e);
                     this.entityDropItem(book, 0.5F);
                     if (remaining <= 0) {
                        break;
                     }
                  }
               }
            }
         }
      }

   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      if (super.processInteract(player, hand)) {
         return true;
      } else {
         ItemStack itemstack = player.getHeldItem(hand);
         if (itemstack.getItem() == Items.ENCHANTED_BOOK && !this.isChild()) {
            Map<Enchantment, Integer> list = EnchantmentHelper.getEnchantments(itemstack);
            if (list.isEmpty()) {
               return false;
            } else {
               int i = 0;

               for(Entry<Enchantment, Integer> e : list.entrySet()) {
                  i += ((Enchantment)e.getKey()).getMinEnchantability(((Integer)e.getValue()).intValue());
               }

               i = (int)((float)i * Config.conversionRate);
               if (i > 0) {
                  this.digesting += i;
                  if (this.digestTimer == 0) {
                     this.digestTimer = this.getDigestTime();
                  }

                  if (!player.capabilities.isCreativeMode) {
                     itemstack.shrink(1);
                  }

                  this.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, this.rand.nextFloat() * 0.1F + 0.9F);
                  return true;
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      }
   }

   protected void playDigestEffect(boolean success) {
      if (this.world.isRemote) {
         EnumParticleTypes enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
         if (success) {
            enumparticletypes = EnumParticleTypes.VILLAGER_HAPPY;
         }

         for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
         }

      }
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setGolden(this.world.rand.nextInt(100) == 0);
      this.setDigestTime(MathHelper.getInt(this.rand, 160, 240));
      this.setMaxLevel(MathHelper.getInt(this.rand, 3, 6));
      if (this.rand.nextInt(5) == 0) {
         this.setGrowingAge(-24000);
      }

      return livingdata;
   }

   public EntityAgeable createChild(EntityAgeable ageable) {
      EntityBookWyrm child = new EntityBookWyrm(this.world);
      if (ageable instanceof EntityBookWyrm) {
         this.setOffspringAttributes((EntityBookWyrm)ageable, child);
      }

      return child;
   }

   protected void setOffspringAttributes(EntityBookWyrm parent, EntityBookWyrm child) {
      boolean flag1 = this.isGolden();
      boolean flag2 = parent.isGolden();
      if (!flag1 && !flag2) {
         child.setGolden(this.rand.nextInt(100) == 0);
      } else {
         int i = 25;
         if (flag1 && flag2) {
            i = 10;
         }

         child.setGolden(this.rand.nextInt(i) == 0);
      }

      int j1 = this.getDigestTime();
      int j2 = parent.getDigestTime();
      int k = j1 + j2 - this.rand.nextInt((int)((double)Math.max(j1, j2) + 0.75D));
      child.setDigestTime(k / 2);
      j1 = this.getMaxLevel();
      j2 = parent.getMaxLevel();
      k = j1 + j2 + this.rand.nextInt(Math.max(j1, j2) + 1);
      child.setMaxLevel(Math.min(k / 2, 30));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("Golden", this.isGolden());
      compound.setInteger("Digest", this.getDigestTime());
      compound.setInteger("MaxLvl", this.getMaxLevel());
      compound.setInteger("Digested", this.digested);
      compound.setInteger("Digesting", this.digesting);
      compound.setInteger("DigestTimer", this.digestTimer);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setGolden(compound.getBoolean("Golden"));
      this.setDigestTime(compound.getInteger("Digest"));
      this.setMaxLevel(compound.getInteger("MaxLvl"));
      this.digested = compound.getInteger("Digested");
      this.digesting = compound.getInteger("Digesting");
      this.digestTimer = compound.getInteger("DigestTimer");
   }

   public void writeSpawnData(ByteBuf buffer) {
      buffer.writeInt(this.digested);
      buffer.writeInt(this.digesting);
      buffer.writeInt(this.digestTimer);
   }

   public void readSpawnData(ByteBuf additionalData) {
      this.digested = additionalData.readInt();
      this.digesting = additionalData.readInt();
      this.digestTimer = additionalData.readInt();
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.bookWyrmIdle;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return ModSounds.bookWyrmHurt;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.bookWyrmDeath;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
   }

   public boolean isGolden() {
      return ((Boolean)this.dataManager.get(GOLDEN)).booleanValue();
   }

   public void setGolden(boolean golden) {
      this.dataManager.set(GOLDEN, Boolean.valueOf(golden));
   }

   public int getDigestTime() {
      return ((Integer)this.dataManager.get(DIGEST_TIME)).intValue();
   }

   public void setDigestTime(int digest) {
      this.dataManager.set(DIGEST_TIME, Integer.valueOf(Math.max(digest, 1)));
   }

   public int getMaxLevel() {
      return ((Integer)this.dataManager.get(MAX_LEVEL)).intValue();
   }

   public void setMaxLevel(int maxLevel) {
      this.dataManager.set(MAX_LEVEL, Integer.valueOf(Math.max(maxLevel, 1)));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return this.isGolden() ? LOOT_GOLDEN : LOOT;
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.getItem() == ModItems.foulCandy;
   }

   class AIHurtByTarget extends EntityAIHurtByTarget {
      public AIHurtByTarget() {
         super(EntityBookWyrm.this, true, new Class[0]);
      }

      public void startExecuting() {
         super.startExecuting();
         if (EntityBookWyrm.this.isChild()) {
            this.alertOthers();
            this.resetTask();
         }

      }

      protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
         if (creatureIn instanceof EntityBookWyrm && !creatureIn.isChild()) {
            super.setEntityAttackTarget(creatureIn, entityLivingBaseIn);
         }

      }

      public boolean shouldExecute() {
         return this.taskOwner.world.getDifficulty() == EnumDifficulty.PEACEFUL ? false : super.shouldExecute();
      }
   }

   class AIPanic extends EntityAIPanic {
      public AIPanic(double speed) {
         super(EntityBookWyrm.this, speed);
      }

      public boolean shouldExecute() {
         return !EntityBookWyrm.this.isChild() && !EntityBookWyrm.this.isBurning() ? false : super.shouldExecute();
      }
   }
}
