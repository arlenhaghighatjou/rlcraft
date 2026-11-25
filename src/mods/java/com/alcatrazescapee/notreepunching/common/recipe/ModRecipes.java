package com.alcatrazescapee.notreepunching.common.recipe;

import com.alcatrazescapee.alcatrazcore.inventory.recipe.IRecipeManager;
import com.alcatrazescapee.alcatrazcore.inventory.recipe.RecipeManager;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.common.blocks.BlockCobble;
import com.alcatrazescapee.notreepunching.common.blocks.BlockPottery;
import com.alcatrazescapee.notreepunching.common.items.ModItems;
import com.alcatrazescapee.notreepunching.util.Util;
import com.alcatrazescapee.notreepunching.util.types.Metal;
import com.alcatrazescapee.notreepunching.util.types.Pottery;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import com.alcatrazescapee.notreepunching.util.types.ToolType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public final class ModRecipes {
   public static final IRecipeManager<FirePitRecipe> FIRE_PIT = new RecipeManager();
   public static final IRecipeManager<KnifeRecipe> KNIFE = new RecipeManager();
   private static final List<IRecipeAction> actions = new LinkedList();

   public ModRecipes() {
   }

   public static void init() {
      for(Pottery type : Pottery.values()) {
         if (type != Pottery.WORKED) {
            BlockPottery block = BlockPottery.get(type);
            GameRegistry.addSmelting(block, block.getFiredType(), 0.1F);
            FIRE_PIT.add(new FirePitRecipe(block.getFiredType(), new ItemStack(block)));
         }
      }

      Map<ItemStack, ItemStack> map = FurnaceRecipes.instance().getSmeltingList();

      for(Entry<ItemStack, ItemStack> m : map.entrySet()) {
         if (((ItemStack)m.getValue()).getItem() instanceof ItemFood) {
            FIRE_PIT.add(new FirePitRecipe(((ItemStack)m.getValue()).copy(), ((ItemStack)m.getKey()).copy()));
         }
      }

      for(Stone stone : Stone.values()) {
         if (stone.isEnabled() && stone.hasCobblestone()) {
            GameRegistry.addSmelting(BlockCobble.get(stone), stone.getStoneStack(), 0.1F);
         }
      }

      GameRegistry.addSmelting(ModItems.GRASS_STRING, new ItemStack(Items.STRING), 0.1F);
      GameRegistry.addSmelting(ModItems.CLAY_BRICK, new ItemStack(Items.BRICK), 0.1F);
      FIRE_PIT.add(new FirePitRecipe(new ItemStack(Items.BRICK), "brickClay", 1));
      KNIFE.add(new KnifeRecipe("rock", 1, new ItemStack[]{new ItemStack(ModItems.FLINT_SHARD)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.FLINT), new ItemStack[]{new ItemStack(ModItems.FLINT_SHARD, 2)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.WOOL, 1, 32767), new ItemStack[]{new ItemStack(Items.STRING, 4)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.WEB), new ItemStack[]{new ItemStack(Items.STRING, 8)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.REEDS), new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER, 2)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.WHEAT), new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER, 1), new ItemStack(Items.WHEAT_SEEDS)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.LEATHER_BOOTS), new ItemStack[]{new ItemStack(Items.LEATHER, 2)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack[]{new ItemStack(Items.LEATHER, 5)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack[]{new ItemStack(Items.LEATHER, 4)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.LEATHER_HELMET), new ItemStack[]{new ItemStack(Items.LEATHER, 3)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.MELON_BLOCK), new ItemStack[]{new ItemStack(Items.MELON, 9)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Items.MELON), new ItemStack[]{new ItemStack(Items.MELON_SEEDS, 1), new ItemStack(ModItems.GRASS_FIBER)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.PUMPKIN), new ItemStack[]{new ItemStack(Items.PUMPKIN_SEEDS, 4), new ItemStack(ModItems.GRASS_FIBER, 2)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.VINE), new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER, 3)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.CACTUS), new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER, 2)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 32767), new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER, 2)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 32767), new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER)}));
      KNIFE.add(new KnifeRecipe(new ItemStack(Blocks.YELLOW_FLOWER, 1, 32767), new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER)}));

      for(int i = 0; i < 6; ++i) {
         KNIFE.add(new KnifeRecipe(new ItemStack(i < 4 ? Blocks.LEAVES : Blocks.LEAVES2, 6, i % 4), new ItemStack[]{new ItemStack(Blocks.SAPLING, 1, i), new ItemStack(ModItems.GRASS_FIBER, 2)}));
      }

      KNIFE.add(new KnifeRecipe("treeSapling", 1, new ItemStack[]{new ItemStack(ModItems.GRASS_FIBER, 2), new ItemStack(Items.STICK)}));
   }

   public static void postInit() {
      actions.forEach(IRecipeAction::apply);
   }

   public static void registerRecipes(Register<IRecipe> event) {
      IForgeRegistryModifiable<IRecipe> r = (IForgeRegistryModifiable)event.getRegistry();

      for(Stone stone : Stone.values()) {
         if (!stone.isDefault() && stone.isEnabled() && stone.hasCobblestone()) {
            String oreName = (String)Util.CASE_CONVERTER.convert("ROCK_" + stone.name());
            register(r, new ShapedOreRecipe(new ResourceLocation("notreepunching", "blocks/cobblestone_" + stone.name().toLowerCase()), BlockCobble.get(stone), new Object[]{"RR", "RR", 'R', oreName}));
         }
      }

      if (ModConfig.TOOLS.enableTinTools) {
         registerToolRecipes(r, Metal.TIN);
      }

      if (ModConfig.TOOLS.enableCopperTools) {
         registerToolRecipes(r, Metal.COPPER);
      }

      if (ModConfig.TOOLS.enableBronzeTools) {
         registerToolRecipes(r, Metal.BRONZE);
      }

      if (ModConfig.TOOLS.enableSteelTools) {
         registerToolRecipes(r, Metal.STEEL);
      }

      if (ModConfig.GENERAL.replaceVanillaRecipes) {
         remove(r, "wooden_pickaxe");
         remove(r, "wooden_shovel");
         remove(r, "wooden_hoe");
         remove(r, "wooden_sword");
         remove(r, "wooden_axe");
         remove(r, "stone_pickaxe");
         remove(r, "stone_shovel");
         remove(r, "stone_hoe");
         remove(r, "stone_sword");
         remove(r, "stone_axe");
         remove(r, "flower_pot");
      }

   }

   public static void addScheduledAction(IRecipeAction action) {
      actions.add(action);
   }

   private static void registerToolRecipes(IForgeRegistry<IRecipe> r, Metal metalType) {
      String metalName = metalType.name().toLowerCase() + "_";
      String ingotName = (String)Util.CASE_CONVERTER.convert("INGOT_" + metalType.name());
      register(r, new ShapedOreRecipe(new ResourceLocation("notreepunching", metalName + "knife"), new ItemStack(ModItems.getTool(ToolType.KNIFE, metalType)), new Object[]{"I", "S", 'I', ingotName, 'S', "stickWood"}));
      register(r, new ShapedOreRecipe(new ResourceLocation("notreepunching", metalName + "saw"), new ItemStack(ModItems.getTool(ToolType.SAW, metalType)), new Object[]{"  S", " SI", "SI ", 'I', ingotName, 'S', "stickWood"}));
      register(r, new ShapedOreRecipe(new ResourceLocation("notreepunching", metalName + "mattock"), new ItemStack(ModItems.getTool(ToolType.MATTOCK, metalType)), new Object[]{"III", " SI", " S ", 'I', ingotName, 'S', "stickWood"}));
   }

   private static void register(IForgeRegistry<IRecipe> registry, IRecipe recipe) {
      recipe.setRegistryName(new ResourceLocation(recipe.getGroup()));
      registry.register(recipe);
   }

   private static void remove(IForgeRegistryModifiable<IRecipe> registry, String name) {
      registry.remove(new ResourceLocation("minecraft:" + name));
   }
}
