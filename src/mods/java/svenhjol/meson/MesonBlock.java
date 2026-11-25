package svenhjol.meson;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import svenhjol.meson.iface.IMesonBlock;
import svenhjol.meson.iface.IMesonEnum;

public abstract class MesonBlock extends Block implements IMesonBlock {
   protected String name;

   public MesonBlock(Material material, String name) {
      super(material);
      this.register(name);
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static enum ColorVariant implements IMesonEnum {
      WHITE,
      ORANGE,
      MAGENTA,
      LIGHT_BLUE,
      YELLOW,
      LIME,
      PINK,
      GRAY,
      SILVER,
      CYAN,
      PURPLE,
      BLUE,
      BROWN,
      GREEN,
      RED,
      BLACK;

      private static final MesonBlock.ColorVariant[] METADATA_LOOKUP = new MesonBlock.ColorVariant[values().length];

      private ColorVariant() {
      }

      public static MesonBlock.ColorVariant byMetadata(int meta) {
         if (meta < 0 || meta >= METADATA_LOOKUP.length) {
            meta = 0;
         }

         return METADATA_LOOKUP[meta];
      }

      public static MesonBlock.ColorVariant random() {
         return byMetadata((new Random()).nextInt(values().length));
      }

      static {
         MesonBlock.ColorVariant[] values = values();

         for(MesonBlock.ColorVariant v : values) {
            METADATA_LOOKUP[v.ordinal()] = v;
         }

      }
   }

   public static enum WoodVariant implements IMesonEnum {
      OAK,
      SPRUCE,
      BIRCH,
      JUNGLE,
      ACACIA,
      DARK_OAK;

      private static final MesonBlock.WoodVariant[] METADATA_LOOKUP = new MesonBlock.WoodVariant[values().length];

      private WoodVariant() {
      }

      public static MesonBlock.WoodVariant byMetadata(int meta) {
         if (meta < 0 || meta >= METADATA_LOOKUP.length) {
            meta = 0;
         }

         return METADATA_LOOKUP[meta];
      }

      public static MesonBlock.WoodVariant random() {
         return byMetadata((new Random()).nextInt(values().length));
      }

      static {
         MesonBlock.WoodVariant[] values = values();

         for(MesonBlock.WoodVariant v : values) {
            METADATA_LOOKUP[v.ordinal()] = v;
         }

      }
   }
}
