package snownee.kiwi.test;

import net.minecraft.block.Block;
import snownee.kiwi.item.IVariant;

public enum VariantTest implements IVariant<Block> {
   A,
   B,
   C;

   private VariantTest() {
   }

   public int getMeta() {
      return this.ordinal();
   }

   public Block getValue() {
      return null;
   }
}
