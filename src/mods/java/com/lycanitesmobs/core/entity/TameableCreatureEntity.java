package com.lycanitesmobs.core.entity;

import com.google.common.base.Optional;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.damagesources.MinionEntityDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.BegGoal;
import com.lycanitesmobs.core.entity.goals.actions.FollowOwnerGoal;
import com.lycanitesmobs.core.entity.goals.actions.StayGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyOwnerAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendOwnerGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeOwnerGoal;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.item.ChargeItem;
import com.lycanitesmobs.core.item.consumable.CreatureTreatItem;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TameableCreatureEntity extends AgeableCreatureEntity implements IEntityOwnable {
   public float hunger = this.getCreatureHungerMax();
   public float stamina = this.getStaminaMax();
   public float staminaRecovery = 0.5F;
   public float sittingGuardRange = 16.0F;
   public UUID ownerUUID;
   public boolean fromSummoningPedestal = false;
   protected static final DataParameter<Byte> TAMED = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Optional<UUID>> OWNER_ID = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   protected static final DataParameter<Float> HUNGER = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.FLOAT);
   protected static final DataParameter<Float> STAMINA = EntityDataManager.createKey(BaseCreatureEntity.class, DataSerializers.FLOAT);
   protected static final DataParameter<Integer> LOYALTY = EntityDataManager.createKey(TameableCreatureEntity.class, DataSerializers.VARINT);

   public TameableCreatureEntity(World world) {
      super(world);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(TAMED, Byte.valueOf((byte)0));
      this.dataManager.register(OWNER_ID, Optional.absent());
      this.dataManager.register(HUNGER, Float.valueOf(this.getCreatureHungerMax()));
      this.dataManager.register(STAMINA, Float.valueOf(this.getStaminaMax()));
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(this.nextReactTargetIndex++, new RevengeOwnerGoal(this));
      this.targetTasks.addTask(this.nextReactTargetIndex++, new CopyOwnerAttackTargetGoal(this));
      this.tasks.addTask(this.nextTravelGoalIndex++, new StayGoal(this));
      super.initEntityAI();
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendOwnerGoal(this));
      this.tasks.addTask(this.nextTravelGoalIndex++, (new FollowOwnerGoal(this)).setStrayDistance((double)CreatureManager.getInstance().config.petFollowDistance).setLostDistance(32.0D).setSpeed(1.0D));
      this.tasks.addTask(this.nextIdleGoalIndex++, new BegGoal(this));
   }

   public String getName() {
      if (this.isTamed() && CreatureManager.getInstance().config.ownerTags) {
         String ownerName = this.getOwnerName();
         String ownerSuffix = "'s ";
         if (ownerName != null && ownerName.length() > 0 && ("s".equals(ownerName.substring(ownerName.length() - 1)) || "S".equals(ownerName.substring(ownerName.length() - 1)))) {
            ownerSuffix = "' ";
         }

         String ownedName = ownerName + ownerSuffix + this.getFullName();
         return this.hasCustomName() ? super.getName() : ownedName;
      } else {
         return super.getName();
      }
   }

   public boolean hasCustomName() {
      return !CreatureManager.getInstance().config.ownerTags ? false : super.hasCustomName();
   }

   protected boolean canDespawn() {
      return this.isTamed() ? false : super.canDespawn();
   }

   public boolean despawnCheck() {
      if (this.getEntityWorld().isRemote) {
         return false;
      } else {
         if (this.getPetEntry() != null) {
            if (this.getPetEntry().entity != this && this.getPetEntry().entity != null) {
               return true;
            }

            if (this.getPetEntry().host == null || !this.getPetEntry().host.isEntityAlive()) {
               this.getPetEntry().saveEntityNBT();
               return true;
            }
         }

         if (this.isTamed() && !this.isTemporary) {
            return false;
         } else {
            return super.despawnCheck();
         }
      }
   }

   public boolean isPersistant() {
      return this.isTamed() || super.isPersistant();
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return this.isTamed() && player == this.getPlayerOwner() ? true : super.canBeLeashedTo(player);
   }

   public void testLeash(float distance) {
      if (this.isSitting() && distance > 10.0F) {
         this.clearLeashed(true, true);
      } else {
         super.testLeash(distance);
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.staminaUpdate();
      if (!this.getEntityWorld().isRemote && this.isPet() && this.getDistanceSq(this.getPlayerOwner()) <= 64.0D) {
         this.ownerEffects();
      }

   }

   public void staminaUpdate() {
      if (!this.getEntityWorld().isRemote) {
         if (this.stamina < this.getStaminaMax() && this.staminaRecovery >= this.getStaminaRecoveryMax() / 2.0F) {
            this.setStamina(Math.min(this.stamina + this.staminaRecovery, this.getStaminaMax()));
         }

         if (this.staminaRecovery < this.getStaminaRecoveryMax()) {
            this.staminaRecovery = Math.min(this.staminaRecovery + this.getStaminaRecoveryMax() / (float)this.getStaminaRecoveryWarmup(), this.getStaminaRecoveryMax());
         }

      }
   }

   public void ownerEffects() {
      if (!this.canBurn()) {
         this.getPlayerOwner().addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 105, 1));
      }

      for(Object possibleEffect : this.getPlayerOwner().getActivePotionEffects().toArray(new Object[0])) {
         if (possibleEffect instanceof PotionEffect) {
            PotionEffect effectInstance = (PotionEffect)possibleEffect;
            if (!this.isPotionApplicable(effectInstance)) {
               this.getPlayerOwner().removePotionEffect(effectInstance.getPotion());
            }
         }
      }

   }

   public boolean isPet() {
      if (this.isTamed() && this.getPlayerOwner() != null) {
         if (this.creatureInfo.isMountable()) {
            return false;
         } else if (this.isTemporary) {
            return false;
         } else {
            return this.getPetEntry() == null || this.isPetType("pet");
         }
      } else {
         return false;
      }
   }

   public void copyPetBehaviourTo(TameableCreatureEntity target) {
      target.setPVP(this.isPVP());
      target.setPassive(this.isPassive());
      target.setAssist(this.isAssisting());
      target.setAggressive(this.isAggressive());
      target.setSitting(this.isSitting());
      target.setFollowing(this.isFollowing());
   }

   public boolean canPerch(EntityLivingBase target) {
      if (!this.creatureInfo.isPerchable()) {
         return false;
      } else {
         return this.getPlayerOwner() == target;
      }
   }

   public HashMap<Integer, String> getInteractCommands(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      HashMap<Integer, String> commands = new HashMap();
      commands.putAll(super.getInteractCommands(player, hand, itemStack));
      if (this.canPerch(player) && !player.isSneaking() && !this.getEntityWorld().isRemote) {
         commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.MAIN.id), "Perch");
      } else if (!this.getEntityWorld().isRemote && this.isTamed() && (itemStack == null || player.isSneaking()) && player == this.getPlayerOwner()) {
         commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.MAIN.id), "GUI");
      }

      if (!this.getEntityWorld().isRemote && itemStack != null && !player.isSneaking()) {
         if (!this.isTamed() && this.isTamingItem(itemStack) && CreatureManager.getInstance().config.tamingEnabled) {
            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.IMPORTANT.id), "Tame");
         }

         if (this.isTamed() && this.isHealingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Feed");
         }

         if (this.isTamed() && !this.isTemporary && !this.isPetType("minion") && itemStack.getItem() instanceof ChargeItem) {
            if (this.isLevelingChargeItem(itemStack)) {
               commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Charge");
            } else {
               player.sendMessage(new TextComponentString(LanguageManager.translate("item.lycanitesmobs.charge.creature.invalid")));
            }
         }

         if (this.isTamed() && !this.isChild() && this.canEquip() && player == this.getPlayerOwner()) {
            String equipSlot = this.inventory.getSlotForEquipment(itemStack);
            if (equipSlot != null && (this.inventory.getEquipmentStack(equipSlot) == null || this.inventory.getEquipmentStack(equipSlot).getItem() != itemStack.getItem())) {
               commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.EQUIPPING.id), "Equip Item");
            }
         }

         if (itemStack.getItem() instanceof ItemSoulstone && this.isTamed()) {
            commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Soulstone");
         }
      }

      return commands;
   }

   public boolean performCommand(String command, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      if (command.equals("GUI")) {
         this.playTameSound();
         this.openGUI(player);
         return true;
      } else if (command.equals("Tame")) {
         this.tame(player);
         this.consumePlayersItem(player, hand, itemStack);
         return true;
      } else if (command.equals("Feed")) {
         int healAmount = 4;
         if (itemStack.getItem() instanceof ItemFood) {
            ItemFood itemFood = (ItemFood)itemStack.getItem();
            healAmount = itemFood.getHealAmount(itemStack);
         }

         this.heal((float)healAmount);
         this.playEatSound();
         if (this.getEntityWorld().isRemote) {
            EnumParticleTypes particle = EnumParticleTypes.HEART;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;

            for(int i = 0; i < 25; ++i) {
               this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
            }
         }

         this.consumePlayersItem(player, hand, itemStack);
         return true;
      } else if (command.equals("Charge")) {
         this.addExperience(this.getExperienceFromChargeItem(itemStack));
         this.consumePlayersItem(player, hand, itemStack);
         return true;
      } else if (command.equals("Equip Item")) {
         ItemStack equippedItem = this.inventory.getEquipmentStack(this.inventory.getSlotForEquipment(itemStack));
         if (equippedItem != null) {
            this.dropItem(equippedItem);
         }

         ItemStack equipStack = itemStack.copy();
         equipStack.setCount(1);
         this.inventory.setEquipmentStack(equipStack.copy());
         this.consumePlayersItem(player, hand, itemStack);
         return true;
      } else if (command.equals("Soulstone")) {
         return false;
      } else if (command.equals("Sit")) {
         this.playTameSound();
         this.setAttackTarget((EntityLivingBase)null);
         this.clearMovement();
         this.setSitting(!this.isSitting());
         this.isJumping = false;
         return true;
      } else if (command.equals("Perch")) {
         this.playTameSound();
         this.perchOnEntity(player);
         return true;
      } else {
         return super.performCommand(command, player, hand, itemStack);
      }
   }

   public boolean canNameTag(EntityPlayer player) {
      if (!this.isTamed()) {
         return super.canNameTag(player);
      } else {
         return this.isTamed() && player == this.getPlayerOwner() ? super.canNameTag(player) : false;
      }
   }

   public void performGUICommand(EntityPlayer player, byte guiCommandID) {
      if (this.petControlsEnabled()) {
         if (player == this.getOwner()) {
            if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.PVP.id) {
               this.setPVP(!this.isPVP());
            } else if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.PASSIVE.id) {
               this.setPassive(true);
            } else if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.DEFENSIVE.id) {
               this.setPassive(false);
               this.setAssist(false);
               this.setAggressive(false);
            } else if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.ASSIST.id) {
               this.setPassive(false);
               this.setAssist(true);
               this.setAggressive(false);
            } else if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.AGGRESSIVE.id) {
               this.setPassive(false);
               this.setAssist(true);
               this.setAggressive(true);
            } else if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.FOLLOW.id) {
               this.setSitting(false);
               this.setFollowing(true);
            } else if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.WANDER.id) {
               this.setSitting(false);
               this.setFollowing(false);
            } else if (guiCommandID == BaseCreatureEntity.PET_COMMAND_ID.SIT.id) {
               this.setSitting(true);
               this.setFollowing(false);
            }

            this.playTameSound();
            if (this.petEntry != null && this.petEntry.summonSet != null) {
               this.petEntry.summonSet.updateBehaviour(this);
            }

            super.performGUICommand(player, guiCommandID);
         }
      }
   }

   public Team getTeam() {
      if (this.isTamed()) {
         Entity owner = this.getOwner();
         if (owner != null) {
            return owner.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isOnSameTeam(Entity target) {
      if (target == null) {
         return false;
      } else if (this.getEntityWorld().isRemote) {
         return super.isOnSameTeam(target);
      } else if (!this.isTamed()) {
         return super.isOnSameTeam(target);
      } else if (target != this.getPlayerOwner() && target != this.getOwner()) {
         if (!(target instanceof EntityPlayer) || this.getEntityWorld().getMinecraftServer().isPVPEnabled() && this.isPVP()) {
            if (target instanceof TameableCreatureEntity) {
               TameableCreatureEntity tamedTarget = (TameableCreatureEntity)target;
               if (tamedTarget.isTamed() && (!this.getEntityWorld().getMinecraftServer().isPVPEnabled() || !this.isPVP() || tamedTarget.getPlayerOwner() == this.getPlayerOwner())) {
                  return true;
               }
            } else if (target instanceof IEntityOwnable) {
               IEntityOwnable tamedTarget = (IEntityOwnable)target;
               if (tamedTarget.getOwner() != null && (!this.getEntityWorld().getMinecraftServer().isPVPEnabled() || !this.isPVP() || tamedTarget.getOwner() == this.getOwner())) {
                  return true;
               }
            }

            if (this.getPlayerOwner() != null) {
               if (this.getPlayerOwner().getRidingEntity() == target) {
                  return true;
               } else {
                  return this.getPlayerOwner().isOnSameTeam(target);
               }
            } else if (this.getOwner() != null) {
               if (this.getOwner().getRidingEntity() == target) {
                  return true;
               } else {
                  return this.getOwner().isOnSameTeam(target);
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public boolean doRangedDamage(Entity target, EntityThrowable projectile, float damage, boolean noPierce) {
      float totalDamage = damage * ((float)this.creatureStats.getDamage() / 2.0F);
      if (target instanceof EntityLiving) {
         EntityLiving livingTarget = (EntityLiving)target;
         if (this.getPlayerOwner() != null && livingTarget.getHealth() > 0.0F && livingTarget.getHealth() - totalDamage <= 0.0F) {
            DamageSource creditSource = this.getDamageSource((EntityDamageSource)null, true);
            creditSource.setDamageBypassesArmor().setDamageIsAbsolute();
            return target.attackEntityFrom(creditSource, totalDamage);
         }
      }

      return super.doRangedDamage(target, projectile, damage, noPierce);
   }

   public boolean attackEntityAsMob(Entity target, double damageScale) {
      if (!this.isEntityAlive()) {
         return false;
      } else if (target == null) {
         return false;
      } else if (!this.canEntityBeSeen(target)) {
         return false;
      } else {
         float damage = this.getAttackDamage(damageScale);
         if (target instanceof EntityLiving) {
            EntityLiving livingTarget = (EntityLiving)target;
            if (this.getPlayerOwner() != null && livingTarget.getHealth() > 0.0F && livingTarget.getHealth() - damage <= 0.0F) {
               DamageSource creditSource = this.getDamageSource((EntityDamageSource)null, true);
               creditSource.setDamageBypassesArmor().setDamageIsAbsolute();
               return target.attackEntityFrom(creditSource, damage);
            }
         }

         return super.attackEntityAsMob(target, damageScale);
      }
   }

   public boolean canAttackClass(Class targetClass) {
      if (this.isPassive()) {
         return false;
      } else {
         return this.isTamed() ? true : super.canAttackClass(targetClass);
      }
   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      if (this.isPassive()) {
         return false;
      } else if (!this.isTamed()) {
         return super.canAttackEntity(targetEntity);
      } else if (this.getOwner() != targetEntity && this.getPlayerOwner() != targetEntity) {
         if (!this.getEntityWorld().isRemote) {
            boolean canPVP = this.getEntityWorld().getMinecraftServer().isPVPEnabled() && this.isPVP();
            if (targetEntity instanceof EntityPlayer && !canPVP) {
               return false;
            }

            if (targetEntity instanceof TameableCreatureEntity) {
               TameableCreatureEntity targetTameable = (TameableCreatureEntity)targetEntity;
               if (targetTameable.isTamed()) {
                  if (!canPVP) {
                     return false;
                  }

                  if (targetTameable.getPlayerOwner() == this.getPlayerOwner()) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public DamageSource getDamageSource(EntityDamageSource nestedDamageSource, boolean playerCredit) {
      if (this.isTamed() && this.getOwner() != null) {
         if (nestedDamageSource == null) {
            nestedDamageSource = (EntityDamageSource)DamageSource.causeMobDamage(this);
         }

         return new MinionEntityDamageSource(nestedDamageSource, this.getOwner(), playerCredit);
      } else {
         return super.getDamageSource(nestedDamageSource, playerCredit);
      }
   }

   public boolean attackEntityFrom(DamageSource damageSrc, float damageAmount) {
      if (this.isEntityInvulnerable(damageSrc)) {
         return false;
      } else {
         if (!this.isPassive()) {
            this.setSitting(false);
         }

         Entity entity = damageSrc.getImmediateSource();
         if (entity instanceof EntityThrowable) {
            entity = ((EntityThrowable)entity).getThrower();
         }

         return this.isTamed() && this.getOwner() == entity ? false : super.attackEntityFrom(damageSrc, damageAmount);
      }
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return "inWall".equals(type) && this.isTamed() ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean isDamageEntityApplicable(Entity entity) {
      if (this.isTamed()) {
         if (entity instanceof EntityPlayer && this.getEntityWorld().getMinecraftServer() != null && !this.getEntityWorld().getMinecraftServer().isPVPEnabled()) {
            return false;
         }

         if (entity == this.getPlayerOwner()) {
            return false;
         }
      }

      return super.isDamageEntityApplicable(entity);
   }

   public void setOwnerId(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
      this.dataManager.set(OWNER_ID, Optional.fromNullable(ownerUUID));
      this.setTamed(ownerUUID != null);
   }

   public Entity getOwner() {
      UUID uuid = this.getOwnerId();
      return (Entity)(uuid == null ? super.getOwner() : this.getEntityWorld().getPlayerEntityByUUID(uuid));
   }

   public void setPlayerOwner(EntityPlayer player) {
      this.setOwnerId(player.getUniqueID());
   }

   public UUID getOwnerId() {
      if (this.getEntityWorld().isRemote) {
         try {
            return (UUID)this.getUUIDFromDataManager(OWNER_ID).orNull();
         } catch (Exception var2) {
            ;
         }
      }

      return this.ownerUUID;
   }

   public EntityPlayer getPlayerOwner() {
      if (this.getEntityWorld().isRemote) {
         Entity owner = this.getOwner();
         return owner instanceof EntityPlayer ? (EntityPlayer)owner : null;
      } else {
         return this.ownerUUID == null ? null : this.getEntityWorld().getPlayerEntityByUUID(this.ownerUUID);
      }
   }

   public String getOwnerName() {
      Entity owner = this.getOwner();
      if (owner != null) {
         return owner instanceof EntityPlayer ? ((EntityPlayer)owner).getDisplayNameString() : owner.getName();
      } else {
         return "";
      }
   }

   public boolean isTamed() {
      try {
         return (this.getByteFromDataManager(TAMED) & TameableCreatureEntity.TAMED_ID.IS_TAMED.id) != 0;
      } catch (Exception var2) {
         return false;
      }
   }

   public void setTamed(boolean isTamed) {
      byte tamed = this.getByteFromDataManager(TAMED);
      if (isTamed) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamed | TameableCreatureEntity.TAMED_ID.IS_TAMED.id)));
         this.spawnEventType = "";
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamed - (tamed & TameableCreatureEntity.TAMED_ID.IS_TAMED.id))));
      }

      this.setAlwaysRenderNameTag(isTamed);
   }

   public boolean isTamingItem(ItemStack itemstack) {
      if (!itemstack.isEmpty() && this.creatureInfo.creatureType != null && !this.isBoss()) {
         if (itemstack.getItem() instanceof CreatureTreatItem) {
            CreatureTreatItem itemTreat = (CreatureTreatItem)itemstack.getItem();
            if (itemTreat.getCreatureType() == this.creatureInfo.creatureType) {
               return this.creatureInfo.isTameable();
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean tame(EntityPlayer player) {
      if (!this.getEntityWorld().isRemote && !this.isRareVariant() && !this.isBoss()) {
         ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
         if (extendedPlayer == null) {
            return this.isTamed();
         } else {
            extendedPlayer.studyCreature(this, CreatureManager.getInstance().config.creatureTreatKnowledge, false, true);
            if (this.isTamed()) {
               return true;
            } else {
               CreatureKnowledge creatureKnowledge = extendedPlayer.getBeastiary().getCreatureKnowledge(this.creatureInfo.getName());
               if (creatureKnowledge != null && creatureKnowledge.rank >= 2) {
                  CreatureRelationshipEntry relationshipEntry = this.relationships.getOrCreateEntry(player);
                  int reputationAmount = 50 + this.getRNG().nextInt(50);
                  relationshipEntry.increaseReputation(reputationAmount);
                  if (this.creatureInfo.isTameable() && relationshipEntry.getReputation() >= this.creatureInfo.getTamingReputation()) {
                     this.setPlayerOwner(player);
                     this.onTamedByPlayer();
                     this.unsetTemporary();
                     String tameMessage = LanguageManager.translate("message.pet.tamed");
                     tameMessage = tameMessage.replace("%creature%", this.getSpeciesName());
                     player.sendMessage(new TextComponentString(tameMessage));
                     this.playTameEffect(this.isTamed());
                     player.addStat(ObjectManager.getStat(this.creatureInfo.getName() + ".tame"), 1);
                     if (this.timeUntilPortal > this.getPortalCooldown()) {
                        this.timeUntilPortal = this.getPortalCooldown();
                     }
                  }

                  this.playTameEffect(this.isTamed());
                  return this.isTamed();
               } else {
                  return this.isTamed();
               }
            }
         }
      } else {
         return super.isTamed();
      }
   }

   public void onTamedByPlayer() {
      this.refreshAttributes();
      this.clearMovement();
      this.setAttackTarget((EntityLivingBase)null);
      this.setSitting(false);
      this.setFollowing(true);
      this.setPassive(false);
      this.setAggressive(false);
      this.setPVP(true);
      this.playTameSound();
      this.spawnEventType = "";
   }

   public void onCreateBaby(AgeableCreatureEntity partner, AgeableCreatureEntity baby) {
      if (this.isTamed() && this.getOwner() instanceof EntityPlayer && partner instanceof TameableCreatureEntity && baby instanceof TameableCreatureEntity) {
         TameableCreatureEntity partnerTameable = (TameableCreatureEntity)partner;
         TameableCreatureEntity babyTameable = (TameableCreatureEntity)baby;
         if (partnerTameable.getPlayerOwner() == this.getPlayerOwner()) {
            babyTameable.setPlayerOwner((EntityPlayer)this.getOwner());
         }
      }

      super.onCreateBaby(partner, baby);
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte status) {
      if (status == 7) {
         this.playTameEffect(true);
      } else if (status == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(status);
      }

   }

   public boolean isHealingItem(ItemStack itemStack) {
      return this.creatureInfo.canEat(itemStack);
   }

   public boolean isLevelingChargeItem(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ChargeItem) {
         ChargeItem chargeItem = (ChargeItem)itemStack.getItem();

         for(ElementInfo elementInfo : this.getElements()) {
            if (chargeItem.getElements().contains(elementInfo)) {
               return true;
            }
         }
      }

      return false;
   }

   public int getExperienceFromChargeItem(ItemStack itemStack) {
      int experience = 0;
      if (itemStack.getItem() instanceof ChargeItem) {
         ChargeItem chargeItem = (ChargeItem)itemStack.getItem();

         for(ElementInfo elementInfo : this.getElements()) {
            if (chargeItem.getElements().contains(elementInfo)) {
               experience += ChargeItem.CHARGE_EXPERIENCE;
            }
         }
      }

      return experience;
   }

   public boolean isBreedingItem(ItemStack itemStack) {
      return this.creatureInfo.isFarmable() || this.isTamed() && !this.isPetType("minion") && !this.isPetType("familiar") && this.getHealth() >= this.getMaxHealth() ? super.isBreedingItem(itemStack) : false;
   }

   public void summonMinion(EntityLivingBase minion, double angle, double distance) {
      if (this.getPlayerOwner() != null && minion instanceof TameableCreatureEntity) {
         ((TameableCreatureEntity)minion).setPlayerOwner(this.getPlayerOwner());
         this.copyPetBehaviourTo((TameableCreatureEntity)minion);
      }

      super.summonMinion(minion, angle, distance);
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public byte behaviourBitMask() {
      return this.getByteFromDataManager(TAMED);
   }

   public boolean isSitting() {
      if (!this.isTamed()) {
         return false;
      } else {
         return (this.getByteFromDataManager(TAMED) & TameableCreatureEntity.TAMED_ID.MOVE_SIT.id) != 0;
      }
   }

   public void setSitting(boolean set) {
      if (this.isTamed()) {
         if (!this.petControlsEnabled()) {
            set = false;
         }

         byte tamedStatus = this.getByteFromDataManager(TAMED);
         if (set) {
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TameableCreatureEntity.TAMED_ID.MOVE_SIT.id)));
            this.setHome((int)this.posX, (int)this.posY, (int)this.posZ, this.sittingGuardRange);
         } else {
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TameableCreatureEntity.TAMED_ID.MOVE_SIT.id))));
            this.detachHome();
         }

      }
   }

   public boolean isFollowing() {
      if (!this.isTamed()) {
         return false;
      } else {
         return (this.getByteFromDataManager(TAMED) & TameableCreatureEntity.TAMED_ID.MOVE_FOLLOW.id) != 0;
      }
   }

   public void setFollowing(boolean set) {
      if (!this.petControlsEnabled()) {
         set = false;
      }

      byte tamedStatus = this.getByteFromDataManager(TAMED);
      if (set) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TameableCreatureEntity.TAMED_ID.MOVE_FOLLOW.id)));
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TameableCreatureEntity.TAMED_ID.MOVE_FOLLOW.id))));
      }

   }

   public boolean isPassive() {
      if (!this.isTamed()) {
         return false;
      } else {
         return (this.getByteFromDataManager(TAMED) & TameableCreatureEntity.TAMED_ID.STANCE_PASSIVE.id) != 0;
      }
   }

   public void setPassive(boolean set) {
      if (!this.petControlsEnabled()) {
         set = false;
      }

      byte tamedStatus = this.getByteFromDataManager(TAMED);
      if (set) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TameableCreatureEntity.TAMED_ID.STANCE_PASSIVE.id)));
         this.setAttackTarget((EntityLivingBase)null);
         this.setStealth(0.0F);
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TameableCreatureEntity.TAMED_ID.STANCE_PASSIVE.id))));
      }

   }

   public boolean isAggressive() {
      if (!this.isTamed()) {
         return super.isAggressive();
      } else {
         return (this.getByteFromDataManager(TAMED) & TameableCreatureEntity.TAMED_ID.STANCE_AGGRESSIVE.id) != 0;
      }
   }

   public void setAggressive(boolean set) {
      if (!this.petControlsEnabled()) {
         set = false;
      }

      byte tamedStatus = this.getByteFromDataManager(TAMED);
      if (set) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TameableCreatureEntity.TAMED_ID.STANCE_AGGRESSIVE.id)));
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TameableCreatureEntity.TAMED_ID.STANCE_AGGRESSIVE.id))));
      }

   }

   public boolean isAssisting() {
      if (!this.isTamed()) {
         return false;
      } else {
         return (this.getByteFromDataManager(TAMED) & TameableCreatureEntity.TAMED_ID.STANCE_ASSIST.id) != 0;
      }
   }

   public void setAssist(boolean set) {
      if (!this.petControlsEnabled()) {
         set = true;
      }

      byte tamedStatus = this.getByteFromDataManager(TAMED);
      if (set) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TameableCreatureEntity.TAMED_ID.STANCE_ASSIST.id)));
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TameableCreatureEntity.TAMED_ID.STANCE_ASSIST.id))));
      }

   }

   public boolean isPVP() {
      return (this.getByteFromDataManager(TAMED) & TameableCreatureEntity.TAMED_ID.PVP.id) != 0;
   }

   public void setPVP(boolean set) {
      if (!this.petControlsEnabled()) {
         set = false;
      }

      byte tamedStatus = this.getByteFromDataManager(TAMED);
      if (set) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TameableCreatureEntity.TAMED_ID.PVP.id)));
      } else {
         this.setAttackTarget((EntityLivingBase)null);
         this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TameableCreatureEntity.TAMED_ID.PVP.id))));
      }

   }

   public float getCreatureHunger() {
      if (this.getEntityWorld() == null) {
         return this.getCreatureHungerMax();
      } else if (!this.getEntityWorld().isRemote) {
         return this.hunger;
      } else {
         try {
            return this.getFloatFromDataManager(HUNGER);
         } catch (Exception var2) {
            return 0.0F;
         }
      }
   }

   public void setCreatureHunger(float setHunger) {
      this.hunger = setHunger;
   }

   public float getCreatureHungerMax() {
      return 20.0F;
   }

   public float getStamina() {
      if (this.getEntityWorld() != null && this.getEntityWorld().isRemote) {
         try {
            this.stamina = this.getFloatFromDataManager(STAMINA);
         } catch (Exception var2) {
            ;
         }
      }

      return this.stamina;
   }

   public void setStamina(float setStamina) {
      this.stamina = setStamina;
      if (this.getEntityWorld() != null && !this.getEntityWorld().isRemote) {
         this.dataManager.set(STAMINA, Float.valueOf(setStamina));
      }

   }

   public float getStaminaMax() {
      return 100.0F;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 200;
   }

   public float getStaminaCost() {
      return 1.0F;
   }

   public void applyStaminaCost() {
      float newStamina = this.getStamina() - this.getStaminaCost();
      if (newStamina < 0.0F) {
         newStamina = 0.0F;
      }

      this.setStamina(newStamina);
      this.staminaRecovery = 0.0F;
   }

   public float getStaminaPercent() {
      return this.getStamina() / this.getStaminaMax();
   }

   public String getStaminaType() {
      return "energy";
   }

   public AgeableCreatureEntity createChild(AgeableCreatureEntity baby) {
      AgeableCreatureEntity spawnedBaby = super.createChild(baby);
      UUID ownerId = this.getOwnerId();
      if (ownerId != null && spawnedBaby != null && spawnedBaby instanceof TameableCreatureEntity) {
         TameableCreatureEntity tamedBaby = (TameableCreatureEntity)spawnedBaby;
         tamedBaby.setOwnerId(ownerId);
      }

      return spawnedBaby;
   }

   public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
      return forSpawnCount && this.isTamed() ? false : super.isCreatureType(type, forSpawnCount);
   }

   public boolean canBeTempted() {
      return super.canBeTempted();
   }

   public boolean shouldDismountInWater(Entity rider) {
      return false;
   }

   public boolean canBeSteered() {
      return this.isTamed();
   }

   protected void playTameEffect(boolean success) {
      EnumParticleTypes particle = EnumParticleTypes.HEART;
      if (!success) {
         particle = EnumParticleTypes.SMOKE_NORMAL;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
      }

   }

   public boolean canBeColored(EntityPlayer player) {
      if (player == null) {
         return true;
      } else {
         return this.isTamed() && player == this.getPlayerOwner();
      }
   }

   public boolean showBossInfo() {
      return this.isTamed() ? false : super.showBossInfo();
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      super.readEntityFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasUniqueId("OwnerId")) {
         this.setOwnerId(nbtTagCompound.getUniqueId("OwnerId"));
      } else {
         this.setOwnerId((UUID)null);
      }

      if (nbtTagCompound.hasKey("FromSummoningPedestal")) {
         this.fromSummoningPedestal = nbtTagCompound.getBoolean("FromSummoningPedestal");
      }

      if (nbtTagCompound.hasKey("Sitting")) {
         this.setSitting(nbtTagCompound.getBoolean("Sitting"));
      } else {
         this.setSitting(false);
      }

      if (nbtTagCompound.hasKey("Following")) {
         this.setFollowing(nbtTagCompound.getBoolean("Following"));
      } else {
         this.setFollowing(true);
      }

      if (nbtTagCompound.hasKey("Passive")) {
         this.setPassive(nbtTagCompound.getBoolean("Passive"));
      } else {
         this.setPassive(false);
      }

      if (nbtTagCompound.hasKey("Aggressive")) {
         this.setAggressive(nbtTagCompound.getBoolean("Aggressive"));
      } else {
         this.setAggressive(false);
      }

      if (nbtTagCompound.hasKey("PVP")) {
         this.setPVP(nbtTagCompound.getBoolean("PVP"));
      } else {
         this.setPVP(true);
      }

      if (nbtTagCompound.hasKey("Hunger")) {
         this.setCreatureHunger(nbtTagCompound.getFloat("Hunger"));
      } else {
         this.setCreatureHunger(this.getCreatureHungerMax());
      }

      if (nbtTagCompound.hasKey("Stamina")) {
         this.setStamina(nbtTagCompound.getFloat("Stamina"));
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      if (this.getOwnerId() != null) {
         nbtTagCompound.setUniqueId("OwnerId", this.getOwnerId());
      }

      nbtTagCompound.setBoolean("FromSummoningPedestal", this.fromSummoningPedestal);
      nbtTagCompound.setBoolean("Sitting", this.isSitting());
      nbtTagCompound.setBoolean("Following", this.isFollowing());
      nbtTagCompound.setBoolean("Passive", this.isPassive());
      nbtTagCompound.setBoolean("Aggressive", this.isAggressive());
      nbtTagCompound.setBoolean("PVP", this.isPVP());
      nbtTagCompound.setFloat("Hunger", this.getCreatureHunger());
      nbtTagCompound.setFloat("Stamina", this.getStamina());
   }

   public int getTalkInterval() {
      return this.isTamed() ? 600 : super.getTalkInterval();
   }

   protected SoundEvent getAmbientSound() {
      String sound = "_say";
      if (this.isTamed() && this.getHealth() < this.getMaxHealth()) {
         sound = "_beg";
      }

      return AssetManager.getSound(this.getSoundName() + sound);
   }

   public void playTameSound() {
      this.playSound(AssetManager.getSound(this.getSoundName() + "_tame"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
   }

   public void playEatSound() {
      this.playSound(AssetManager.getSound(this.getSoundName() + "_eat"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
   }

   public static enum TAMED_ID {
      IS_TAMED((byte)1),
      MOVE_SIT((byte)2),
      MOVE_FOLLOW((byte)4),
      STANCE_PASSIVE((byte)8),
      STANCE_AGGRESSIVE((byte)16),
      STANCE_ASSIST((byte)32),
      PVP((byte)64);

      public final byte id;

      private TAMED_ID(byte value) {
         this.id = value;
      }

      public byte getValue() {
         return this.id;
      }
   }
}
