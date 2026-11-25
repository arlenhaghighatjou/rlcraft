package org.orecruncher.dsurround.registry.item;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.config.ModConfiguration;
import org.orecruncher.dsurround.registry.item.compat.ItemDataProducer;
import org.orecruncher.lib.ItemStackUtil;
import org.orecruncher.lib.MCHelper;

@SideOnly(Side.CLIENT)
public final class ItemRegistry extends Registry {
   private static final String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
   private static final Pattern FQCN = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*");
   private static final Pattern ITEM_PATTERN = Pattern.compile("([\\w\\-]+:[\\w\\.\\-/]+)[:]?(\\d+|\\*)?(\\{.*\\})?");
   private static final int SET_CAPACITY = 64;
   private static final int MAP_CAPACITY = 256;
   private SimpleItemData NONE_DATA;
   private EnumMap<ItemClass, Set<Class<?>>> classMap;
   private Map<Item, IItemData> items;

   public ItemRegistry() {
      super("Item Registry");
   }

   protected void preInit() {
      this.classMap = new EnumMap(ItemClass.class);
      this.items = new IdentityHashMap(256);
      this.NONE_DATA = (SimpleItemData)SimpleItemData.CACHE.get(ItemClass.NONE);
      Item.REGISTRY.iterator().forEachRemaining((item) -> {
         ItemUtils.setItemData(item, this.NONE_DATA);
      });
      ItemUtils.setItemData(Items.AIR, (IItemData)SimpleItemData.CACHE.get(ItemClass.EMPTY));

      for(ItemClass ic : ItemClass.values()) {
         this.classMap.put(ic, new ReferenceOpenHashSet(64));
      }

   }

   protected void init(@Nonnull ModConfiguration cfg) {
      for(Entry<String, List<String>> entry : cfg.items.entrySet()) {
         this.process((List)entry.getValue(), (String)entry.getKey());
      }

   }

   protected void postInit() {
      for(Item item : Item.REGISTRY) {
         if (!this.items.containsKey(item)) {
            ItemClass ic = this.resolveClass(item);
            if (ic != ItemClass.NONE) {
               this.items.put(item, ItemDataProducer.create(item, ic));
            }
         }
      }

      this.items.forEach(ItemUtils::setItemData);
      this.items = null;
      this.classMap = null;
   }

   private ItemClass resolveClass(@Nonnull Item item) {
      for(ItemClass ic : ItemClass.values()) {
         Set<Class<?>> itemSet = (Set)this.classMap.get(ic);
         if (this.doesBelong(itemSet, item)) {
            return ic;
         }
      }

      return ItemClass.NONE;
   }

   private boolean doesBelong(@Nonnull Set<Class<?>> itemSet, @Nonnull Item item) {
      Class<?> itemClass = item.getClass();
      if (itemSet.contains(itemClass)) {
         return true;
      } else {
         Optional<Class<?>> result = itemSet.stream().filter((c) -> {
            return c.isAssignableFrom(itemClass);
         }).findFirst();
         if (result.isPresent()) {
            itemSet.add(itemClass);
            return true;
         } else {
            return false;
         }
      }
   }

   private void process(@Nullable List<String> items, @Nonnull String itemClass) {
      if (items != null && !items.isEmpty()) {
         ItemClass ic = ItemClass.valueOf(itemClass);
         Set<Class<?>> theList = (Set)this.classMap.get(ic);

         for(String c : items) {
            Matcher match = ITEM_PATTERN.matcher(c);
            if (match.matches()) {
               String itemName = match.group(1);
               Item item = MCHelper.getItemByName(itemName);
               if (item != null) {
                  this.items.put(item, ItemDataProducer.create(item, ic));
               } else {
                  ModBase.log().warn("Cannot locate item [%s] for ItemRegistry", new Object[]{c});
               }
            } else {
               match = FQCN.matcher(c);
               if (match.matches()) {
                  try {
                     Class<?> clazz = Class.forName(c, false, ItemRegistry.class.getClassLoader());
                     theList.add(clazz);
                  } catch (ClassNotFoundException var10) {
                     ModBase.log().warn("Cannot locate class '%s' for ItemRegistry", new Object[]{c});
                  }
               } else {
                  ModBase.log().warn("Unrecognized pattern '%s' for ItemRegistry", new Object[]{c});
               }
            }
         }

      }
   }

   @Nonnull
   public IItemData getItemClass(@Nonnull ItemStack stack) {
      return (IItemData)(ItemStackUtil.isValidItemStack(stack) ? ItemUtils.getItemData(stack.getItem()) : this.NONE_DATA);
   }
}
