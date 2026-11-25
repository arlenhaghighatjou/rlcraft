package crafttweaker.mc1120.item;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.creativetabs.MCCreativeTab;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.Optional;

public class MCItemDefinition implements IItemDefinition {
   private final String id;
   private final Item item;

   public MCItemDefinition(String id, Item item) {
      this.id = id;
      this.item = item;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.item.getUnlocalizedName();
   }

   public String getOwner() {
      return this.getId().split(":")[0];
   }

   public IItemStack makeStack(@Optional int meta) {
      return CraftTweakerMC.getIItemStackWildcardSize(new ItemStack(this.item, 1, meta));
   }

   public List<IOreDictEntry> getOres() {
      List<IOreDictEntry> result = new ArrayList();

      for(String key : OreDictionary.getOreNames()) {
         for(ItemStack is : OreDictionary.getOres(key)) {
            if (is.getItem() == this.item) {
               result.add(CraftTweakerAPI.oreDict.get(key));
               break;
            }
         }
      }

      return result;
   }

   public IItemStack getDefaultInstance() {
      return new MCItemStack(this.item.getDefaultInstance());
   }

   public void setHarvestLevel(String type, int level) {
      this.item.setHarvestLevel(type, level);
   }

   public ICreativeTab getCreativeTab() {
      return MCCreativeTab.getICreativeTab(this.item.getCreativeTab());
   }

   public void setCreativeTab(ICreativeTab tab) {
      this.item.setCreativeTab(CraftTweakerMC.getCreativeTabs(tab));
   }

   public ICreativeTab[] getCreativeTabs() {
      CreativeTabs[] mcTabs = this.item.getCreativeTabs();
      ICreativeTab[] output = new ICreativeTab[mcTabs.length];

      for(int i = 0; i < output.length; ++i) {
         output[i] = MCCreativeTab.getICreativeTab(mcTabs[i]);
      }

      return output;
   }

   public void setNoRepair() {
      this.item.setNoRepair();
   }

   public boolean canItemEditBlocks() {
      return this.item.canItemEditBlocks();
   }

   public int getItemEnchantability() {
      return this.item.getItemEnchantability();
   }

   public void setContainerItem(IItemDefinition itemDef) {
      this.item.setContainerItem((Item)itemDef.getInternal());
   }

   public List<IItemStack> getSubItems() {
      return this.getSubItems(this.getCreativeTab());
   }

   public List<IItemStack> getSubItems(ICreativeTab tab) {
      NonNullList<ItemStack> list = NonNullList.create();
      this.item.getSubItems(tab == null ? CreativeTabs.SEARCH : (CreativeTabs)tab.getInternal(), list);
      return (List)list.stream().map(MCItemStack::new).collect(Collectors.toList());
   }

   public int getItemBurnTime(IItemStack itemStack) {
      return this.item.getItemBurnTime(CraftTweakerMC.getItemStack(itemStack));
   }

   public Object getInternal() {
      return this.item;
   }

   public int hashCode() {
      int hash = 7;
      hash = 37 * hash + (this.item != null ? this.item.hashCode() : 0);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         MCItemDefinition other = (MCItemDefinition)obj;
         return Objects.equals(this.item, other.item);
      }
   }
}
