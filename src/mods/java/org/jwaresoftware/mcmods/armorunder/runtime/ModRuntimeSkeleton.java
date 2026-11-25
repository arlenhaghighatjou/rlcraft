package org.jwaresoftware.mcmods.armorunder.runtime;

import java.util.Hashtable;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jwaresoftware.mcmods.armorunder.ModConfig;
import org.jwaresoftware.mcmods.armorunder.ModInfo;
import org.jwaresoftware.mcmods.armorunder.ModItem;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.armorunder.item.GooPak;
import org.jwaresoftware.mcmods.armorunder.recipes.ArmorRecipes;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.ModIntegration;
import org.jwaresoftware.mcmods.lib.RID;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.Whatis;
import org.jwaresoftware.mcmods.lib.api.mod.IModInventoryRenderingHelper;
import org.jwaresoftware.mcmods.lib.api.mod.IModRuntime;
import org.jwaresoftware.mcmods.lib.impl.ModLootLoadedListener;
import org.jwaresoftware.mcmods.lib.integration.TANHelper;
import org.jwaresoftware.mcmods.lib.loot.Loot;
import org.jwaresoftware.mcmods.lib.loot.RandomChanceWithLuckCondition;

abstract class ModRuntimeSkeleton implements IModRuntime {
   protected static final Logger _modLogInstance = LogManager.getLogger("AUW");
   protected static final EaglercraftRandom _modPRNG = new EaglercraftRandom();
   protected final Map<String, Boolean> _flags = new Hashtable();
   protected ModConfig _mainConfig = ModConfig.getInstance();

   protected ModRuntimeSkeleton() {
   }

   protected abstract String getId();

   public Logger getLog() {
      return _modLogInstance;
   }

   public EaglercraftRandom getPRNG() {
      return _modPRNG;
   }

   public void beginStartUp(FMLStateEvent event) {
      this.getLog().info("Starting[B] {}...", this.getId());
      this._mainConfig = (new ModConfig((FMLPreInitializationEvent)event)).setInstance();
      MinecraftForge.EVENT_BUS.register(new ModRegisterObjectsListener(this));
      ModItems.icon = ModItem.modicon();
      this.makeModItems();
   }

   protected void afterItemsRegistered() {
      this.installModHandlers();
   }

   protected void registerRenderModels() {
   }

   protected void registerModRecipes() {
      this.registerModSharedDictIds();
      ArmorRecipes.initFinal(this.getConfig());
      if (ModIntegration.TOUGH_AS_NAILS.isLoaded()) {
         TANHelper.initArmorLining();
         if (this._mainConfig.includePortablePaks()) {
            TANHelper.initPortablePaks();
         }
      }

      this.registerModLoot();
   }

   public void midStartUp(FMLStateEvent event) {
   }

   public void finishStartUp(FMLStateEvent event) {
      this.getLog().info("Starting[E] {}...", this.getId());
   }

   public void shutDown(FMLServerStoppingEvent event) {
      this.getLog().info("Stopping {}...", this.getId());
   }

   public Boolean isEnabled(String id) {
      Boolean flag = null;
      if ("ui".equals(id)) {
         flag = this.hasUI();
      } else {
         flag = (Boolean)this._flags.get(id);
      }

      return flag;
   }

   public boolean hasUI() {
      return false;
   }

   public final ModConfig getConfig() {
      return this._mainConfig;
   }

   public void reloadConfig() {
      this._mainConfig.changed();
   }

   protected IModInventoryRenderingHelper getRenderingHelper() {
      return IModInventoryRenderingHelper.noINSTANCE;
   }

   public final CreativeTabs getOwnedTab() {
      return this._mainConfig.getTab();
   }

   public final void setOwnedTab(CreativeTabs tab) {
      this._mainConfig.setTab(tab);
   }

   protected void makeModItems() {
      ModItems.cooling_goo = ModItem.simple("cooling_goo");
      ModItems.heating_goo = ModItem.simple("heating_goo");
      ModItems.cool_liner_material = ModItem.simple("cool_liner_material");
      ModItems.warm_liner_material = ModItem.simple("warm_liner_material");
      ModItems.liner_snips = ModItem.unlimited("liner_snips");
      ModItems.cool_boots_liner = ModItem.simple("cool_boots_liner");
      ModItems.cool_chestplate_liner = ModItem.simple("cool_chestplate_liner");
      ModItems.cool_helmet_liner = ModItem.simple("cool_helmet_liner");
      ModItems.cool_leggings_liner = ModItem.simple("cool_leggings_liner");
      ModItems.warm_boots_liner = ModItem.simple("warm_boots_liner");
      ModItems.warm_chestplate_liner = ModItem.simple("warm_chestplate_liner");
      ModItems.warm_helmet_liner = ModItem.simple("warm_helmet_liner");
      ModItems.warm_leggings_liner = ModItem.simple("warm_leggings_liner");
      if (this._mainConfig.includePortablePaks()) {
         ModItems.cooling_goopak = GooPak.cooling();
         ModItems.heating_goopak = GooPak.warming();
         ModItems.spent_goopak = ModItem.simple("goopak_spent");
      }

      if (this._mainConfig.allowSpecialLinerCrafting(true)) {
         ModItems.flipflop_goo = ModItem.simple("flipflop_goo");
         ModItems.auto_liner_material = ModItem.simple("flipflop_liner_material");
      }

      ModItems.auto_chestplate_liner = ModItem.special("auto_chestplate_liner");
      ModItems.auto_leggings_liner = ModItem.special("auto_leggings_liner");
      if (this._mainConfig.includeSpecialLiners()) {
         if (this._mainConfig.allowSpecialLinerCrafting(false)) {
            ModItems.diamond_strong_thread = ModItem.simple("diamond_strong_thread");
            ModItems.obsidian_goo = ModItem.simple("obsidian_goo");
            ModItems.obsidian_wrap_material = ModItem.simple("obsidian_wrap_material");
         }

         ModItems.heatbarrier_boots_liner = ModItem.special("barrier_boots_liner");
         ModItems.heatbarrier_chestplate_liner = ModItem.special("barrier_chestplate_liner");
         ModItems.heatbarrier_helmet_liner = ModItem.special("barrier_helmet_liner");
         ModItems.heatbarrier_leggings_liner = ModItem.special("barrier_leggings_liner");
         ModItems.antifreeze_boots_liner = ModItem.shinyshiny("antifreeze_boots_liner");
         ModItems.antifreeze_chestplate_liner = ModItem.shinyshiny("antifreeze_chestplate_liner");
         ModItems.antifreeze_helmet_liner = ModItem.shinyshiny("antifreeze_helmet_liner");
         ModItems.antifreeze_leggings_liner = ModItem.shinyshiny("antifreeze_leggings_liner");
      }

   }

   private void registerModSharedDictIds() {
      RID.initDefaults();
      Armory.registerSharedDictIds();
      if (this._mainConfig.includePortablePaks()) {
         Item filler = Whatis.wheat_seeds;
         if (this._mainConfig.isVanillaFoodPantryPresent()) {
            Item preferred = ModIntegration.VANILLA_FOODPANTRY.item("drying_agent_unprocessed");
            if (SharedGlue.isDefined(preferred)) {
               filler = preferred;
            }
         }

         OreDictionary.registerOre("itemPakFiller", filler);
      }

   }

   protected void installModHandlers() {
      if (this._mainConfig.allowLootTableEnrichment()) {
         MinecraftForge.EVENT_BUS.register(new ModLootLoadedListener(ModInfo.getInstance()));
      }

      if (this._mainConfig.includeSpecialLiners()) {
         MinecraftForge.EVENT_BUS.register(new ModDamageMitigationEventsHandler());
      }

   }

   private void registerModLoot() {
      if (this._mainConfig.allowLootTableEnrichment()) {
         String auw_xlootpool = Loot.asExtensionPool("auw");
         Loot.addLootEntry("armorunder", LootTableList.CHESTS_DESERT_PYRAMID, (String)null, Loot.createEntryItem("auw_goo_cool", ModItems.cooling_goo, SharedGlue.UNCOMMON_WEIGHT(), -1));
         Loot.addLootEntry("armorunder", LootTableList.CHESTS_DESERT_PYRAMID, (String)null, Loot.createEntryItem("auw_goo_heat", ModItems.heating_goo, SharedGlue.UNCOMMON_WEIGHT(), -1));
         Loot.addLootEntry("armorunder", LootTableList.CHESTS_END_CITY_TREASURE, (String)null, Loot.createEntryItem("auw_ozzy_chestplate_end", ModItems.auto_chestplate_liner, SharedGlue.TREASURE_WEIGHT(), -1));
         Loot.addLootEntry("armorunder", LootTableList.CHESTS_NETHER_BRIDGE, (String)null, Loot.createEntryItem("auw_ozzy_leggings_nether", ModItems.auto_leggings_liner, SharedGlue.TREASURE_WEIGHT(), -1));
         Loot.addLootEntry("armorunder", LootTableList.CHESTS_WOODLAND_MANSION, (String)null, Loot.createEntryItem("auw_ozzy_leggings_mansion", ModItems.auto_leggings_liner, SharedGlue.TREASURE_WEIGHT(), -1));
         Loot.addLootEntry("armorunder", LootTableList.CHESTS_IGLOO_CHEST, auw_xlootpool, Loot.createEntryItem("auw_ozzy_chestplate_igloo", ModItems.auto_chestplate_liner, 2, 0, new LootCondition[]{new RandomChanceWithLuckCondition(0.05F, 0.05F)}, new LootFunction[0]));
         if (this._mainConfig.includeSpecialLiners()) {
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_NETHER_BRIDGE, (String)null, Loot.createEntryItem("auw_ollie_boots_nether", ModItems.heatbarrier_boots_liner, SharedGlue.UNCOMMON_WEIGHT(), -1));
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_NETHER_BRIDGE, auw_xlootpool, Loot.createEntryItem("auw_ollie_chestplate_nether", ModItems.heatbarrier_chestplate_liner, 2, 0, new LootCondition[]{new RandomChanceWithLuckCondition(0.1F, 0.05F)}, new LootFunction[0]));
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_NETHER_BRIDGE, (String)null, Loot.createEntryItem("auw_ollie_helmet_nether", ModItems.heatbarrier_helmet_liner, SharedGlue.UNCOMMON_WEIGHT(), -1));
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_NETHER_BRIDGE, auw_xlootpool, Loot.createEntryItem("auw_ollie_leggings_nether", ModItems.heatbarrier_leggings_liner, 2, 0, new LootCondition[]{new RandomChanceWithLuckCondition(0.15F, 0.03F)}, new LootFunction[0]));
            auw_xlootpool = Loot.asExtensionPool("auw_1");
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_IGLOO_CHEST, auw_xlootpool, Loot.createEntryItem("auw_otto_chestplate_igloo", ModItems.antifreeze_chestplate_liner, 2, 0, new LootCondition[]{new RandomChanceWithLuckCondition(0.4F, 0.05F)}, new LootFunction[0]));
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_IGLOO_CHEST, auw_xlootpool, Loot.createEntryItem("auw_otto_helmet_igloo", ModItems.antifreeze_helmet_liner, 2, 0, new LootCondition[]{new RandomChanceWithLuckCondition(0.2F, 0.02F)}, new LootFunction[0]));
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_IGLOO_CHEST, auw_xlootpool, Loot.createEntryItem("auw_otto_leggings_igloo", ModItems.antifreeze_leggings_liner, 2, 0, new LootCondition[]{new RandomChanceWithLuckCondition(0.4F, 0.05F)}, new LootFunction[0]));
            Loot.addLootEntry("armorunder", LootTableList.CHESTS_IGLOO_CHEST, auw_xlootpool, Loot.createEntryItem("auw_otto_boots_igloo", ModItems.antifreeze_boots_liner, 2, 0, new LootCondition[]{new RandomChanceWithLuckCondition(0.2F, 0.02F)}, new LootFunction[0]));
         }
      }

   }

   public final void doRenderSetupOrFail(String oid, Item singleton) {
      this.getRenderingHelper().doRenderSetupOrFail(oid, singleton);
   }

   final void doRenderSetupOrFail(ModItem singleton) {
      this.getRenderingHelper().doRenderSetupOrFail(singleton.oid(), singleton);
   }

   public final void doRenderSetupOrFail(String oid, Block singleton) {
      this.getRenderingHelper().doRenderSetupOrFail(oid, singleton);
   }

   public final void doRenderSetupOrFail(String oid, Item singleton, String[] fmlids, int[] metas) {
      this.getRenderingHelper().doRenderSetupOrFail(oid, singleton, fmlids, metas);
   }

   public final void doRenderSetupOrFail(String oid, Block singleton, String[] fmlids, int[] metas) {
      this.getRenderingHelper().doRenderSetupOrFail(oid, singleton, fmlids, metas);
   }

   public final void doRenderFluidOrFail(String oid, Block singleton, Fluid fluid) {
      this.getRenderingHelper().doRenderFluidOrFail(oid, singleton, fluid);
   }
}
