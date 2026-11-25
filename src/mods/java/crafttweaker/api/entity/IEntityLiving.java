package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.entity.IEntityLiving")
@ZenRegister
public interface IEntityLiving extends IEntityLivingBase {
   @ZenGetter("attackTarget")
   IEntityLivingBase getAttackTarget();

   @ZenSetter("attackTarget")
   void setAttackTarget(IEntityLivingBase var1);

   @ZenGetter("talkInterval")
   int getTalkInterval();

   @ZenMethod
   void playLivingSound();

   @ZenMethod
   void spawnExplosionParticle();

   @ZenGetter
   boolean canSpawnHere();

   @ZenGetter
   boolean isColliding();

   @ZenGetter("renderSizeModifier")
   float getRenderSizeModifier();

   @ZenGetter("maxSpawnedInChunk")
   int getMaxSpawnedInChunk();

   @ZenGetter
   boolean canBeSteered();

   @ZenMethod
   void setDropChance(IEntityEquipmentSlot var1, float var2);

   @ZenGetter
   boolean canPickUpLoot();

   @ZenSetter("canPickUpLoot")
   void setCanPickUpLoot(boolean var1);

   @ZenMethod
   void enablePersistence();

   @ZenGetter
   boolean isNoDespawnRequired();

   @ZenMethod
   void clearLeashed(boolean var1, boolean var2);

   @ZenMethod
   boolean canBeLeashedTo(IPlayer var1);

   @ZenGetter
   boolean isLeashed();

   @ZenGetter("leashedToEntity")
   IEntity getLeashedToEntity();

   @ZenMethod
   void setLeashedToEntity(IEntity var1, boolean var2);

   @ZenGetter
   boolean isAIDisabled();

   @ZenSetter("isAIDisabled")
   void setAIDisabled(boolean var1);

   @ZenGetter
   boolean isLeftHanded();

   @ZenSetter("isLeftHanded")
   void setLeftHanded(boolean var1);
}
