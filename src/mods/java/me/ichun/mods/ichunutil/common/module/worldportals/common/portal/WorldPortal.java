package me.ichun.mods.ichunutil.common.module.worldportals.common.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import me.ichun.mods.ichunutil.common.module.worldportals.common.packet.PacketEntityLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class WorldPortal {
   private EnumFacing faceOn;
   private EnumFacing upDir;
   private QuaternionFormula quaternionFormula;
   private AxisAlignedBB plane;
   private AxisAlignedBB flatPlane;
   public AxisAlignedBB scanRange;
   public AxisAlignedBB portalInsides;
   private float width;
   private float height;
   private Vec3d position;
   private BlockPos posBlock;
   private HashSet<AxisAlignedBB> collisions;
   private WorldPortal pair;
   public World world;
   public int time;
   public List<Entity> lastScanEntities = new ArrayList();
   public HashMap<Entity, Integer> teleportCooldown = new HashMap();
   private boolean firstUpdate;
   public boolean renderAll = false;

   public WorldPortal(World world) {
      this.world = world;
      this.position = new Vec3d(0.0D, 0.0D, 0.0D);
      this.posBlock = new BlockPos(this.position);
      this.faceOn = EnumFacing.NORTH;
      this.upDir = EnumFacing.UP;
      this.time = 0;
      this.firstUpdate = true;
   }

   public WorldPortal(World world, Vec3d position, EnumFacing faceOn, EnumFacing upDir, float width, float height) {
      this.world = world;
      this.position = position;
      this.posBlock = new BlockPos(this.position);
      this.faceOn = faceOn;
      this.upDir = upDir;
      this.width = width;
      this.height = height;
      this.setupAABBs();
      this.time = 0;
      this.firstUpdate = true;
   }

   public abstract float getPlaneOffset();

   public abstract boolean canCollideWithBorders();

   public abstract String owner();

   @SideOnly(Side.CLIENT)
   public abstract void drawPlane(float var1);

   public void setFace(EnumFacing faceOut, EnumFacing upDir) {
      this.faceOn = faceOut;
      this.upDir = upDir;
      this.setupAABBs();
   }

   public EnumFacing getFaceOn() {
      return this.faceOn;
   }

   public EnumFacing getUpDir() {
      return this.upDir;
   }

   public BlockPos getPos() {
      return this.posBlock;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }

   public void setSize(float width, float height) {
      this.width = width;
      this.height = height;
      this.setupAABBs();
   }

   public float getScanDistance() {
      return 3.0F;
   }

   public void updateWorldPortal() {
      if (this.firstUpdate) {
         this.firstUpdate = false;
      }

      ++this.time;
      if (this.canTeleportEntities()) {
         Iterator<Entry<Entity, Integer>> ite = this.teleportCooldown.entrySet().iterator();

         while(ite.hasNext()) {
            Entry<Entity, Integer> e = (Entry)ite.next();
            e.setValue(Integer.valueOf(((Integer)e.getValue()).intValue() - 1));
            if (((Integer)e.getValue()).intValue() < 0) {
               WorldPortals.eventHandler.removeMonitoredEntity((Entity)e.getKey(), this);
               ite.remove();
            }
         }

         if (this.hasPair()) {
            EnumFacing faceOn = this.getFaceOn();
            List<Entity> entitiesInRange = this.world.getEntitiesWithinAABB(Entity.class, this.scanRange);

            for(int i = entitiesInRange.size() - 1; i >= 0; --i) {
               Entity ent = (Entity)entitiesInRange.get(i);
               if (this.isAgainstWall()) {
                  WorldPortals.eventHandler.addMonitoredEntity(ent, this);
               }

               if (!this.canEntityTeleport(ent)) {
                  entitiesInRange.remove(i);
               } else if (!this.teleportCooldown.containsKey(ent) && (!(ent instanceof EntityPlayerMP) || ent.getEntityWorld().isRemote)) {
                  double[] motions = EntityHelper.simulateMoveEntity(ent, ent.motionX, ent.motionY, ent.motionZ);
                  Vec3d newEntPos = new Vec3d(ent.posX + motions[0], ent.posY + (double)ent.getEyeHeight() + motions[1], ent.posZ + motions[2]);
                  boolean teleport = false;
                  AxisAlignedBB teleportPlane = this.flatPlane;
                  float offset = 0.0F;
                  if (this.isAgainstWall() && ent instanceof EntityPlayer) {
                     offset = Math.min(0.05F, (float)Math.abs((this.flatPlane.minX - ent.posX) * (double)faceOn.getFrontOffsetX() + (this.flatPlane.minY - ent.posY) * (double)faceOn.getFrontOffsetY() + (this.flatPlane.minZ - ent.posZ) * (double)faceOn.getFrontOffsetZ()));
                     if (!this.scanRange.offset((double)((float)faceOn.getFrontOffsetX() * offset), (double)((float)faceOn.getFrontOffsetY() * offset), (double)((float)faceOn.getFrontOffsetZ() * offset)).contains(newEntPos) && this.portalInsides.offset((double)((float)faceOn.getFrontOffsetX() * offset), (double)((float)faceOn.getFrontOffsetY() * offset), (double)((float)faceOn.getFrontOffsetZ() * offset)).contains(newEntPos) && (faceOn.getAxis().isHorizontal() && ent.getEntityBoundingBox().minY >= this.flatPlane.minY && ent.getEntityBoundingBox().maxY <= this.flatPlane.maxY || faceOn.getAxis().isVertical() && ent.getEntityBoundingBox().minX >= this.flatPlane.minX && ent.getEntityBoundingBox().maxX <= this.flatPlane.maxX && ent.getEntityBoundingBox().minZ >= this.flatPlane.minZ && ent.getEntityBoundingBox().maxZ <= this.flatPlane.maxZ)) {
                        teleportPlane = this.getTeleportPlane(offset);
                        teleport = true;
                     }
                  } else if (!this.scanRange.contains(newEntPos) && this.portalInsides.contains(newEntPos)) {
                     teleport = true;
                  }

                  if (teleport) {
                     double centerX = (teleportPlane.maxX + teleportPlane.minX) / 2.0D;
                     double centerY = (teleportPlane.maxY + teleportPlane.minY) / 2.0D;
                     double centerZ = (teleportPlane.maxZ + teleportPlane.minZ) / 2.0D;
                     if (this.pair != null) {
                        float[] appliedOffset = this.getQuaternionFormula().applyPositionalRotation(new float[]{(float)(newEntPos.x - centerX), (float)(newEntPos.y - centerY), (float)(newEntPos.z - centerZ)});
                        float[] appliedMotion = this.getQuaternionFormula().applyPositionalRotation(new float[]{(float)motions[0], (float)motions[1], (float)motions[2]});
                        float[] appliedRotation = this.getQuaternionFormula().applyRotationalRotation(new float[]{ent.rotationYaw, ent.rotationPitch, ent.getEntityWorld().isRemote ? this.getRoll(ent) : 0.0F});
                        AxisAlignedBB pairTeleportPlane = this.pair.getTeleportPlane(offset);
                        double destX = (pairTeleportPlane.maxX + pairTeleportPlane.minX) / 2.0D;
                        double destY = (pairTeleportPlane.maxY + pairTeleportPlane.minY) / 2.0D;
                        double destZ = (pairTeleportPlane.maxZ + pairTeleportPlane.minZ) / 2.0D;
                        EntityTransformationStack ets = new EntityTransformationStack(ent);
                        ets.translate(destX - ent.posX + (double)appliedOffset[0], destY - (ent.posY + (double)ent.getEyeHeight()) + (double)appliedOffset[1], destZ - ent.posZ + (double)appliedOffset[2]);
                        ets.rotate(appliedRotation[0], appliedRotation[1], appliedRotation[2]);
                        ent.setPosition(ent.posX, ent.posY, ent.posZ);
                        double maxWidthHeight = (double)Math.max(ent.width, ent.height);
                        EntityHelper.putEntityWithinAABB(ent, this.pair.scanRange.expand((double)this.pair.getFaceOn().getFrontOffsetX() * -maxWidthHeight, (double)this.pair.getFaceOn().getFrontOffsetY() * -maxWidthHeight, (double)this.pair.getFaceOn().getFrontOffsetZ() * -maxWidthHeight));
                        ent.motionX = (double)appliedMotion[0];
                        ent.motionY = (double)appliedMotion[1];
                        ent.motionZ = (double)appliedMotion[2];
                        if (Math.abs(ent.motionX) > 0.99D) {
                           ent.motionX /= Math.abs(ent.motionX) + 0.001D;
                        }

                        if (Math.abs(ent.motionY) > 0.99D) {
                           ent.motionY /= Math.abs(ent.motionY) + 0.001D;
                        }

                        if (Math.abs(ent.motionZ) > 0.99D) {
                           ent.motionZ /= Math.abs(ent.motionZ) + 0.001D;
                        }

                        ent.fallDistance = 0.1F * ((float)ent.motionY / -0.1F * (float)ent.motionY / -0.1F);
                        ent.setPosition(ent.posX, ent.posY, ent.posZ);
                        this.pair.teleportCooldown.put(ent, Integer.valueOf(3));
                        this.pair.lastScanEntities.add(ent);
                        if (this.pair.isAgainstWall()) {
                           WorldPortals.eventHandler.addMonitoredEntity(ent, this.pair);
                        }

                        this.teleportCooldown.put(ent, Integer.valueOf(3));
                        this.lastScanEntities.remove(ent);
                        this.handleSpecialEntities(ent);
                        if (ent.getEntityWorld().isRemote) {
                           this.handleClientEntityTeleport(ent, appliedRotation);
                        } else {
                           WorldPortals.channel.sendToAllAround(new PacketEntityLocation(ent), new TargetPoint(ent.dimension, ent.posX, ent.posY, ent.posZ, 256.0D));
                        }
                     }
                  }
               }
            }

            if (this.world.isRemote) {
               this.handleClient();
            }

            if (this.isAgainstWall()) {
               this.lastScanEntities.removeAll(entitiesInRange);

               for(Entity ent : this.lastScanEntities) {
                  if (!this.teleportCooldown.containsKey(ent)) {
                     WorldPortals.eventHandler.removeMonitoredEntity(ent, this);
                  }
               }

               this.lastScanEntities = entitiesInRange;
            }

         }
      }
   }

   @SideOnly(Side.CLIENT)
   public float getRoll(Entity ent) {
      return ent == Minecraft.getMinecraft().getRenderViewEntity() ? WorldPortals.eventHandlerClient.cameraRoll : 0.0F;
   }

   public boolean canEntityTeleport(Entity ent) {
      return true;
   }

   public void handleSpecialEntities(Entity ent) {
      if (ent instanceof EntityBlock) {
         ((EntityBlock)ent).timeExisting = 2;
      } else if (ent instanceof EntityFallingBlock) {
         ((EntityFallingBlock)ent).fallTime = 2;
      } else if (ent instanceof EntityFireball) {
         EntityFireball fireball = (EntityFireball)ent;
         float[] appliedAcceleration = this.getQuaternionFormula().applyPositionalRotation(new float[]{(float)fireball.accelerationX, (float)fireball.accelerationY, (float)fireball.accelerationZ});
         fireball.accelerationX = (double)appliedAcceleration[0];
         fireball.accelerationY = (double)appliedAcceleration[1];
         fireball.accelerationZ = (double)appliedAcceleration[2];
      } else if (ent instanceof EntityArrow) {
         ((EntityArrow)ent).inGround = false;
      }

   }

   @SideOnly(Side.CLIENT)
   public void handleClient() {
      Minecraft mc = Minecraft.getMinecraft();
      EnumFacing faceOn = this.getFaceOn();

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 2; ++j) {
            for(Particle particle : mc.effectRenderer.fxLayers[i][j]) {
               Vec3d particlePos = new Vec3d(particle.prevPosX, particle.prevPosY, particle.prevPosZ);
               Vec3d newParticlePos = new Vec3d(particle.posX, particle.posY, particle.posZ);
               float offset = (float)Math.abs((particle.prevPosX - particle.posX) * (double)faceOn.getFrontOffsetX() * 1.5D + (particle.prevPosY - particle.posY) * (double)faceOn.getFrontOffsetY() * 1.5D + (particle.prevPosZ - particle.posZ) * (double)faceOn.getFrontOffsetZ() * 1.5D);
               boolean isRain = particle instanceof ParticleRain && faceOn == EnumFacing.UP && this.scanRange.contains(particlePos);
               if (isRain || !this.portalInsides.offset((double)((float)faceOn.getFrontOffsetX() * offset), (double)((float)faceOn.getFrontOffsetY() * offset), (double)((float)faceOn.getFrontOffsetZ() * offset)).intersects(particle.getBoundingBox()) && this.portalInsides.offset((double)((float)faceOn.getFrontOffsetX() * offset), (double)((float)faceOn.getFrontOffsetY() * offset), (double)((float)faceOn.getFrontOffsetZ() * offset)).intersects(particle.getBoundingBox().offset(particle.motionX, particle.motionY, particle.motionZ))) {
                  AxisAlignedBB teleportPlane = this.getTeleportPlane(offset);
                  double centerX = (teleportPlane.maxX + teleportPlane.minX) / 2.0D;
                  double centerY = (teleportPlane.maxY + teleportPlane.minY) / 2.0D;
                  double centerZ = (teleportPlane.maxZ + teleportPlane.minZ) / 2.0D;
                  if (this.pair != null) {
                     float[] appliedOffset = this.getQuaternionFormula().applyPositionalRotation(new float[]{(float)(newParticlePos.x - centerX), (float)(newParticlePos.y - centerY), (float)(newParticlePos.z - centerZ)});
                     float[] appliedMotion = this.getQuaternionFormula().applyPositionalRotation(new float[]{(float)(newParticlePos.x - particlePos.x), (float)(newParticlePos.y - particlePos.y), (float)(newParticlePos.z - particlePos.z)});
                     AxisAlignedBB pairTeleportPlane = this.pair.getTeleportPlane(offset);
                     double destX = (pairTeleportPlane.maxX + pairTeleportPlane.minX) / 2.0D;
                     double destY = (pairTeleportPlane.maxY + pairTeleportPlane.minY) / 2.0D;
                     double destZ = (pairTeleportPlane.maxZ + pairTeleportPlane.minZ) / 2.0D;
                     double x = destX - particle.posX + (double)appliedOffset[0];
                     double y = destY - particle.posY + (double)appliedOffset[1];
                     double z = destZ - particle.posZ + (double)appliedOffset[2];
                     particle.posX += x;
                     particle.posY += y;
                     particle.posZ += z;
                     particle.prevPosX += x;
                     particle.prevPosY += y;
                     particle.prevPosZ += z;
                     particle.setPosition(particle.posX, particle.posY, particle.posZ);
                     particle.motionX = (double)appliedMotion[0];
                     particle.motionY = (double)appliedMotion[1];
                     particle.motionZ = (double)appliedMotion[2];
                     if (isRain) {
                        particle.motionX *= 5.0D;
                        particle.motionY *= 5.0D;
                        particle.motionZ *= 5.0D;
                     }
                  }
               }
            }
         }
      }

   }

   public void terminate() {
      if (this.isAgainstWall()) {
         for(Entity ent : this.lastScanEntities) {
            if (ent.getEntityBoundingBox().intersects(this.portalInsides)) {
               EnumFacing faceOn = this.getFaceOn();
               EntityHelper.putEntityWithinAABB(ent, this.flatPlane.offset((double)faceOn.getFrontOffsetX() * 0.5D, (double)faceOn.getFrontOffsetY() * 0.5D, (double)faceOn.getFrontOffsetZ() * 0.5D));
               ent.setPosition(ent.posX, ent.posY, ent.posZ);
            }

            WorldPortals.eventHandler.removeMonitoredEntity(ent, this);
         }
      }

      if (this.hasPair()) {
         this.pair.setPair((WorldPortal)null);
         this.setPair((WorldPortal)null);
      }

   }

   public boolean isValid() {
      return !this.firstUpdate;
   }

   public boolean isFirstUpdate() {
      return this.firstUpdate;
   }

   public void forceFirstUpdate() {
      this.firstUpdate = true;
   }

   public boolean isAgainstWall() {
      return false;
   }

   private AxisAlignedBB createPlaneAround(double size) {
      return this.createPlaneAround(this.getPosition(), size);
   }

   private AxisAlignedBB createPlaneAround(Vec3d pos, double size) {
      double halfW = (double)this.width / 2.0D;
      double halfH = (double)this.height / 2.0D;
      AxisAlignedBB plane = new AxisAlignedBB(pos.x - halfW, pos.y - halfH, pos.z - size, pos.x + halfW, pos.y + halfH, pos.z + size);
      EnumFacing faceOn = this.getFaceOn();
      if (faceOn.getAxis() == Axis.Y) {
         plane = EntityHelper.rotateAABB(Axis.X, plane, faceOn == EnumFacing.UP ? -90.0D : 90.0D, pos.x, pos.y, pos.z);
      }

      plane = EntityHelper.rotateAABB(Axis.Y, plane, faceOn.getAxis() == Axis.X ? 90.0D : (faceOn.getAxis() == Axis.Y && this.getUpDir().getAxis() == Axis.X ? 90.0D : 0.0D), pos.x, pos.y, pos.z).offset((double)((float)faceOn.getFrontOffsetX() * this.getPlaneOffset()), (double)((float)faceOn.getFrontOffsetY() * this.getPlaneOffset()), (double)((float)faceOn.getFrontOffsetZ() * this.getPlaneOffset()));
      return plane;
   }

   public AxisAlignedBB getCollisionRemovalAabbForEntity(Entity ent) {
      double max = Math.max((double)Math.max(ent.width, ent.height) + Math.sqrt(ent.motionX * ent.motionX + ent.motionY * ent.motionY + ent.motionZ * ent.motionZ), 1.0D);
      EnumFacing faceOn = this.getFaceOn();
      return this.flatPlane.expand((double)faceOn.getFrontOffsetX() * -max, (double)faceOn.getFrontOffsetY() * -max, (double)faceOn.getFrontOffsetZ() * -max);
   }

   public AxisAlignedBB getPortalInsides(Entity ent) {
      if (this.isAgainstWall() && ent instanceof EntityPlayer) {
         EnumFacing faceOn = this.getFaceOn();
         float offset = Math.min(0.05F, (float)Math.abs((this.flatPlane.minX - ent.posX) * (double)faceOn.getFrontOffsetX() + (this.flatPlane.minY - ent.posY) * (double)faceOn.getFrontOffsetY() + (this.flatPlane.minZ - ent.posZ) * (double)faceOn.getFrontOffsetZ()));
         return this.portalInsides.offset((double)((float)faceOn.getFrontOffsetX() * offset), (double)((float)faceOn.getFrontOffsetY() * offset), (double)((float)faceOn.getFrontOffsetZ() * offset));
      } else {
         return this.portalInsides;
      }
   }

   public AxisAlignedBB getPlane() {
      return this.plane;
   }

   private void setupAABBs() {
      EnumFacing faceOn = this.getFaceOn();
      this.plane = this.createPlaneAround(0.0125D);
      this.flatPlane = this.createPlaneAround(0.0D);
      this.scanRange = this.flatPlane.expand((double)((float)faceOn.getFrontOffsetX() * this.getScanDistance()), (double)((float)faceOn.getFrontOffsetY() * this.getScanDistance()), (double)((float)faceOn.getFrontOffsetZ() * this.getScanDistance()));
      this.portalInsides = this.flatPlane.expand((double)faceOn.getFrontOffsetX() * -100.0D, (double)faceOn.getFrontOffsetY() * -100.0D, (double)faceOn.getFrontOffsetZ() * -100.0D);
   }

   public AxisAlignedBB getFlatPlane() {
      return this.flatPlane;
   }

   public AxisAlignedBB getTeleportPlane(float offset) {
      if (offset != 0.0F) {
         EnumFacing faceOn = this.getFaceOn();
         return this.flatPlane.offset((double)((float)faceOn.getFrontOffsetX() * offset), (double)((float)faceOn.getFrontOffsetY() * offset), (double)((float)faceOn.getFrontOffsetZ() * offset));
      } else {
         return this.flatPlane;
      }
   }

   public boolean getCullRender() {
      return !this.renderAll;
   }

   public void setCullRender(boolean flag) {
      this.renderAll = !flag;
   }

   public boolean canTeleportEntities() {
      return true;
   }

   public HashSet<AxisAlignedBB> getCollisionBoundaries() {
      if (this.collisions == null) {
         this.collisions = new HashSet(4);
         if (this.canCollideWithBorders()) {
            double size = 0.0125D;
            AxisAlignedBB plane = this.flatPlane;
            if (plane.maxX - plane.minX > size * 3.0D) {
               this.collisions.add(new AxisAlignedBB(plane.maxX, plane.minY, plane.minZ, plane.maxX + size, plane.maxY, plane.maxZ));
               this.collisions.add(new AxisAlignedBB(plane.minX - size, plane.minY, plane.minZ, plane.minX, plane.maxY, plane.maxZ));
            }

            if (plane.maxY - plane.minY > size * 3.0D) {
               this.collisions.add(new AxisAlignedBB(plane.minX, plane.maxY, plane.minZ, plane.maxX, plane.maxY + size, plane.maxZ));
               this.collisions.add(new AxisAlignedBB(plane.minX, plane.minY - size, plane.minZ, plane.maxX, plane.minY, plane.maxZ));
            }

            if (plane.maxZ - plane.minZ > size * 3.0D) {
               this.collisions.add(new AxisAlignedBB(plane.minX, plane.minY, plane.maxZ, plane.maxX, plane.maxY, plane.maxZ + size));
               this.collisions.add(new AxisAlignedBB(plane.minX, plane.minY, plane.minZ - size, plane.maxX, plane.maxY, plane.minZ));
            }
         }
      }

      return this.collisions;
   }

   public boolean hasPair() {
      return this.pair != null && this.pair.position.y > 0.0D;
   }

   public void setPair(WorldPortal portal) {
      if (this.pair != portal) {
         this.pair = portal;
         if (this.pair != null) {
            this.quaternionFormula = QuaternionFormula.createFromPlanes(this.getFaceOn(), this.getUpDir(), this.pair.getFaceOn(), this.pair.getUpDir());
         }
      }

   }

   public WorldPortal getPair() {
      return this.pair;
   }

   public void setPosition(Vec3d v) {
      this.position = v;
      this.posBlock = new BlockPos(v);
      this.setupAABBs();
   }

   public Vec3d getPosition() {
      return this.position;
   }

   public QuaternionFormula getQuaternionFormula() {
      return this.pair != null ? this.quaternionFormula : QuaternionFormula.NO_ROTATION;
   }

   public NBTTagCompound write(NBTTagCompound tag) {
      return this.writePair(this.writeSelf(tag));
   }

   public NBTTagCompound writeSelf(NBTTagCompound tag) {
      tag.setFloat("width", this.width);
      tag.setFloat("height", this.height);
      tag.setInteger("faceOn", this.faceOn.getIndex());
      tag.setInteger("up", this.upDir.getIndex());
      tag.setDouble("posX", this.position.x);
      tag.setDouble("posY", this.position.y);
      tag.setDouble("posZ", this.position.z);
      tag.setInteger("time", this.time);
      return tag;
   }

   public NBTTagCompound writePair(NBTTagCompound tag) {
      if (this.hasPair()) {
         tag.setTag("pair", this.pair.writeSelf(new NBTTagCompound()));
      }

      return tag;
   }

   public void read(NBTTagCompound tag) {
      this.readSelf(tag);
      this.readPair(tag);
   }

   public void readSelf(NBTTagCompound tag) {
      this.setSize(tag.getFloat("width"), tag.getFloat("height"));
      this.setFace(EnumFacing.getFront(tag.getInteger("faceOn")), EnumFacing.getFront(tag.getInteger("up")));
      this.setPosition(new Vec3d(tag.getDouble("posX"), tag.getDouble("posY"), tag.getDouble("posZ")));
      this.time = tag.getInteger("time");
      this.firstUpdate = true;
   }

   public void readPair(NBTTagCompound tag) {
      if (tag.hasKey("pair")) {
         this.setPair(this.createFakeInstance(tag.getCompoundTag("pair")));
      }

   }

   public abstract <T extends WorldPortal> T createFakeInstance(NBTTagCompound var1);

   @SideOnly(Side.CLIENT)
   public void handleClientEntityTeleport(Entity ent, float[] rotations) {
      if (ent == Minecraft.getMinecraft().player) {
         WorldPortals.eventHandlerClient.prevCameraRoll = WorldPortals.eventHandlerClient.cameraRoll = rotations[2];
         WorldPortals.channel.sendToServer(new PacketEntityLocation(ent));
      }

   }

   @SideOnly(Side.CLIENT)
   public boolean shouldRenderFront(Entity viewer, float partialTicks) {
      Vec3d position = RendererHelper.getCameraPosition(viewer, partialTicks);
      return !this.getCullRender() || this.faceOn.getFrontOffsetX() < 0 && position.x < this.flatPlane.minX || this.faceOn.getFrontOffsetX() > 0 && position.x > this.flatPlane.minX || this.faceOn.getFrontOffsetY() < 0 && position.y < this.flatPlane.minY || this.faceOn.getFrontOffsetY() > 0 && position.y > this.flatPlane.minY || this.faceOn.getFrontOffsetZ() < 0 && position.z < this.flatPlane.minZ || this.faceOn.getFrontOffsetZ() > 0 && position.z > this.flatPlane.minZ;
   }

   @SideOnly(Side.CLIENT)
   public int getRenderDistanceChunks() {
      return iChunUtil.config.renderDistanceChunks == 0 ? Minecraft.getMinecraft().gameSettings.renderDistanceChunks : iChunUtil.config.renderDistanceChunks;
   }
}
