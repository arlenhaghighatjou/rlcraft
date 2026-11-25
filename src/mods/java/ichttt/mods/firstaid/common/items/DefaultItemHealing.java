package ichttt.mods.firstaid.common.items;

import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import ichttt.mods.firstaid.api.item.ItemHealing;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DefaultItemHealing extends ItemHealing {
   DefaultItemHealing(String name, Function<ItemStack, AbstractPartHealer> healerFunction, Function<ItemStack, Integer> applyTime) {
      super(healerFunction, applyTime);
      this.setMaxStackSize(16);
      this.setRegistryName(new ResourceLocation("firstaid", name));
      this.setUnlocalizedName(name);
   }
}
