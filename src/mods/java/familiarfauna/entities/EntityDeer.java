package familiarfauna.entities;

import com.google.common.collect.Sets;
import familiarfauna.api.FFSounds;
import familiarfauna.config.ConfigurationHandler;
import familiarfauna.init.ModLootTable;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityDeer extends EntityAnimal implements IAnimals {
   private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(new Item[]{Items.APPLE});
   private static final DataParameter<Byte> TYPE = EntityDataManager.createKey(EntityDeer.class, DataSerializers.BYTE);

   public EntityDeer(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 1.6F);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(TYPE, Byte.valueOf((byte)0));
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 2.0D));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(3, new EntityAITempt(this, 0.8D, true, TEMPTATION_ITEMS));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(6, new EntityDeer.AIAvoidEntity(this, EntityPlayer.class, 5.0F, 2.0D, 2.5D));
      this.tasks.addTask(7, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(9.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return FFSounds.deer_hurt;
   }

   protected SoundEvent getDeathSound() {
      return FFSounds.deer_dead;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(FFSounds.deer_step, 0.15F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("DeerType", this.getDeerType());
   }

   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setDeerType(tagCompund.getInteger("DeerType"));
   }

   public EntityDeer createChild(EntityAgeable ageable) {
      return new EntityDeer(this.world);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return TEMPTATION_ITEMS.contains(stack.getItem());
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return ModLootTable.DEER_LOOT;
   }

   protected boolean canMate() {
      return !this.isChild() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
   }

   public boolean canMateWith(EntityAnimal otherAnimal) {
      if (otherAnimal instanceof EntityDeer) {
         EntityDeer deer = (EntityDeer)otherAnimal;
         if (this.getDeerType() != deer.getDeerType()) {
            return this.canMate() && deer.canMate();
         }
      }

      return false;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote && !ConfigurationHandler.deerEnable) {
         this.setDead();
      }

   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      int i = this.rand.nextInt(2);
      int ii = this.rand.nextInt(5);
      boolean flag = false;
      if (livingdata instanceof EntityDeer.DeerTypeData) {
         i = ((EntityDeer.DeerTypeData)livingdata).typeData;
         flag = true;
      } else {
         livingdata = new EntityDeer.DeerTypeData(i);
      }

      this.setDeerType(i);
      if (ii == 0) {
         this.setGrowingAge(-24000);
      }

      return livingdata;
   }

   public int getDeerType() {
      return ((Byte)this.dataManager.get(TYPE)).byteValue();
   }

   public void setDeerType(int deerTypeId) {
      this.dataManager.set(TYPE, Byte.valueOf((byte)deerTypeId));
   }

   static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      private final EntityDeer deer;

      public AIAvoidEntity(EntityDeer deer, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_) {
         super(deer, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
         this.deer = deer;
      }
   }

   public static class DeerTypeData implements IEntityLivingData {
      public int typeData;

      public DeerTypeData(int type) {
         this.typeData = type;
      }
   }
}
