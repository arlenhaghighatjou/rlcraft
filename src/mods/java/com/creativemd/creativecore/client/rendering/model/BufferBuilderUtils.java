package com.creativemd.creativecore.client.rendering.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferBuilderUtils {
   private static final Logger LOGGER = LogManager.getLogger();
   public static Field byteBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[]{"byteBuffer", "byteBuffer"});
   public static Field rawIntBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[]{"rawIntBuffer", "rawIntBuffer"});
   public static Field rawFloatBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[]{"rawFloatBuffer", "rawFloatBuffer"});
   public static Field rawShortBufferField = ReflectionHelper.findField(BufferBuilder.class, new String[]{"rawShortBuffer", "rawShortBuffer"});
   public static Field vertexCountField = ReflectionHelper.findField(BufferBuilder.class, new String[]{"vertexCount", "vertexCount"});
   public static Method growBuffer = ReflectionHelper.findMethod(BufferBuilder.class, "growBuffer", "growBuffer", new Class[]{Integer.TYPE});
   private static Field quadSpritesField;
   private static Field quadSpritesPrevField;

   public BufferBuilderUtils() {
   }

   public static void growBuffer(BufferBuilder builder, int size) {
      try {
         growBuffer.invoke(builder, size);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var3) {
         var3.printStackTrace();
      }

   }

   public static void ensureTotalSize(BufferBuilder builder, int size) {
      int toAdd = size - getBufferSizeByte(builder);
      if (size > 0) {
         growBufferSmall(builder, toAdd);
      }

   }

   public static void growBufferSmall(BufferBuilder builder, int size) {
      try {
         ByteBuffer oldByteBuffer = builder.getByteBuffer();
         IntBuffer intBuffer = (IntBuffer)rawIntBufferField.get(builder);
         if (MathHelper.roundUp(size, 4) / 4 > intBuffer.remaining() || vertexCountField.getInt(builder) * builder.getVertexFormat().getNextOffset() + size > oldByteBuffer.capacity()) {
            int i = oldByteBuffer.capacity();
            int j = i + size;
            int k = intBuffer.position();
            ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(j);
            oldByteBuffer.position(0);
            byteBuffer.put(oldByteBuffer);
            byteBuffer.rewind();
            byteBufferField.set(builder, byteBuffer);
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            if (!FMLClientHandler.instance().hasOptifine()) {
               floatBuffer = floatBuffer.asReadOnlyBuffer();
            }

            rawFloatBufferField.set(builder, floatBuffer);
            rawIntBufferField.set(builder, byteBuffer.asIntBuffer());
            ((IntBuffer)rawIntBufferField.get(builder)).position(k);
            rawShortBufferField.set(builder, byteBuffer.asShortBuffer());
            ((ShortBuffer)rawShortBufferField.get(builder)).position(k << 1);
         }
      } catch (IllegalAccessException | IllegalArgumentException var9) {
         var9.printStackTrace();
      }

   }

   private static int getBufferQuadSize(BufferBuilder builder) {
      try {
         return ((IntBuffer)rawIntBufferField.get(builder)).capacity() * 4 / (builder.getVertexFormat().getIntegerSize() * 4);
      } catch (IllegalAccessException | IllegalArgumentException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static int getBufferSizeInt(BufferBuilder builder) {
      return builder.getVertexFormat().getIntegerSize() * builder.getVertexCount();
   }

   public static int getBufferSizeByte(BufferBuilder builder) {
      return builder.getVertexFormat().getNextOffset() * builder.getVertexCount();
   }

   public static int get(BufferBuilder builder, int index) {
      try {
         return ((IntBuffer)rawIntBufferField.get(builder)).get(index);
      } catch (IllegalAccessException | IllegalArgumentException var3) {
         var3.printStackTrace();
         return 0;
      }
   }

   public static void addVertexDataSmall(BufferBuilder builder, int[] vertexData) {
      growBufferSmall(builder, vertexData.length * 4 + builder.getVertexFormat().getNextOffset());

      try {
         IntBuffer intBuffer = (IntBuffer)rawIntBufferField.get(builder);
         intBuffer.position(getBufferSizeInt(builder));
         intBuffer.put(vertexData);
         vertexCountField.setInt(builder, vertexCountField.getInt(builder) + vertexData.length / builder.getVertexFormat().getIntegerSize());
      } catch (IllegalAccessException | IllegalArgumentException var3) {
         var3.printStackTrace();
      }

   }

   public static void addBuffer(BufferBuilder buffer, BufferBuilder toAdd) {
      int size = toAdd.getVertexFormat().getIntegerSize() * toAdd.getVertexCount();

      try {
         IntBuffer rawIntBuffer = (IntBuffer)rawIntBufferField.get(toAdd);
         rawIntBuffer.rewind();
         rawIntBuffer.limit(size);
         IntBuffer chunkIntBuffer = (IntBuffer)rawIntBufferField.get(buffer);
         chunkIntBuffer.position(getBufferSizeInt(buffer));
         chunkIntBuffer.put(rawIntBuffer);
         vertexCountField.setInt(buffer, buffer.getVertexCount() + toAdd.getVertexCount());
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void addBuffer(BufferBuilder buffer, ByteBuffer toAdd, int length, int count) {
      try {
         toAdd.rewind();
         toAdd.limit(length);
         ByteBuffer chunkByteBuffer = (ByteBuffer)byteBufferField.get(buffer);
         int size = getBufferSizeByte(buffer);
         chunkByteBuffer.limit(size + length);
         chunkByteBuffer.position(size);
         chunkByteBuffer.put(toAdd);
         chunkByteBuffer.rewind();
         vertexCountField.setInt(buffer, buffer.getVertexCount() + count);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   static {
      if (FMLClientHandler.instance().hasOptifine()) {
         quadSpritesField = ReflectionHelper.findField(BufferBuilder.class, new String[]{"quadSprites", "quadSprites"});
         quadSpritesPrevField = ReflectionHelper.findField(BufferBuilder.class, new String[]{"quadSpritesPrev", "quadSpritesPrev"});
      }

   }
}
