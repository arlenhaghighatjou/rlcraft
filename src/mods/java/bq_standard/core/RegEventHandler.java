package bq_standard.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber
public class RegEventHandler {
   public static final List<Item> ALL_ITEMS = new ArrayList();

   public RegEventHandler() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void registerModelEvent(ModelRegistryEvent event) {
      BQ_Standard.proxy.registerRenderers();
   }

   @SubscribeEvent
   public static void registerItemEvent(Register<Item> event) {
      event.getRegistry().registerAll((IForgeRegistryEntry[])ALL_ITEMS.toArray(new Item[0]));
   }

   public static void registerItem(Item i, String name) {
      ResourceLocation res = new ResourceLocation("bq_standard:" + name);
      ALL_ITEMS.add(i.setRegistryName(res));
   }

   static {
      registerItem(BQ_Standard.lootChest, "loot_chest");
   }
}
