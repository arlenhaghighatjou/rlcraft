package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockPolishedNetherrack extends BlockMetaVariants<BlockPolishedNetherrack.Variants> implements IQuarkBlock {
   public BlockPolishedNetherrack() {
      super("polished_netherrack", Material.ROCK, BlockPolishedNetherrack.Variants.class);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static enum Variants implements IStringSerializable {
      POLISHED_NETHERRACK,
      POLISHED_NETHERRACK_BRICKS;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
