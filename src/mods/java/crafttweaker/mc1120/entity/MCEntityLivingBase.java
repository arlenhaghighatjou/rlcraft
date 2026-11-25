package crafttweaker.mc1120.entity;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.entity.attribute.IEntityAttributeInstance;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.potions.IPotionEffect;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityLivingBase;

public class MCEntityLivingBase extends MCEntity implements IEntityLivingBase {
   private final EntityLivingBase entityLivingBase;

   public MCEntityLivingBase(EntityLivingBase entity) {
      super(entity);
      this.entityLivingBase = entity;
   }

   public boolean canBreatheUnderwater() {
      return this.entityLivingBase.canBreatheUnderwater();
   }

   public boolean isPotionActive(IPotion potion) {
      return this.entityLivingBase.isPotionActive(CraftTweakerMC.getPotion(potion));
   }

   public float getHealth() {
      return this.entityLivingBase.getHealth();
   }

   public void setHealth(float amount) {
      this.entityLivingBase.setHealth(amount);
   }

   public boolean isChild() {
      return this.entityLivingBase.isChild();
   }

   public void clearActivePotions() {
      this.entityLivingBase.clearActivePotions();
   }

   public boolean isUndead() {
      return this.entityLivingBase.isEntityUndead();
   }

   public void heal(float amount) {
      this.entityLivingBase.heal(amount);
   }

   public float getMaxHealth() {
      return this.entityLivingBase.getMaxHealth();
   }

   public IItemStack getHeldItemMainHand() {
      return CraftTweakerMC.getIItemStack(this.entityLivingBase.getHeldItemMainhand());
   }

   public IItemStack getHeldItemOffHand() {
      return CraftTweakerMC.getIItemStack(this.entityLivingBase.getHeldItemOffhand());
   }

   public IEntityAttributeInstance getAttribute(String name) {
      return CraftTweakerMC.getAttributeInstance(this.entityLivingBase.getAttributeMap().getAttributeInstanceByName(name));
   }

   public IEntityLivingBase getRevengeTarget() {
      return CraftTweakerMC.getIEntityLivingBase(this.entityLivingBase.getRevengeTarget());
   }

   public void setRevengeTarger(IEntityLivingBase target) {
      this.entityLivingBase.setRevengeTarget(CraftTweakerMC.getEntityLivingBase(target));
   }

   public IEntityLivingBase getLastAttackedEntity() {
      return CraftTweakerMC.getIEntityLivingBase(this.entityLivingBase.getLastAttackedEntity());
   }

   public void setLastAttackedEntity(IEntityLivingBase entity) {
      this.entityLivingBase.setLastAttackedEntity(CraftTweakerMC.getEntityLivingBase(entity));
   }

   public int getLastAttackedEntityTime() {
      return this.entityLivingBase.getLastAttackedEntityTime();
   }

   public List<IPotionEffect> getActivePotionEffects() {
      return (List)this.entityLivingBase.getActivePotionEffects().stream().map(CraftTweakerMC::getIPotionEffect).collect(Collectors.toList());
   }

   public IPotionEffect getActivePotionEffect(IPotion potion) {
      return CraftTweakerMC.getIPotionEffect(this.entityLivingBase.getActivePotionEffect(CraftTweakerMC.getPotion(potion)));
   }

   public boolean isPotionEffectApplicable(IPotionEffect potionEffect) {
      return this.entityLivingBase.isPotionApplicable(CraftTweakerMC.getPotionEffect(potionEffect));
   }

   public IDamageSource getLastDamageSource() {
      return CraftTweakerMC.getIDamageSource(this.entityLivingBase.getLastDamageSource());
   }

   public void onDeath(IDamageSource source) {
      this.entityLivingBase.onDeath(CraftTweakerMC.getDamageSource(source));
   }

   public void knockBack(IEntity entity, float strength, double xRatio, double zRatio) {
      this.entityLivingBase.knockBack(CraftTweakerMC.getEntity(entity), strength, xRatio, zRatio);
   }

   public boolean isOnLadder() {
      return this.entityLivingBase.isOnLadder();
   }

   public int getTotalArmorValue() {
      return this.entityLivingBase.getTotalArmorValue();
   }

   public IEntityLivingBase getAttackingEntity() {
      return CraftTweakerMC.getIEntityLivingBase(this.entityLivingBase.getAttackingEntity());
   }

   public int getArrowCountInEntity() {
      return this.entityLivingBase.getArrowCountInEntity();
   }

   public void setArrowCountInEntity(int arrows) {
      this.entityLivingBase.setArrowCountInEntity(arrows);
   }

   public float getAIMoveSpeed() {
      return this.entityLivingBase.getAIMoveSpeed();
   }

   public void setAIMoveSpeed(float speed) {
      this.entityLivingBase.setAIMoveSpeed(speed);
   }

   public void onLivingUpdate() {
      this.entityLivingBase.onLivingUpdate();
   }

   public boolean canEntityBeSeen(IEntity other) {
      return this.entityLivingBase.canEntityBeSeen(CraftTweakerMC.getEntity(other));
   }

   public void addPotionEffect(IPotionEffect potionEffect) {
      this.entityLivingBase.addPotionEffect(CraftTweakerMC.getPotionEffect(potionEffect));
   }

   public void setItemToSlot(IEntityEquipmentSlot slot, IItemStack itemStack) {
      this.entityLivingBase.setItemStackToSlot(CraftTweakerMC.getEntityEquipmentSlot(slot), CraftTweakerMC.getItemStack(itemStack));
   }

   public boolean hasItemInSlot(IEntityEquipmentSlot slot) {
      return this.entityLivingBase.hasItemInSlot(CraftTweakerMC.getEntityEquipmentSlot(slot));
   }

   public IItemStack getItemInSlot(IEntityEquipmentSlot slot) {
      return CraftTweakerMC.getIItemStack(this.entityLivingBase.getItemStackFromSlot(CraftTweakerMC.getEntityEquipmentSlot(slot)));
   }
}
