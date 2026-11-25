package svenhjol.charm.world.compat;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import svenhjol.meson.decorator.MesonDecoratorItems;
import svenhjol.meson.decorator.MesonInnerDecorator;
import vazkii.quark.decoration.entity.EntityGlassItemFrame;

public class QuarkDecoratorItems extends MesonDecoratorItems {
   public QuarkDecoratorItems(MesonInnerDecorator generator) {
      super(generator);
   }

   public void addGlassFramedItem(int x, int y, int z, EnumFacing direction, @Nullable ItemStack displayedItem) {
      BlockPos place = this.generator.getRelativePos(new BlockPos(x, y, z));
      if (!this.generator.isNotInBox(place)) {
         EntityGlassItemFrame frame = new EntityGlassItemFrame(this.world, place, this.generator.getRelativeFacing(direction));
         if (displayedItem != null) {
            frame.setDisplayedItem(displayedItem);
         }

         this.world.spawnEntity(frame);
      }
   }
}
