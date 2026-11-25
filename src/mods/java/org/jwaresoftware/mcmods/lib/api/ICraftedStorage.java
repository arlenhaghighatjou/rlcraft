package org.jwaresoftware.mcmods.lib.api;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jwaresoftware.mcmods.lib.Strings;

public interface ICraftedStorage {
   ICraftedStorage.Type storedType();

   int quantityStored(ItemStack var1, @Nullable EntityPlayer var2);

   int quantityStored(IBlockState var1, @Nullable EntityPlayer var2);

   public static enum Type {
      GENERAL,
      ITEM,
      BLOCK,
      XP,
      PORTIONS;

      private final String _i18n_name = this.name().toLowerCase(Locale.US);

      private Type() {
      }

      public final String lname() {
         return this._i18n_name;
      }

      public final String getI18nKey() {
         return Strings.tooltipKey("storage." + this._i18n_name + ".value");
      }
   }
}
