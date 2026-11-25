package greymerk.roguelike.treasure.loot;

import com.github.fnar.minecraft.item.RldItemStack;
import greymerk.roguelike.util.IWeighted;
import java.util.HashMap;
import java.util.Map;

public class GreymerkLootProvider {
   private final Map<Integer, LootSettings> loot = new HashMap();

   public GreymerkLootProvider() {
      for(int i = 0; i < 5; ++i) {
         this.loot.put(Integer.valueOf(i), new LootSettings(i));
      }

   }

   public void put(int level, LootSettings settings) {
      this.loot.put(Integer.valueOf(level), settings);
   }

   public IWeighted<RldItemStack> get(GreymerkChestType type, int level) {
      if (level < 0) {
         return ((LootSettings)this.loot.get(Integer.valueOf(0))).get(type);
      } else {
         return level > 4 ? ((LootSettings)this.loot.get(Integer.valueOf(4))).get(type) : ((LootSettings)this.loot.get(Integer.valueOf(level))).get(type);
      }
   }
}
