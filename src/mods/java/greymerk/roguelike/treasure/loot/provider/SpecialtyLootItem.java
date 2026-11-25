package greymerk.roguelike.treasure.loot.provider;

import com.github.fnar.minecraft.item.RldItemStack;
import com.github.fnar.roguelike.loot.special.SpecialEquipment;
import greymerk.roguelike.treasure.loot.Equipment;
import greymerk.roguelike.treasure.loot.Quality;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class SpecialtyLootItem extends LootItem {
   private Equipment type;
   private Quality quality;

   public SpecialtyLootItem(int weight, int level) {
      super(weight, level);
   }

   public SpecialtyLootItem(int weight, int level, Quality q) {
      super(weight, level);
      this.quality = q;
   }

   public SpecialtyLootItem(int weight, int level, Equipment type, Quality quality) {
      super(weight, level);
      this.type = type;
      this.quality = quality;
   }

   public static boolean rollForSpecial(Random random) {
      return random.nextDouble() < 0.05D;
   }

   public RldItemStack getLootItem(Random random) {
      Equipment equipmentType = (Equipment)Optional.ofNullable(this.type).orElseGet(() -> {
         return Equipment.random(random);
      });
      Quality quality = (Quality)Optional.ofNullable(this.quality).orElseGet(() -> {
         return Equipment.rollQuality(random, this.level);
      });
      return SpecialEquipment.newRandomSpecialEquipment(random, equipmentType, quality);
   }
}
