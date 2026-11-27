package lykrast.defiledlands.common.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import lykrast.defiledlands.common.enchantment.EnchantmentBlazing;
import lykrast.defiledlands.common.enchantment.EnchantmentDestructive;
import lykrast.defiledlands.common.enchantment.EnchantmentEconomical;
import lykrast.defiledlands.common.enchantment.EnchantmentSafeguard;
import lykrast.defiledlands.common.enchantment.EnchantmentSharpshooter;
import lykrast.defiledlands.common.item.IEnchantDestructive;
import lykrast.defiledlands.common.item.ItemGun;
import lykrast.defiledlands.common.item.ItemUmbraBlaster;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModEnchantments {
   private static List<Enchantment> enchantmentList = new ArrayList();
   public static final EnumEnchantmentType GUN = EnumHelper.addEnchantmentType("GUN", (item) -> {
      return item instanceof ItemGun;
   });
   public static final EnumEnchantmentType DESTRUCTIVE = EnumHelper.addEnchantmentType("DESTRUCTIVE", (item) -> {
      return item instanceof IEnchantDestructive;
   });
   public static final EnumEnchantmentType UMBRA_BLASTER = EnumHelper.addEnchantmentType("UMBRA_BLASTER", (item) -> {
      return item instanceof ItemUmbraBlaster;
   });
   public static Enchantment sharpshooter = register(new EnchantmentSharpshooter(Rarity.COMMON), "sharpshooter");
   public static Enchantment economical = register(new EnchantmentEconomical(Rarity.UNCOMMON), "economical");
   public static Enchantment destructive = register(new EnchantmentDestructive(Rarity.COMMON), "destructive");
   public static Enchantment safeguard = register(new EnchantmentSafeguard(Rarity.COMMON), "safeguard");
   public static Enchantment blazing = register(new EnchantmentBlazing(Rarity.VERY_RARE), "blazing");

   public ModEnchantments() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Enchantment.class, ModEnchantments::registerEnchantments);
   }

   public static void registerEnchantments(Register<Enchantment> event) {
      for(Enchantment e : enchantmentList) {
         event.getRegistry().register(e);
      }

      enchantmentList = null;
   }

   public static Enchantment register(Enchantment enchant, String name) {
      enchant.setName(name).setRegistryName("defiledlands", name);
      enchantmentList.add(enchant);
      return enchant;
   }
}
