package melonslise.locks.mixin;

import melonslise.locks.common.util.LocksUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VanillaInventoryCodeHooks.class})
public class VanillaInventoryCodeHooksMixin {
   public VanillaInventoryCodeHooksMixin() {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getItemHandler(Lnet/minecraft/world/World;DDDLnet/minecraft/util/EnumFacing;)Lorg/apache/commons/lang3/tuple/Pair;"},
      cancellable = true,
      remap = false
   )
   private static void getItemHandler(World world, double x, double y, double z, EnumFacing side, CallbackInfoReturnable<Pair> cir) {
      if (world != null) {
         BlockPos pos = new BlockPos(x, y, z);
         if (LocksUtil.locked(world, pos)) {
            TileEntity te = world.getTileEntity(pos);
            if (te == null) {
               cir.setReturnValue((Object)null);
            } else {
               cir.setReturnValue(ImmutablePair.of(EmptyHandler.INSTANCE, te));
            }
         }

      }
   }
}
