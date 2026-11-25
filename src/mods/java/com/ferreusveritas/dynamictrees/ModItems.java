package com.ferreusveritas.dynamictrees;

import com.ferreusveritas.dynamictrees.api.BoneMealHelper;
import com.ferreusveritas.dynamictrees.items.DendroPotion;
import com.ferreusveritas.dynamictrees.items.DirtBucket;
import com.ferreusveritas.dynamictrees.items.Staff;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(
   modid = "dynamictrees"
)
public class ModItems {
   public static DendroPotion dendroPotion;
   public static DirtBucket dirtBucket;
   public static Staff treeStaff;

   public ModItems() {
   }

   public static void preInit() {
      dendroPotion = new DendroPotion();
      dirtBucket = new DirtBucket();
      treeStaff = new Staff();
   }

   @SubscribeEvent
   public static void register(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();
      ArrayList<Item> treeItems = new ArrayList();
      ModTrees.baseFamilies.forEach((tree) -> {
         tree.getRegisterableItems(treeItems);
      });
      ModTrees.dynamicCactus.getRegisterableItems(treeItems);
      registry.registerAll(new Item[]{dendroPotion, dirtBucket, treeStaff});
      registry.registerAll((IForgeRegistryEntry[])treeItems.toArray(new Item[0]));
      BoneMealHelper.addItem(Items.DYE, 15);
   }
}
