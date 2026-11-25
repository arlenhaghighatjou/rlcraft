package snownee.kiwi.util.definition;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;
import snownee.kiwi.Kiwi;
import snownee.kiwi.crafting.input.ProcessingInput;

public final class ItemDefinition implements Comparable<ItemDefinition>, ProcessingInput {
   public static final ItemDefinition EMPTY = of(Items.AIR);
   private final IRegistryDelegate<Item> item;
   private final int metadata;

   public static ItemDefinition of(Item item) {
      return of(item, 0);
   }

   public static ItemDefinition of(ItemStack stack) {
      return stack.isEmpty() ? of(Items.AIR) : of(stack.getItem(), stack.getHasSubtypes() ? stack.getMetadata() : 0);
   }

   public static ItemDefinition of(Block block) {
      return of(Item.getItemFromBlock(block));
   }

   public static ItemDefinition of(Block block, int metadata) {
      return of(Item.getItemFromBlock(block), metadata);
   }

   public static ItemDefinition of(Item item, int metadata) {
      return new ItemDefinition(item, metadata);
   }

   public static ItemDefinition parse(String string, boolean allowWildcard) {
      String[] parts = string.split(":");
      if (parts.length >= 2 && parts.length <= 3) {
         int meta = 0;
         if (parts.length == 3) {
            if (allowWildcard && parts[2].equals("*")) {
               meta = 32767;
            } else {
               try {
                  meta = Integer.parseInt(parts[2]);
               } catch (Exception var5) {
                  ;
               }

               if (!allowWildcard && meta == 32767) {
                  meta = 0;
               }
            }
         }

         Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
         if (item != null && item != Items.AIR) {
            return new ItemDefinition(item, meta);
         }
      }

      Kiwi.logger.error("Fail to parse \"{}\" to ItemDefinition.", string);
      return EMPTY;
   }

   public ItemDefinition(Item item, int metadata) {
      this.item = item.delegate;
      this.metadata = metadata;
   }

   public Item getItem() {
      return (Item)this.item.get();
   }

   public int getMetadata() {
      return this.metadata;
   }

   @Nonnull
   public List<ItemStack> examples() {
      if (this.metadata == 32767) {
         CreativeTabs itemGroup = ((Item)this.item.get()).getCreativeTab();
         if (itemGroup != null) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            ((Item)this.item.get()).getSubItems(itemGroup, stacks);
            return stacks;
         } else {
            return Collections.emptyList();
         }
      } else {
         return Collections.singletonList(this.getItemStack());
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ItemDefinition that = (ItemDefinition)o;
         return this.item.equals(that.item) && this.metadata == that.metadata;
      } else {
         return false;
      }
   }

   public boolean matches(ItemStack stack) {
      return stack.getItem().delegate == this.item && (32767 == this.metadata || stack.getMetadata() == this.metadata);
   }

   public int count() {
      return 1;
   }

   public int hashCode() {
      return this.item.hashCode() * 31 + this.metadata;
   }

   public int compareTo(ItemDefinition o) {
      int result = this.item.name().compareTo(o.item.name());
      return result == 0 ? this.metadata - o.metadata : result;
   }

   public String toString() {
      return this.item.name() + ":" + (this.metadata != 32767 ? this.metadata : "*");
   }

   public ItemStack getItemStack() {
      return new ItemStack((Item)this.item.get(), 1, this.metadata, (NBTTagCompound)null);
   }

   public boolean isEmpty() {
      return this.item.get() == Items.AIR;
   }
}
