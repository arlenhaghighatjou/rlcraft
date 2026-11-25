package ivorius.reccomplex.item;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.tuple.Pair;

public interface ItemSyncableTags extends ItemSyncable {
   default void writeSyncedNBT(NBTTagCompound compound, ItemStack stack) {
      this.getSyncedNBTTags().forEach((pair) -> {
         if (stack.hasTagCompound() && stack.getTagCompound().hasKey((String)pair.getLeft(), ((Integer)pair.getRight()).intValue())) {
            compound.setTag((String)pair.getLeft(), stack.getTagCompound().getTag((String)pair.getLeft()));
         }

      });
   }

   default void readSyncedNBT(NBTTagCompound compound, ItemStack stack) {
      this.getSyncedNBTTags().forEach((pair) -> {
         if (compound.hasKey((String)pair.getLeft(), ((Integer)pair.getRight()).intValue())) {
            stack.setTagInfo((String)pair.getLeft(), compound.getTag((String)pair.getLeft()));
         }

      });
   }

   List<Pair<String, Integer>> getSyncedNBTTags();
}
