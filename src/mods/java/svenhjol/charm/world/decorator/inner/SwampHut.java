package svenhjol.charm.world.decorator.inner;

import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import svenhjol.charm.world.decorator.theme.SwampHutTheme;
import svenhjol.meson.decorator.MesonDecoratorTheme;
import svenhjol.meson.decorator.MesonInnerDecorator;

public class SwampHut extends MesonInnerDecorator {
   public SwampHut(net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.SwampHut structure, World world, StructureBoundingBox box) {
      super(structure, world, box);
   }

   protected Class<? extends MesonDecoratorTheme> getThemeClass() {
      return SwampHutTheme.class;
   }

   public void generate() {
      this.items.spawnCat(4, 3, 4, 2, true, (EntityPlayer)null);
      if (this.common()) {
         this.items.addStorageBlock(2, 2, 6, EnumFacing.SOUTH);
      }

      if (this.common()) {
         this.add(Blocks.CAULDRON.getDefaultState().withProperty(BlockCauldron.LEVEL, Integer.valueOf(3)), 4, 2, 6, EnumFacing.SOUTH);
      }

      this.add(Blocks.AIR.getDefaultState(), 1, 3, 5);
      this.add(this.items.getFlowerPot(7), 1, 3, 4);
   }
}
