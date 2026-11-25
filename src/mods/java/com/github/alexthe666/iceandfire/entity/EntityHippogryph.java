package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModKeys;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIAirTarget;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIAttackMelee;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIMate;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITarget;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIWander;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.message.MessageHippogryphArmor;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHippogryph extends EntityTameable implements IAnimatedEntity, IDragonFlute, IVillagerFear, IAnimalFear, IDropArmor {
   public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "hippogryph"));
   private static final int FLIGHT_CHANCE_PER_TICK = 1200;
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> SADDLE = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> ARMOR = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> CHESTED = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> HOVERING = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BYTE);
   private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.VARINT);
   public static Animation ANIMATION_EAT;
   public static Animation ANIMATION_SPEAK;
   public static Animation ANIMATION_SCRATCH;
   public static Animation ANIMATION_BITE;
   public EntityHippogryph.HippogryphInventory hippogryphInventory;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer roll_buffer;
   public float sitProgress;
   public float hoverProgress;
   public float flyProgress;
   public int spacebarTicks;
   public BlockPos airTarget;
   public int airBorneCounter;
   private boolean isSitting;
   private boolean isHovering;
   private boolean isFlying;
   private int animationTick;
   private Animation currentAnimation;
   private int flyTicks;
   private int hoverTicks;
   private boolean hasChestVarChanged = false;
   public BlockPos homePos;
   public boolean hasHomePosition = false;

   public EntityHippogryph(World worldIn) {
      super(worldIn);
      ANIMATION_EAT = Animation.create(25);
      ANIMATION_SPEAK = Animation.create(15);
      ANIMATION_SCRATCH = Animation.create(25);
      ANIMATION_BITE = Animation.create(20);
      this.initHippogryphInv();
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.roll_buffer = new IFChainBuffer();
      }

      this.setSize(1.7F, 1.6F);
      this.stepHeight = 1.0F;
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 7 + this.world.rand.nextInt(10);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, this.aiSit = new EntityAISit(this));
      this.tasks.addTask(3, new HippogryphAIAttackMelee(this, 1.5D, true));
      this.tasks.addTask(4, new HippogryphAIMate(this, 1.0D));
      this.tasks.addTask(5, new EntityAITempt(this, 1.0D, Items.RABBIT, false));
      this.tasks.addTask(5, new EntityAITempt(this, 1.0D, Items.COOKED_RABBIT, false));
      this.tasks.addTask(6, new HippogryphAIAirTarget(this));
      this.tasks.addTask(7, new HippogryphAIWander(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLivingBase.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(4, new HippogryphAITargetItems(this, false));
      this.targetTasks.addTask(5, new HippogryphAITarget(this, EntityLivingBase.class, false, new Predicate<Entity>() {
         public boolean apply(@Nullable Entity entity) {
            return entity instanceof EntityLivingBase && !(entity instanceof AbstractHorse) && DragonUtils.isAlive((EntityLivingBase)entity);
         }
      }));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VARIANT, Integer.valueOf(0));
      this.dataManager.register(ARMOR, Integer.valueOf(0));
      this.dataManager.register(SADDLE, Boolean.valueOf(false));
      this.dataManager.register(CHESTED, Boolean.valueOf(false));
      this.dataManager.register(HOVERING, Boolean.valueOf(false));
      this.dataManager.register(FLYING, Boolean.valueOf(false));
      this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte)0));
      this.dataManager.register(COMMAND, Integer.valueOf(0));
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   public boolean canBeSteered() {
      return true;
   }

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
      if (this.isPassenger(passenger)) {
         this.renderYawOffset = this.rotationYaw;
         this.rotationYaw = passenger.rotationYaw;
      }

      passenger.setPosition(this.posX, this.posY + 1.0499999523162842D, this.posZ);
   }

   private void initHippogryphInv() {
      EntityHippogryph.HippogryphInventory animalchest = this.hippogryphInventory;
      this.hippogryphInventory = new EntityHippogryph.HippogryphInventory("hippogryphInventory", 18, this);
      this.hippogryphInventory.setCustomName(this.getName());
      if (animalchest != null) {
         int i = Math.min(animalchest.getSizeInventory(), this.hippogryphInventory.getSizeInventory());

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = animalchest.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               this.hippogryphInventory.setInventorySlotContents(j, itemstack.copy());
            }
         }

         if (this.world.isRemote) {
            ItemStack saddle = animalchest.getStackInSlot(0);
            ItemStack chest = animalchest.getStackInSlot(1);
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, this.getIntFromArmor(animalchest.getStackInSlot(2))));
         }
      }

   }

   @Nullable
   public Entity getControllingPassenger() {
      for(Entity passenger : this.getPassengers()) {
         if (passenger instanceof EntityPlayer && this.getAttackTarget() != passenger) {
            EntityPlayer player = (EntityPlayer)passenger;
            if (this.isTamed() && this.getOwnerId() != null && this.getOwnerId().equals(player.getUniqueID())) {
               return player;
            }
         }
      }

      return null;
   }

   public int getIntFromArmor(ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.iron_hippogryph_armor) {
         return 1;
      } else if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.gold_hippogryph_armor) {
         return 2;
      } else {
         return !stack.isEmpty() && stack.getItem() != null && stack.getItem() == ModItems.diamond_hippogryph_armor ? 3 : 0;
      }
   }

   public boolean isBlinking() {
      return this.ticksExisted % 50 > 43;
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      String s = TextFormatting.getTextWithoutFormattingCodes(player.getName());
      boolean isDev = s.equals("Alexthe666") || s.equals("Raptorfarian");
      if (this.isTamed() && this.isOwner(player)) {
         if (itemstack != null && itemstack.getItem() == Items.DYE && itemstack.getMetadata() == 1 && this.getEnumVariant() != EnumHippogryphTypes.ALEX && isDev) {
            this.setEnumVariant(EnumHippogryphTypes.ALEX);
            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1.0F, 1.0F);

            for(int i = 0; i < 20; ++i) {
               this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }

            return true;
         }

         if (itemstack != null && itemstack.getItem() == Items.DYE && itemstack.getMetadata() == 7 && this.getEnumVariant() != EnumHippogryphTypes.RAPTOR && isDev) {
            this.setEnumVariant(EnumHippogryphTypes.RAPTOR);
            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1.0F, 1.0F);

            for(int i = 0; i < 20; ++i) {
               this.world.spawnParticle(EnumParticleTypes.CLOUD, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }

            return true;
         }

         if (itemstack != null && itemstack.getItem() == Items.RABBIT_STEW && this.getGrowingAge() == 0 && !this.isInLove()) {
            this.setInLove(player);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            return true;
         }

         if (itemstack != null && itemstack.getItem() == Items.STICK) {
            if (player.isSneaking()) {
               BlockPos pos = new BlockPos(this);
               this.homePos = pos;
               this.hasHomePosition = true;
               player.sendStatusMessage(new TextComponentTranslation("hippogryph.command.new_home", new Object[]{this.homePos.getX(), this.homePos.getY(), this.homePos.getZ()}), true);
               return true;
            }

            this.setCommand(this.getCommand() + 1);
            if (this.getCommand() > 1) {
               this.setCommand(0);
            }

            player.sendStatusMessage(new TextComponentTranslation("hippogryph.command." + (this.getCommand() == 1 ? "sit" : "stand"), new Object[0]), true);
            return true;
         }

         if (itemstack != null && itemstack.getItem() == Items.SPECKLED_MELON && this.getEnumVariant() != EnumHippogryphTypes.DODO) {
            this.setEnumVariant(EnumHippogryphTypes.DODO);
            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1.0F, 1.0F);

            for(int i = 0; i < 20; ++i) {
               this.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }

            return true;
         }

         if (itemstack != null && itemstack.getItem() instanceof ItemFood && ((ItemFood)itemstack.getItem()).isWolfsFavoriteMeat() && this.getHealth() < this.getMaxHealth()) {
            this.heal(5.0F);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);

            for(int i = 0; i < 3; ++i) {
               this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, 0.0D, 0.0D, 0.0D, new int[]{Item.getIdFromItem(itemstack.getItem())});
            }

            if (!player.isCreative()) {
               itemstack.shrink(1);
            }

            return true;
         }

         if (itemstack.isEmpty()) {
            if (player.isSneaking()) {
               this.openGUI(player);
               return true;
            }

            if (this.isSaddled() && !this.isChild() && !player.isRiding()) {
               player.startRiding(this, true);
               return true;
            }
         }
      }

      return super.processInteract(player, hand);
   }

   public void openGUI(EntityPlayer playerEntity) {
      if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity))) {
         playerEntity.openGui(IceAndFire.INSTANCE, 4, this.world, this.getEntityId(), 0, 0);
      }

   }

   public boolean up() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() & 1) == 1;
   }

   public boolean down() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 1 & 1) == 1;
   }

   public boolean attack() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 2 & 1) == 1;
   }

   public boolean dismount() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 3 & 1) == 1;
   }

   public void up(boolean up) {
      this.setStateField(0, up);
   }

   public void down(boolean down) {
      this.setStateField(1, down);
   }

   public void attack(boolean attack) {
      this.setStateField(2, attack);
   }

   public void dismount(boolean dismount) {
      this.setStateField(3, dismount);
   }

   private void setStateField(int i, boolean newState) {
      byte prevState = ((Byte)this.dataManager.get(CONTROL_STATE)).byteValue();
      if (newState) {
         this.dataManager.set(CONTROL_STATE, Byte.valueOf((byte)(prevState | 1 << i)));
      } else {
         this.dataManager.set(CONTROL_STATE, Byte.valueOf((byte)(prevState & ~(1 << i))));
      }

   }

   public byte getControlState() {
      return ((Byte)this.dataManager.get(CONTROL_STATE)).byteValue();
   }

   public void setControlState(byte state) {
      this.dataManager.set(CONTROL_STATE, Byte.valueOf(state));
   }

   public void setCommand(int command) {
      this.dataManager.set(COMMAND, Integer.valueOf(command));
      if (command == 1) {
         this.setSitting(true);
      } else {
         this.setSitting(false);
      }

   }

   public int getCommand() {
      return ((Integer)this.dataManager.get(COMMAND)).intValue().intValue();
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Variant", this.getVariant());
      compound.setBoolean("Chested", this.isChested());
      compound.setBoolean("Saddled", this.isSaddled());
      compound.setBoolean("Hovering", this.isHovering());
      compound.setBoolean("Flying", this.isFlying());
      compound.setInteger("Armor", this.getArmor());
      if (this.hippogryphInventory != null) {
         NBTTagList nbttaglist = new NBTTagList();

         for(int i = 0; i < this.hippogryphInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.hippogryphInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               NBTTagCompound nbttagcompound = new NBTTagCompound();
               nbttagcompound.setByte("Slot", (byte)i);
               itemstack.writeToNBT(nbttagcompound);
               nbttaglist.appendTag(nbttagcompound);
            }
         }

         compound.setTag("Items", nbttaglist);
      }

      if (this.getCustomNameTag() != null && !this.getCustomNameTag().isEmpty()) {
         compound.setString("CustomName", this.getCustomNameTag());
      }

      compound.setBoolean("HasHomePosition", this.hasHomePosition);
      if (this.homePos != null && this.hasHomePosition) {
         compound.setInteger("HomeAreaX", this.homePos.getX());
         compound.setInteger("HomeAreaY", this.homePos.getY());
         compound.setInteger("HomeAreaZ", this.homePos.getZ());
      }

      compound.setInteger("Command", this.getCommand());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setVariant(compound.getInteger("Variant"));
      this.setChested(compound.getBoolean("Chested"));
      this.setSaddled(compound.getBoolean("Saddled"));
      this.setHovering(compound.getBoolean("Hovering"));
      this.setFlying(compound.getBoolean("Flying"));
      this.setArmor(compound.getInteger("Armor"));
      if (this.hippogryphInventory != null) {
         NBTTagList nbttaglist = compound.getTagList("Items", 10);
         this.initHippogryphInv();

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            this.hippogryphInventory.setInventorySlotContents(j, new ItemStack(nbttagcompound));
         }
      } else {
         NBTTagList nbttaglist = compound.getTagList("Items", 10);
         this.initHippogryphInv();

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            this.initHippogryphInv();
            this.hippogryphInventory.setInventorySlotContents(j, new ItemStack(nbttagcompound));
            ItemStack saddle = this.hippogryphInventory.getStackInSlot(0);
            ItemStack chest = this.hippogryphInventory.getStackInSlot(1);
            if (this.world.isRemote) {
               IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
               IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
               IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, this.getIntFromArmor(this.hippogryphInventory.getStackInSlot(2))));
            }
         }
      }

      this.hasHomePosition = compound.getBoolean("HasHomePosition");
      if (this.hasHomePosition && compound.getInteger("HomeAreaX") != 0 && compound.getInteger("HomeAreaY") != 0 && compound.getInteger("HomeAreaZ") != 0) {
         this.homePos = new BlockPos(compound.getInteger("HomeAreaX"), compound.getInteger("HomeAreaY"), compound.getInteger("HomeAreaZ"));
      }

      this.setCommand(compound.getInteger("Command"));
   }

   public int getVariant() {
      return ((Integer)this.dataManager.get(VARIANT)).intValue().intValue();
   }

   public EnumHippogryphTypes getEnumVariant() {
      return EnumHippogryphTypes.values()[this.getVariant()];
   }

   public void setEnumVariant(EnumHippogryphTypes variant) {
      this.setVariant(variant.ordinal());
   }

   public void setVariant(int variant) {
      this.dataManager.set(VARIANT, Integer.valueOf(variant));
   }

   public boolean isSaddled() {
      return ((Boolean)this.dataManager.get(SADDLE)).booleanValue().booleanValue();
   }

   public void setSaddled(boolean saddle) {
      this.dataManager.set(SADDLE, Boolean.valueOf(saddle));
   }

   public boolean isChested() {
      return ((Boolean)this.dataManager.get(CHESTED)).booleanValue().booleanValue();
   }

   public void setChested(boolean chested) {
      this.dataManager.set(CHESTED, Boolean.valueOf(chested));
      this.hasChestVarChanged = true;
   }

   public boolean isSitting() {
      if (this.world.isRemote) {
         boolean isSitting = (((Byte)this.dataManager.get(TAMED)).byteValue() & 1) != 0;
         this.isSitting = isSitting;
         return isSitting;
      } else {
         return this.isSitting;
      }
   }

   public void setSitting(boolean sitting) {
      if (!this.world.isRemote) {
         this.isSitting = sitting;
      }

      byte b0 = ((Byte)this.dataManager.get(TAMED)).byteValue();
      if (sitting) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 | 1)));
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 & -2)));
      }

   }

   public boolean isHovering() {
      return this.world.isRemote ? (this.isHovering = ((Boolean)this.dataManager.get(HOVERING)).booleanValue().booleanValue()) : this.isHovering;
   }

   public void setHovering(boolean hovering) {
      this.dataManager.set(HOVERING, Boolean.valueOf(hovering));
      if (!this.world.isRemote) {
         this.isHovering = hovering.booleanValue();
      }

   }

   public boolean isFlying() {
      return this.world.isRemote ? (this.isFlying = ((Boolean)this.dataManager.get(FLYING)).booleanValue().booleanValue()) : this.isFlying;
   }

   public void setFlying(boolean flying) {
      this.dataManager.set(FLYING, Boolean.valueOf(flying));
      if (!this.world.isRemote) {
         this.isFlying = flying.booleanValue();
      }

   }

   public int getArmor() {
      return ((Integer)this.dataManager.get(ARMOR)).intValue().intValue();
   }

   public void setArmor(int armorType) {
      this.dataManager.set(ARMOR, Integer.valueOf(armorType));
      double armorValue = 0.0D;
      switch(armorType) {
      case 1:
         armorValue = 10.0D;
         break;
      case 2:
         armorValue = 20.0D;
         break;
      case 3:
         armorValue = 30.0D;
      }

      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(armorValue);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public boolean canMove() {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      if (properties != null && properties.isStone) {
         return false;
      } else {
         return !this.isSitting() && this.getControllingPassenger() == null && this.sitProgress == 0.0F;
      }
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setEnumVariant(EnumHippogryphTypes.getBiomeType(this.world.getBiome(this.getPosition())));
      return livingdata;
   }

   public boolean attackEntityFrom(DamageSource dmg, float i) {
      return this.isBeingRidden() && dmg.getTrueSource() != null && this.getControllingPassenger() != null && dmg.getTrueSource() == this.getControllingPassenger() ? false : super.attackEntityFrom(dmg, i);
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable ageable) {
      return null;
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public void playLivingSound() {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SPEAK);
      }

      super.playLivingSound();
   }

   protected void playHurtSound(DamageSource source) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SPEAK);
      }

      super.playHurtSound(source);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.HIPPOGRYPH_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return ModSounds.HIPPOGRYPH_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return ModSounds.HIPPOGRYPH_DIE;
   }

   public Animation[] getAnimations() {
      return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_EAT, ANIMATION_BITE, ANIMATION_SPEAK, ANIMATION_SCRATCH};
   }

   public boolean isRidingPlayer(EntityPlayer player) {
      return this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer && this.getControllingPassenger().getUniqueID().equals(player.getUniqueID());
   }

   public boolean shouldDismountInWater(Entity rider) {
      return false;
   }

   public boolean isDirectPathBetweenPoints(Vec3d vec1, Vec3d vec2) {
      RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec1, new Vec3d(vec2.x, vec2.y + (double)this.height * 0.5D, vec2.z), false, true, false);
      return movingobjectposition == null || movingobjectposition.typeOfHit != Type.BLOCK;
   }

   @SideOnly(Side.CLIENT)
   protected void updateClientControls() {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.isRidingPlayer(mc.player)) {
         byte previousState = this.getControlState();
         this.up(mc.gameSettings.keyBindJump.isKeyDown());
         this.down(ModKeys.dragon_down.isKeyDown());
         this.attack(ModKeys.dragon_strike.isKeyDown());
         this.dismount(mc.gameSettings.keyBindSneak.isKeyDown());
         byte controlState = this.getControlState();
         if (controlState != previousState) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.posX, this.posY, this.posZ));
         }
      }

      if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
         byte previousState = this.getControlState();
         this.dismount(mc.gameSettings.keyBindSneak.isKeyDown());
         byte controlState = this.getControlState();
         if (controlState != previousState) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.posX, this.posY, this.posZ));
         }
      }

   }

   public void travel(float strafe, float forward, float vertical) {
      if (!this.canMove() && !this.isBeingRidden()) {
         strafe = 0.0F;
         forward = 0.0F;
         super.travel(strafe, forward, vertical);
      } else {
         if (this.isBeingRidden() && this.canBeSteered()) {
            EntityLivingBase controller = (EntityLivingBase)this.getControllingPassenger();
            if (controller != null) {
               strafe = controller.moveStrafing * 0.5F;
               forward = controller.moveForward;
               if (forward <= 0.0F) {
                  forward *= 0.25F;
               }

               if (this.isFlying() || this.isHovering()) {
                  this.motionX *= 1.06D;
                  this.motionZ *= 1.06D;
               }

               this.jumpMovementFactor = 0.05F;
               this.setAIMoveSpeed(this.onGround ? (float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() : 2.0F);
               vertical = 0.0F;
               super.travel(strafe, 0.0F, forward);
               return;
            }
         }

         super.travel(strafe, forward, vertical);
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getAnimation() != ANIMATION_SCRATCH && this.getAnimation() != ANIMATION_BITE) {
         this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
         return false;
      } else {
         return true;
      }
   }

   public EntityItem createEgg(EntityHippogryph partner) {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY);
      int k = MathHelper.floor(this.posZ);
      ItemStack stack = new ItemStack(ModItems.hippogryph_egg);
      EntityItem egg = new EntityItem(this.world, (double)i, (double)j, (double)k, stack);
      return egg;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote) {
         if (this.isSitting() && (this.getCommand() != 1 || this.getControllingPassenger() != null)) {
            this.setSitting(false);
         }

         if (!this.isSitting() && this.getCommand() == 1 && this.getControllingPassenger() == null) {
            this.setSitting(true);
         }

         if (this.isSitting()) {
            this.getNavigator().clearPath();
         }

         if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }
      }

      if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 8.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         }
      }

      if (this.getAnimation() == ANIMATION_SCRATCH && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 8.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
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

      if (!this.world.isRemote && this.onGround && this.getNavigator().noPath() && this.getAttackTarget() != null && this.getAttackTarget().posY - 3.0D > this.posY && this.getRNG().nextInt(15) == 0 && this.canMove() && !this.isHovering() && !this.isFlying()) {
         this.setHovering(true);
         this.hoverTicks = 0;
         this.flyTicks = 0;
      }

      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      if (properties != null && properties.isStone) {
         this.setFlying(false);
         this.setHovering(false);
      }

      if (!this.onGround) {
         ++this.airBorneCounter;
      } else {
         this.airBorneCounter = 0;
      }

      if (this.hasChestVarChanged && this.hippogryphInventory != null && !this.isChested()) {
         for(int i = 3; i < 18; ++i) {
            if (!this.hippogryphInventory.getStackInSlot(i).isEmpty()) {
               if (!this.world.isRemote) {
                  this.entityDropItem(this.hippogryphInventory.getStackInSlot(i), 1.0F);
               }

               this.hippogryphInventory.removeStackFromSlot(i);
            }
         }

         this.hasChestVarChanged = false;
      }

      if (!this.onGround && this.airTarget != null) {
         this.setFlying(true);
      }

      if (this.isFlying() && this.ticksExisted % 40 == 0 || this.isFlying() && this.isSitting()) {
         this.setFlying(true);
      }

      if (!this.canMove() && this.getAttackTarget() != null) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      if (!this.canMove()) {
         this.getNavigator().clearPath();
      }

      if (this.getControllingPassenger() != null) {
         if (this.motionY > 0.5D && (this.isFlying() || this.isHovering())) {
            this.motionY = 0.5D;
         }

         if (this.motionY < -0.5D) {
            this.motionY = -0.5D;
         }
      } else {
         if (this.motionY > 0.8D) {
            this.motionY = 0.5D;
         }

         if (this.motionY < -0.8D) {
            this.motionY = -0.8D;
         }

         if (this.motionY > 1.0D) {
            this.motionY = 0.0D;
         }
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
      boolean sitting = this.isSitting() && !this.isHovering() && !this.isFlying();
      if (sitting && this.sitProgress < 20.0F) {
         this.sitProgress += 0.5F;
      } else if (!sitting && this.sitProgress > 0.0F) {
         this.sitProgress -= 0.5F;
      }

      boolean hovering = this.isHovering();
      if (hovering && this.hoverProgress < 20.0F) {
         this.hoverProgress += 0.5F;
      } else if (!hovering && this.hoverProgress > 0.0F) {
         this.hoverProgress -= 0.5F;
      }

      boolean flying = this.isFlying() || !this.isHovering() && this.airBorneCounter > 50;
      if (flying && this.flyProgress < 20.0F) {
         this.flyProgress += 0.5F;
      } else if (!flying && this.flyProgress > 0.0F) {
         this.flyProgress -= 0.5F;
      }

      if ((flying || hovering) && this.ticksExisted % 20 == 0 && !this.onGround) {
         this.playSound(SoundEvents.ENTITY_ENDERDRAGON_FLAP, this.getSoundVolume() * (float)(IceAndFire.CONFIG.dragonFlapNoiseDistance / 2), 0.6F + this.rand.nextFloat() * 0.6F * this.getSoundPitch());
      }

      if (this.onGround && this.doesWantToLand() && (this.isFlying() || this.isHovering())) {
         this.setFlying(false);
         this.setHovering(false);
      }

      if (this.getControllingPassenger() != null && !this.onGround && (this.isFlying() || this.isHovering())) {
         this.motionY *= 0.0D;
      }

      if (this.isHovering()) {
         if (this.isSitting()) {
            this.setHovering(false);
         }

         ++this.hoverTicks;
         if (this.doesWantToLand()) {
            this.motionY -= 0.25D;
         } else {
            if (this.getControllingPassenger() == null) {
               this.motionY += 0.08D;
            }

            if (this.hoverTicks > 40) {
               if (!this.isChild()) {
                  this.setFlying(true);
               }

               this.setHovering(false);
               this.hoverTicks = 0;
               this.flyTicks = 0;
            }
         }
      }

      if (this.isSitting()) {
         this.getNavigator().clearPath();
      }

      if (!this.isFlying() && !this.isHovering() && this.airTarget != null && this.onGround) {
         this.airTarget = null;
      }

      if (this.isFlying() && this.airTarget == null && this.onGround && this.getControllingPassenger() == null) {
         this.setFlying(false);
      }

      if (this.isFlying() && this.getAttackTarget() == null) {
         this.flyAround();
      } else if (this.getAttackTarget() != null) {
         this.flyTowardsTarget();
      }

      if (this.onGround && this.flyTicks != 0) {
         this.flyTicks = 0;
      }

      if (this.isFlying() && this.doesWantToLand()) {
         this.setFlying(false);
         this.setHovering(!this.onGround);
         if (this.onGround) {
            this.flyTicks = 0;
            this.setFlying(false);
         }
      }

      if (this.isFlying()) {
         ++this.flyTicks;
      }

      if ((this.isHovering() || this.isFlying()) && this.isSitting()) {
         this.setFlying(false);
         this.setHovering(false);
      }

      if ((properties == null || properties != null && !properties.isStone) && (!this.world.isRemote && this.getRNG().nextInt(1200) == 0 && !this.isSitting() && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && !this.isHovering() && !this.isSitting() && this.canMove() && this.onGround || this.posY < -1.0D)) {
         this.setHovering(true);
         this.hoverTicks = 0;
         this.flyTicks = 0;
      }

      if (this.getAttackTarget() != null && !this.getPassengers().isEmpty() && this.getOwner() != null && this.getPassengers().contains(this.getOwner())) {
         this.setAttackTarget((EntityLivingBase)null);
      }

   }

   public boolean doesWantToLand() {
      return this.flyTicks > 6000 || this.down() || this.flyTicks > 40 && this.flyProgress == 0.0F;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.world.isRemote) {
         this.updateClientControls();
      }

      if (this.up()) {
         if (this.airBorneCounter == 0) {
            ++this.motionY;
         }

         if (!this.isFlying() && !this.isHovering()) {
            this.spacebarTicks += 2;
         }

         if (this.isFlying() || this.isHovering()) {
            this.motionY += 0.4D;
         }
      } else if (this.dismount() && (this.isFlying() || this.isHovering())) {
         this.motionY -= 0.4D;
         this.setFlying(false);
         this.setHovering(false);
      }

      if (this.down() && (this.isFlying() || this.isHovering())) {
         this.motionY -= 0.4D;
      }

      if (!this.dismount() && (this.isFlying() || this.isHovering())) {
         this.motionY += 0.01D;
      }

      if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer) {
         EntityLivingBase target = DragonUtils.riderLookingAtEntity(this, (EntityPlayer)this.getControllingPassenger(), 3.0D);
         if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_SCRATCH) {
            this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
         }

         if (target != null) {
            target.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         }
      }

      if (this.getControllingPassenger() != null && this.getControllingPassenger().isSneaking()) {
         this.getControllingPassenger().dismountRidingEntity();
      }

      if (this.isFlying() && !this.isHovering() && this.getControllingPassenger() != null && !this.onGround && Math.max(Math.abs(this.motionZ), Math.abs(this.motionX)) < 0.10000000149011612D) {
         this.setHovering(true);
         this.setFlying(false);
      }

      if (this.isHovering() && !this.isFlying() && this.getControllingPassenger() != null && !this.onGround && Math.max(Math.abs(this.motionZ), Math.abs(this.motionX)) > 0.10000000149011612D) {
         this.setFlying(true);
         this.setHovering(false);
      }

      if (this.spacebarTicks > 0) {
         --this.spacebarTicks;
      }

      if (this.spacebarTicks > 20 && this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
         this.setHovering(true);
      }

      if (this.world.isRemote) {
         this.roll_buffer.calculateChainFlapBuffer(35.0F, 8, 6.0F, this);
      }

      if (this.getAttackTarget() != null && this.getRidingEntity() == null && this.getAttackTarget().isDead || this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityDragonBase && ((EntityDragonBase)this.getAttackTarget()).isDead) {
         this.setAttackTarget((EntityLivingBase)null);
      }

   }

   public void flyAround() {
      if (this.airTarget != null && this.isFlying()) {
         if (!this.isTargetInAir() || this.flyTicks > 6000 || !this.isFlying()) {
            this.airTarget = null;
         }

         this.flyTowardsTarget();
      }

   }

   public boolean isTargetBlocked(Vec3d target) {
      if (target != null) {
         RayTraceResult rayTrace = this.world.rayTraceBlocks(new Vec3d(this.getPosition()), target, false);
         if (rayTrace != null && rayTrace.hitVec != null) {
            BlockPos pos = new BlockPos(rayTrace.hitVec);
            if (!this.world.isAirBlock(pos)) {
               return true;
            }
         }
      }

      return false;
   }

   public void flyTowardsTarget() {
      if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d((double)this.airTarget.getX(), this.posY, (double)this.airTarget.getZ())) > 3.0F) {
         double targetX = (double)this.airTarget.getX() + 0.5D - this.posX;
         double targetY = (double)Math.min(this.airTarget.getY(), 256) + 1.0D - this.posY;
         double targetZ = (double)this.airTarget.getZ() + 0.5D - this.posZ;
         this.motionX += (Math.signum(targetX) * 0.5D - this.motionX) * 0.100000000372529D * 2.0D;
         this.motionY += (Math.signum(targetY) * 0.5D - this.motionY) * 0.100000000372529D * 2.0D;
         this.motionZ += (Math.signum(targetZ) * 0.5D - this.motionZ) * 0.100000000372529D * 2.0D;
         float angle = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0D / 3.141592653589793D) - 90.0F;
         float rotation = MathHelper.wrapDegrees(angle - this.rotationYaw);
         this.moveForward = 0.5F;
         this.prevRotationYaw = this.rotationYaw;
         this.rotationYaw += rotation;
         if (!this.isFlying()) {
            this.setFlying(true);
         }
      } else {
         this.airTarget = null;
      }

      if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d((double)this.airTarget.getX(), this.posY, (double)this.airTarget.getZ())) < 3.0F && this.doesWantToLand()) {
         this.setFlying(false);
         this.setHovering(true);
      }

   }

   protected boolean isTargetInAir() {
      return this.airTarget != null && (this.world.getBlockState(this.airTarget).getMaterial() == Material.AIR || this.world.getBlockState(this.airTarget).getMaterial() == Material.AIR);
   }

   public float getDistanceSquared(Vec3d vec3d) {
      float f = (float)(this.posX - vec3d.x);
      float f1 = (float)(this.posY - vec3d.y);
      float f2 = (float)(this.posZ - vec3d.z);
      return f * f + f1 * f1 + f2 * f2;
   }

   public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn) {
      int j = inventorySlot - 500 + 2;
      if (j >= 0 && j < this.hippogryphInventory.getSizeInventory()) {
         this.hippogryphInventory.setInventorySlotContents(j, itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (this.hippogryphInventory != null && !this.world.isRemote) {
         for(int i = 0; i < this.hippogryphInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.hippogryphInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack, 0.0F);
            }
         }
      }

   }

   public void refreshInventory() {
      ItemStack saddle = this.hippogryphInventory.getStackInSlot(0);
      ItemStack chest = this.hippogryphInventory.getStackInSlot(1);
      this.setSaddled(saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty());
      this.setChested(chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty());
      this.setArmor(this.getIntFromArmor(this.hippogryphInventory.getStackInSlot(2)));
      if (this.world.isRemote) {
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
         IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, this.getIntFromArmor(this.hippogryphInventory.getStackInSlot(2))));
      }

   }

   public void onHearFlute(EntityPlayer player) {
      if (this.isTamed() && this.isOwner(player) && (this.isFlying() || this.isHovering())) {
         this.airTarget = null;
         this.setFlying(false);
         this.setHovering(false);
      }

   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   public boolean shouldAnimalsFear(Entity entity) {
      return DragonUtils.canTameDragonAttack(this, entity);
   }

   public void dropArmor() {
      if (this.hippogryphInventory != null && !this.world.isRemote) {
         for(int i = 0; i < this.hippogryphInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.hippogryphInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               this.entityDropItem(itemstack, 0.0F);
            }
         }
      }

   }

   public class HippogryphInventory extends ContainerHorseChest {
      public HippogryphInventory(String inventoryTitle, int slotCount, EntityHippogryph hippogryph) {
         super(inventoryTitle, slotCount);
         this.addInventoryChangeListener(EntityHippogryph.this.new HippogryphInventoryListener(hippogryph));
      }
   }

   class HippogryphInventoryListener implements IInventoryChangedListener {
      EntityHippogryph hippogryph;

      public HippogryphInventoryListener(EntityHippogryph hippogryph) {
         this.hippogryph = hippogryph;
      }

      public void onInventoryChanged(IInventory invBasic) {
         this.hippogryph.refreshInventory();
      }
   }
}
