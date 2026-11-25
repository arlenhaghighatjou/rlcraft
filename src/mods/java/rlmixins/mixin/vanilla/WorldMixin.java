package rlmixins.mixin.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({World.class})
public abstract class WorldMixin {
   public WorldMixin() {
   }

   @Redirect(
      method = {"updateEntityWithOptionalForce"},
      slice = @Slice(
   from = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;",
   ordinal = 0
),
   to = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;",
   ordinal = 1
)
),
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;setPositionNonDirty()Z",
   ordinal = 0
)
   )
   public boolean alwaysLoadChunk(Entity entityIn) {
      return true;
   }
}
