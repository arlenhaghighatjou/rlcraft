package invtweaks;

import invtweaks.api.IItemTreeItem;
import java.util.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvTweaksItemTreeItem implements IItemTreeItem {
   private String name;
   @Nullable
   private String id;
   private int damage;
   private NBTTagCompound extraData;
   private int order;
   private String path;

   public InvTweaksItemTreeItem(String name_, String id_, int damage_, NBTTagCompound extraData_, int order_, String path_) {
      this.name = name_;
      this.id = InvTweaksObfuscation.getNamespacedID(id_);
      this.damage = damage_;
      this.extraData = extraData_;
      this.order = order_;
      this.path = path_;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getId() {
      return this.id;
   }

   public int getDamage() {
      return this.damage;
   }

   public NBTTagCompound getExtraData() {
      return this.extraData;
   }

   public int getOrder() {
      return this.order;
   }

   public String getPath() {
      return this.path;
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && o instanceof IItemTreeItem) {
         IItemTreeItem item = (IItemTreeItem)o;
         return Objects.equals(this.id, item.getId()) && NBTUtil.areNBTEquals(this.extraData, item.getExtraData(), true) && (this.damage == 32767 || this.damage == item.getDamage());
      } else {
         return false;
      }
   }

   public String toString() {
      return this.name;
   }

   public int compareTo(@NotNull IItemTreeItem item) {
      return item.getOrder() - this.order;
   }
}
