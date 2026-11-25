package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.entity.IEntityLiving;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.EntityLiving;

public class MCEntityLiving extends MCEntityLivingBase implements IEntityLiving {
   private final EntityLiving entityLiving;

   public MCEntityLiving(EntityLiving entity) {
      super(entity);
      this.entityLiving = entity;
   }

   public IEntityLivingBase getAttackTarget() {
      return CraftTweakerMC.getIEntityLivingBase(this.entityLiving.getAttackTarget());
   }

   public void setAttackTarget(IEntityLivingBase attackTarget) {
      this.entityLiving.setAttackTarget(CraftTweakerMC.getEntityLivingBase(attackTarget));
   }

   public int getTalkInterval() {
      return this.entityLiving.getTalkInterval();
   }

   public void playLivingSound() {
      this.entityLiving.playLivingSound();
   }

   public void spawnExplosionParticle() {
      this.entityLiving.spawnExplosionParticle();
   }

   public boolean canSpawnHere() {
      return this.entityLiving.getCanSpawnHere();
   }

   public boolean isColliding() {
      return !this.entityLiving.isNotColliding();
   }

   public float getRenderSizeModifier() {
      return this.entityLiving.getRenderSizeModifier();
   }

   public int getMaxSpawnedInChunk() {
      return this.entityLiving.getMaxSpawnedInChunk();
   }

   public boolean canBeSteered() {
      return this.entityLiving.canBeSteered();
   }

   public void setDropChance(IEntityEquipmentSlot slot, float chance) {
      this.entityLiving.setDropChance(CraftTweakerMC.getEntityEquipmentSlot(slot), chance);
   }

   public boolean canPickUpLoot() {
      return this.entityLiving.canPickUpLoot();
   }

   public void setCanPickUpLoot(boolean canPickUpLoot) {
      this.entityLiving.setCanPickUpLoot(canPickUpLoot);
   }

   public void enablePersistence() {
      this.entityLiving.enablePersistence();
   }

   public boolean isNoDespawnRequired() {
      return this.entityLiving.isNoDespawnRequired();
   }

   public void clearLeashed(boolean sendPacket, boolean dropLead) {
      this.entityLiving.clearLeashed(sendPacket, dropLead);
   }

   public boolean canBeLeashedTo(IPlayer player) {
      return this.entityLiving.canBeLeashedTo(CraftTweakerMC.getPlayer(player));
   }

   public boolean isLeashed() {
      return this.entityLiving.getLeashed();
   }

   public IEntity getLeashedToEntity() {
      return CraftTweakerMC.getIEntity(this.entityLiving.getLeashHolder());
   }

   public void setLeashedToEntity(IEntity entity, boolean sendAttachNotification) {
      this.entityLiving.setLeashHolder(CraftTweakerMC.getEntity(entity), sendAttachNotification);
   }

   public boolean isAIDisabled() {
      return this.entityLiving.isAIDisabled();
   }

   public void setAIDisabled(boolean isAIDisabled) {
      this.entityLiving.setNoAI(isAIDisabled);
   }

   public boolean isLeftHanded() {
      return this.entityLiving.isLeftHanded();
   }

   public void setLeftHanded(boolean isLeftHanded) {
      this.entityLiving.setLeftHanded(isLeftHanded);
   }
}
