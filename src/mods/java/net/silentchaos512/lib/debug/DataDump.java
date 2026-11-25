package net.silentchaos512.lib.debug;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.silentchaos512.lib.SilentLib;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public final class DataDump {
   private static final String REGISTRY_NAME_IS_NULL = "Registry name is null! This indicates a broken mod and is a serious problem!";
   private static final String SEPARATOR = "--------------------------------------------------------------------------------";

   private DataDump() {
      throw new IllegalAccessError("Utility class");
   }

   public static void dumpBlocks() {
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
      List<String> lines = new ArrayList();

      for(Block block : ForgeRegistries.BLOCKS) {
         try {
            ResourceLocation name = (ResourceLocation)Objects.requireNonNull(block.getRegistryName(), "Registry name is null! This indicates a broken mod and is a serious problem!");
            String translatedName = I18n.format(block.getUnlocalizedName() + ".name", new Object[0]);
            lines.add(String.format("%-60s %-60s", name, translatedName));
         } catch (Exception var5) {
            SilentLib.LOGGER.warn("*** Error on block: {} ***", block);
            SilentLib.LOGGER.catching(var5);
         }
      }

      lines.sort(String::compareToIgnoreCase);
      Logger var10001 = SilentLib.LOGGER;
      SilentLib.LOGGER.getClass();
      lines.forEach(var10001::info);
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
   }

   public static void dumpEnchantments() {
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");

      for(Enchantment ench : Enchantment.REGISTRY) {
         try {
            ResourceLocation name = (ResourceLocation)Objects.requireNonNull(ench.getRegistryName(), "Registry name is null! This indicates a broken mod and is a serious problem!");
            String translatedName = ench.getTranslatedName(1).replaceFirst(" I$", "");
            String type = ench.type == null ? "null" : ench.type.name();
            SilentLib.LOGGER.info(String.format("%-30s %-40s type=%-10s", translatedName, name, type));
         } catch (Exception var5) {
            SilentLib.LOGGER.warn("*** Error on enchantment: {} ***", ench);
            SilentLib.LOGGER.catching(var5);
         }
      }

      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
   }

   public static void dumpEntityList() {
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
      List<String> list = Lists.newArrayList();

      for(EntityEntry entry : ForgeRegistries.ENTITIES) {
         try {
            ResourceLocation name = (ResourceLocation)Objects.requireNonNull(entry.getRegistryName(), "Registry name is null! This indicates a broken mod and is a serious problem!");
            Class<? extends Entity> clazz = EntityList.getClass(entry.getRegistryName());
            String oldName = EntityList.getTranslationName(name);
            int id = EntityList.getID(clazz);
            list.add(String.format("%-30s %4d   %-40s %-40s", oldName, id, name, clazz));
         } catch (Exception var7) {
            SilentLib.LOGGER.warn("*** Error on entity: {} ***", entry.getEntityClass());
            SilentLib.LOGGER.catching(var7);
         }
      }

      list.sort(String::compareToIgnoreCase);
      Logger var10001 = SilentLib.LOGGER;
      SilentLib.LOGGER.getClass();
      list.forEach(var10001::info);
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
   }

   public static void dumpItems() {
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
      List<String> lines = new ArrayList();

      for(Item item : ForgeRegistries.ITEMS) {
         try {
            ResourceLocation name = (ResourceLocation)Objects.requireNonNull(item.getRegistryName(), "Registry name is null! This indicates a broken mod and is a serious problem!");
            String translatedName = I18n.format(item.getUnlocalizedName() + ".name", new Object[0]);
            lines.add(String.format("%-60s %-60s", name, translatedName));
         } catch (Exception var5) {
            SilentLib.LOGGER.warn("*** Error on item: {} ***", item);
            SilentLib.LOGGER.catching(var5);
         }
      }

      Logger var10001 = SilentLib.LOGGER;
      SilentLib.LOGGER.getClass();
      lines.forEach(var10001::info);
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
   }

   public static void dumpPotionEffects() {
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");

      for(Potion pot : Potion.REGISTRY) {
         try {
            ResourceLocation name = (ResourceLocation)Objects.requireNonNull(pot.getRegistryName(), "Registry name is null! This indicates a broken mod and is a serious problem!");
            SilentLib.LOGGER.info(String.format("%-30s %-40s", pot.getName(), name));
         } catch (Exception var3) {
            SilentLib.LOGGER.warn("*** Error on potion: {} ***", pot);
            SilentLib.LOGGER.catching(var3);
         }
      }

      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
   }

   public static void dumpRecipes() {
      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
      SilentLib.LOGGER.info("The following is a list of all recipes registered as of Silent Lib's post-init:");

      for(IRecipe rec : CraftingManager.REGISTRY) {
         try {
            ResourceLocation name = (ResourceLocation)Objects.requireNonNull(rec.getRegistryName(), "Registry name is null! This indicates a broken mod and is a serious problem!");
            int id = CraftingManager.REGISTRY.getIDForObject(rec);
            if (id < 0) {
               throw new IndexOutOfBoundsException("id < 0");
            }

            SilentLib.LOGGER.info(String.format("%-6d %-40s", id, name));
         } catch (Exception var4) {
            SilentLib.LOGGER.info("*** Error on recipe: {} ***", rec);
            throw var4;
         }
      }

      SilentLib.LOGGER.info("--------------------------------------------------------------------------------");
   }
}
