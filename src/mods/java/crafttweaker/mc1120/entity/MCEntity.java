package crafttweaker.mc1120.entity;

import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.block.IMaterial;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.game.ITeam;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.server.IServer;
import crafttweaker.api.util.Position3f;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IRayTraceResult;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.command.MCCommandSender;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.server.MCServer;
import crafttweaker.mc1120.util.MCPosition3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MCEntity extends MCCommandSender implements IEntity {
   private Entity entity;

   public MCEntity(Entity entity) {
      super(entity);
      this.entity = entity;
   }

   public int getDimension() {
      return this.entity.dimension;
   }

   public void setDimension(int dimensionID) {
      this.entity = this.entity.changeDimension(dimensionID);
   }

   public double getX() {
      return this.entity.posX;
   }

   public double getY() {
      return this.entity.posY;
   }

   public double getZ() {
      return this.entity.posZ;
   }

   public Position3f getPosition3f() {
      return new MCPosition3f((float)this.entity.posX, (float)this.entity.posY, (float)this.entity.posZ);
   }

   public void setPosition3f(Position3f position3f) {
      this.entity.setPosition((double)position3f.getX(), (double)position3f.getY(), (double)position3f.getZ());
   }

   public void setPosition(IBlockPos position) {
      this.entity.setPosition((double)position.getX(), (double)position.getY(), (double)position.getZ());
   }

   public void setDead() {
      this.entity.setDead();
   }

   public void setFire(int seconds) {
      this.entity.setFire(seconds);
   }

   public void extinguish() {
      this.entity.extinguish();
   }

   public boolean isWet() {
      return this.entity.isWet();
   }

   public List<IEntity> getPassengers() {
      return (List)this.entity.getPassengers().stream().map(CraftTweakerMC::getIEntity).collect(Collectors.toList());
   }

   public double getDistanceSqToEntity(IEntity otherEntity) {
      return (double)this.entity.getDistance(CraftTweakerMC.getEntity(otherEntity));
   }

   public boolean isAlive() {
      return this.entity.isEntityAlive();
   }

   public IEntity getRidingEntity() {
      return CraftTweakerMC.getIEntity(this.entity.getRidingEntity());
   }

   public IItemStack getPickedResult() {
      return CraftTweakerMC.getIItemStack(this.entity.getPickedResult(new RayTraceResult(this.entity)));
   }

   public String getCustomName() {
      return this.entity.getCustomNameTag();
   }

   public void setCustomName(String name) {
      this.entity.setCustomNameTag(name);
   }

   public boolean isImmuneToFire() {
      return this.entity.isImmuneToFire();
   }

   public int getAir() {
      return this.entity.getAir();
   }

   public void setAir(int amount) {
      this.entity.setAir(amount);
   }

   public Entity getInternal() {
      return this.entity;
   }

   public boolean canTrample(IWorld world, IBlockDefinition block, IBlockPos pos, float fall) {
      return this.entity.canTrample(CraftTweakerMC.getWorld(world), CraftTweakerMC.getBlock(block), CraftTweakerMC.getBlockPos(pos), fall);
   }

   public String toString() {
      return this.entity.toString();
   }

   public boolean isInvulnerableTo(IDamageSource source) {
      return this.entity.isEntityInvulnerable(CraftTweakerMC.getDamageSource(source));
   }

   public boolean isInvulnerable() {
      return this.entity.getIsInvulnerable();
   }

   public void setInvulnerable(boolean invulnerable) {
      this.entity.setEntityInvulnerable(invulnerable);
   }

   public void setToLocationFrom(IEntity other) {
      this.entity.copyLocationAndAnglesFrom(CraftTweakerMC.getEntity(other));
   }

   public boolean isBoss() {
      return !this.entity.isNonBoss();
   }

   public int getMaxFallHeight() {
      return this.entity.getMaxFallHeight();
   }

   public boolean doesTriggerPressurePlate() {
      return !this.entity.doesEntityNotTriggerPressurePlate();
   }

   public boolean isPushedByWater() {
      return this.entity.isPushedByWater();
   }

   public String getDisplayName() {
      return this.entity.getDisplayName().getFormattedText();
   }

   public boolean hasCustomName() {
      return this.entity.hasCustomName();
   }

   public boolean alwaysRenderNameTag() {
      return this.entity.getAlwaysRenderNameTag();
   }

   public void setAlwaysRenderNameTag(boolean alwaysRenderNameTag) {
      this.entity.setAlwaysRenderNameTag(alwaysRenderNameTag);
   }

   public float getEyeHight() {
      return this.entity.getEyeHeight();
   }

   public boolean isOutsideBorder() {
      return this.entity.isOutsideBorder();
   }

   public void setOutsideBorder(boolean outsideBorder) {
      this.entity.setOutsideBorder(outsideBorder);
   }

   public IServer getServer() {
      return new MCServer(this.entity.getServer());
   }

   public boolean isImmuneToExplosions() {
      return this.entity.isImmuneToExplosions();
   }

   public boolean shouldRiderSit() {
      return this.entity.shouldRiderSit();
   }

   public boolean canRiderInteract() {
      return this.entity.canRiderInteract();
   }

   public boolean shouldRiderDismountInWater(IEntity rider) {
      return this.entity.shouldDismountInWater(CraftTweakerMC.getEntity(rider));
   }

   public IEntity getControllingPassenger() {
      return CraftTweakerMC.getIEntity(this.entity.getControllingPassenger());
   }

   public boolean isPassenger(IEntity passenger) {
      return this.entity.isPassenger(CraftTweakerMC.getEntity(passenger));
   }

   public List<IEntity> getPassengersRecursive() {
      return (List)this.entity.getRecursivePassengers().stream().map(CraftTweakerMC::getIEntity).collect(Collectors.toList());
   }

   public IEntity getLowestRidingEntity() {
      return CraftTweakerMC.getIEntity(this.entity.getLowestRidingEntity());
   }

   public boolean isRidingSameEntity(IEntity other) {
      return this.entity.isRidingSameEntity(CraftTweakerMC.getEntity(other));
   }

   public boolean canPassengerSteer() {
      return this.entity.canPassengerSteer();
   }

   public float getRotationYaw() {
      return this.entity.rotationYaw;
   }

   public void setRotationYaw(float rotationYaw) {
      this.entity.rotationYaw = rotationYaw;
   }

   public float getRotationPitch() {
      return this.entity.rotationPitch;
   }

   public void setRotationPitch(float rotationPitch) {
      this.entity.rotationPitch = rotationPitch;
   }

   public double getMotionX() {
      return this.entity.motionX;
   }

   public void setMotionX(double motionX) {
      this.entity.motionX = motionX;
   }

   public double getMotionY() {
      return this.entity.motionY;
   }

   public void setMotionY(double motionY) {
      this.entity.motionY = motionY;
   }

   public double getMotionZ() {
      return this.entity.motionZ;
   }

   public void setMotionZ(double motionZ) {
      this.entity.motionZ = motionZ;
   }

   public double getPosX() {
      return this.entity.posX;
   }

   public void setPosX(double posX) {
      this.entity.setPosition(posX, this.entity.posY, this.entity.posZ);
   }

   public double getPosY() {
      return this.entity.posY;
   }

   public void setPosY(double posY) {
      this.entity.setPosition(this.entity.posX, posY, this.entity.posZ);
   }

   public double getPosZ() {
      return this.entity.posZ;
   }

   public void setPosZ(double posZ) {
      this.entity.setPosition(this.entity.posX, this.entity.posY, posZ);
   }

   public IVector3d getLookingDirection() {
      return CraftTweakerMC.getIVector3d(this.entity.getLookVec());
   }

   public IRayTraceResult getRayTrace(double blockReachDistance, float partialTicks) {
      return CraftTweakerMC.getIRayTraceResult(this.entity.rayTrace(blockReachDistance, partialTicks));
   }

   public IRayTraceResult getRayTrace(double blockReachDistance, float partialTicks, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
      Vec3d positionEyes = this.entity.getPositionEyes(partialTicks);
      Vec3d lookingDirection = this.entity.getLook(partialTicks);
      Vec3d lookingVector = positionEyes.addVector(lookingDirection.x * blockReachDistance, lookingDirection.y * blockReachDistance, lookingDirection.z * blockReachDistance);
      return CraftTweakerMC.getIRayTraceResult(this.entity.world.rayTraceBlocks(positionEyes, lookingVector, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock));
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.entity.getEntityWorld());
   }

   public IEntityDefinition getDefinition() {
      for(Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries()) {
         if (((EntityEntry)entry.getValue()).getEntityClass() == this.entity.getClass()) {
            return new MCEntityDefinition((EntityEntry)entry.getValue());
         }
      }

      return null;
   }

   public void setWorld(IWorld world) {
      this.entity.setWorld(CraftTweakerMC.getWorld(world));
   }

   public void onEntityUpdate() {
      this.entity.onEntityUpdate();
   }

   public void onUpdate() {
      this.entity.onUpdate();
   }

   public boolean isSprinting() {
      return this.entity.isSprinting();
   }

   public void setSprinting(boolean sprinting) {
      this.entity.setSprinting(sprinting);
   }

   public boolean isGlowing() {
      return this.entity.isGlowing();
   }

   public void setGlowing(boolean glowing) {
      this.entity.setGlowing(glowing);
   }

   public int getID() {
      return this.entity.getEntityId();
   }

   public void setID(int id) {
      this.entity.setEntityId(id);
   }

   public List<String> getTags() {
      return new ArrayList(this.entity.getTags());
   }

   public void addTag(String tag) {
      this.entity.addTag(tag);
   }

   public void removeTag(String tag) {
      this.entity.removeTag(tag);
   }

   public void onKillCommand() {
      this.entity.onKillCommand();
   }

   public int getMaxInPortalTime() {
      return this.entity.getMaxInPortalTime();
   }

   public int getPortalCooldown() {
      return this.entity.getPortalCooldown();
   }

   public boolean isSilent() {
      return this.entity.isSilent();
   }

   public void setSilent(boolean silent) {
      this.entity.setSilent(silent);
   }

   public boolean hasNoGravity() {
      return this.entity.hasNoGravity();
   }

   public void setNoGravity(boolean noGravity) {
      this.entity.setNoGravity(noGravity);
   }

   public boolean isInWater() {
      return this.entity.isInWater();
   }

   public boolean isOverWater() {
      return this.entity.isOverWater();
   }

   public void spawnRunningParticles() {
      this.entity.spawnRunningParticles();
   }

   public boolean isInsideOfMaterial(IMaterial material) {
      return this.entity.isInsideOfMaterial(CraftTweakerMC.getMaterial(material));
   }

   public boolean isInLava() {
      return this.entity.isInLava();
   }

   public boolean attackEntityFrom(IDamageSource source, float amount) {
      return this.entity.attackEntityFrom(CraftTweakerMC.getDamageSource(source), amount);
   }

   public boolean canBeCollidedWith() {
      return this.entity.canBeCollidedWith();
   }

   public boolean canBePushed() {
      return this.entity.canBePushed();
   }

   public IData getNBT() {
      NBTTagCompound tag = new NBTTagCompound();
      this.entity.writeToNBT(tag);
      return NBTConverter.from(tag, true);
   }

   public IEntityItem dropItem(IItemStack itemStack, float offset) {
      return CraftTweakerMC.getIEntityItem(this.entity.entityDropItem(CraftTweakerMC.getItemStack(itemStack), offset));
   }

   public boolean isInsideOpaqueBlock() {
      return this.entity.isEntityInsideOpaqueBlock();
   }

   public void removePassengers() {
      this.entity.removePassengers();
   }

   public void dismountRidingEntity() {
      this.entity.dismountRidingEntity();
   }

   public List<IItemStack> getHeldEquipment() {
      List<IItemStack> output = new ArrayList();
      this.entity.getHeldEquipment().forEach((item) -> {
         output.add(CraftTweakerMC.getIItemStack(item));
      });
      return output;
   }

   public List<IItemStack> getArmorInventoryList() {
      List<IItemStack> output = new ArrayList();
      this.entity.getArmorInventoryList().forEach((item) -> {
         output.add(CraftTweakerMC.getIItemStack(item));
      });
      return output;
   }

   public List<IItemStack> getEquipmentAndArmor() {
      List<IItemStack> output = new ArrayList();
      this.entity.getEquipmentAndArmor().forEach((item) -> {
         output.add(CraftTweakerMC.getIItemStack(item));
      });
      return output;
   }

   public boolean isBurning() {
      return this.entity.isBurning();
   }

   public boolean isRiding() {
      return this.entity.isRiding();
   }

   public boolean isBeingRidden() {
      return this.entity.isBeingRidden();
   }

   public boolean isSneaking() {
      return this.entity.isSneaking();
   }

   public void setSneaking(boolean sneaking) {
      this.entity.setSneaking(sneaking);
   }

   public boolean isInvisible() {
      return this.entity.isInvisible();
   }

   public ITeam getTeam() {
      return CraftTweakerMC.getITeam(this.entity.getTeam());
   }

   public void setInvisible(boolean invisible) {
      this.entity.setInvisible(invisible);
   }

   public boolean isOnSameTeam(IEntity other) {
      return this.entity.isOnSameTeam(CraftTweakerMC.getEntity(other));
   }

   public void setInWeb() {
      this.entity.setInWeb();
   }

   public IEntity[] getParts() {
      Entity[] parts = this.entity.getParts();
      if (parts == null) {
         return new IEntity[0];
      } else {
         IEntity[] output = new IEntity[parts.length];

         for(int i = 0; i < parts.length; ++i) {
            output[i] = CraftTweakerMC.getIEntity(parts[i]);
         }

         return output;
      }
   }

   public boolean isEntityEqual(IEntity other) {
      return this.entity.isEntityEqual(CraftTweakerMC.getEntity(other));
   }

   public boolean canBeAttackedWithItem() {
      return this.entity.canBeAttackedWithItem();
   }

   public void update(IData data) {
      NBTConverter.updateMap(this.entity.getEntityData(), data);
   }

   public boolean onGround() {
      return this.entity.onGround;
   }

   public boolean equals(Object obj) {
      return obj instanceof MCEntity && this.entity.isEntityEqual(((MCEntity)obj).entity) || super.equals(obj);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.entity});
   }
}
