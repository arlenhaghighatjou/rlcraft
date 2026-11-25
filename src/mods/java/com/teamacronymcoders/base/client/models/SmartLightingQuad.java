package com.teamacronymcoders.base.client.models;

import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;

public class SmartLightingQuad extends BakedQuad {
   private static Field parent;
   private static Field blockInfo;
   BlockPos blockPos;
   int[][] relativePos;
   boolean ignoreLight;
   public static int staticBrightness;

   public SmartLightingQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, VertexFormat format, BlockPos p) {
      super(vertexDataIn, tintIndexIn, faceIn, spriteIn, false, format);
      this.blockPos = p;
      this.relativePos = new int[4][];
      this.ignoreLight = false;

      for(int i = 0; i < 4; ++i) {
         this.relativePos[i] = new int[]{(int)Math.floor((double)Float.intBitsToFloat(vertexDataIn[7 * i])), (int)Math.floor((double)Float.intBitsToFloat(vertexDataIn[7 * i + 1])), (int)Math.floor((double)Float.intBitsToFloat(vertexDataIn[7 * i + 2]))};
      }

   }

   public SmartLightingQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn, TextureAtlasSprite spriteIn, VertexFormat format) {
      super(vertexDataIn, tintIndexIn, faceIn, spriteIn, false, format);
      this.ignoreLight = true;
   }

   public void pipe(@Nonnull IVertexConsumer consumer) {
      IBlockAccess world = null;
      if (consumer instanceof VertexLighterFlat) {
         try {
            BlockInfo info = (BlockInfo)blockInfo.get(consumer);
            world = info.getWorld();
            consumer = (IVertexConsumer)parent.get(consumer);
         } catch (Throwable var13) {
            var13.printStackTrace();
         }
      }

      consumer.setQuadOrientation(this.getFace());
      if (this.hasTintIndex()) {
         consumer.setQuadTint(this.getTintIndex());
      }

      float[] data = new float[4];
      VertexFormat format = consumer.getVertexFormat();
      int count = format.getElementCount();
      int[] eMap = LightUtil.mapFormats(format, DefaultVertexFormats.ITEM);
      int itemCount = DefaultVertexFormats.ITEM.getElementCount();
      eMap[eMap.length - 1] = 2;

      for(int v = 0; v < 4; ++v) {
         for(int e = 0; e < count; ++e) {
            if (eMap[e] == itemCount) {
               consumer.put(e, new float[]{0.0F});
            } else {
               if (format.getElement(e).getUsage() == EnumUsage.UV && format.getElement(e).getType() == EnumType.SHORT) {
                  int brightness;
                  if (!this.ignoreLight && world != null && !(world instanceof ChunkCache)) {
                     BlockPos here = this.blockPos.add(this.relativePos[v][0], this.relativePos[v][1], this.relativePos[v][2]);
                     brightness = world.getCombinedLight(here, 0);
                  } else {
                     brightness = staticBrightness;
                  }

                  data[0] = (float)(brightness >> 4 & 15) * 32.0F / 65535.0F;
                  data[1] = (float)(brightness >> 20 & 15) * 32.0F / 65535.0F;
               } else {
                  LightUtil.unpack(this.getVertexData(), data, DefaultVertexFormats.ITEM, v, eMap[e]);
               }

               consumer.put(e, data);
            }
         }
      }

   }

   static {
      try {
         blockInfo = VertexLighterFlat.class.getDeclaredField("blockInfo");
         blockInfo.setAccessible(true);
         parent = QuadGatheringTransformer.class.getDeclaredField("parent");
         parent.setAccessible(true);
      } catch (Exception var1) {
         var1.printStackTrace();
      }

   }
}
