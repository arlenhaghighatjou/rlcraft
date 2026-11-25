package dev.satyrn.wolfarmor.mixin;

import com.google.common.collect.Multimap;
import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.advancements.WolfArmorTrigger;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.api.item.IItemWolfArmor;
import dev.satyrn.wolfarmor.api.util.CreatureFoodStats;
import dev.satyrn.wolfarmor.api.util.Criteria;
import dev.satyrn.wolfarmor.api.util.DataHelper;
import dev.satyrn.wolfarmor.api.util.Items;
import dev.satyrn.wolfarmor.common.network.MessageBase;
import dev.satyrn.wolfarmor.common.network.packets.RemovePotionEffectMessage;
import dev.satyrn.wolfarmor.common.network.packets.UpdateFoodStatsMessage;
import dev.satyrn.wolfarmor.common.network.packets.UpdatePotionEffectMessage;
import dev.satyrn.wolfarmor.config.WolfArmorConfig;
import dev.satyrn.wolfarmor.item.ItemWolfArmor;
import dev.satyrn.wolfarmor.mixin.accessors.EntityLivingAccessor;
import dev.satyrn.wolfarmor.util.OreDictHelper;
import dev.satyrn.wolfarmor.util.WolfFoodStatsLevel;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityWolf.class})
public abstract class MixinEntityWolf extends MixinEntityTameable implements IArmoredWolf {
   private int entityXpCooldown;
   private ContainerHorseChest inventory;
   private CreatureFoodStats foodStats;
   private WolfArmorConfig config;
   private SimpleNetworkWrapper connection;

   public MixinEntityWolf() {
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void onConstructed(CallbackInfo ci) {
      this.config = WolfArmorMod.getConfig();
      this.connection = WolfArmorMod.getNetworkChannel();
      float minimumHealth = this.config.getCanStarve() ? 0.0F : 8.0F;
      boolean damageEntity = this.config.getFoodStatsLevel() == WolfFoodStatsLevel.FULL;
      this.foodStats = new CreatureFoodStats(minimumHealth, damageEntity);
      this.inventoryInit();
   }

   @Nonnull
   public CreatureFoodStats getFoodStats() {
      return this.foodStats;
   }

   public void addExhaustion(float exhaustion) {
      if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.foodStats != null && !this.getEntityWorld().isRemote) {
         this.foodStats.addExhaustion(exhaustion);
      }

   }

   public void onInventoryChanged(@Nonnull IInventory invBasic) {
      if (!this.getEntityWorld().isRemote) {
         ItemStack armorItemStack = invBasic.getStackInSlot(0);
         this.setArmorItemStack(armorItemStack);
         this.applyArmorModifiers(this.getEntityAttribute(SharedMonsterAttributes.ARMOR), armorItemStack);
         this.applyArmorModifiers(this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS), armorItemStack);
      }
   }

   public Item getChestType() {
      ItemStack chestType = (ItemStack)this.dataManager.get(DataHelper.CHEST_TYPE);
      return chestType.isEmpty() ? null : chestType.getItem();
   }

   public void setChestType(@Nonnull ItemStack stack) {
      ItemStack chestType = stack.copy();
      if (!chestType.isEmpty()) {
         chestType.setCount(1);
      }

      this.dataManager.set(DataHelper.CHEST_TYPE, chestType);
   }

   public boolean getHasChest() {
      return ((Boolean)this.dataManager.get(DataHelper.HAS_CHEST)).booleanValue();
   }

   public void setHasChest(boolean hasChest) {
      this.dataManager.set(DataHelper.HAS_CHEST, Boolean.valueOf(hasChest));
      if (!hasChest) {
         this.dataManager.set(DataHelper.CHEST_TYPE, ItemStack.EMPTY);
      }

   }

   public int getMaxSizeInventory() {
      return 1 + this.config.getChestSize().getColumns() * this.config.getChestSize().getRows();
   }

   @Nonnull
   public InventoryBasic getInventory() {
      return this.inventory;
   }

   @Nonnull
   public ItemStack getArmorItemStack() {
      return (ItemStack)this.dataManager.get(DataHelper.ARMOR_ITEM);
   }

   public void setArmorItemStack(@Nonnull ItemStack armorItemStack) {
      if (armorItemStack != this.dataManager.get(DataHelper.ARMOR_ITEM)) {
         this.dataManager.set(DataHelper.ARMOR_ITEM, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.HEAD, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.CHEST, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.LEGS, armorItemStack);
         this.setItemStackToSlot(EntityEquipmentSlot.FEET, armorItemStack);
         Arrays.fill(((EntityLivingAccessor)this).getInventoryArmorDropChances(), 0.0F);
         Arrays.fill(((EntityLivingAccessor)this).getInventoryHandsDropChances(), 0.0F);
      }

   }

   public boolean getHasArmor() {
      return !this.getArmorItemStack().isEmpty();
   }

   public boolean canEquipItem(@Nonnull ItemStack armorItemStack) {
      return armorItemStack.isEmpty() || !this.getHasArmor() && armorItemStack.getItem() instanceof ItemWolfArmor;
   }

   public void equipArmor(@Nonnull ItemStack armorItemStack) {
      if (this.canEquipItem(armorItemStack)) {
         this.inventory.setInventorySlotContents(0, armorItemStack);
      }

   }

   public void setInventoryItem(int index, @Nonnull ItemStack itemStack) {
      if (index >= 0 && index < this.inventory.getSizeInventory()) {
         this.inventory.setInventorySlotContents(index, itemStack);
      } else {
         WolfArmorMod.getLogger().log(Level.ERROR, String.format("Invalid slot: %d", index));
      }

   }

   public void dropEquipment() {
      if (this.getHasArmor()) {
         ItemStack armorItemStack = this.getArmorItemStack();
         if (!this.getEntityWorld().isRemote) {
            this.entityDropItem(armorItemStack, 0.0F);
         }

         this.equipArmor(ItemStack.EMPTY);
      }

   }

   public void dropChest() {
      if (this.getHasChest()) {
         if (!this.getEntityWorld().isRemote) {
            Item chestItem = this.getChestType();
            this.entityDropItem(new ItemStack(chestItem == null ? Item.getItemFromBlock(Blocks.CHEST) : chestItem, 1), 0.0F);
         }

         this.setHasChest(false);
      }

   }

   public void dropInventoryContents() {
      for(int slotIndex = 1; slotIndex <= this.getMaxSizeInventory() - 1; ++slotIndex) {
         ItemStack stackInSlot = this.inventory.getStackInSlot(slotIndex);
         if (!stackInSlot.isEmpty()) {
            if (!this.getEntityWorld().isRemote) {
               this.entityDropItem(stackInSlot, 0.0F);
            }

            this.setInventoryItem(slotIndex, ItemStack.EMPTY);
         }
      }

   }

   @Shadow
   public abstract boolean isAngry();

   @Intrinsic
   protected void damageArmor(float damage) {
      super.damageArmor(damage);
      if (this.getHasArmor()) {
         ItemStack stackInSlot = this.inventory.getStackInSlot(0);
         if (!stackInSlot.isEmpty()) {
            stackInSlot.damageItem((int)Math.ceil((double)damage), (EntityLivingBase)this);
            if (stackInSlot.getCount() == 0) {
               ItemStack particleStack = stackInSlot.copy();
               particleStack.setCount(1);
               this.equipArmor(ItemStack.EMPTY);
               this.renderBrokenItemStack(particleStack);
            }
         }
      }

   }

   @Intrinsic
   public void travel(float strafe, float vertical, float forward) {
      double initX = this.posX;
      double initY = this.posY;
      double initZ = this.posZ;
      super.travel(strafe, vertical, forward);
      this.addMovementStat(this.posX - initX, this.posY - initY, this.posZ - initZ);
   }

   @Intrinsic
   protected void onNewPotionEffect(PotionEffect effect) {
      super.onNewPotionEffect(effect);
      if (!this.getEntityWorld().isRemote) {
         this.dispatchPotionEffectMessage(effect, false);
      }

   }

   @Intrinsic
   protected void onChangedPotionEffect(PotionEffect effect, boolean sendUpdate) {
      super.onChangedPotionEffect(effect, sendUpdate);
      if (sendUpdate && !this.getEntityWorld().isRemote) {
         this.dispatchPotionEffectMessage(effect, false);
      }

   }

   @Intrinsic
   protected void onFinishedPotionEffect(PotionEffect effect) {
      super.onFinishedPotionEffect(effect);
      if (!this.getEntityWorld().isRemote) {
         this.dispatchPotionEffectMessage(effect, true);
      }

   }

   @Intrinsic
   public void onDeath(DamageSource damageSource) {
      this.dropEquipment();
      this.dropChest();
      this.dropInventoryContents();
      super.onDeath(damageSource);
   }

   @Inject(
      method = {"attackEntityAsMob"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAttackEntityAsMob(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
      float damage = (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
      int knockback = 0;
      if (entityIn instanceof EntityLivingBase) {
         damage += EnchantmentHelper.getModifierForCreature(this.getArmorItemStack(), ((EntityLivingBase)entityIn).getCreatureAttribute());
         knockback += EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.KNOCKBACK, (EntityLivingBase)this);
      }

      boolean atkFlag = entityIn.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), damage);
      if (atkFlag) {
         if (knockback > 0) {
            ((EntityLivingBase)entityIn).knockBack((Entity)this, (float)knockback * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
            this.motionX *= 0.6000000238418579D;
            this.motionZ *= 0.6000000238418579D;
         }

         int fireAspect = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FIRE_ASPECT, (EntityLivingBase)this);
         if (fireAspect > 0) {
            entityIn.setFire(fireAspect * 4);
         }

         this.applyEnchantments((EntityLivingBase)this, entityIn);
         this.foodStats.addExhaustion(0.1F);
      }

      cir.setReturnValue(Boolean.valueOf(atkFlag));
      cir.cancel();
   }

   @Inject(
      method = {"entityInit"},
      at = {@At("RETURN")}
   )
   private void onEntityInit(CallbackInfo ci) {
      this.dataManager.register(DataHelper.HAS_CHEST, Boolean.valueOf(false));
      this.dataManager.register(DataHelper.ARMOR_ITEM, ItemStack.EMPTY);
      this.dataManager.register(DataHelper.CHEST_TYPE, ItemStack.EMPTY);
   }

   @Inject(
      method = {"onUpdate"},
      at = {@At("TAIL")}
   )
   private void onUpdate(CallbackInfo ci) {
      if (this.foodStats != null && this.isTamed() && !this.getEntityWorld().isRemote && this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED) {
         this.foodStats.onUpdate((EntityLivingBase)this);
         this.connection.sendToAll(new UpdateFoodStatsMessage(this.getEntityId(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
      }

      --this.entityXpCooldown;
   }

   @Inject(
      method = {"onLivingUpdate"},
      at = {@At("TAIL")}
   )
   private void onLivingUpdate(CallbackInfo ci) {
      if (!this.isDead && !this.getEntityWorld().isRemote) {
         ItemStack enchantedItem = EnchantmentHelper.getEnchantedItem(Enchantments.MENDING, (EntityLivingBase)this);
         if (!enchantedItem.isEmpty() && enchantedItem.isItemDamaged()) {
            AxisAlignedBB aabb;
            if (this.isRiding() && !this.getRidingEntity().isDead) {
               aabb = this.getEntityBoundingBox().union(this.getRidingEntity().getEntityBoundingBox());
            } else {
               aabb = this.getEntityBoundingBox();
            }

            for(Entity entity : this.getEntityWorld().getEntitiesWithinAABBExcludingEntity((Entity)this, aabb)) {
               if (entity instanceof EntityXPOrb && !entity.isDead) {
                  EntityXPOrb xpOrb = (EntityXPOrb)entity;
                  if (xpOrb.delayBeforeCanPickup == 0 && this.entityXpCooldown == 0) {
                     float ratio = enchantedItem.getItem().getXpRepairRatio(enchantedItem);
                     int repairAmount = Math.min(this.xpRoundAverage((float)xpOrb.xpValue * ratio), enchantedItem.getItemDamage());
                     xpOrb.xpValue -= this.xpRoundAverage((float)xpOrb.xpValue / ratio);
                     enchantedItem.setItemDamage(enchantedItem.getItemDamage() - repairAmount);
                     this.entityXpCooldown = 2;
                  }

                  if (xpOrb.xpValue == 0) {
                     xpOrb.setDead();
                  }
               }
            }
         }
      }

   }

   @Inject(
      method = {"getAmbientSound"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetAmbientSound(CallbackInfoReturnable<SoundEvent> ci) {
      if (!this.isAngry() && this.isTamed() && this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.foodStats != null) {
         if (this.getRNG().nextInt(3) == 0 && (float)this.foodStats.getFoodLevel() + this.foodStats.getSaturationLevel() <= 18.0F) {
            ci.setReturnValue(SoundEvents.ENTITY_WOLF_WHINE);
            ci.cancel();
         }

      }
   }

   @Inject(
      method = {"writeEntityToNBT"},
      at = {@At("RETURN")}
   )
   private void onWriteEntityToNBT(@Nonnull NBTTagCompound compound, CallbackInfo ci) {
      boolean hasChest = this.getHasChest();
      compound.setBoolean("HasChest", hasChest);
      if (hasChest) {
         ItemStack chestInSlot = (ItemStack)this.dataManager.get(DataHelper.CHEST_TYPE);
         NBTTagCompound chestItem = new NBTTagCompound();
         chestInSlot.writeToNBT(chestItem);
         compound.setTag("ChestType", chestItem);
         NBTTagList inventoryItems = new NBTTagList();
         IInventory inventory = this.getInventory();

         for(byte slotIndex = 0; slotIndex < inventory.getSizeInventory(); ++slotIndex) {
            ItemStack stackInSlot = inventory.getStackInSlot(slotIndex);
            if (!stackInSlot.isEmpty()) {
               NBTTagCompound stackInSlotTags = new NBTTagCompound();
               stackInSlotTags.setByte("Slot", slotIndex);
               stackInSlot.writeToNBT(stackInSlotTags);
               inventoryItems.appendTag(stackInSlotTags);
            }
         }

         compound.setTag("Inventory", inventoryItems);
      }

      if (this.getHasArmor()) {
         ItemStack armorItemStack = this.getArmorItemStack();
         if (!armorItemStack.isEmpty()) {
            NBTTagCompound armorItemTag = armorItemStack.writeToNBT(new NBTTagCompound());
            compound.setTag("ArmorItem", armorItemTag);
         } else {
            compound.removeTag("ArmorItem");
         }
      }

      if (this.foodStats != null) {
         this.foodStats.writeNBT(compound);
      }

   }

   @Inject(
      method = {"readEntityFromNBT"},
      at = {@At("RETURN")}
   )
   private void onReadEntityFromNBT(@Nonnull NBTTagCompound compound, CallbackInfo ci) {
      if (compound.hasKey("ForgeCaps")) {
         NBTTagCompound capabilities = compound.getCompoundTag("ForgeCaps");
         if (capabilities.hasKey("wolfarmor:wolf_armor")) {
            this.processLegacyDataTags(capabilities.getCompoundTag("wolfarmor:wolf_armor"));
            return;
         }
      }

      boolean hasChest = compound.getBoolean("HasChest");
      this.setHasChest(hasChest);
      if (hasChest) {
         this.inventoryInit();
         ItemStack chestTypeStack = new ItemStack(compound.getCompoundTag("ChestType"));
         this.dataManager.set(DataHelper.CHEST_TYPE, chestTypeStack);
         NBTTagList inventoryTagList = compound.getTagList("Inventory", 10);

         for(int index = 0; index < inventoryTagList.tagCount(); ++index) {
            NBTTagCompound compoundTagAt = inventoryTagList.getCompoundTagAt(index);
            byte slotIndex = compoundTagAt.getByte("Slot");
            if (slotIndex >= 0 && slotIndex < this.inventory.getSizeInventory()) {
               this.inventory.setInventorySlotContents(slotIndex, new ItemStack(compoundTagAt));
            } else {
               WolfArmorMod.getLogger().log(Level.WARN, String.format("[NBT LOAD] Discarded invalid slot information at index %d", slotIndex));
            }
         }
      }

      NBTTagCompound armorTags = compound.getCompoundTag("ArmorItem");
      if (!armorTags.hasNoTags()) {
         ItemStack armorItemStack = new ItemStack(armorTags);
         this.equipArmor(armorItemStack);
      }

      if (this.foodStats != null) {
         this.foodStats.readNBT(compound);
      }

   }

   @Inject(
      method = {"processInteract"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onProcessInteract(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
      if (player.isSneaking() && this.isOwner(player) && this.isTamed() && !this.isChild()) {
         this.openWolfInventory(player);
         cir.setReturnValue(Boolean.valueOf(true));
         cir.cancel();
      } else {
         ItemStack itemStack = player.getHeldItem(hand);
         if (!itemStack.isEmpty()) {
            if (this.config.getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED && this.foodStats != null && itemStack.getItem() instanceof ItemFood) {
               ItemFood foodItem = (ItemFood)itemStack.getItem();
               cir.setReturnValue(Boolean.valueOf(false));
               if (!foodItem.isWolfsFavoriteMeat() && !this.isBreedingItem(itemStack)) {
                  if (this.isOwner(player) && !this.world.isRemote) {
                     this.setSitting(!this.isSitting());
                     this.isJumping = false;
                     this.getNavigator().clearPath();
                     this.setAttackTarget((EntityLivingBase)null);
                  }
               } else if (foodItem.isWolfsFavoriteMeat() && this.foodStats.needFood()) {
                  this.foodStats.addStats(foodItem, itemStack);
                  this.consumeItemFromStack(player, itemStack);
                  cir.setReturnValue(Boolean.valueOf(true));
               } else if (this.isBreedingItem(itemStack)) {
                  if (this.getGrowingAge() == 0 && !this.isInLove()) {
                     this.consumeItemFromStack(player, itemStack);
                     this.setInLove(player);
                     cir.setReturnValue(Boolean.valueOf(true));
                  } else if (this.isChild()) {
                     this.consumeItemFromStack(player, itemStack);
                     this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
                     cir.setReturnValue(Boolean.valueOf(true));
                  }
               } else {
                  cir.setReturnValue(Boolean.valueOf(false));
               }

               cir.cancel();
               return;
            }

            if (this.isTamed() && this.isOwner(player) && !this.isChild()) {
               if (this.config.getChestEnabled() && !this.getHasChest() && OreDictHelper.isOre(false, "chestWood", itemStack)) {
                  if (!this.getEntityWorld().isRemote) {
                     ItemStack chestItem = itemStack.copy();
                     chestItem.setCount(1);
                     this.playEquipSound(chestItem);
                     this.setHasChest(true);
                     this.setChestType(chestItem);
                     this.consumeItemFromStack(player, itemStack);
                     if (player instanceof EntityPlayerMP) {
                        ((WolfArmorTrigger)Criteria.EQUIP_WOLF_CHEST).trigger((EntityPlayerMP)player, (EntityWolf)this);
                     }
                  }

                  cir.setReturnValue(Boolean.valueOf(true));
                  cir.cancel();
                  return;
               }

               if (Items.isValidWolfArmor(itemStack)) {
                  if (this.getArmorItemStack().isEmpty()) {
                     ItemStack toEquip = itemStack.copy();
                     toEquip.setCount(1);
                     this.equipArmor(toEquip);
                     this.consumeItemFromStack(player, itemStack);
                  } else {
                     this.openWolfInventory(player);
                  }

                  cir.setReturnValue(Boolean.valueOf(true));
                  cir.cancel();
               }
            }
         }

      }
   }

   private void applyArmorModifiers(@Nullable IAttributeInstance instance, @Nonnull ItemStack stack) {
      if (instance != null) {
         instance.removeModifier(IItemWolfArmor.ARMOR_UUID);
         if (!stack.isEmpty() && stack.getItem() instanceof ItemWolfArmor) {
            ItemWolfArmor armorItem = (ItemWolfArmor)stack.getItem();
            Multimap<String, AttributeModifier> map = armorItem.getAttributeModifiers(EntityEquipmentSlot.CHEST, stack);
            if (map.containsKey(instance.getAttribute().getName())) {
               map.get(instance.getAttribute().getName()).forEach(instance::applyModifier);
            }

         }
      }
   }

   private void addMovementStat(double deltaX, double deltaY, double deltaZ) {
      if (!this.isRiding()) {
         if (this.isInsideOfMaterial(Material.WATER)) {
            int magnitude3d = Math.round(MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0F);
            if (magnitude3d > 0) {
               this.addExhaustion(0.01F * (float)magnitude3d * 0.01F);
            }
         } else if (this.isInWater()) {
            int magnitude2d = Math.round(MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
            if (magnitude2d > 0) {
               this.addExhaustion(0.01F * (float)magnitude2d * 0.01F);
            }
         } else if (this.onGround) {
            int magnitude2d = Math.round(MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
            if (magnitude2d > 0 && this.isSprinting()) {
               this.addExhaustion(0.1F * (float)magnitude2d * 0.01F);
            }
         }
      }

   }

   private void inventoryInit() {
      ContainerHorseChest inventoryExisting = this.inventory;
      this.inventory = new ContainerHorseChest("inventory.wolfarmor.wolf", this.getMaxSizeInventory());
      String customWolfName = this.getCustomNameTag();
      if (!customWolfName.isEmpty()) {
         this.inventory.setCustomName(customWolfName);
      }

      if (inventoryExisting != null) {
         inventoryExisting.removeInventoryChangeListener(this);
         int slotCount = Math.min(this.inventory.getSizeInventory(), inventoryExisting.getSizeInventory());

         for(int slotIndex = 0; slotIndex < slotCount; ++slotIndex) {
            ItemStack stackInSlot = inventoryExisting.getStackInSlot(slotIndex);
            if (!stackInSlot.isEmpty()) {
               this.inventory.setInventorySlotContents(slotIndex, stackInSlot.copy());
            }
         }
      }

      this.inventory.addInventoryChangeListener(this);
      this.inventory.markDirty();
   }

   private void playEquipSound(@Nonnull ItemStack itemStack) {
      if (!itemStack.isEmpty()) {
         SoundEvent sound = null;
         if (OreDictHelper.isOre(false, "chestWood", itemStack)) {
            sound = SoundEvents.ENTITY_CHICKEN_EGG;
         }

         if (sound != null) {
            this.playSound(sound, 1.0F, (this.getRNG().nextFloat() - this.getRNG().nextFloat()) * 0.2F + 1.0F);
         }

      }
   }

   private void openWolfInventory(@Nonnull EntityPlayer player) {
      if (!this.getEntityWorld().isRemote) {
         this.getAISit().setSitting(true);
         player.openGui(WolfArmorMod.getInstance(), this.getEntityId(), this.getEntityWorld(), MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
      }

   }

   private void processLegacyDataTags(@Nonnull NBTTagCompound compound) {
      WolfArmorMod.getLogger().log(Level.INFO, "[NBT LOAD] Updating capable wolf to mixin wolf...");
      boolean hasChest = compound.getBoolean("hasChest");
      this.setHasChest(hasChest);
      if (hasChest) {
         this.inventoryInit();
         this.dataManager.set(DataHelper.CHEST_TYPE, new ItemStack(Blocks.CHEST, 1));
         NBTTagList inventoryTagList = compound.getTagList("inventory", 10);

         for(int index = 0; index < inventoryTagList.tagCount(); ++index) {
            NBTTagCompound compoundTagAt = inventoryTagList.getCompoundTagAt(index);
            byte slotIndex = compoundTagAt.getByte("slot");
            if (slotIndex >= 0 && slotIndex < this.inventory.getSizeInventory()) {
               this.inventory.setInventorySlotContents(slotIndex, new ItemStack(compoundTagAt));
            } else {
               WolfArmorMod.getLogger().log(Level.WARN, String.format("[NBT LOAD] Discarded invalid slot information at index %d", slotIndex));
            }
         }
      }

      NBTTagCompound armorTags = compound.getCompoundTag("armorItem");
      if (!armorTags.hasNoTags()) {
         ItemStack armorItemStack = new ItemStack(armorTags);
         this.equipArmor(armorItemStack);
      }

   }

   private void dispatchPotionEffectMessage(PotionEffect effect, boolean removeEffect) {
      MessageBase<?> message = (MessageBase<?>)(removeEffect ? new RemovePotionEffectMessage(this.getEntityId(), effect.getPotion()) : new UpdatePotionEffectMessage(this.getEntityId(), effect));
      this.connection.sendToAll(message);
   }

   private int xpRoundAverage(float value) {
      float floor = (float)MathHelper.floor(value);
      return (int)floor + (Math.random() < (double)(value - floor) ? 1 : 0);
   }

   static {
      DataHelper.HAS_CHEST = EntityDataManager.createKey(EntityWolf.class, DataSerializers.BOOLEAN);
      DataHelper.ARMOR_ITEM = EntityDataManager.createKey(EntityWolf.class, DataSerializers.ITEM_STACK);
      DataHelper.CHEST_TYPE = EntityDataManager.createKey(EntityWolf.class, DataSerializers.ITEM_STACK);
   }
}
