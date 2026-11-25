package svenhjol.meson;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import svenhjol.meson.helper.ItemHelper;
import svenhjol.meson.iface.IMesonPotion;

public abstract class MesonPotion extends Potion implements IMesonPotion {
   public String name;

   public MesonPotion(String name, boolean badEffect, int color) {
      super(badEffect, color);
      this.register(name);
      this.name = name;
   }

   public List<ItemStack> getCurativeItems() {
      return ItemHelper.getCurativeItems();
   }

   public String getName() {
      return this.getModId() + ".potion." + this.name + ".name";
   }
}
