package me.jellysquid.mods.phosphor.mixins.lighting.common;

import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ExtendedBlockStorage.class})
public class MixinExtendedBlockStorage {
   @Shadow
   private NibbleArray skyLight;
   @Shadow
   private int blockRefCount;
   @Shadow
   private NibbleArray blockLight;
   private int lightRefCount = -1;

   public MixinExtendedBlockStorage() {
   }

   @Overwrite
   public void setSkyLight(int x, int y, int z, int value) {
      this.skyLight.set(x, y, z, value);
      this.lightRefCount = -1;
   }

   @Overwrite
   public void setBlockLight(int x, int y, int z, int value) {
      this.blockLight.set(x, y, z, value);
      this.lightRefCount = -1;
   }

   @Overwrite
   public void setBlockLight(NibbleArray array) {
      this.blockLight = array;
      this.lightRefCount = -1;
   }

   @Overwrite
   public void setSkyLight(NibbleArray array) {
      this.skyLight = array;
      this.lightRefCount = -1;
   }

   @Overwrite
   public boolean isEmpty() {
      if (this.blockRefCount != 0) {
         return false;
      } else {
         if (this.lightRefCount == -1) {
            if (this.checkLightArrayEqual(this.skyLight, (byte)-1) && this.checkLightArrayEqual(this.blockLight, (byte)0)) {
               this.lightRefCount = 0;
            } else {
               this.lightRefCount = 1;
            }
         }

         return this.lightRefCount == 0;
      }
   }

   private boolean checkLightArrayEqual(NibbleArray storage, byte val) {
      if (storage == null) {
         return true;
      } else {
         byte[] arr = storage.getData();

         for(byte b : arr) {
            if (b != val) {
               return false;
            }
         }

         return true;
      }
   }
}
