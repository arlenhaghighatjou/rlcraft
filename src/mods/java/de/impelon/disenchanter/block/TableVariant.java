package de.impelon.disenchanter.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public enum TableVariant {
   AUTOMATIC,
   BULKDISENCHANTING,
   CYCLING,
   VOIDING;

   private TableVariant() {
   }

   public String toString() {
      return this.name().toLowerCase();
   }

   public TextComponentTranslation getDescription() {
      return new TextComponentTranslation("msg." + this.toString() + ".txt", new Object[0]);
   }

   public boolean hasVariantAt(World world, BlockPos position) {
      return this.hasVariant(world.getBlockState(position));
   }

   public boolean hasVariant(IBlockState state) {
      return ((Boolean)state.getValue(PropertyBool.create(this.toString()))).booleanValue();
   }
}
