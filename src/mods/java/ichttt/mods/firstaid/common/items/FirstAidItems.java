package ichttt.mods.firstaid.common.items;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.common.damagesystem.PartHealer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class FirstAidItems {
   public static Item BANDAGE;
   public static Item PLASTER;
   public static Item MORPHINE;

   public FirstAidItems() {
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("firstaid", "bandage"), (v) -> BANDAGE = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("firstaid", "plaster"), (v) -> PLASTER = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("firstaid", "morphine"), (v) -> MORPHINE = v);
   }

   public static void registerItems(IForgeRegistry<Item> registry) {
      FirstAidConfig.InternalHealing healing = FirstAidConfig.internalHealing;
      registry.register(new DefaultItemHealing("bandage", (stack) -> {
         return new PartHealer(healing.bandage.secondsPerHeal * 20, healing.bandage.totalHeals, stack);
      }, (stack) -> {
         return healing.bandage.applyTime;
      }));
      registry.register(new DefaultItemHealing("plaster", (stack) -> {
         return new PartHealer(healing.plaster.secondsPerHeal * 20, healing.plaster.totalHeals, stack);
      }, (stack) -> {
         return healing.plaster.applyTime;
      }));
      registry.register(new ItemMorphine());
   }
}
