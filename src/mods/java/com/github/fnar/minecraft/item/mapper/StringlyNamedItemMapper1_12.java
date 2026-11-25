package com.github.fnar.minecraft.item.mapper;

import com.github.fnar.minecraft.item.StringlyNamedItem;
import greymerk.roguelike.dungeon.settings.DungeonSettingParseException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StringlyNamedItemMapper1_12 extends RldBaseItemMapper1_12<StringlyNamedItem> {
   public StringlyNamedItemMapper1_12() {
   }

   public Class<StringlyNamedItem> getClazz() {
      return StringlyNamedItem.class;
   }

   public ItemStack map(StringlyNamedItem item) {
      String name = item.getItemName();
      Item object = (Item)Item.REGISTRY.getObject(new ResourceLocation(name));
      if (object == null) {
         throw new DungeonSettingParseException("Invalid item: " + name + ". Check for typos, and ensure that it exists in this pack.");
      } else {
         return new ItemStack(object);
      }
   }
}
