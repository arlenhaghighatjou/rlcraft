package melonslise.locks.common.init;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import melonslise.locks.common.enchantment.ComplexityEnchantment;
import melonslise.locks.common.enchantment.ShockingEnchantment;
import melonslise.locks.common.enchantment.SturdyEnchantment;
import melonslise.locks.common.item.LockItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;

public class LocksEnchantments {
   public static final EnumEnchantmentType LOCK_TYPE = EnumHelper.addEnchantmentType("LOCK", (item) -> {
      return item instanceof LockItem;
   });
   public static final List<Enchantment> ENCHANTMENTS = new ArrayList(3);
   public static final Enchantment SHOCKING = add("shocking", new ShockingEnchantment());
   public static final Enchantment STURDY = add("sturdy", new SturdyEnchantment());
   public static final Enchantment COMPLEXITY = add("complexity", new ComplexityEnchantment());

   public LocksEnchantments() {
   }

   public static void register(Register<Enchantment> event) {
      for(Enchantment ench : ENCHANTMENTS) {
         event.getRegistry().register(ench);
      }

   }

   public static Enchantment add(String name, Enchantment ench) {
      ENCHANTMENTS.add(((Enchantment)ench.setRegistryName("locks", name)).setName("locks." + name));
      return ench;
   }
}
