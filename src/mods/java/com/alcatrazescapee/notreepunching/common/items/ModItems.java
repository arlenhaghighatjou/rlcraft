package com.alcatrazescapee.notreepunching.common.items;

import com.alcatrazescapee.alcatrazcore.item.ItemCore;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.alcatrazcore.util.OreDictionaryHelper;
import com.alcatrazescapee.alcatrazcore.util.RegistryHelper;
import com.alcatrazescapee.alcatrazcore.util.collections.ImmutableEnumTable;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.client.ModTabs;
import com.alcatrazescapee.notreepunching.common.ModMaterials;
import com.alcatrazescapee.notreepunching.util.types.Metal;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import com.alcatrazescapee.notreepunching.util.types.ToolType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;

@ObjectHolder("notreepunching")
public final class ModItems {
   public static final Item FLINT_SHARD = (Item)CoreHelpers.getNull();
   public static final Item GRASS_FIBER = (Item)CoreHelpers.getNull();
   public static final Item GRASS_STRING = (Item)CoreHelpers.getNull();
   public static final Item CLAY_BRICK = (Item)CoreHelpers.getNull();
   public static final Item CERAMIC_SMALL_VESSEL = (Item)CoreHelpers.getNull();
   public static final Item CERAMIC_BUCKET = (Item)CoreHelpers.getNull();
   public static final Item CLAY_TOOL = (Item)CoreHelpers.getNull();
   public static final Item FIRE_STARTER = (Item)CoreHelpers.getNull();
   private static ImmutableMap<ToolType, Item> FLINT_TOOLS;
   private static ImmutableEnumTable<ToolType, Metal, Item> METAL_TOOLS;

   public ModItems() {
   }

   public static Item getFlintTool(ToolType type) {
      return (Item)FLINT_TOOLS.get(type);
   }

   public static Item getTool(ToolType type, Metal metal) {
      return (Item)METAL_TOOLS.get(type, metal);
   }

   public static void preInit() {
      RegistryHelper r = RegistryHelper.get("notreepunching");
      r.registerItem(new ItemCore(), "grass_fiber", ModTabs.TAB_ITEMS);
      r.registerItem(new ItemClayTool(), "clay_tool", ModTabs.TAB_TOOLS);
      r.registerItem(new ItemFireStarter(), "fire_starter", ModTabs.TAB_TOOLS);
      r.registerItem(new ItemSmallVessel(), "ceramic_small_vessel", ModTabs.TAB_ITEMS);
      r.registerItem(new ItemCeramicBucket(), "ceramic_bucket", ModTabs.TAB_ITEMS);
      Item item;
      r.registerItem(item = new ItemCore(), "grass_string", ModTabs.TAB_ITEMS);
      OreDictionaryHelper.register(item, new Object[]{"kindling"});
      OreDictionaryHelper.register(item, new Object[]{"string"});
      ItemCore var12;
      r.registerItem(var12 = new ItemCore(), "flint_shard", ModTabs.TAB_ITEMS);
      OreDictionaryHelper.register(var12, new Object[]{"shard", "flint"});
      r.registerItem(var12 = new ItemCore(), "clay_brick", ModTabs.TAB_ITEMS);
      OreDictionaryHelper.register(var12, new Object[]{"brick", "clay"});

      for(Stone type : Stone.values()) {
         if (type.isEnabled()) {
            r.registerItem(new ItemRock(type), "rock/" + type.name(), ModTabs.TAB_ITEMS);
         }
      }

      Builder<ToolType, Item> flintTools = new Builder();
      com.alcatrazescapee.alcatrazcore.util.collections.ImmutableEnumTable.Builder<ToolType, Metal, Item> metalTools = new com.alcatrazescapee.alcatrazcore.util.collections.ImmutableEnumTable.Builder(ToolType.class, Metal.class);

      for(ToolType type : ToolType.values()) {
         if (type.isFlintTool) {
            Item var14;
            flintTools.put(type, r.registerItem(var14 = type.createFlint(ModMaterials.TOOL_FLINT), type.name() + "/flint", ModTabs.TAB_TOOLS));
            OreDictionaryHelper.register(var14, new Object[]{"tool", "weak", type.name()});
         }

         if (type.isNewTool) {
            for(Metal metal : Metal.values()) {
               if (metal.isEnabled) {
                  Item var15;
                  metalTools.put(type, metal, var15 = r.registerItem(type.createTool(metal.toolMaterial), type.name() + "/" + metal.name(), ModTabs.TAB_TOOLS));
                  OreDictionaryHelper.register(var15, new Object[]{"tool", type.name()});
               }
            }
         }
      }

      FLINT_TOOLS = flintTools.build();
      METAL_TOOLS = metalTools.build();
   }

   public static void init() {
      OreDictionary.registerOre("kindling", Items.PAPER);
      OreDictionary.registerOre("kindling", Blocks.HAY_BLOCK);
      OreDictionaryHelper.register(CLAY_TOOL, new Object[]{"tool", "clay"});
      if (ModConfig.GENERAL.replaceVanillaRecipes) {
         Items.WOODEN_AXE.setCreativeTab((CreativeTabs)null);
         Items.WOODEN_HOE.setCreativeTab((CreativeTabs)null);
         Items.WOODEN_PICKAXE.setCreativeTab((CreativeTabs)null);
         Items.WOODEN_SHOVEL.setCreativeTab((CreativeTabs)null);
         Items.WOODEN_SWORD.setCreativeTab((CreativeTabs)null);
         Items.STONE_AXE.setCreativeTab((CreativeTabs)null);
         Items.STONE_HOE.setCreativeTab((CreativeTabs)null);
         Items.STONE_PICKAXE.setCreativeTab((CreativeTabs)null);
         Items.STONE_SHOVEL.setCreativeTab((CreativeTabs)null);
         Items.STONE_SWORD.setCreativeTab((CreativeTabs)null);
      }

   }
}
