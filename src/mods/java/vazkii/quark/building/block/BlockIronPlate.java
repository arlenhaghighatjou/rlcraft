package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockIronPlate extends BlockMetaVariants<BlockIronPlate.Variants> implements IQuarkBlock {
   public BlockIronPlate() {
      super("iron_plate", Material.IRON, BlockIronPlate.Variants.class);
      this.setHardness(5.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.METAL);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static enum Variants implements IStringSerializable {
      IRON_PLATE,
      RUSTY_IRON_PLATE;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
