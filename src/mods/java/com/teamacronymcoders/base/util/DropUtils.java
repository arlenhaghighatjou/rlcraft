package com.teamacronymcoders.base.util;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.items.WeightedDropTable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class DropUtils {
   private static final String FORTUNE_DELIM = "\\$";
   private static final String WEIGHT_DELIM = "%";
   private static final String DROP_DELIM = "#";
   private static Map<String, WeightedDropTable> wdMap = new HashMap();
   private static Pattern bracketPattern = Pattern.compile("([^\\[\\]]+)");
   private static Matcher bracketMatcher = bracketPattern.matcher("");

   private DropUtils() {
   }

   public static WeightedDropTable parseDrops(String drops) {
      if (wdMap.containsKey(drops)) {
         return (WeightedDropTable)wdMap.get(drops);
      } else {
         List<List<ItemStack>> dropTable = new LinkedList();
         List<Boolean> fortuneTable = new LinkedList();
         List<String> itemSlots = new LinkedList();
         bracketMatcher.reset(drops);

         while(bracketMatcher.find()) {
            itemSlots.add(bracketMatcher.group());
         }

         if (itemSlots.size() == 0) {
            itemSlots.add(drops);
         }

         for(String itemSlot : itemSlots) {
            String[] fortuneArray = itemSlot.split("\\$");
            boolean fortune = fortuneArray.length > 1 && Boolean.parseBoolean(fortuneArray[1]);
            List<ItemStack> slotTable = new LinkedList();

            for(String drop : fortuneArray[0].split("#")) {
               String[] dropArray = drop.split("%");
               int weight = dropArray.length > 1 ? Integer.parseInt(dropArray[1]) : 1;
               int count = dropArray.length > 2 ? Integer.parseInt(dropArray[2]) : 1;
               String[] itemArray = dropArray[0].split(":");
               String itemString = itemArray[0];
               ItemStack itemDrop = ItemStack.EMPTY;
               String i = itemString.toLowerCase();
               byte var20 = -1;
               switch(i.hashCode()) {
               case -1206224488:
                  if (i.equals("oredict")) {
                     var20 = 1;
                  }
                  break;
               case 96634189:
                  if (i.equals("empty")) {
                     var20 = 0;
                  }
               }

               switch(var20) {
               case 0:
                  break;
               case 1:
                  itemDrop = OreDictUtils.getPreferredItemStack(itemArray[1]);
                  break;
               default:
                  int meta = 0;
                  if (itemArray.length > 1) {
                     itemString = itemString + ":" + itemArray[1];
                     if (itemArray.length > 2) {
                        meta = Integer.parseInt(itemArray[2]);
                     }
                  }

                  Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemString));
                  if (item != null) {
                     itemDrop = new ItemStack(item, count, meta);
                  } else {
                     Base.instance.getLogger().error("Could not find Item for name: " + itemString);
                  }
               }

               for(int i = 0; i < weight; ++i) {
                  slotTable.add(itemDrop);
               }
            }

            dropTable.add(slotTable);
            fortuneTable.add(Boolean.valueOf(fortune));
         }

         WeightedDropTable wd = new WeightedDropTable(dropTable, fortuneTable);
         wdMap.put(drops, wd);
         return wd;
      }
   }
}
