package crafttweaker.mc1120.game;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.game.IGame;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.world.IBiome;
import crafttweaker.mc1120.actions.ActionSetTranslation;
import crafttweaker.mc1120.brackets.BracketHandlerEnchantments;
import crafttweaker.mc1120.brackets.BracketHandlerItem;
import crafttweaker.mc1120.brackets.BracketHandlerLiquid;
import crafttweaker.mc1120.brackets.BracketHandlerPotion;
import crafttweaker.mc1120.entity.MCEntityDefinition;
import crafttweaker.mc1120.item.MCItemDefinition;
import crafttweaker.mc1120.liquid.MCLiquidDefinition;
import crafttweaker.mc1120.potions.MCPotion;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MCGame implements IGame {
   public static final MCGame INSTANCE = new MCGame();
   private static final Map<String, String> TRANSLATIONS;
   private static final List<IEntityDefinition> ENTITY_DEFINITIONS = new ArrayList();
   public static final List<IAction> TRANSLATION_ACTIONS = new ArrayList();

   private MCGame() {
   }

   public List<IItemDefinition> getItems() {
      return (List)BracketHandlerItem.getItemNames().keySet().stream().map((item) -> {
         return new MCItemDefinition(item, (Item)BracketHandlerItem.getItemNames().get(item));
      }).collect(Collectors.toList());
   }

   public List<IBlockDefinition> getBlocks() {
      return (List)Block.REGISTRY.getKeys().stream().map((block) -> {
         return CraftTweakerMC.getBlockDefinition((Block)Block.REGISTRY.getObject(block));
      }).collect(Collectors.toList());
   }

   public List<ILiquidDefinition> getLiquids() {
      return (List)FluidRegistry.getRegisteredFluids().entrySet().stream().map((entry) -> {
         return new MCLiquidDefinition((Fluid)entry.getValue());
      }).collect(Collectors.toList());
   }

   public ILiquidStack getLiquid(String name) {
      return BracketHandlerLiquid.getLiquid(name);
   }

   public List<IBiome> getBiomes() {
      List<IBiome> result = new ArrayList();

      for(IBiome biome : CraftTweakerMC.biomes) {
         if (biome != null) {
            result.add(biome);
         }
      }

      return result;
   }

   public List<IPotion> getPotions() {
      ArrayList<IPotion> potions = new ArrayList();
      BracketHandlerPotion.getPotionNames().forEach((s, potion) -> {
         potions.add(new MCPotion(potion));
      });
      return potions;
   }

   public List<IEntityDefinition> getEntities() {
      if (ENTITY_DEFINITIONS.isEmpty()) {
         ForgeRegistries.ENTITIES.forEach((entry) -> {
            ENTITY_DEFINITIONS.add(new MCEntityDefinition(entry));
         });
      }

      return ENTITY_DEFINITIONS;
   }

   public IEntityDefinition getEntity(String entityName) {
      for(IEntityDefinition ent : this.getEntities()) {
         if (ent.getName().equalsIgnoreCase(entityName)) {
            return ent;
         }
      }

      boolean needsReloading = false;

      for(ResourceLocation res : ForgeRegistries.ENTITIES.getKeys()) {
         if (res.getResourcePath().equalsIgnoreCase(entityName)) {
            needsReloading = true;
            break;
         }
      }

      if (needsReloading) {
         ENTITY_DEFINITIONS.clear();
         ForgeRegistries.ENTITIES.forEach((entry) -> {
            ENTITY_DEFINITIONS.add(new MCEntityDefinition(entry));
         });
      }

      return (IEntityDefinition)this.getEntities().stream().filter((entx) -> {
         return ent.getName().equals(entityName);
      }).findFirst().orElse((Object)null);
   }

   public List<IEnchantmentDefinition> getEnchantments() {
      return new ArrayList(BracketHandlerEnchantments.enchantments.values());
   }

   public void setLocalization(String key, String value) {
      ActionSetTranslation action = new ActionSetTranslation((String)null, key, value);
      TRANSLATION_ACTIONS.add(action);
      CraftTweakerAPI.apply(action);
   }

   public void setLocalization(String lang, String key, String value) {
      ActionSetTranslation action = new ActionSetTranslation(lang, key, value);
      TRANSLATION_ACTIONS.add(action);
      CraftTweakerAPI.apply(action);
   }

   public String localize(String key) {
      return LanguageMap.instance.translateKey(key);
   }

   public String localize(String key, String lang) {
      return this.localize(key);
   }

   public static Map<String, String> getTRANSLATIONS() {
      return TRANSLATIONS;
   }

   static {
      TRANSLATIONS = LanguageMap.instance.languageList;
   }
}
