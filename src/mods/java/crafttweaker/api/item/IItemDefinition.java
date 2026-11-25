package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.creativetabs.ICreativeTab;
import crafttweaker.api.oredict.IOreDictEntry;
import java.util.List;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.item.IItemDefinition")
@ZenRegister
public interface IItemDefinition {
   @ZenGetter("id")
   String getId();

   @ZenGetter("name")
   String getName();

   @ZenGetter("owner")
   String getOwner();

   @ZenMethod
   IItemStack makeStack(@Optional int var1);

   @ZenGetter("ores")
   List<IOreDictEntry> getOres();

   @ZenGetter("defaultInstance")
   IItemStack getDefaultInstance();

   @ZenMethod
   void setHarvestLevel(String var1, int var2);

   @ZenGetter("creativeTab")
   ICreativeTab getCreativeTab();

   @ZenSetter("creativeTab")
   void setCreativeTab(ICreativeTab var1);

   @ZenGetter("creativeTabs")
   ICreativeTab[] getCreativeTabs();

   @ZenMethod
   void setNoRepair();

   @ZenGetter("canItemEditBlocks")
   boolean canItemEditBlocks();

   @ZenGetter("itemEnchantability")
   int getItemEnchantability();

   @ZenMethod
   void setContainerItem(IItemDefinition var1);

   @ZenGetter("subItems")
   List<IItemStack> getSubItems();

   @ZenMethod
   List<IItemStack> getSubItems(ICreativeTab var1);

   @ZenMethod
   int getItemBurnTime(IItemStack var1);

   Object getInternal();
}
