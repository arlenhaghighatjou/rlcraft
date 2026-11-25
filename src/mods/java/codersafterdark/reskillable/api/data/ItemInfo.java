package codersafterdark.reskillable.api.data;

import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemInfo extends NBTLockKey {
   private int metadata;
   private Item item;

   public ItemInfo(Item item, int metadata) {
      this(item, metadata, (NBTTagCompound)null);
   }

   public ItemInfo(Item item, int metadata, NBTTagCompound tag) {
      super(tag);
      this.item = item;
      this.metadata = metadata;
   }

   public ItemInfo(ItemStack stack) {
      this(stack.getItem(), stack.getMetadata(), stack.getTagCompound());
   }

   public LockKey getNotFuzzy() {
      return this.isNotFuzzy() ? this : new ItemInfo(this.item, this.metadata);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ItemInfo)) {
         return false;
      } else {
         ItemInfo other = (ItemInfo)o;
         if (this.getItem() != other.getItem()) {
            return false;
         } else if (this.getTag() == null) {
            return other.getTag() == null && (this.getMetadata() == 32767 || other.getMetadata() == 32767 || this.getMetadata() == other.getMetadata());
         } else {
            return this.getTag().equals(other.getTag()) && (this.getMetadata() == 32767 || other.getMetadata() == 32767 || this.getMetadata() == other.getMetadata());
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.item, this.tag});
   }

   public Item getItem() {
      return this.item;
   }

   public int getMetadata() {
      return this.metadata;
   }
}
