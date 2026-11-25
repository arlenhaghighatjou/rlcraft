package ivorius.reccomplex.events;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.IItemHandlerModifiable;

@Cancelable
public class ItemGenerationEvent extends Event {
   public final WorldServer server;
   public final IItemHandlerModifiable inventory;
   public final Random random;
   public final ItemStack fromStack;
   public final int fromSlot;

   public ItemGenerationEvent(WorldServer server, IItemHandlerModifiable inventory, Random random, ItemStack fromStack, int fromSlot) {
      this.server = server;
      this.inventory = inventory;
      this.random = random;
      this.fromStack = fromStack;
      this.fromSlot = fromSlot;
   }

   public static class Artifact extends ItemGenerationEvent {
      public Artifact(WorldServer server, IItemHandlerModifiable inventory, Random random, ItemStack fromStack, int fromSlot) {
         super(server, inventory, random, fromStack, fromSlot);
      }
   }

   public static class Book extends ItemGenerationEvent {
      public Book(WorldServer server, IItemHandlerModifiable inventory, Random random, ItemStack fromStack, int fromSlot) {
         super(server, inventory, random, fromStack, fromSlot);
      }
   }
}
