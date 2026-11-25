package vazkii.quark.world.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSlate extends BlockMetaVariants<BlockSlate.Variants> implements IQuarkBlock {
   public BlockSlate() {
      super("slate", Material.ROCK, BlockSlate.Variants.class);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static enum Variants implements IStringSerializable {
      STONE_SLATE,
      STONE_SLATE_SMOOTH;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
