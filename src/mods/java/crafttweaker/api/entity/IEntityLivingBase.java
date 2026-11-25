package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.attribute.IEntityAttributeInstance;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.potions.IPotionEffect;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.entity.IEntityLivingBase")
@ZenRegister
public interface IEntityLivingBase extends IEntity {
   @ZenGetter
   boolean canBreatheUnderwater();

   @ZenMethod
   boolean isPotionActive(IPotion var1);

   @ZenGetter("health")
   float getHealth();

   @ZenSetter("health")
   void setHealth(float var1);

   @ZenGetter
   boolean isChild();

   @ZenMethod
   void clearActivePotions();

   @ZenGetter
   boolean isUndead();

   @ZenMethod
   void heal(float var1);

   @ZenGetter("maxHealth")
   float getMaxHealth();

   @ZenGetter("mainHandHeldItem")
   IItemStack getHeldItemMainHand();

   @ZenMethod
   @ZenGetter("offHandHeldItem")
   IItemStack getHeldItemOffHand();

   @ZenMethod
   IEntityAttributeInstance getAttribute(String var1);

   @ZenGetter("revengeTarget")
   IEntityLivingBase getRevengeTarget();

   @ZenSetter("revengeTarget")
   void setRevengeTarger(IEntityLivingBase var1);

   @ZenGetter("lastAttackedEntity")
   IEntityLivingBase getLastAttackedEntity();

   @ZenSetter("lastAttackedEntity")
   void setLastAttackedEntity(IEntityLivingBase var1);

   @ZenGetter("lastAttackedEntityTime")
   int getLastAttackedEntityTime();

   @ZenGetter("activePotionEffects")
   List<IPotionEffect> getActivePotionEffects();

   @ZenMethod
   IPotionEffect getActivePotionEffect(IPotion var1);

   @ZenMethod
   boolean isPotionEffectApplicable(IPotionEffect var1);

   @ZenGetter("lastDamageSource")
   IDamageSource getLastDamageSource();

   @ZenMethod
   void onDeath(IDamageSource var1);

   @ZenMethod
   void knockBack(IEntity var1, float var2, double var3, double var5);

   @ZenGetter
   boolean isOnLadder();

   @ZenGetter("totalArmorValue")
   int getTotalArmorValue();

   @ZenGetter("attackingEntity")
   IEntityLivingBase getAttackingEntity();

   @ZenGetter("arrowsInEntity")
   int getArrowCountInEntity();

   @ZenSetter("arrowsInEntity")
   void setArrowCountInEntity(int var1);

   @ZenGetter("AIMovementSpeed")
   float getAIMoveSpeed();

   @ZenSetter("AIMovementSpeed")
   void setAIMoveSpeed(float var1);

   @ZenMethod
   void onLivingUpdate();

   @ZenMethod
   boolean canEntityBeSeen(IEntity var1);

   @ZenMethod
   void addPotionEffect(IPotionEffect var1);

   @ZenMethod
   void setItemToSlot(IEntityEquipmentSlot var1, IItemStack var2);

   @ZenMethod
   boolean hasItemInSlot(IEntityEquipmentSlot var1);

   @ZenMethod
   IItemStack getItemInSlot(IEntityEquipmentSlot var1);
}
