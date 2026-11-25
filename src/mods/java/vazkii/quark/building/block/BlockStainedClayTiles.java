package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockStainedClayTiles extends BlockMetaVariants<BlockStainedClayTiles.Variants> implements IQuarkBlock {
   public BlockStainedClayTiles() {
      super("stained_clay_tiles", Material.ROCK, BlockStainedClayTiles.Variants.class);
      this.setHardness(1.25F);
      this.setResistance(7.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public static enum Variants implements IStringSerializable {
      STAINED_CLAY_TILES_WHITE,
      STAINED_CLAY_TILES_ORANGE,
      STAINED_CLAY_TILES_MAGENTA,
      STAINED_CLAY_TILES_LIGHT_BLUE,
      STAINED_CLAY_TILES_YELLOW,
      STAINED_CLAY_TILES_LIME,
      STAINED_CLAY_TILES_PINK,
      STAINED_CLAY_TILES_GRAY,
      STAINED_CLAY_TILES_SILVER,
      STAINED_CLAY_TILES_CYAN,
      STAINED_CLAY_TILES_PURPLE,
      STAINED_CLAY_TILES_BLUE,
      STAINED_CLAY_TILES_BROWN,
      STAINED_CLAY_TILES_GREEN,
      STAINED_CLAY_TILES_RED,
      STAINED_CLAY_TILES_BLACK;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
