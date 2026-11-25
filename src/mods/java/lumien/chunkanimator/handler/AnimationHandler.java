package lumien.chunkanimator.handler;

import java.util.WeakHashMap;
import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;
import penner.easing.Back;
import penner.easing.Bounce;
import penner.easing.Circ;
import penner.easing.Cubic;
import penner.easing.Elastic;
import penner.easing.Expo;
import penner.easing.Linear;
import penner.easing.Quad;
import penner.easing.Quart;
import penner.easing.Quint;
import penner.easing.Sine;

public class AnimationHandler {
   WeakHashMap<Object, AnimationHandler.AnimationData> timeStamps = new WeakHashMap();

   public AnimationHandler() {
   }

   public void preRender(Object renderChunk, BlockPos pos) {
      Vector3f offset = this.getOffset(renderChunk, pos);
      if (offset != null) {
         GlStateManager.translate(offset.x, offset.y, offset.z);
      }

   }

   public Vector3f getOffset(Object renderChunk, BlockPos position) {
      if (this.timeStamps.containsKey(renderChunk)) {
         AnimationHandler.AnimationData animationData = (AnimationHandler.AnimationData)this.timeStamps.get(renderChunk);
         long time = animationData.timeStamp;
         int mode = ChunkAnimator.INSTANCE.config.getMode();
         if (time == -1L) {
            time = System.currentTimeMillis();
            animationData.timeStamp = time;
            if (mode == 4) {
               BlockPos zeroedPlayerPosition = Minecraft.getMinecraft().player.getPosition();
               zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);
               BlockPos zeroedCenteredChunkPos = position.add(8, -position.getY(), 8);
               Vec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);
               int difX = Math.abs(dif.getX());
               int difZ = Math.abs(dif.getZ());
               EnumFacing chunkFacing;
               if (difX > difZ) {
                  if (dif.getX() > 0) {
                     chunkFacing = EnumFacing.EAST;
                  } else {
                     chunkFacing = EnumFacing.WEST;
                  }
               } else if (dif.getZ() > 0) {
                  chunkFacing = EnumFacing.SOUTH;
               } else {
                  chunkFacing = EnumFacing.NORTH;
               }

               animationData.chunkFacing = chunkFacing;
            }
         }

         long timeDif = System.currentTimeMillis() - time;
         int animationDuration = ChunkAnimator.INSTANCE.config.getAnimationDuration();
         if (timeDif < (long)animationDuration) {
            int chunkY = position.getY();
            if (mode == 2) {
               if ((double)chunkY < Minecraft.getMinecraft().world.provider.getHorizon()) {
                  mode = 0;
               } else {
                  mode = 1;
               }
            }

            if (mode == 4) {
               mode = 3;
            }

            switch(mode) {
            case 0:
               return new Vector3f(0.0F, (float)(-chunkY) + this.getFunctionValue((float)timeDif, 0.0F, (float)chunkY, (float)animationDuration), 0.0F);
            case 1:
               return new Vector3f(0.0F, (float)(256 - chunkY) - this.getFunctionValue((float)timeDif, 0.0F, (float)(256 - chunkY), (float)animationDuration), 0.0F);
            case 2:
            default:
               break;
            case 3:
               EnumFacing chunkFacing = animationData.chunkFacing;
               if (chunkFacing != null) {
                  Vec3i vec = chunkFacing.getDirectionVec();
                  float mod = -(200.0F - 200.0F / (float)animationDuration * (float)timeDif);
                  mod = -(200.0F - this.getFunctionValue((float)timeDif, 0.0F, 200.0F, (float)animationDuration));
                  return new Vector3f((float)vec.getX() * mod, 0.0F, (float)vec.getZ() * mod);
               }
            }
         } else {
            this.timeStamps.remove(renderChunk);
         }
      }

      return null;
   }

   private float getFunctionValue(float t, float b, float c, float d) {
      switch(ChunkAnimator.INSTANCE.config.getEasingFunction()) {
      case 0:
         return Linear.easeOut(t, b, c, d);
      case 1:
         return Quad.easeOut(t, b, c, d);
      case 2:
         return Cubic.easeOut(t, b, c, d);
      case 3:
         return Quart.easeOut(t, b, c, d);
      case 4:
         return Quint.easeOut(t, b, c, d);
      case 5:
         return Expo.easeOut(t, b, c, d);
      case 6:
         return Sine.easeOut(t, b, c, d);
      case 7:
         return Circ.easeOut(t, b, c, d);
      case 8:
         return Back.easeOut(t, b, c, d);
      case 9:
         return Bounce.easeOut(t, b, c, d);
      case 10:
         return Elastic.easeOut(t, b, c, d);
      default:
         return Sine.easeOut(t, b, c, d);
      }
   }

   public void setOrigin(Object renderChunk, BlockPos position) {
      if (Minecraft.getMinecraft().player != null) {
         boolean flag = true;
         BlockPos zeroedPlayerPosition = Minecraft.getMinecraft().player.getPosition();
         zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);
         BlockPos zeroedCenteredChunkPos = position.add(8, -position.getY(), 8);
         if (ChunkAnimator.INSTANCE.config.disableAroundPlayer()) {
            flag = zeroedPlayerPosition.distanceSq(zeroedCenteredChunkPos) > 4096.0D;
         }

         if (flag) {
            EnumFacing chunkFacing = null;
            if (ChunkAnimator.INSTANCE.config.getMode() == 3) {
               Vec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);
               int difX = Math.abs(dif.getX());
               int difZ = Math.abs(dif.getZ());
               if (difX > difZ) {
                  if (dif.getX() > 0) {
                     chunkFacing = EnumFacing.EAST;
                  } else {
                     chunkFacing = EnumFacing.WEST;
                  }
               } else if (dif.getZ() > 0) {
                  chunkFacing = EnumFacing.SOUTH;
               } else {
                  chunkFacing = EnumFacing.NORTH;
               }
            }

            AnimationHandler.AnimationData animationData = new AnimationHandler.AnimationData(-1L, chunkFacing);
            this.timeStamps.put(renderChunk, animationData);
         } else if (this.timeStamps.containsKey(renderChunk)) {
            this.timeStamps.remove(renderChunk);
         }
      }

   }

   private class AnimationData {
      public long timeStamp;
      public EnumFacing chunkFacing;

      public AnimationData(long timeStamp, EnumFacing chunkFacing) {
         this.timeStamp = timeStamp;
         this.chunkFacing = chunkFacing;
      }
   }
}
