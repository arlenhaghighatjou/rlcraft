package ivorius.reccomplex.capability;

import ivorius.ivtoolkit.blocks.BlockArea;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface SelectionOwner {
   @Nullable
   static SelectionOwner getOwner(Object object, @Nullable EnumFacing facing) {
      if (object instanceof SelectionOwner) {
         return (SelectionOwner)object;
      } else {
         return object instanceof ICapabilityProvider ? CapabilitySelection.get((ICapabilityProvider)object, facing) : null;
      }
   }

   default boolean hasValidSelection() {
      return this.getSelection() != null;
   }

   @Nullable
   BlockPos getSelectedPoint1();

   void setSelectedPoint1(@Nullable BlockPos var1);

   @Nullable
   BlockPos getSelectedPoint2();

   void setSelectedPoint2(@Nullable BlockPos var1);

   @Nullable
   default BlockArea getSelection() {
      BlockPos l = this.getSelectedPoint1();
      BlockPos r = this.getSelectedPoint2();
      return l != null && r != null ? new BlockArea(l, r) : null;
   }

   default void setSelection(@Nullable BlockArea area) {
      this.setSelectedPoint1(area != null ? area.getPoint1() : null);
      this.setSelectedPoint2(area != null ? area.getPoint2() : null);
   }
}
