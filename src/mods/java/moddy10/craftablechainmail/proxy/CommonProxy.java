package moddy10.craftablechainmail.proxy;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import moddy10.craftablechainmail.item.ModItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
   }

   public void init(FMLInitializationEvent e) {
   }

   public void postInit(FMLPostInitializationEvent e) {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, CommonProxy::registerItems);
   }

   public static void registerItems(Register<Item> event) {
      IForgeRegistry<Item> reg = event.getRegistry();
      reg.register((new ModItem("chainmail_plate")).setCreativeTab(CreativeTabs.MISC));
   }
}
