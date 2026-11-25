package ivorius.reccomplex.world.storage.loot;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.item.GeneratingItem;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.registry.MCRegistrySpecial;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Triple;

public class LootGenerationHandler {
   public LootGenerationHandler() {
   }

   public static void generateAllTags(WorldServer server, IItemHandlerModifiable inventory, MCRegistrySpecial.ItemHidingRegistry registry, EaglercraftRandom random) {
      List<Triple<ItemStack, GeneratingItem, Integer>> foundTags = new ArrayList();
      boolean didChange = true;
      int cycles = 0;

      while(true) {
         if (didChange) {
            for(int i = 0; i < inventory.getSlots(); ++i) {
               ItemStack stack = inventory.getStackInSlot(i);
               if (!stack.isEmpty()) {
                  Item item = registry.containedItem(stack);
                  if (item instanceof GeneratingItem) {
                     foundTags.add(Triple.of(stack, (GeneratingItem)item, Integer.valueOf(i)));
                     inventory.setStackInSlot(i, ItemStack.EMPTY);
                  }
               }
            }

            didChange = false;
         }

         if (foundTags.size() > 0) {
            Triple<ItemStack, GeneratingItem, Integer> pair = (Triple)foundTags.get(0);
            ((GeneratingItem)pair.getMiddle()).generateInInventory(server, inventory, random, (ItemStack)pair.getLeft(), ((Integer)pair.getRight()).intValue());
            foundTags.remove(0);
            didChange = true;
         }

         ++cycles;
         if (foundTags.size() <= 0 && !didChange || cycles >= 1000) {
            break;
         }
      }

   }

   public static void generateAllTags(@Nonnull StructureSpawnContext context, IItemHandlerModifiable inventory) {
      generateAllTags(context.environment.world, inventory, RecurrentComplex.specialRegistry.itemHidingMode(), context.random);
   }
}
