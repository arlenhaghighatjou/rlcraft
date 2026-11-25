package familiarfauna.entities;

import com.google.common.collect.Sets;
import familiarfauna.api.FFSounds;
import familiarfauna.config.ConfigurationHandler;
import familiarfauna.init.ModLootTable;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityTurkey extends EntityAnimal implements IAnimals {
   private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
   private static final DataParameter<Byte> TYPE = EntityDataManager.createKey(EntityTurkey.class, DataSerializers.BYTE);
   public float wingRotation;
   public float destPos;
   public float oFlapSpeed;
   public float oFlap;
   public float wingRotDelta = 1.0F;

   public EntityTurkey(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 1.4F);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(TYPE, Byte.valueOf((byte)0));
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.0D));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(3, new EntityAITempt(this, 1.0D, false, TEMPTATION_ITEMS));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(6, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(7.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.oFlap = this.wingRotation;
      this.oFlapSpeed = this.destPos;
      this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
      this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
      if (!this.onGround && this.wingRotDelta < 1.0F) {
         this.wingRotDelta = 1.0F;
      }

      this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
      if (!this.onGround && this.motionY < 0.0D) {
         this.motionY *= 0.6D;
      }

      this.wingRotation += this.wingRotDelta * 2.0F;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   protected SoundEvent getAmbientSound() {
      return FFSounds.turkey_ambient;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return FFSounds.turkey_hurt;
   }

   protected SoundEvent getDeathSound() {
      return FFSounds.turkey_dead;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(FFSounds.turkey_step, 0.15F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("TurkeyType", this.getTurkeyType());
   }

   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setTurkeyType(tagCompund.getInteger("TurkeyType"));
   }

   public EntityTurkey createChild(EntityAgeable ageable) {
      return new EntityTurkey(this.world);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return TEMPTATION_ITEMS.contains(stack.getItem());
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return ModLootTable.TURKEY_LOOT;
   }

   protected boolean canMate() {
      return !this.isChild() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
   }

   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal instanceof EntityTurkey) {
         EntityTurkey turkey = (EntityTurkey)otherAnimal;
         if (this.getTurkeyType() != turkey.getTurkeyType()) {
            return this.canMate() && turkey.canMate();
         }
      }

      return false;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote && !ConfigurationHandler.turkeyEnable) {
         this.setDead();
      }

   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      int i = this.rand.nextInt(2);
      boolean flag = false;
      if (livingdata instanceof EntityTurkey.TurkeyTypeData) {
         i = ((EntityTurkey.TurkeyTypeData)livingdata).typeData;
         flag = true;
      } else {
         livingdata = new EntityTurkey.TurkeyTypeData(i);
      }

      this.setTurkeyType(i);
      return livingdata;
   }

   public int getTurkeyType() {
      return ((Byte)this.dataManager.get(TYPE)).byteValue();
   }

   public void setTurkeyType(int turkeyTypeId) {
      this.dataManager.set(TYPE, Byte.valueOf((byte)turkeyTypeId));
   }

   public static class TurkeyTypeData implements IEntityLivingData {
      public int typeData;

      public TurkeyTypeData(int type) {
         this.typeData = type;
      }
   }
}
