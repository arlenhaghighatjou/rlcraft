package net.silentchaos512.lib.collection;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class ItemMatchList extends AbstractMatchList<Item> {
   public ItemMatchList(boolean whitelist, boolean allowUserToChangeType, String... defaultValues) {
      super(whitelist, allowUserToChangeType, defaultValues);
   }

   public ItemMatchList(boolean whitelist, boolean allowUserToChangeType, Item... defaultValues) {
      super(whitelist, allowUserToChangeType, (String[])Arrays.stream(defaultValues).map(Impl::getRegistryName).filter(Objects::nonNull).map(ResourceLocation::toString).toArray((x$0) -> {
         return new String[x$0];
      }));
   }

   protected boolean contains(Item item) {
      ResourceLocation name = item.getRegistryName();
      return name != null && this.containsKey(name.toString());
   }
}
