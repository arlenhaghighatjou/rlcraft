package greymerk.roguelike.treasure.loot.provider;

import com.github.fnar.minecraft.item.RldItemStack;
import com.github.fnar.roguelike.loot.special.weapons.SpecialBow;
import com.github.fnar.roguelike.loot.special.weapons.SpecialSword;
import greymerk.roguelike.treasure.loot.Equipment;
import greymerk.roguelike.treasure.loot.Quality;
import java.util.Optional;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Supplier;

public class WeaponLootItem extends LootItem {
   private Equipment type;
   private boolean isEnchanted;
   private Quality quality;

   public WeaponLootItem(int weight, int level) {
      super(weight, level);
   }

   public WeaponLootItem(int weight, int level, Equipment type, Quality quality, boolean isEnchanted) {
      super(weight, level);
      this.type = type;
      this.isEnchanted = isEnchanted;
      this.quality = quality;
   }

   public RldItemStack getLootItem(EaglercraftRandom random) {
      return SpecialtyLootItem.rollForSpecial(random) ? this.createSpecialWeapon(random) : Equipment.asWeaponType(this.getType(random)).asItem().withQuality(this.getQuality(random)).plzEnchantAtLevel(this.getEnchantLevel(random, this.level)).asStack();
   }

   private RldItemStack createSpecialWeapon(EaglercraftRandom random) {
      return random.nextBoolean() ? SpecialBow.newSpecialBow(random, this.getQuality(random)).asStack() : SpecialSword.newSpecialSword(random, this.getQuality(random)).asStack();
   }

   private Equipment getType(EaglercraftRandom random) {
      return (Equipment)Optional.ofNullable(this.type).orElseGet(() -> {
         return random.nextInt(8) == 0 ? Equipment.BOW : Equipment.SWORD;
      });
   }

   private Quality getQuality(EaglercraftRandom random) {
      return (Quality)Optional.ofNullable(this.quality).orElseGet(() -> {
         return Equipment.rollQuality(random, this.level);
      });
   }

   private int getEnchantLevel(EaglercraftRandom random, int level) {
      return this.isEnchanted ? LootItem.getEnchantmentLevel(random, level) : 0;
   }
}
