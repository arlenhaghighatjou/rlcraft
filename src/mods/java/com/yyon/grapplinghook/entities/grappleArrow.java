package com.yyon.grapplinghook.entities;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class grappleArrow extends EntityThrowable implements IEntityAdditionalSpawnData {
   public Entity shootingEntity = null;
   public int shootingEntityID;
   private boolean firstattach = false;
   public vec thispos;
   public boolean righthand = true;
   public boolean attached = false;
   public double pull;
   public double taut = 1.0D;
   public boolean ignoreFrustumCheck = true;
   public boolean isdouble = false;
   public double r;
   public SegmentHandler segmenthandler = null;
   public GrappleCustomization customization = null;
   public vec prevpos = null;
   public boolean foundblock = false;
   public boolean wasinair = false;
   public BlockPos magnetblock = null;
   public final int RenderBoundingBoxSize = 999;

   public grappleArrow(World worldIn) {
      super(worldIn);
      this.segmenthandler = new SegmentHandler(this.world, this, vec.positionvec(this), vec.positionvec(this));
      this.customization = new GrappleCustomization();
   }

   public grappleArrow(World worldIn, EntityLivingBase shooter, boolean righthand, GrappleCustomization customization, boolean isdouble) {
      super(worldIn, shooter);
      this.shootingEntity = shooter;
      this.shootingEntityID = this.shootingEntity.getEntityId();
      this.isdouble = isdouble;
      vec pos = vec.positionvec(this.shootingEntity).add(new vec(0.0D, (double)this.shootingEntity.getEyeHeight(), 0.0D));
      this.segmenthandler = new SegmentHandler(this.world, this, new vec(pos), new vec(pos));
      this.customization = customization;
      this.r = customization.maxlen;
      this.righthand = righthand;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.shootingEntityID != 0 && this.shootingEntity != null) {
         if (this.firstattach) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.firstattach = false;
            super.setPosition(this.thispos.x, this.thispos.y, this.thispos.z);
         }

         if (!this.world.isRemote && this.shootingEntity != null && !this.attached) {
            if (this.segmenthandler.hookpastbend(this.r)) {
               System.out.println("around bend");
               vec farthest = this.segmenthandler.getfarthest();
               this.serverAttach(this.segmenthandler.getbendblock(1), farthest, (EnumFacing)null);
            }

            if (this.customization.phaserope) {
               this.segmenthandler.updatepos(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0.0D, (double)this.shootingEntity.getEyeHeight(), 0.0D)), this.r);
            } else {
               this.segmenthandler.update(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0.0D, (double)this.shootingEntity.getEyeHeight(), 0.0D)), this.r, true);
               if (this.customization.sticky && this.segmenthandler.segments.size() > 2) {
                  int bendnumber = this.segmenthandler.segments.size() - 2;
                  vec closest = (vec)this.segmenthandler.segments.get(bendnumber);
                  BlockPos blockpos = this.segmenthandler.getbendblock(bendnumber);

                  for(int i = 1; i <= bendnumber; ++i) {
                     this.segmenthandler.removesegment(1);
                  }

                  this.serverAttach(blockpos, closest, (EnumFacing)null);
               }
            }

            vec farthest = this.segmenthandler.getfarthest();
            double distToFarthest = this.segmenthandler.getDistToFarthest();
            vec ropevec = vec.positionvec(this).sub(farthest);
            double d = ropevec.length();
            if (this.customization.reelin && this.shootingEntity.isSneaking()) {
               double newdist = d + distToFarthest - 0.4D;
               if (newdist > 1.0D && newdist <= this.customization.maxlen) {
                  this.r = newdist;
               }
            }

            if (d + distToFarthest > this.r) {
               vec motion = vec.motionvec(this);
               if (motion.dot(ropevec) > 0.0D) {
                  motion = motion.removealong(ropevec);
               }

               this.setVelocityActually(motion.x, motion.y, motion.z);
               ropevec.changelen_ip(this.r - distToFarthest);
               vec newpos = ropevec.add(farthest);
               this.setPosition(newpos.x, newpos.y, newpos.z);
            }
         }

         if (this.customization.attract && vec.positionvec(this).sub(vec.positionvec(this.shootingEntity)).length() > this.customization.attractradius && !this.foundblock && !this.world.isRemote) {
            vec playerpos = vec.positionvec(this.shootingEntity);
            vec pos = vec.positionvec(this);
            if (this.magnetblock == null && this.prevpos != null) {
               HashMap<BlockPos, Boolean> checkedset = new HashMap();
               vec vector = pos.sub(this.prevpos);
               vec normvector = vector.normalize();

               for(int i = 0; (double)i < vector.length(); ++i) {
                  double dist = this.prevpos.sub(playerpos).length();
                  int radius = (int)dist / 4;
                  BlockPos found = this.check(this.prevpos, checkedset);
                  if (found != null) {
                     vec distvec = new vec((double)found.getX(), (double)found.getY(), (double)found.getZ());
                     distvec.sub_ip(this.prevpos);
                     if (distvec.length() < (double)radius) {
                        this.setPositionAndUpdate(this.prevpos.x, this.prevpos.y, this.prevpos.z);
                        pos = this.prevpos;
                        this.magnetblock = found;
                        break;
                     }
                  } else {
                     this.wasinair = true;
                  }

                  this.prevpos.add_ip(normvector);
               }
            }

            if (this.magnetblock != null) {
               IBlockState blockstate = this.world.getBlockState(this.magnetblock);
               AxisAlignedBB BB = blockstate.getCollisionBoundingBox(this.world, this.magnetblock);
               vec blockvec = new vec((double)this.magnetblock.getX() + (BB.maxX + BB.minX) / 2.0D, (double)this.magnetblock.getY() + (BB.maxY + BB.minY) / 2.0D, (double)this.magnetblock.getZ() + (BB.maxZ + BB.minZ) / 2.0D);
               vec newvel = blockvec.sub(pos);
               double l = newvel.length();
               newvel.changelen((double)this.getVelocity());
               this.motionX = newvel.x;
               this.motionY = newvel.y;
               this.motionZ = newvel.z;
               if (l < 0.2D) {
                  this.serverAttach(this.magnetblock, blockvec, EnumFacing.UP);
               }
            }

            this.prevpos = pos;
         }

      } else {
         this.remove();
      }
   }

   public void setVelocityActually(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(x * x + z * z);
         this.rotationYaw = (float)(MathHelper.atan2(x, z) * 57.29577951308232D);
         this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * 57.29577951308232D);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRender3d(double x, double y, double z) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      if (this.shootingEntity == null) {
         return super.getRenderBoundingBox();
      } else {
         AxisAlignedBB bb = this.segmenthandler.getBoundingBox(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0.0D, (double)this.shootingEntity.getEyeHeight(), 0.0D)));
         return bb;
      }
   }

   public void setPosition(double x, double y, double z) {
      if (this.thispos != null) {
         x = this.thispos.x;
         y = this.thispos.y;
         z = this.thispos.z;
      }

      super.setPosition(x, y, z);
   }

   public void writeSpawnData(ByteBuf data) {
      data.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : 0);
      data.writeBoolean(this.righthand);
      data.writeBoolean(this.isdouble);
      if (this.customization == null) {
         System.out.println("error: customization null");
      }

      this.customization.writeToBuf(data);
   }

   public void readSpawnData(ByteBuf data) {
      this.shootingEntityID = data.readInt();
      this.shootingEntity = this.world.getEntityByID(this.shootingEntityID);
      this.righthand = data.readBoolean();
      this.isdouble = data.readBoolean();
      this.customization = new GrappleCustomization();
      this.customization.readFromBuf(data);
   }

   public void remove() {
      this.setDead();
   }

   public String toString() {
      return super.toString() + String.valueOf(System.identityHashCode(this)) + "]";
   }

   protected void onImpact(RayTraceResult movingobjectposition) {
      if (!this.world.isRemote) {
         if (this.attached) {
            return;
         }

         if (this.shootingEntityID != 0) {
            if (movingobjectposition == null) {
               return;
            }

            Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (movingobjectposition.typeOfHit == Type.ENTITY && !GrappleConfig.getconf().hookaffectsentities) {
               this.onImpact(this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false));
               return;
            }

            if (grapplemod.anyignoresblocks && movingobjectposition.typeOfHit == Type.BLOCK) {
               BlockPos blockpos = movingobjectposition.getBlockPos();
               if (blockpos != null) {
                  Block block = this.world.getBlockState(blockpos).getBlock();
                  if (grapplemod.grapplingignoresblocks.contains(block)) {
                     System.out.println("ignore block");
                     this.onImpact(this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false));
                     return;
                  }
               }
            }

            if (grapplemod.anybreakblocks && movingobjectposition.typeOfHit == Type.BLOCK) {
               BlockPos blockpos = movingobjectposition.getBlockPos();
               if (blockpos != null) {
                  Block block = this.world.getBlockState(blockpos).getBlock();
                  if (grapplemod.grapplingbreaksblocks.contains(block)) {
                     this.world.destroyBlock(blockpos, true);
                     System.out.println("break block");
                     this.onImpact(this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false));
                     return;
                  }
               }
            }

            if (movingobjectposition.typeOfHit == Type.ENTITY) {
               Entity entityhit = movingobjectposition.entityHit;
               if (entityhit == this.shootingEntity) {
                  return;
               }

               vec playerpos = vec.positionvec(this.shootingEntity);
               vec entitypos = vec.positionvec(entityhit);
               vec yank = playerpos.sub(entitypos).mult(0.4D);
               entityhit.addVelocity(yank.x, Math.min(yank.y, 2.0D), yank.z);
               this.removeServer();
               return;
            }

            if (movingobjectposition.typeOfHit == Type.BLOCK) {
               BlockPos blockpos = movingobjectposition.getBlockPos();
               vec vec3 = new vec(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z);
               this.serverAttach(blockpos, vec3, movingobjectposition.sideHit);
            } else {
               System.out.println("unknown impact?");
            }
         }
      }

   }

   public void serverAttach(BlockPos blockpos, vec pos, EnumFacing sideHit) {
      if (!this.attached) {
         this.attached = true;
         if (blockpos != null && !grapplemod.anyblocks) {
            Block block = this.world.getBlockState(blockpos).getBlock();
            if (!grapplemod.removeblocks && !grapplemod.grapplingblocks.contains(block) || grapplemod.removeblocks && grapplemod.grapplingblocks.contains(block)) {
               this.removeServer();
               return;
            }
         }

         vec vec3 = vec.positionvec(this);
         vec3.add_ip(vec.motionvec(this));
         if (pos != null) {
            this.setPositionAndUpdate(pos.x, pos.y, pos.z);
         }

         if (sideHit == EnumFacing.DOWN) {
            this.posY -= 0.3D;
         } else if (sideHit == EnumFacing.WEST) {
            this.posX -= 0.05D;
         } else if (sideHit == EnumFacing.NORTH) {
            this.posZ -= 0.05D;
         } else if (sideHit == EnumFacing.SOUTH) {
            this.posZ += 0.05D;
         } else if (sideHit == EnumFacing.EAST) {
            this.posX += 0.05D;
         } else if (sideHit == EnumFacing.UP) {
            this.posY += 0.05D;
         }

         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
         this.thispos = vec.positionvec(this);
         this.firstattach = true;
         grapplemod.attached.add(Integer.valueOf(this.shootingEntityID));
         grapplemod.sendtocorrectclient(new GrappleAttachMessage(this.getEntityId(), this.posX, this.posY, this.posZ, this.getControlId(), this.shootingEntityID, blockpos, this.segmenthandler.segments, this.segmenthandler.segmenttopsides, this.segmenthandler.segmentbottomsides, this.customization), this.shootingEntityID, this.world);
         if (this.shootingEntity instanceof EntityPlayerMP) {
            EntityPlayerMP sender = (EntityPlayerMP)this.shootingEntity;
            int dimension = sender.dimension;
            MinecraftServer minecraftServer = sender.mcServer;

            for(EntityPlayerMP player : minecraftServer.getPlayerList().getPlayers()) {
               GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getEntityId(), this.posX, this.posY, this.posZ);
               if (dimension == player.dimension) {
                  grapplemod.sendtocorrectclient(msg, player.getEntityId(), player.world);
               }
            }
         }

      }
   }

   public void clientAttach(double x, double y, double z) {
      this.setAttachPos(x, y, z);
      if (this.shootingEntity instanceof EntityPlayer) {
         grapplemod.proxy.resetlaunchertime(this.shootingEntityID);
      }

   }

   protected float getGravityVelocity() {
      return (float)this.customization.hookgravity * 0.1F;
   }

   public float getVelocity() {
      return (float)this.customization.throwspeed;
   }

   public void removeServer() {
      this.setDead();
      this.shootingEntityID = 0;
   }

   public int getControlId() {
      return grapplemod.GRAPPLEID;
   }

   public void setAttachPos(double x, double y, double z) {
      this.setPositionAndUpdate(x, y, z);
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
      this.firstattach = true;
      this.attached = true;
      this.thispos = new vec(x, y, z);
   }

   public BlockPos check(vec p, HashMap<BlockPos, Boolean> checkedset) {
      int radius = (int)Math.floor(this.customization.attractradius);
      BlockPos closestpos = null;
      double closestdist = 0.0D;

      for(int x = (int)p.x - radius; x <= (int)p.x + radius; ++x) {
         for(int y = (int)p.y - radius; y <= (int)p.y + radius; ++y) {
            for(int z = (int)p.z - radius; z <= (int)p.z + radius; ++z) {
               BlockPos pos = new BlockPos(x, y, z);
               if (pos != null && this.hasblock(pos, checkedset)) {
                  vec distvec = new vec((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                  distvec.sub_ip(p);
                  double dist = distvec.length();
                  if (closestpos == null || dist < closestdist) {
                     closestpos = pos;
                     closestdist = dist;
                  }
               }
            }
         }
      }

      return closestpos;
   }

   public boolean hasblock(BlockPos pos, HashMap<BlockPos, Boolean> checkedset) {
      if (checkedset.containsKey(pos)) {
         return ((Boolean)checkedset.get(pos)).booleanValue();
      } else {
         boolean isblock = false;
         IBlockState blockstate = this.world.getBlockState(pos);
         Block b = blockstate.getBlock();
         if ((grapplemod.anyblocks || (grapplemod.removeblocks || grapplemod.grapplingblocks.contains(b)) && (!grapplemod.removeblocks || !grapplemod.grapplingblocks.contains(b))) && !b.isAir(blockstate, this.world, pos)) {
            AxisAlignedBB BB = blockstate.getCollisionBoundingBox(this.world, pos);
            if (BB != null) {
               isblock = true;
            }
         }

         checkedset.put(pos, Boolean.valueOf(isblock));
         return isblock;
      }
   }
}
