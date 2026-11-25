package melonslise.locks.mixin;

import java.util.function.Function;
import melonslise.locks.common.item.LockItem;
import melonslise.locks.common.util.LocksPredicates;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public class BlockMixin {
   public BlockMixin() {
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"getExplosionResistance(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/world/Explosion;)F"},
      cancellable = true,
      remap = false
   )
   private void getExplosionResistance(World world, BlockPos pos, Entity ent, Explosion ex, CallbackInfoReturnable<Float> cir) {
      cir.setReturnValue(Float.valueOf(Math.max(((Float)cir.getReturnValue()).floatValue(), ((Float)LocksUtil.intersecting(world, pos).filter(LocksPredicates.LOCKED).findFirst().map((lkb) -> {
         return (void)LockItem.getResistance(lkb.stack);
      }).orElse(Float.valueOf(0.0F))).floatValue())));
   }
}
