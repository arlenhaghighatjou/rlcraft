package melonslise.locks.mixin;

import melonslise.locks.common.util.LocksUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({World.class})
public class WorldMixin {
   public WorldMixin() {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"isBlockPowered(Lnet/minecraft/util/math/BlockPos;)Z"},
      cancellable = true
   )
   private void isBlockPowered(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (LocksUtil.locked((World)this, pos)) {
         cir.setReturnValue(Boolean.valueOf(false));
      }

   }
}
