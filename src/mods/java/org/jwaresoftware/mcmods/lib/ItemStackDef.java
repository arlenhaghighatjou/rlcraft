package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

public final class ItemStackDef {
   public Item item;
   public int meta;
   public int count;
   private String _item_resource;
   public static final ItemStackDef EMPTY = new ItemStackDef();
   public static final ItemStackDef UNUSED = new ItemStackDef(Items.BOAT, 123, 0);

   public ItemStackDef() {
      this.item = Items.AIR;
      this.meta = 0;
      this.count = 1;
      this._item_resource = null;
   }

   public ItemStackDef(Item obj, int num, int id) {
      this.item = Items.AIR;
      this.meta = 0;
      this.count = 1;
      this._item_resource = null;
      this.item = obj;
      this.meta = id;
      this.count = num;
   }

   public ItemStackDef(Item obj, int num) {
      this.item = Items.AIR;
      this.meta = 0;
      this.count = 1;
      this._item_resource = null;
      this.item = obj;
      this.count = num;
   }

   public ItemStackDef(Item obj) {
      this.item = Items.AIR;
      this.meta = 0;
      this.count = 1;
      this._item_resource = null;
      this.item = obj;
   }

   public ItemStackDef(Block obj, int num, int id) {
      this(Item.getItemFromBlock(obj), num, id);
   }

   public ItemStackDef(Block obj, int num) {
      this(Item.getItemFromBlock(obj), num);
   }

   public ItemStackDef(Block obj) {
      this(Item.getItemFromBlock(obj));
   }

   public ItemStackDef(@Nonnull ItemStack obj) {
      this(obj.getItem(), obj.getCount(), obj.getMetadata());
   }

   public ItemStackDef(String rid, int num, int id) {
      this.item = Items.AIR;
      this.meta = 0;
      this.count = 1;
      this._item_resource = null;
      Validate.notBlank(rid, "A non-blank item resource name is required", new Object[0]);
      this._item_resource = rid;
      this.item = null;
      this.count = num;
      this.meta = id;
   }

   private ItemStackDef(Item obj, int num, int id, String rid) {
      this(obj, num, id);
      this._item_resource = rid;
   }

   public final Item getItem() {
      return this.item;
   }

   public final int getMetadata() {
      return this.meta;
   }

   public final int getCount() {
      return this.count;
   }

   public final ItemStackDef copy() {
      return new ItemStackDef(this.item, this.count, this.meta, this._item_resource);
   }

   public final ItemStack get() {
      return this == EMPTY ? ItemStack.EMPTY : (this.item != null ? new ItemStack(this.item, this.count, this.meta) : (this._item_resource != null ? this.getnamed() : ItemStack.EMPTY));
   }

   public final ItemStack safeget() {
      try {
         return this.get();
      } catch (RuntimeException var2) {
         return ItemStack.EMPTY;
      }
   }

   private final ItemStack getnamed() {
      Item registered = (Item)Item.REGISTRY.getObject(new ResourceLocation(this._item_resource));
      if (registered != null) {
         this.item = registered;
         return new ItemStack(this.item, this.count, this.meta);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public final boolean isEmpty() {
      return this == EMPTY || this.item == null && this._item_resource == null || this.item == Items.AIR || this.count <= 0;
   }

   public static final boolean isEmpty(ItemStackDef o) {
      return o == null || o.isEmpty();
   }

   public final boolean isSingle() {
      return this.count == 1 && !this.isEmpty();
   }

   public String toString() {
      if (this.isEmpty()) {
         return "*EMPTY*";
      } else {
         String itemstring = ClassUtils.getShortCanonicalName(this.item, "null");
         return "{[@]" + this.meta + ",[#]" + this.count + ",[o]" + itemstring + "}";
      }
   }

   @Nonnull
   public static final ItemStack convert(@Nullable Object o) {
      if (o == null) {
         return ItemStack.EMPTY;
      } else if (o instanceof ItemStack) {
         return (ItemStack)o;
      } else {
         assert o instanceof ItemStackDef;

         return ((ItemStackDef)o).safeget();
      }
   }

   public static final ItemStackDef copy(@Nullable ItemStackDef from) {
      return from != null && !from.isEmpty() ? from.copy() : EMPTY;
   }
}
