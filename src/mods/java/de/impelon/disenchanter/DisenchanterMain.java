package de.impelon.disenchanter;

import de.impelon.disenchanter.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
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
   @Instance("disenchanter")
   public static DisenchanterMain instance;
   @SidedProxy(
      clientSide = "de.impelon.disenchanter.proxy.CombinedClientProxy",
      serverSide = "de.impelon.disenchanter.proxy.ServerProxy"
   )
   public static CommonProxy proxy;

   public DisenchanterMain() {
   }

   @EventHandler
   public static void preInit(FMLPreInitializationEvent ev) {
      proxy.preInit(ev);
   }

   @EventHandler
   public static void load(FMLInitializationEvent ev) {
      proxy.load(ev);
   }

   @EventHandler
   public static void postInit(FMLPostInitializationEvent ev) {
      proxy.postInit(ev);
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> ev) {
      proxy.registerBlocks(ev);
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> ev) {
      proxy.registerItems(ev);
   }

   @SubscribeEvent
   public static void registerSoundEvents(Register<SoundEvent> ev) {
      proxy.registerSoundEvents(ev);
   }

   @SubscribeEvent
   public static void registerRecipes(Register<IRecipe> ev) {
      proxy.registerRecipes(ev);
   }
}
