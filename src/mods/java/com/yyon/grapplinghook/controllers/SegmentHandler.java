package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.SegmentMessage;
import java.util.LinkedList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class SegmentHandler {
   public LinkedList<vec> segments = new LinkedList();
   public LinkedList<EnumFacing> segmentbottomsides;
   public LinkedList<EnumFacing> segmenttopsides;
   public World world;
   public grappleArrow arrow;
   vec prevhookpos = null;
   vec prevplayerpos = null;
   final double bendoffset = 0.05D;
   final double intoblock = 0.05D;
   double ropelen;

   public SegmentHandler(World w, grappleArrow arrow, vec hookpos, vec playerpos) {
      this.segments.add(hookpos);
      this.segments.add(playerpos);
      this.segmentbottomsides = new LinkedList();
      this.segmentbottomsides.add(null);
      this.segmentbottomsides.add(null);
      this.segmenttopsides = new LinkedList();
      this.segmenttopsides.add(null);
      this.segmenttopsides.add(null);
      this.world = w;
      this.arrow = arrow;
      this.prevhookpos = new vec(hookpos);
      this.prevplayerpos = new vec(playerpos);
   }

   public void forceSetPos(vec hookpos, vec playerpos) {
      this.prevhookpos = new vec(hookpos);
      this.prevplayerpos = new vec(playerpos);
      this.segments.set(0, new vec(hookpos));
      this.segments.set(this.segments.size() - 1, new vec(playerpos));
   }

   public void updatepos(vec hookpos, vec playerpos, double ropelen) {
      this.segments.set(0, hookpos);
      this.segments.set(this.segments.size() - 1, playerpos);
      this.ropelen = ropelen;
   }

   public void update(vec hookpos, vec playerpos, double ropelen, boolean movinghook) {
      if (this.prevhookpos == null) {
         this.prevhookpos = hookpos;
         this.prevplayerpos = playerpos;
      }

      this.segments.set(0, hookpos);
      this.segments.set(this.segments.size() - 1, playerpos);
      this.ropelen = ropelen;
      vec closest = (vec)this.segments.get(this.segments.size() - 2);

      while(this.segments.size() != 2) {
         int index = this.segments.size() - 2;
         closest = (vec)this.segments.get(index);
         EnumFacing bottomside = (EnumFacing)this.segmentbottomsides.get(index);
         EnumFacing topside = (EnumFacing)this.segmenttopsides.get(index);
         vec ropevec = playerpos.sub(closest);
         vec beforepoint = (vec)this.segments.get(index - 1);
         vec edgevec = this.getnormal(bottomside).cross(this.getnormal(topside));
         vec planenormal = beforepoint.sub(closest).cross(edgevec);
         if (ropevec.dot(planenormal) <= 0.0D) {
            break;
         }

         this.removesegment(index);
      }

      vec farthest = (vec)this.segments.get(1);
      if (movinghook) {
         while(this.segments.size() != 2) {
            int index = 1;
            farthest = (vec)this.segments.get(index);
            EnumFacing bottomside = (EnumFacing)this.segmentbottomsides.get(index);
            EnumFacing topside = (EnumFacing)this.segmenttopsides.get(index);
            vec ropevec = farthest.sub(hookpos);
            vec beforepoint = (vec)this.segments.get(index + 1);
            vec edgevec = this.getnormal(bottomside).cross(this.getnormal(topside));
            vec planenormal = beforepoint.sub(farthest).cross(edgevec);
            if (ropevec.dot(planenormal) <= 0.0D && ropevec.length() >= 0.1D) {
               break;
            }

            this.removesegment(index);
         }

         while(this.getDistToFarthest() > ropelen) {
            this.removesegment(1);
         }
      }

      if (movinghook) {
         farthest = (vec)this.segments.get(1);
         vec prevfarthest = farthest;
         if (this.segments.size() == 2) {
            prevfarthest = this.prevplayerpos;
         }

         this.updatesegment(hookpos, this.prevhookpos, farthest, prevfarthest, 1, 0);
      }

      vec prevclosest = closest;
      if (this.segments.size() == 2) {
         prevclosest = this.prevhookpos;
      }

      this.updatesegment(closest, prevclosest, playerpos, this.prevplayerpos, this.segments.size() - 1, 0);
      this.prevhookpos = hookpos;
      this.prevplayerpos = playerpos;
   }

   public void removesegment(int index) {
      this.segments.remove(index);
      this.segmentbottomsides.remove(index);
      this.segmenttopsides.remove(index);
      if (!this.world.isRemote) {
         SegmentMessage addmessage = new SegmentMessage(this.arrow.getEntityId(), false, index, new vec(0.0D, 0.0D, 0.0D), EnumFacing.DOWN, EnumFacing.DOWN);
         vec playerpoint = vec.positionvec(this.arrow.shootingEntity);
         grapplemod.network.sendToAllAround(addmessage, new TargetPoint(this.world.provider.getDimension(), playerpoint.x, playerpoint.y, playerpoint.z, 100.0D));
      }

   }

   public void updatesegment(vec top, vec prevtop, vec bottom, vec prevbottom, int index, int numberrecursions) {
      RayTraceResult bottomraytraceresult = this.world.rayTraceBlocks(bottom.toVec3d(), top.toVec3d(), false, true, false);
      if (bottomraytraceresult != null) {
         if (this.world.rayTraceBlocks(prevbottom.toVec3d(), prevtop.toVec3d(), false, true, false) != null) {
            return;
         }

         vec bottomhitvec = new vec(bottomraytraceresult.hitVec.x, bottomraytraceresult.hitVec.y, bottomraytraceresult.hitVec.z);
         EnumFacing bottomside = bottomraytraceresult.sideHit;
         vec bottomnormal = this.getnormal(bottomside);
         double prevropelen = prevtop.sub(prevbottom).length();
         vec cornerbound1 = bottomhitvec.add(bottomnormal.changelen(-0.05D));
         vec bound_option1 = this.line_plane_intersection(prevtop, prevbottom, cornerbound1, bottomnormal);
         vec bound_option2 = this.line_plane_intersection(top, prevtop, cornerbound1, bottomnormal);
         vec bound_option3 = this.line_plane_intersection(prevbottom, bottom, cornerbound1, bottomnormal);

         for(vec cornerbound2 : new vec[]{bound_option1, bound_option2, bound_option3}) {
            if (cornerbound2 != null) {
               RayTraceResult cornerraytraceresult = this.world.rayTraceBlocks(cornerbound2.toVec3d(), cornerbound1.toVec3d(), false, true, false);
               if (cornerraytraceresult != null) {
                  vec cornerhitpos = new vec(cornerraytraceresult.hitVec.x, cornerraytraceresult.hitVec.y, cornerraytraceresult.hitVec.z);
                  EnumFacing cornerside = cornerraytraceresult.sideHit;
                  if (cornerside != bottomside && cornerside.getOpposite() != bottomside) {
                     vec actualcorner = cornerhitpos.add(bottomnormal.changelen(0.05D));
                     vec bend = actualcorner.add(bottomnormal.changelen(0.05D)).add(this.getnormal(cornerside).changelen(0.05D));
                     vec topropevec = bend.sub(top);
                     vec bottomropevec = bend.sub(bottom);
                     if ((topropevec.length() >= 0.05D || this.segmentbottomsides.get(index - 1) != bottomside || this.segmenttopsides.get(index - 1) != cornerside) && (bottomropevec.length() >= 0.05D || this.segmentbottomsides.get(index) != bottomside || this.segmenttopsides.get(index) != cornerside)) {
                        this.actuallyaddsegment(index, bend, bottomside, cornerside);
                        if (this.getDistToAnchor() + 0.2D <= this.ropelen) {
                           double newropelen = topropevec.length() + bottomropevec.length();
                           double prevtoptobend = topropevec.length() * prevropelen / newropelen;
                           vec prevbend = prevtop.add(prevbottom.sub(prevtop).changelen(prevtoptobend));
                           if (numberrecursions < 10) {
                              this.updatesegment(top, prevtop, bend, prevbend, index, numberrecursions + 1);
                           } else {
                              System.out.println("Warning: number recursions exceeded");
                           }
                           break;
                        }

                        this.removesegment(index);
                     }
                  }
               }
            }
         }
      }

   }

   public vec line_plane_intersection(vec linepoint1, vec linepoint2, vec planepoint, vec planenormal) {
      vec linevec = linepoint2.sub(linepoint1);
      if (linevec.dot(planenormal) == 0.0D) {
         return null;
      } else {
         double d = planepoint.sub(linepoint1).dot(planenormal) / linevec.dot(planenormal);
         return linepoint1.add(linevec.mult(d));
      }
   }

   public vec getnormal(EnumFacing facing) {
      Vec3i facingvec = facing.getDirectionVec();
      return new vec((double)facingvec.getX(), (double)facingvec.getY(), (double)facingvec.getZ());
   }

   public boolean hookpastbend(double ropelen) {
      return this.getDistToFarthest() > ropelen;
   }

   public BlockPos getbendblock(int index) {
      vec bendpos = (vec)this.segments.get(index);
      vec var10001 = this.getnormal((EnumFacing)this.segmentbottomsides.get(index));
      this.getClass();
      bendpos.add_ip(var10001.changelen(-0.05D * 2.0D));
      var10001 = this.getnormal((EnumFacing)this.segmenttopsides.get(index));
      this.getClass();
      bendpos.add_ip(var10001.changelen(-0.05D * 2.0D));
      return new BlockPos(bendpos.x, bendpos.y, bendpos.z);
   }

   public void actuallyaddsegment(int index, vec bendpoint, EnumFacing bottomside, EnumFacing topside) {
      this.segments.add(index, bendpoint);
      this.segmentbottomsides.add(index, bottomside);
      this.segmenttopsides.add(index, topside);
      if (!this.world.isRemote) {
         SegmentMessage addmessage = new SegmentMessage(this.arrow.getEntityId(), true, index, bendpoint, topside, bottomside);
         vec playerpoint = vec.positionvec(this.arrow.shootingEntity);
         grapplemod.network.sendToAllAround(addmessage, new TargetPoint(this.world.provider.getDimension(), playerpoint.x, playerpoint.y, playerpoint.z, 100.0D));
      }

   }

   public void print() {
      for(int i = 1; i < this.segments.size() - 1; ++i) {
         System.out.print(i);
         System.out.print(" ");
         System.out.print(((EnumFacing)this.segmenttopsides.get(i)).toString());
         System.out.print(" ");
         System.out.print(((EnumFacing)this.segmentbottomsides.get(i)).toString());
         System.out.print(" ");
         ((vec)this.segments.get(i)).print();
      }

   }

   public vec getclosest(vec hookpos) {
      this.segments.set(0, hookpos);
      return (vec)this.segments.get(this.segments.size() - 2);
   }

   public double getDistToAnchor() {
      double dist = 0.0D;

      for(int i = 0; i < this.segments.size() - 2; ++i) {
         dist += ((vec)this.segments.get(i)).sub((vec)this.segments.get(i + 1)).length();
      }

      return dist;
   }

   public vec getfarthest() {
      return (vec)this.segments.get(1);
   }

   public double getDistToFarthest() {
      double dist = 0.0D;

      for(int i = 1; i < this.segments.size() - 1; ++i) {
         dist += ((vec)this.segments.get(i)).sub((vec)this.segments.get(i + 1)).length();
      }

      return dist;
   }

   public double getDist(vec hookpos, vec playerpos) {
      this.segments.set(0, hookpos);
      this.segments.set(this.segments.size() - 1, playerpos);
      double dist = 0.0D;

      for(int i = 0; i < this.segments.size() - 1; ++i) {
         dist += ((vec)this.segments.get(i)).sub((vec)this.segments.get(i + 1)).length();
      }

      return dist;
   }

   public AxisAlignedBB getBoundingBox(vec hookpos, vec playerpos) {
      this.updatepos(hookpos, playerpos, this.ropelen);
      vec minvec = new vec(hookpos);
      vec maxvec = new vec(hookpos);

      for(int i = 1; i < this.segments.size(); ++i) {
         vec segpos = (vec)this.segments.get(i);
         if (segpos.x < minvec.x) {
            minvec.x = segpos.x;
         } else if (segpos.x > maxvec.x) {
            maxvec.x = segpos.x;
         }

         if (segpos.y < minvec.y) {
            minvec.y = segpos.y;
         } else if (segpos.y > maxvec.y) {
            maxvec.y = segpos.y;
         }

         if (segpos.z < minvec.z) {
            minvec.z = segpos.z;
         } else if (segpos.z > maxvec.z) {
            maxvec.z = segpos.z;
         }
      }

      AxisAlignedBB bb = new AxisAlignedBB(minvec.x, minvec.y, minvec.z, maxvec.x, maxvec.y, maxvec.z);
      return bb;
   }
}
