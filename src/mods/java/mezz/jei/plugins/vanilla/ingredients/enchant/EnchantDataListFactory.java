package mezz.jei.plugins.vanilla.ingredients.enchant;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class EnchantDataListFactory {
   private EnchantDataListFactory() {
   }

   public static List<EnchantmentData> create() {
      List<EnchantmentData> enchantData = new ArrayList();

      for(Enchantment enchant : ForgeRegistries.ENCHANTMENTS.getValuesCollection()) {
         for(int lvl = enchant.getMinLevel(); lvl <= enchant.getMaxLevel(); ++lvl) {
            enchantData.add(new EnchantmentData(enchant, lvl));
         }
      }

      return enchantData;
   }
}
