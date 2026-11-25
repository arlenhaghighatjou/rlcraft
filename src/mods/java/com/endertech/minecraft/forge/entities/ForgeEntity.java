package com.endertech.minecraft.forge.entities;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.endertech.minecraft.forge.blocks.ForgeBlock;
import com.endertech.minecraft.forge.math.ForgeMath;
import com.endertech.minecraft.forge.math.Rotation;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.forge.world.ForgeWorld;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class ForgeEntity extends Entity {
   public ForgeEntity(World world) {
      super(world);
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound tag) {
   }

   protected void writeEntityToNBT(NBTTagCompound tag) {
   }

   public Vect3d getBBCenter() {
      return ForgeMath.getBBCenter(this.getBB());
   }

   public static Vect3d getCenteredPosTo(BlockPos pos) {
      return ForgeWorld.getBlockCenter(pos).withY((double)pos.getY());
   }

   public static Vect3d getCenterPosition(Entity entity) {
      return ForgeMath.getBBCenter(getBB(entity));
   }

   public static Vect3d getCenterPosition(Entity entity, float tickProgress) {
      return ForgeMath.getBBCenter(getBB(entity, getCurPosition(entity, tickProgress)));
   }

   public static Entity findEntity(World world, int id) {
      return world != null ? world.getEntityByID(id) : null;
   }

   public static void setCollisionReduction(Entity entity, float factor) {
      if (entity != null) {
         entity.entityCollisionReduction = factor;
      }

   }

   public static Vect3d getMotion(Entity entity) {
      return entity != null ? Vect3d.from(entity.motionX, entity.motionY, entity.motionZ) : Vect3d.ZERO;
   }

   public Vect3d getMotion() {
      return getMotion(this);
   }

   public static void setMotion(Entity entity, Vect3d vec) {
      if (entity != null && vec != null) {
         boolean motionChanged = getMotion(entity).notEqual(vec);
         entity.motionX = vec.x;
         entity.motionY = vec.y;
         entity.motionZ = vec.z;
         if (motionChanged) {
            entity.velocityChanged = true;
         }
      }

   }

   public void setMotion(Vect3d vec) {
      setMotion(this, vec);
   }

   public void move() {
      this.setPrevPosition(this.getCurPosition());
      this.setCurPosition(this.getCurPosition().add(this.getMotion()));
   }

   public static void addMotion(Entity entity, Vect3d vec) {
      if (entity != null && vec != null) {
         setMotion(entity, getMotion(entity).add(vec));
      }

   }

   public void addMotion(Vect3d vec) {
      addMotion(this, vec);
   }

   public static void copyAllPositionsWithOffset(Entity source, Entity target, Vect3d offset) {
      if (source != null && target != null) {
         setPrevPosition(target, getPrevPosition(source).add(offset));
         setLastTickPosition(target, getLastTickPosition(source).add(offset));
         setCurPosition(target, getCurPosition(source).add(offset));
      }

   }

   public static BlockPos getPosition(Entity entity) {
      return new BlockPos(entity.posX, entity.posY, entity.posZ);
   }

   public static Vect3d getCurPosition(Entity entity) {
      return entity != null ? Vect3d.from(entity.posX, entity.posY, entity.posZ) : Vect3d.ZERO;
   }

   public static Vect3d getCurPosition(Entity entity, float tickProgress) {
      return Vect3d.approx(tickProgress, getLastTickPosition(entity), getCurPosition(entity));
   }

   public Vect3d getCurPosition() {
      return getCurPosition(this);
   }

   public Vect3d getCurPosition(float tickProgress) {
      return getCurPosition(this, tickProgress);
   }

   public static BlockPos getPositionEyes(Entity entity) {
      double y = entity.posY + (double)entity.getEyeHeight();
      BlockPos pos = new BlockPos(entity.posX, y, entity.posZ);
      return pos;
   }

   public static BlockPos getPositionTop(Entity entity) {
      double y = entity.posY + (double)entity.height;
      BlockPos pos = new BlockPos(entity.posX, y, entity.posZ);
      return pos;
   }

   public static Rotation getPrevRotation(Entity entity) {
      return entity != null ? new Rotation(entity.prevRotationPitch, entity.prevRotationYaw) : Rotation.ZERO;
   }

   public static Rotation getRotation(Entity entity) {
      return entity != null ? new Rotation(entity.rotationPitch, entity.rotationYaw) : Rotation.ZERO;
   }

   public static Rotation getRotation(Entity entity, float tickProgress) {
      return getPrevRotation(entity).approx(tickProgress, getRotation(entity));
   }

   public Rotation getRotation(float tickProgress) {
      return getRotation(this, tickProgress);
   }

   public Rotation getPrevRotation() {
      return getPrevRotation(this);
   }

   public Rotation getCurRotation() {
      return getRotation(this);
   }

   public static Vect3d getLastTickPosition(Entity entity) {
      return entity != null ? Vect3d.from(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ) : Vect3d.ZERO;
   }

   public Vect3d getLastTickPosition() {
      return getLastTickPosition(this);
   }

   public static Vect3d getPrevPosition(Entity entity) {
      return entity != null ? Vect3d.from(entity.prevPosX, entity.prevPosY, entity.prevPosZ) : Vect3d.ZERO;
   }

   public Vect3d getPrevPosition() {
      return getPrevPosition(this);
   }

   public static Vect3d getNextPosition(Entity entity) {
      return entity != null ? getCurPosition(entity).add(getMotion(entity)) : Vect3d.ZERO;
   }

   public static void swapPositionsOf(Entity entA, Entity entB) {
      if (entA != null && entB != null) {
         Vect3d prev = getPrevPosition(entA);
         Vect3d lastTick = getLastTickPosition(entA);
         Vect3d cur = getCurPosition(entA);
         setPrevPosition(entA, getPrevPosition(entB));
         setLastTickPosition(entA, getLastTickPosition(entB));
         setCurPosition(entA, getCurPosition(entB));
         setPrevPosition(entB, prev);
         setLastTickPosition(entB, lastTick);
         setCurPosition(entB, cur);
      }

   }

   public void swapPositionWith(Entity entity) {
      swapPositionsOf(this, entity);
   }

   public BlockPos getPosition() {
      return getPosition(this);
   }

   public Vect3d getNextPosition() {
      return getNextPosition(this);
   }

   public static void setCurPosition(Entity entity, Vect3d vec) {
      if (entity != null && vec != null) {
         entity.setPosition(vec.x, vec.y, vec.z);
      }

   }

   public static void setPosition_NoBB(Entity entity, Vect3d vec) {
      if (entity != null && vec != null) {
         entity.posX = vec.x;
         entity.posY = vec.y;
         entity.posZ = vec.z;
      }

   }

   public static void setLastTickPosition(Entity entity, Vect3d vec) {
      if (entity != null && vec != null) {
         entity.lastTickPosX = vec.x;
         entity.lastTickPosY = vec.y;
         entity.lastTickPosZ = vec.z;
      }

   }

   public void setLastTickPosition(Vect3d vec) {
      setLastTickPosition(this, vec);
   }

   public static void setPrevPosition(Entity entity, Vect3d vec) {
      setLastTickPosition(entity, vec);
      if (entity != null && vec != null) {
         entity.prevPosX = vec.x;
         entity.prevPosY = vec.y;
         entity.prevPosZ = vec.z;
      }

   }

   public void setPrevPosition(Vect3d vec) {
      setPrevPosition(this, vec);
   }

   public void setCurPosition(Vect3d vec) {
      setCurPosition(this, vec);
   }

   public static void setPrevRotation(Entity entity, Rotation rotation) {
      if (entity != null && rotation != null) {
         entity.prevRotationPitch = rotation.pitch;
         entity.prevRotationYaw = rotation.yaw;
      }

   }

   public void setPrevRotation(Rotation rotation) {
      setPrevRotation(this, rotation);
   }

   public static void setRotation(Entity entity, Rotation rotation) {
      if (entity != null && rotation != null) {
         entity.rotationPitch = rotation.pitch;
         entity.rotationYaw = rotation.yaw;
      }

   }

   public void setCurRotation(Rotation rotation) {
      setRotation(this, rotation);
   }

   public static void setAllPositions(Entity entity, Vect3d vec) {
      if (entity != null && vec != null) {
         entity.lastTickPosX = entity.prevPosX = entity.posX = vec.x;
         entity.lastTickPosY = entity.prevPosY = entity.posY = vec.y;
         entity.lastTickPosZ = entity.prevPosZ = entity.posZ = vec.z;
         setCurPosition(entity, vec);
      }

   }

   public void setAllPositions(Vect3d vec) {
      setAllPositions(this, vec);
   }

   public static void setAllRotations(Entity entity, Rotation rotation) {
      if (entity != null && rotation != null) {
         entity.prevRotationPitch = entity.rotationPitch = rotation.pitch;
         entity.prevRotationYaw = entity.rotationYaw = rotation.yaw;
      }

   }

   public void setAllRotations(Rotation rotation) {
      setAllRotations(this, rotation);
   }

   public static float getSize(Entity entity) {
      return entity != null ? entity.width * entity.height : 0.0F;
   }

   public static int getId(Entity entity) {
      return entity != null ? entity.getEntityId() : -1;
   }

   public int getId() {
      return getId(this);
   }

   public float getSize() {
      return getSize(this);
   }

   public static Collection<Entity> getLowestRidingWithAllPassengers(Entity entity) {
      Collection<Entity> collection = Collections.emptySet();
      if (entity != null) {
         Entity lowestRiding = entity.getLowestRidingEntity();
         collection = lowestRiding.getRecursivePassengers();
         collection.add(lowestRiding);
      }

      return collection;
   }

   public static float getWeight(Entity entity, boolean includeAllConnected) {
      float weight = 0.0F;
      if (entity != null) {
         if (includeAllConnected) {
            for(Entity connected : getLowestRidingWithAllPassengers(entity)) {
               weight += getWeight(connected, false);
            }
         } else {
            weight += getSize(entity);
         }
      }

      return weight;
   }

   public static EnumFacing getFacingTo(BlockPos targetPos, EntityLivingBase entity) {
      double dist = 2.0D;
      if (Math.abs(entity.posX - (double)targetPos.getX()) < 2.0D && Math.abs(entity.posZ - (double)targetPos.getZ()) < 2.0D) {
         double eyeY = entity.posY + (double)entity.getEyeHeight();
         if (eyeY - (double)targetPos.getY() > 2.0D) {
            return EnumFacing.DOWN;
         }

         if ((double)targetPos.getY() - eyeY > 0.0D) {
            return EnumFacing.UP;
         }
      }

      return entity.getHorizontalFacing();
   }

   public static void setFallDistance(Entity entity, float distance, boolean includeAllConnected) {
      if (entity != null) {
         if (includeAllConnected) {
            for(Entity connected : getLowestRidingWithAllPassengers(entity)) {
               setFallDistance(connected, distance, false);
            }
         } else {
            entity.fallDistance = distance;
         }
      }

   }

   public static void updateBB(Entity entity) {
      if (entity != null) {
         entity.setEntityBoundingBox(getBB(entity, getCurPosition(entity)));
      }

   }

   public static AxisAlignedBB getBB(Entity entity, Vect3d position) {
      if (entity != null && position != null) {
         double widthHalf = (double)entity.width / 2.0D;
         return new AxisAlignedBB(position.x - widthHalf, position.y, position.z - widthHalf, position.x + widthHalf, position.y + (double)entity.height, position.z + widthHalf);
      } else {
         return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      }
   }

   public static AxisAlignedBB getBB(Entity entity) {
      return getBB(entity, getCurPosition(entity));
   }

   public AxisAlignedBB getBB(Vect3d position) {
      return getBB(this, position);
   }

   public AxisAlignedBB getBB() {
      return getBB(this, this.getCurPosition());
   }

   public void updateBB() {
      updateBB(this);
   }

   public static void stopMoving(Entity entity) {
      if (entity != null) {
         setMotion(entity, Vect3d.ZERO);
      }

   }

   public void stopMoving() {
      stopMoving(this);
   }

   public static boolean isUnderRain(Entity entity) {
      BlockPos pos = getPositionTop(entity);
      return ForgeBlock.isUnderRain(entity.world, pos);
   }

   public static boolean isUnderOpenSky(Entity entity) {
      BlockPos pos = getPositionTop(entity);
      return ForgeBlock.isUnderOpenSky(entity.world, pos);
   }

   public static boolean isServerSide(Entity entity) {
      return entity != null ? ForgeWorld.isServerSide(entity.world) : false;
   }

   public boolean isServerSide() {
      return isServerSide(this);
   }

   public static boolean isClientSide(Entity entity) {
      return entity != null ? ForgeWorld.isClientSide(entity.world) : false;
   }

   public boolean isClientSide() {
      return isClientSide(this);
   }

   public List<Entity> getEntitiesWithinAABB(AxisAlignedBB aaBB) {
      return (List<Entity>)(this.world != null ? new ArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, aaBB)) : Collections.emptyList());
   }

   public List<Entity> getCollidedEntitiesIfMove(Vect3d motion) {
      List<Entity> collidedEntities = this.getEntitiesWithinAABB(this.getBB(this.getNextPosition()));
      collidedEntities.remove(this);
      return collidedEntities;
   }

   public static void setBB(Entity entity, Vect3d minPoint, Vect3d maxPoint) {
      if (entity != null && minPoint != null && maxPoint != null) {
         entity.setEntityBoundingBox(new AxisAlignedBB(minPoint.x, minPoint.y, minPoint.z, maxPoint.x, maxPoint.y, maxPoint.z));
      }

   }

   public void setBB(Vect3d minPoint, Vect3d maxPoint) {
      setBB(this, minPoint, maxPoint);
   }

   public static boolean hasSwingingPlayerPassenger(Entity entity) {
      if (entity != null) {
         for(Entity passenger : entity.getPassengers()) {
            if (passenger instanceof EntityPlayer && ((EntityPlayer)passenger).isSwingInProgress) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasSwingingPlayerPassenger() {
      return hasSwingingPlayerPassenger(this);
   }

   public static boolean hasStack(Entity entity, ItemStack stack) {
      if (entity instanceof EntityItem) {
         return ((EntityItem)entity).getItem().equals(stack);
      } else if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         return player.inventory.mainInventory.contains(stack) || player.inventory.offHandInventory.contains(stack) || player.inventory.armorInventory.contains(stack);
      } else {
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase)entity;

            for(ItemStack equipedStack : Iterables.concat(living.getHeldEquipment(), living.getArmorInventoryList())) {
               if (equipedStack.equals(stack)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Nullable
   public static RayTraceResult rayTraceBlockBeingLookedAt(Entity entity, float partialTicks, float maxDistance) {
      World world = entity.getEntityWorld();
      Vec3d vecEyes = entity.getPositionEyes(partialTicks);
      Vec3d vecLook = entity.getLook(partialTicks).scale((double)maxDistance);
      Vec3d vecTarget = vecEyes.add(vecLook);
      return world.rayTraceBlocks(vecEyes, vecTarget, true, false, true);
   }

   public static Iterable<ItemStack> getEquipmentOn(Entity entity) {
      List<ItemStack> list = new ArrayList();

      for(ItemStack stack : entity.getArmorInventoryList()) {
         if (!stack.isEmpty()) {
            list.add(stack);
         }
      }

      if (entity instanceof EntityPlayer && Loader.isModLoaded("baubles")) {
         IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer)entity);

         for(int slot = 0; slot < handler.getSlots(); ++slot) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               list.add(stack);
            }
         }
      }

      return list;
   }
}
