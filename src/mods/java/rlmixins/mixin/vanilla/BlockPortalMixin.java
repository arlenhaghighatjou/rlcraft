package rlmixins.mixin.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockPortal.class})
public abstract class BlockPortalMixin {
   public BlockPortalMixin() {
   }

   @Inject(
      method = {"updateTick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockBreakable;updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/EaglercraftRandom;)V",
   shift = Shift.AFTER
)},
      cancellable = true
   )
   public void rlmixins_vanillaBlockPortal_updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand, CallbackInfo ci) {
      ci.cancel();
   }
}
