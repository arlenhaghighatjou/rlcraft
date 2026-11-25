package crafttweaker.mc1120.mods;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.mods.IMod;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.ModContainer;

public class MCMod implements IMod {
   private final ModContainer mod;

   public MCMod(ModContainer mod) {
      this.mod = mod;
   }

   public String getId() {
      return this.mod.getModId();
   }

   public String getName() {
      return this.mod.getName();
   }

   public String getVersion() {
      return this.mod.getVersion();
   }

   public String getDescription() {
      return this.mod.getMetadata().description;
   }

   public IItemStack[] getItems() {
      List<IItemStack> stacks = new ArrayList();

      for(Item item : (List)BracketHandlerItem.getItemNames().values().stream().filter((itemx) -> {
         return item.getRegistryName().getResourceDomain().equals(this.mod.getModId());
      }).collect(Collectors.toList())) {
         if (item != null) {
            NonNullList<ItemStack> list = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, list);

            for(ItemStack stack : list) {
               stacks.add(CraftTweakerMC.getIItemStack(stack));
            }
         }
      }

      return (IItemStack[])stacks.toArray(new IItemStack[0]);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCMod mcMod = (MCMod)o;
         return Objects.equals(this.mod, mcMod.mod);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.mod});
   }
}
