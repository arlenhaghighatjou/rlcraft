package slimeknights.mantle.client.book.data.element;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandGive;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.repository.BookRepository;

public class ItemStackData {
   public String itemList = null;
   public transient BookRepository source;
   public transient ResourceLocation itemListLocation = null;
   public transient String action;
   public String id = "";
   public byte amount = 1;
   public short damage = 0;
   public JsonObject nbt;

   public ItemStackData() {
   }

   public NonNullList<ItemStack> getItems() {
      if (this.itemListLocation != null && this.source.resourceExists(this.itemListLocation)) {
         try {
            ItemStackData.ItemsList itemsList = (ItemStackData.ItemsList)BookLoader.GSON.fromJson(this.source.resourceToString(this.source.getResource(this.itemListLocation)), ItemStackData.ItemsList.class);
            NonNullList<ItemStack> items = NonNullList.withSize(itemsList.items.length, ItemStack.EMPTY);

            for(int i = 0; i < itemsList.items.length; ++i) {
               items.set(i, itemsList.items[i].getItem());
            }

            this.action = itemsList.action;
            return items;
         } catch (Exception var4) {
            ;
         }
      }

      return NonNullList.withSize(1, this.getItem());
   }

   private ItemStack getItem() {
      boolean isMissingItem = false;

      Item item;
      try {
         item = CommandGive.getItemByText((ICommandSender)null, this.id);
      } catch (NumberInvalidException var7) {
         item = Item.getItemFromBlock(Blocks.BARRIER);
         isMissingItem = true;
      }

      if (item == Items.AIR) {
         item = Item.getItemFromBlock(Blocks.BARRIER);
         isMissingItem = true;
      }

      ItemStack itemStack = new ItemStack(item, this.amount, this.damage);
      if (this.nbt != null) {
         try {
            itemStack.setTagCompound(JsonToNBT.getTagFromJson(filterJsonQuotes(this.nbt.toString())));
         } catch (NBTException var6) {
            ;
         }
      }

      if (isMissingItem) {
         NBTTagCompound display = itemStack.getOrCreateSubCompound("display");
         display.setString("Name", "\u00a7rUnknown Item");
         NBTTagList lore = new NBTTagList();
         lore.appendTag(new NBTTagString("\u00a7r\u00a7eItem Name:"));
         lore.appendTag(new NBTTagString("\u00a7r\u00a7e" + this.id));
         display.setTag("Lore", lore);
      }

      return itemStack;
   }

   public static ItemStackData getItemStackData(ItemStack stack) {
      return getItemStackData(stack, false);
   }

   public static ItemStackData getItemStackData(ItemStack stack, boolean ignoreNbt) {
      ItemStackData data = new ItemStackData();
      data.id = ((ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem())).toString();
      data.amount = (byte)stack.getCount();
      data.damage = (short)stack.getItemDamage();
      if (!ignoreNbt && stack.getTagCompound() != null) {
         data.nbt = BookLoader.GSON.toJsonTree(stack.getTagCompound(), NBTTagCompound.class).getAsJsonObject();
      }

      return data;
   }

   public static String filterJsonQuotes(String s) {
      return s.replaceAll("\"(\\w+)\"\\s*:", "$1: ");
   }

   public static class ItemLink {
      public ItemStackData item = new ItemStackData();
      public boolean damageSensitive = false;
      public String action = "";

      public ItemLink() {
      }
   }

   private static class ItemsList {
      public ItemStackData[] items = new ItemStackData[0];
      public String action;

      private ItemsList() {
      }
   }
}
