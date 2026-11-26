package bl4ckscor3.mod.particleculling;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class CullThread {
   public CullThread() {
   }

   public void run() {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.world != null) {
         for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 2; ++j) {
               this.iterateParticles(mc, mc.effectRenderer.fxLayers[i][j]);
            }
         }
      }

   }

   private void iterateParticles(Minecraft mc, List<Particle> particles) {
      for(int i = 0, l = particles.size(); i < l; ++i) {
         Particle particle = particles.get(i);
         if (particle != null) {
            ((CullCheck)particle).setCulled(this.shouldCullParticle(particle, mc));
         }
      }

   }

   private boolean shouldCullParticle(Particle particle, Minecraft mc) {
      if (ParticleCulling.Configuration.cullingEnabled && (ParticleCulling.Configuration.cullInSpectator || !mc.player.isSpectator())) {
         ICamera camera = ((CameraHolder)mc.renderGlobal).getCamera();
         if (camera == null) {
            return false;
         } else {
            List<String> ignoredParticleClasses = ParticleCulling.Configuration.ignoredParticleClasses;
            for(int i = 0, l = ignoredParticleClasses.size(); i < l; ++i) {
               String ignoredParticleClass = ignoredParticleClasses.get(i);
               for(Class<?> clazz = particle.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
                  if (ignoredParticleClass.equals(clazz.getName())) {
                     return false;
                  }
               }
            }

            if (camera.isBoundingBoxInFrustum(particle.getBoundingBox())) {
               if (ParticleCulling.Configuration.cullBehindBlocks) {
                  Entity entity = mc.getRenderViewEntity();
                  if (entity != null) {
                     return this.shouldCull(entity.world, new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ), new Vec3d(particle.posX, particle.posY, particle.posZ));
                  }
               }

               return false;
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean shouldCull(World world, Vec3d from, Vec3d to) {
      if (!Double.isNaN(from.x) && !Double.isNaN(from.y) && !Double.isNaN(from.z) && !Double.isNaN(to.x) && !Double.isNaN(to.y) && !Double.isNaN(to.z)) {
         boolean opacityCheck = false;
         int blocks = 0;
         int toX = MathHelper.floor(to.x);
         int toY = MathHelper.floor(to.y);
         int toZ = MathHelper.floor(to.z);
         int checkX = MathHelper.floor(from.x);
         int checkY = MathHelper.floor(from.y);
         int checkZ = MathHelper.floor(from.z);
         MutableBlockPos pos = new MutableBlockPos(checkX, checkY, checkZ);
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         if (state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB && block.canCollideCheck(state, false) && state.collisionRayTrace(world, pos, from, to) != null) {
            ++blocks;
            opacityCheck = opacityCheck || ParticleCulling.Configuration.cullBehindGlass || state.isFullCube() && state.isOpaqueCube();
         }

         int maxIterations = 50;

         while(true) {
            if (maxIterations-- < 0) {
               return opacityCheck && blocks > ParticleCulling.Configuration.blockBuffer;
            }

            if (checkX == toX && checkY == toY && checkZ == toZ) {
               boolean var41;
               if (opacityCheck) {
                  ++blocks;
                  if (blocks > ParticleCulling.Configuration.blockBuffer) {
                     var41 = true;
                     return var41;
                  }
               }

               var41 = false;
               return var41;
            }

            boolean wasXChanged = true;
            boolean wasYChanged = true;
            boolean wasZChanged = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;
            if (toX > checkX) {
               d0 = (double)checkX + 1.0D;
            } else if (toX < checkX) {
               d0 = (double)checkX + 0.0D;
            } else {
               wasXChanged = false;
            }

            if (toY > checkY) {
               d1 = (double)checkY + 1.0D;
            } else if (toY < checkY) {
               d1 = (double)checkY + 0.0D;
            } else {
               wasYChanged = false;
            }

            if (toZ > checkZ) {
               d2 = (double)checkZ + 1.0D;
            } else if (toZ < checkZ) {
               d2 = (double)checkZ + 0.0D;
            } else {
               wasZChanged = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            double d6 = to.x - from.x;
            double d7 = to.y - from.y;
            double d8 = to.z - from.z;
            if (wasXChanged) {
               d3 = (d0 - from.x) / d6;
            }

            if (wasYChanged) {
               d4 = (d1 - from.y) / d7;
            }

            if (wasZChanged) {
               d5 = (d2 - from.z) / d8;
            }

            if (d3 == -0.0D) {
               d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
               d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
               d5 = -1.0E-4D;
            }

            EnumFacing facing;
            if (d3 < d4 && d3 < d5) {
               facing = toX > checkX ? EnumFacing.WEST : EnumFacing.EAST;
               from.x = d0;
               from.y += d7 * d3;
               from.z += d8 * d3;
            } else if (d4 < d5) {
               facing = toY > checkY ? EnumFacing.DOWN : EnumFacing.UP;
               from.x += d6 * d4;
               from.y = d1;
               from.z += d8 * d4;
            } else {
               facing = toZ > checkZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
               from.x += d6 * d5;
               from.y += d7 * d5;
               from.z = d2;
            }

            checkX = MathHelper.floor(from.x) - (facing == EnumFacing.EAST ? 1 : 0);
            checkY = MathHelper.floor(from.y) - (facing == EnumFacing.UP ? 1 : 0);
            checkZ = MathHelper.floor(from.z) - (facing == EnumFacing.SOUTH ? 1 : 0);
            pos.setPos(checkX, checkY, checkZ);
            state = world.getBlockState(pos);
            block = state.getBlock();
            if (state.getMaterial() == Material.PORTAL || state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB && block.canCollideCheck(state, false) && state.collisionRayTrace(world, pos, from, to) != null) {
               opacityCheck = opacityCheck || ParticleCulling.Configuration.cullBehindGlass || state.isFullCube() && state.isOpaqueCube();
               ++blocks;
               if (blocks > ParticleCulling.Configuration.blockBuffer) {
                  break;
               }
            }
         }

         return opacityCheck;
      } else {
         return false;
      }
   }
}
