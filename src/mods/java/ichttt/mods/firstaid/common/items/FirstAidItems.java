package ichttt.mods.firstaid.common.items;

import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.common.damagesystem.PartHealer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder("firstaid")
public class FirstAidItems {
   public static final Item BANDAGE = (Item)getNull();
   public static final Item PLASTER = (Item)getNull();
   public static final Item MORPHINE = (Item)getNull();

   public FirstAidItems() {
   }

   @Nonnull
   private static <T> T getNull() {
      return (T)null;
   }

   public static void registerItems(IForgeRegistry<Item> registry) {
      FirstAidConfig.InternalHealing healing = FirstAidConfig.internalHealing;
      registry.register(new DefaultItemHealing("bandage", (stack) -> {
         return (void)(new PartHealer(healing.bandage.secondsPerHeal * 20, healing.bandage.totalHeals, stack));
      }, (stack) -> {
         return (void)healing.bandage.applyTime;
      }));
      registry.register(new DefaultItemHealing("plaster", (stack) -> {
         return (void)(new PartHealer(healing.plaster.secondsPerHeal * 20, healing.plaster.totalHeals, stack));
      }, (stack) -> {
         return (void)healing.plaster.applyTime;
      }));
      registry.register(new ItemMorphine());
   }
}
