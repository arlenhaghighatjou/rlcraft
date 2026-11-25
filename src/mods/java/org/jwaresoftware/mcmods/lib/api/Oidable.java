package org.jwaresoftware.mcmods.lib.api;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface Oidable {
   @Nonnull
   String oid();

   default String oid(ItemStack stack) {
      return this.oid();
   }

   public static final class WrapForName implements Oidable {
      private final String _name;

      public WrapForName(String s) {
         this._name = s;
      }

      public String oid() {
         return this._name;
      }

      public String oid(ItemStack stack) {
         return this.oid();
      }

      public String toString() {
         return this.oid();
      }
   }
}
