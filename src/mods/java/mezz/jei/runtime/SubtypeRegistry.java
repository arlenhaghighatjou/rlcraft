package mezz.jei.runtime;

import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SubtypeRegistry implements ISubtypeRegistry {
   private final Map<Item, ISubtypeRegistry.ISubtypeInterpreter> interpreters = new IdentityHashMap();

   public SubtypeRegistry() {
   }

   public void useNbtForSubtypes(Item... items) {
      for(Item item : items) {
         this.registerSubtypeInterpreter(item, SubtypeRegistry.AllNbt.INSTANCE);
      }

   }

   public void registerNbtInterpreter(Item item, ISubtypeRegistry.ISubtypeInterpreter interpreter) {
      this.registerSubtypeInterpreter(item, interpreter);
   }

   public void registerSubtypeInterpreter(Item item, ISubtypeRegistry.ISubtypeInterpreter interpreter) {
      ErrorUtil.checkNotNull(item, "item ");
      ErrorUtil.checkNotNull(interpreter, "interpreter");
      if (this.interpreters.containsKey(item)) {
         Log.get().error("An interpreter is already registered for this item: {}", item, new IllegalArgumentException());
      } else {
         this.interpreters.put(item, interpreter);
      }
   }

   @Nullable
   public String getSubtypeInfo(ItemStack itemStack) {
      ErrorUtil.checkNotEmpty(itemStack);
      Item item = itemStack.getItem();
      ISubtypeRegistry.ISubtypeInterpreter subtypeInterpreter = (ISubtypeRegistry.ISubtypeInterpreter)this.interpreters.get(item);
      return subtypeInterpreter != null ? subtypeInterpreter.getSubtypeInfo(itemStack) : null;
   }

   public boolean hasSubtypeInterpreter(ItemStack itemStack) {
      ErrorUtil.checkNotEmpty(itemStack);
      Item item = itemStack.getItem();
      return this.interpreters.containsKey(item);
   }

   private static class AllNbt implements ISubtypeRegistry.ISubtypeInterpreter {
      public static final SubtypeRegistry.AllNbt INSTANCE = new SubtypeRegistry.AllNbt();

      private AllNbt() {
      }

      public String apply(ItemStack itemStack) {
         NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
         return nbtTagCompound != null && !nbtTagCompound.hasNoTags() ? nbtTagCompound.toString() : "";
      }
   }
}
