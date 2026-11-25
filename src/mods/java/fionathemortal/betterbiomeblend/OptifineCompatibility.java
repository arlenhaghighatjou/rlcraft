package fionathemortal.betterbiomeblend;

import java.lang.reflect.Field;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public final class OptifineCompatibility {
   private static Class<?> ChunkCacheOFClass;
   private static Field ChunkCacheOFChunkCacheField;
   private static boolean successfullyInitialized;

   public OptifineCompatibility() {
   }

   public static boolean isChunkCacheOF(IBlockAccess blockAccess) {
      boolean result = false;
      if (successfullyInitialized) {
         result = ChunkCacheOFClass.isInstance(blockAccess);
      }

      return result;
   }

   public static ChunkCache getChunkCacheFromChunkCacheOF(IBlockAccess blockAccess) {
      ChunkCache result = null;

      try {
         result = (ChunkCache)ChunkCacheOFChunkCacheField.get(blockAccess);
      } catch (IllegalAccessException var3) {
         ;
      }

      return result;
   }

   public static boolean isOptifinePresent() {
      Class<?> config = null;

      try {
         config = Class.forName("net.optifine.Config");
      } catch (ClassNotFoundException var4) {
         try {
            config = Class.forName("Config");
         } catch (ClassNotFoundException var3) {
            ;
         }
      }

      boolean result = config != null;
      return result;
   }

   static {
      if (isOptifinePresent()) {
         try {
            ChunkCacheOFClass = Class.forName("net.optifine.override.ChunkCacheOF");
            ChunkCacheOFChunkCacheField = ObfuscationReflectionHelper.findField(ChunkCacheOFClass, "chunkCache");
            successfullyInitialized = true;
         } catch (Throwable var1) {
            ;
         }
      }

   }
}
