package de.impelon.disenchanter;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import de.impelon.disenchanter.proxy.CommonProxy;
import de.impelon.disenchanter.crafting.ConfigConditionFactory;
import de.impelon.disenchanter.crafting.UpgradeJarRecipeFactory;
import de.impelon.disenchanter.crafting.UpgradeTableRecipeFactory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "disenchanter",
   name = "Disenchanter",
   version = "1.8",
   updateJSON = "https://raw.githubusercontent.com/Impelon/Disenchanter/meta/versions.json"
)
public class DisenchanterMain {
   public static final String NAME = "Disenchanter";
   public static final String MODID = "disenchanter";
   public static final String VERSION = "1.8";
   public static final String UPDATE_JSON = "https://raw.githubusercontent.com/Impelon/Disenchanter/meta/versions.json";
   public static DisenchanterMain instance;
   public static CommonProxy proxy;

   public DisenchanterMain() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new de.impelon.disenchanter.proxy.CombinedClientProxy() : new de.impelon.disenchanter.proxy.ServerProxy();
         de.impelon.disenchanter.DisenchanterConfig.register();
         de.impelon.disenchanter.DisenchanterConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
         de.impelon.disenchanter.DisenchanterMain.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         DisenchanterMain.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         DisenchanterMain.load((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         DisenchanterMain.postInit((FMLPostInitializationEvent)event);
      }

   }

   public static void preInit(FMLPreInitializationEvent ev) {
      proxy.preInit(ev);
      ConfigConditionFactory condition = new ConfigConditionFactory();
      CraftingHelper.register(new ResourceLocation(MODID, "disenchantment_table_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "automatic_upgrade_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "bulkdisenchanting_upgrade_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "cycling_upgrade_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "voiding_upgrade_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "clear_table_upgrades_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "experience_jar_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "overload_upgrade_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "capacity_upgrade_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "clear_jar_upgrades_enabled"), condition);
      CraftingHelper.register(new ResourceLocation(MODID, "upgrade_jar"), new UpgradeJarRecipeFactory());
      CraftingHelper.register(new ResourceLocation(MODID, "upgrade_table"), new UpgradeTableRecipeFactory());
   }

   public static void load(FMLInitializationEvent ev) {
      proxy.load(ev);
   }

   public static void postInit(FMLPostInitializationEvent ev) {
      proxy.postInit(ev);
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, DisenchanterMain::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, DisenchanterMain::registerItems);
      bus.registerGeneric(Register.class, SoundEvent.class, DisenchanterMain::registerSoundEvents);
      bus.registerGeneric(Register.class, IRecipe.class, DisenchanterMain::registerRecipes);
   }

   public static void registerBlocks(Register<Block> ev) {
      proxy.registerBlocks(ev);
   }

   public static void registerItems(Register<Item> ev) {
      proxy.registerItems(ev);
   }

   public static void registerSoundEvents(Register<SoundEvent> ev) {
      proxy.registerSoundEvents(ev);
   }

   public static void registerRecipes(Register<IRecipe> ev) {
      proxy.registerRecipes(ev);
   }
}
