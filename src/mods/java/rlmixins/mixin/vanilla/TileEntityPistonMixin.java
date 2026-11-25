package rlmixins.mixin.vanilla;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({TileEntityPiston.class})
public abstract class TileEntityPistonMixin extends TileEntity {
   public TileEntityPistonMixin() {
   }

   @Inject(
      method = {"moveCollidedEntities"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/lang/ThreadLocal;set(Ljava/lang/Object;)V",
   ordinal = 1,
   shift = Shift.AFTER
)},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   public void updateEntity(float p_184322_1_, CallbackInfo ci, EnumFacing enumfacing, double d0, List list, AxisAlignedBB axisalignedbb, List list1, boolean flag, int i, Entity entity, double d1) {
      this.world.updateEntityWithOptionalForce(entity, false);
   }

   @Surrogate
   public void updateEntity(float p_184322_1_, CallbackInfo ci, EnumFacing enumfacing, double d0, List list, AxisAlignedBB axisalignedbb, List list1, boolean flag, int i, Entity entity, double d1, int quark0) {
      this.world.updateEntityWithOptionalForce(entity, false);
   }
}
