package com.lycanitesmobs.core.entity;

import com.google.common.base.Predicate;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal;
import com.lycanitesmobs.core.entity.goals.actions.MateGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindParentGoal;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ItemDrop;
import com.lycanitesmobs.core.info.Variant;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public abstract class AgeableCreatureEntity extends BaseCreatureEntity {
   private float scaledWidth = -1.0F;
   private float scaledHeight;
   private AgeableCreatureEntity breedingTarget;
   public int growthTime = -24000;
   public boolean canGrow = true;
   public double babySpawnChance = 0.0D;
   public int loveTime;
   private int loveTimeMax = 600;
   public int breedingCooldown = 6000;
   public boolean hasBeenFarmed = false;
   protected static final DataParameter<Integer> AGE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.VARINT);
   protected static final DataParameter<Integer> LOVE = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.VARINT);

   public AgeableCreatureEntity(World world) {
      super(world);
   }

   public void setupMob() {
      if (this.babySpawnChance > 0.0D && this.rand.nextDouble() < this.babySpawnChance) {
         this.setGrowingAge(this.growthTime);
      }

      super.setupMob();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(AGE, Integer.valueOf(0));
      this.dataManager.register(LOVE, Integer.valueOf(0));
   }

   protected void initEntityAI() {
      this.tasks.addTask(this.nextTravelGoalIndex++, (new MateGoal(this)).setMateDistance(5.0D));
      super.initEntityAI();
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindParentGoal(this)).setSightCheck(false).setDistance(32.0D));
      this.tasks.addTask(this.nextTravelGoalIndex++, (new FollowParentGoal(this)).setSpeed(1.0D).setStrayDistance(3.0D));
   }

   public String getAgeName() {
      return this.isChild() ? LanguageManager.translate("entity.baby") : "";
   }

   public boolean isPersistant() {
      return this.hasBeenFarmed ? true : super.isPersistant();
   }

   public void setFarmed() {
      this.hasBeenFarmed = true;
      if (this.timeUntilPortal > this.getPortalCooldown()) {
         this.timeUntilPortal = this.getPortalCooldown();
      }

   }

   public void getRandomSubspecies() {
      if (!this.isChild()) {
         super.getRandomSubspecies();
      }
   }

   public void getRandomVariant() {
      if (!this.isChild()) {
         super.getRandomVariant();
      }
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getEntityWorld().isRemote) {
         this.setScaleForAge(this.isChild());
      } else if (this.canGrow) {
         int age = this.getGrowingAge();
         if (age < 0) {
            ++age;
            this.setGrowingAge(age);
         } else if (age > 0) {
            --age;
            this.setGrowingAge(age);
         }
      }

      if (!this.canBreed()) {
         this.loveTime = 0;
      }

      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(LOVE, Integer.valueOf(this.loveTime));
      }

      if (this.getEntityWorld().isRemote) {
         this.loveTime = this.getIntFromDataManager(LOVE);
      }

      if (this.isInLove()) {
         this.setFarmed();
         --this.loveTime;
         if (this.getEntityWorld().isRemote) {
            EnumParticleTypes particle = EnumParticleTypes.HEART;
            if (this.loveTime % 10 == 0) {
               double d0 = this.rand.nextGaussian() * 0.02D;
               double d1 = this.rand.nextGaussian() * 0.02D;
               double d2 = this.rand.nextGaussian() * 0.02D;
               this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
            }
         }
      }

   }

   protected void updateAITasks() {
      if (!this.canBreed()) {
         this.loveTime = 0;
      }

      super.updateAITasks();
   }

   public boolean canDropItem(ItemDrop itemDrop) {
      return itemDrop.adultOnly && this.isChild() ? false : super.canDropItem(itemDrop);
   }

   public HashMap<Integer, String> getInteractCommands(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      HashMap<Integer, String> commands = new HashMap();
      commands.putAll(super.getInteractCommands(player, hand, itemStack));
      if (itemStack != null) {
         if (itemStack.getItem() == this.creatureInfo.creatureType.getSpawnEgg()) {
            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Spawn Baby");
         }

         if (this.isBreedingItem(itemStack) && this.canBreed() && !this.isInLove()) {
            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Breed");
         }
      }

      return commands;
   }

   public boolean performCommand(String command, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      if (command.equals("Spawn Baby") && !this.getEntityWorld().isRemote) {
         ItemCustomSpawnEgg itemCustomSpawnEgg = (ItemCustomSpawnEgg)itemStack.getItem();
         CreatureInfo spawnEggCreatureInfo = itemCustomSpawnEgg.getCreatureInfo(itemStack);
         if (spawnEggCreatureInfo != null && spawnEggCreatureInfo.entityClass != null && spawnEggCreatureInfo.entityClass.isAssignableFrom(this.getClass())) {
            AgeableCreatureEntity baby = this.createChild(this);
            if (baby != null) {
               baby.setGrowingAge(baby.growthTime);
               baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
               baby.setFarmed();
               this.getEntityWorld().spawnEntity(baby);
               if (itemStack.hasDisplayName()) {
                  baby.setCustomNameTag(itemStack.getDisplayName());
               }

               this.consumePlayersItem(player, hand, itemStack);
            }
         }

         return true;
      } else if (command.equals("Breed") && this.breed()) {
         this.consumePlayersItem(player, hand, itemStack);
         return true;
      } else {
         return super.performCommand(command, player, hand, itemStack);
      }
   }

   public int getGrowingAge() {
      return this.getIntFromDataManager(AGE);
   }

   public void setGrowingAge(int age) {
      this.dataManager.set(AGE, Integer.valueOf(age));
      this.setScaleForAge(this.isChild());
   }

   public void addGrowth(int growth) {
      int age = this.getGrowingAge();
      age = age + growth * 20;
      if (age > 0) {
         age = 0;
      }

      this.setGrowingAge(age);
   }

   public boolean isChild() {
      return this.getGrowingAge() < 0;
   }

   public boolean shouldFollowParent() {
      return this.isChild();
   }

   public boolean shouldFindParent() {
      return this.isChild();
   }

   public void setScaleForAge(boolean age) {
      this.setAgeScale(age ? 0.5F : 1.0F);
   }

   protected final void setAgeScale(float age) {
      super.setSize(this.scaledWidth * age, this.scaledHeight * age);
   }

   protected void setSize(float width, float height) {
      boolean validWidth = this.scaledWidth > 0.0F;
      this.scaledWidth = width;
      this.scaledHeight = height;
      if (!validWidth) {
         this.setAgeScale(1.0F);
      }

      super.setSize(width, height);
   }

   public void updateSize() {
      this.setSize(this.setWidth, this.setHeight);
      this.setScaleForAge(this.isChild());
   }

   public boolean canBeTempted() {
      return !this.isInLove() && super.canBeTempted();
   }

   public AgeableCreatureEntity getBreedingTarget() {
      return this.breedingTarget;
   }

   public void setBreedingTarget(AgeableCreatureEntity target) {
      this.breedingTarget = target;
   }

   public AgeableCreatureEntity createChild(AgeableCreatureEntity partener) {
      return (AgeableCreatureEntity)this.creatureInfo.createEntity(this.getEntityWorld());
   }

   public boolean isBreedingItem(ItemStack itemStack) {
      return this.creatureInfo.isFarmable() && this.getAir() > -100 ? this.creatureInfo.canEat(itemStack) : false;
   }

   public boolean canBreedWith(AgeableCreatureEntity partner) {
      if (partner == this) {
         return false;
      } else if (partner.getClass() != this.getClass()) {
         return false;
      } else if (this.getSubspecies() != partner.getSubspecies()) {
         return false;
      } else {
         return this.isInLove() && partner.isInLove();
      }
   }

   public boolean isInLove() {
      return this.loveTime > 0;
   }

   public boolean canMate() {
      return this.isInLove();
   }

   public boolean breed() {
      if (!this.canBreed()) {
         return false;
      } else {
         this.loveTime = this.loveTimeMax;
         return true;
      }
   }

   public boolean canBreed() {
      return this.getGrowingAge() == 0;
   }

   public void procreate(AgeableCreatureEntity partner) {
      AgeableCreatureEntity baby = this.createChild(partner);
      if (baby != null) {
         this.finishBreeding();
         partner.finishBreeding();
         baby.setGrowingAge(baby.growthTime);
         baby.setSubspecies(this.getSubspeciesIndex());
         Variant babyVariant = this.getSubspecies().getChildVariant(this, this.getVariant(), partner.getVariant());
         baby.applyVariant(babyVariant != null ? babyVariant.index : 0);
         baby.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

         for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.getEntityWorld().spawnParticle(EnumParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
         }

         this.onCreateBaby(partner, baby);
         this.getEntityWorld().spawnEntity(baby);

         for(EntityPlayer player : this.getEntityWorld().getPlayers(EntityPlayer.class, (playerx) -> {
            return player != null && this.getDistance(player) <= 5.0F;
         })) {
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
            if (extendedPlayer != null) {
               extendedPlayer.studyCreature(this, CreatureManager.getInstance().config.creatureBreedKnowledge, false, true);
            }
         }
      }

   }

   public void onCreateBaby(AgeableCreatureEntity partner, AgeableCreatureEntity baby) {
   }

   public void finishBreeding() {
      this.setGrowingAge(this.breedingCooldown);
      this.setBreedingTarget((AgeableCreatureEntity)null);
      this.loveTime = 0;
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      super.readEntityFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("Age")) {
         this.setGrowingAge(nbtTagCompound.getInteger("Age"));
      } else {
         this.setGrowingAge(0);
      }

      if (nbtTagCompound.hasKey("InLove")) {
         this.loveTime = nbtTagCompound.getInteger("InLove");
      } else {
         this.loveTime = 0;
      }

      if (nbtTagCompound.hasKey("HasBeenFarmed") && nbtTagCompound.getBoolean("HasBeenFarmed")) {
         this.setFarmed();
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      nbtTagCompound.setInteger("Age", this.getGrowingAge());
      nbtTagCompound.setInteger("InLove", this.loveTime);
      nbtTagCompound.setBoolean("HasBeenFarmed", this.hasBeenFarmed);
   }
}
