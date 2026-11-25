package crafttweaker.api.entity;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IMaterial;
import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.data.IData;
import crafttweaker.api.game.ITeam;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.util.Position3f;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IRayTraceResult;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import java.util.List;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.entity.IEntity")
@ZenRegister
public interface IEntity extends ICommandSender {
   @ZenGetter("definition")
   IEntityDefinition getDefinition();

   @ZenSetter("world")
   void setWorld(IWorld var1);

   @ZenGetter("dimension")
   @ZenMethod
   int getDimension();

   @ZenSetter("dimension")
   @ZenMethod
   void setDimension(int var1);

   @ZenMethod
   @ZenGetter("x")
   double getX();

   @ZenMethod
   @ZenGetter("y")
   double getY();

   @ZenMethod
   @ZenGetter("z")
   double getZ();

   @ZenMethod
   @ZenGetter("position3f")
   Position3f getPosition3f();

   @ZenMethod
   @ZenSetter("position3f")
   default void setPosition3f(Position3f position3f) {
      CraftTweakerAPI.logWarning(String.format("Class %s does not seem to override IEntity#setPosition3f, please report to the mod author!", this.getClass().getCanonicalName()));
      this.setPosX((double)position3f.getX());
      this.setPosY((double)position3f.getY());
      this.setPosZ((double)position3f.getZ());
   }

   @ZenMethod
   @ZenSetter("position")
   void setPosition(IBlockPos var1);

   @ZenMethod
   void setDead();

   @ZenMethod
   @ZenSetter("fire")
   void setFire(int var1);

   @ZenMethod
   void extinguish();

   @ZenMethod
   @ZenGetter("wet")
   boolean isWet();

   @ZenMethod
   @ZenGetter("passengers")
   List<IEntity> getPassengers();

   @ZenMethod
   double getDistanceSqToEntity(IEntity var1);

   @ZenMethod
   @ZenGetter("alive")
   boolean isAlive();

   @ZenMethod
   @ZenGetter("ridingEntity")
   IEntity getRidingEntity();

   @ZenMethod
   IItemStack getPickedResult();

   @ZenMethod
   @ZenGetter("customName")
   String getCustomName();

   @ZenMethod
   @ZenSetter("customName")
   void setCustomName(String var1);

   @ZenMethod
   @ZenGetter("immuneToFire")
   boolean isImmuneToFire();

   @ZenMethod
   @ZenGetter("air")
   int getAir();

   @ZenMethod
   @ZenSetter("air")
   void setAir(int var1);

   Object getInternal();

   @ZenMethod
   boolean canTrample(IWorld var1, IBlockDefinition var2, IBlockPos var3, float var4);

   @ZenMethod
   void onEntityUpdate();

   @ZenMethod
   void onUpdate();

   @ZenGetter
   boolean isSprinting();

   @ZenSetter("isSprinting")
   void setSprinting(boolean var1);

   @ZenGetter
   boolean isGlowing();

   @ZenSetter("isGlowing")
   void setGlowing(boolean var1);

   @ZenGetter("id")
   int getID();

   @ZenSetter("id")
   void setID(int var1);

   @ZenGetter("tags")
   List<String> getTags();

   @ZenMethod
   void addTag(String var1);

   @ZenMethod
   void removeTag(String var1);

   @ZenMethod
   void onKillCommand();

   @ZenGetter("maxInPortalTime")
   int getMaxInPortalTime();

   @ZenGetter("portalCooldown")
   int getPortalCooldown();

   @ZenGetter
   boolean isSilent();

   @ZenSetter("isSilent")
   void setSilent(boolean var1);

   @ZenGetter
   boolean hasNoGravity();

   @ZenSetter("hasNoGravity")
   void setNoGravity(boolean var1);

   @ZenGetter
   boolean isInWater();

   @ZenGetter
   boolean isOverWater();

   @ZenMethod
   void spawnRunningParticles();

   @ZenMethod
   boolean isInsideOfMaterial(IMaterial var1);

   @ZenGetter
   boolean isInLava();

   @ZenMethod
   boolean attackEntityFrom(IDamageSource var1, float var2);

   @ZenGetter
   boolean canBeCollidedWith();

   @ZenGetter
   boolean canBePushed();

   @ZenGetter("nbt")
   @ZenMethod
   IData getNBT();

   @ZenMethod
   IEntityItem dropItem(IItemStack var1, @Optional float var2);

   @ZenGetter
   boolean isInsideOpaqueBlock();

   @ZenMethod
   void removePassengers();

   @ZenMethod
   void dismountRidingEntity();

   @ZenGetter("heldEquipment")
   List<IItemStack> getHeldEquipment();

   @ZenGetter("armorInventory")
   List<IItemStack> getArmorInventoryList();

   @ZenGetter("equipmentAndArmor")
   List<IItemStack> getEquipmentAndArmor();

   @ZenGetter
   boolean isBurning();

   @ZenGetter
   boolean isRiding();

   @ZenGetter
   boolean isBeingRidden();

   @ZenGetter
   boolean isSneaking();

   @ZenSetter("isSneaking")
   void setSneaking(boolean var1);

   @ZenGetter
   boolean isInvisible();

   @ZenGetter("team")
   ITeam getTeam();

   @ZenSetter("isInvisible")
   void setInvisible(boolean var1);

   @ZenMethod
   boolean isOnSameTeam(IEntity var1);

   @ZenMethod
   void setInWeb();

   @ZenMethod("parts")
   IEntity[] getParts();

   @ZenMethod
   boolean isEntityEqual(IEntity var1);

   @ZenGetter
   boolean canBeAttackedWithItem();

   @ZenMethod
   boolean isInvulnerableTo(IDamageSource var1);

   @ZenGetter
   boolean isInvulnerable();

   @ZenSetter("isInvulnerable")
   void setInvulnerable(boolean var1);

   @ZenMethod
   void setToLocationFrom(IEntity var1);

   @ZenGetter
   boolean isBoss();

   @ZenGetter("maxFallHeight")
   int getMaxFallHeight();

   @ZenGetter
   boolean doesTriggerPressurePlate();

   @ZenGetter
   boolean isPushedByWater();

   @ZenGetter
   boolean hasCustomName();

   @ZenGetter("alwaysRenderNameTag")
   boolean alwaysRenderNameTag();

   @ZenSetter("alwaysRenderNameTag")
   void setAlwaysRenderNameTag(boolean var1);

   @ZenGetter("eyeHeight")
   float getEyeHight();

   @ZenGetter
   boolean isOutsideBorder();

   @ZenSetter("isOutsideBorder")
   void setOutsideBorder(boolean var1);

   @ZenGetter
   boolean isImmuneToExplosions();

   @ZenGetter
   boolean shouldRiderSit();

   @ZenGetter
   boolean canRiderInteract();

   @ZenMethod
   boolean shouldRiderDismountInWater(IEntity var1);

   @ZenGetter("controllingPassenger")
   IEntity getControllingPassenger();

   @ZenMethod
   boolean isPassenger(IEntity var1);

   @ZenGetter("passengersRecursive")
   List<IEntity> getPassengersRecursive();

   @ZenGetter("lowestRidingEntity")
   IEntity getLowestRidingEntity();

   @ZenMethod
   boolean isRidingSameEntity(IEntity var1);

   @ZenGetter
   boolean canPassengerSteer();

   @ZenGetter("rotationYaw")
   float getRotationYaw();

   @ZenSetter("rotationYaw")
   void setRotationYaw(float var1);

   @ZenGetter("rotationPitch")
   float getRotationPitch();

   @ZenSetter("rotationPitch")
   void setRotationPitch(float var1);

   @ZenGetter("motionX")
   double getMotionX();

   @ZenSetter("motionX")
   void setMotionX(double var1);

   @ZenGetter("motionY")
   double getMotionY();

   @ZenSetter("motionY")
   void setMotionY(double var1);

   @ZenGetter("motionZ")
   double getMotionZ();

   @ZenSetter("motionZ")
   void setMotionZ(double var1);

   @ZenGetter("posX")
   double getPosX();

   @ZenSetter("posX")
   void setPosX(double var1);

   @ZenGetter("posY")
   double getPosY();

   @ZenSetter("posY")
   void setPosY(double var1);

   @ZenGetter("posZ")
   double getPosZ();

   @ZenSetter("posZ")
   void setPosZ(double var1);

   @ZenGetter("lookingDirection")
   IVector3d getLookingDirection();

   /** @deprecated */
   @Deprecated
   IRayTraceResult getRayTrace(double var1, float var3);

   @ZenMethod
   default IRayTraceResult getRayTrace(double blockReachDistance, float partialTicks, @Optional boolean stopOnLiquid, @Optional boolean ignoreBlockWithoutBoundingBox, @Optional(valueBoolean = true) boolean returnLastUncollidableBlock) {
      CraftTweakerAPI.logError(this.getClass().getName() + " does not properly implement IEntity#getRayTrace, ask the author to fix this");
      return this.getRayTrace(blockReachDistance, partialTicks);
   }

   @ZenMethod
   default void update(IData data) {
      CraftTweakerAPI.logError("IEntity#update not overwritten by implementation " + this.getClass() + "!");
   }

   @ZenSetter("nbt")
   @ZenMethod
   default void setNBT(IData data) {
      this.update(data);
   }

   @ZenGetter
   @ZenMethod
   default boolean onGround() {
      return false;
   }
}
