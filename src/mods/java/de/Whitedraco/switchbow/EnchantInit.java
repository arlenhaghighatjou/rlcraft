package de.Whitedraco.switchbow;

import de.Whitedraco.switchbow.Config.EnchantmentConfig;
import de.Whitedraco.switchbow.enchant.EnchantActiveScope;
import de.Whitedraco.switchbow.enchant.EnchantCooldownReduce;
import de.Whitedraco.switchbow.enchant.EnchantPullSpeed;
import java.util.ArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;

public class EnchantInit {
   public static final Enchantment PullSpeed = new EnchantPullSpeed();
   public static final Enchantment ActivScope = new EnchantActiveScope();
   public static final Enchantment CrossbowCooldownReduce = new EnchantCooldownReduce();
   public static ArrayList<Enchantment> enchantList = new ArrayList();

   public EnchantInit() {
   }

   public static void prepareEnchant() {
      if (EnchantmentConfig.enchantPullingSpeedRegistry.getBool()) {
         enchantList.add(PullSpeed);
      }

      if (EnchantmentConfig.enchantActivScopeRegistry.getBool()) {
         enchantList.add(ActivScope);
      }

      if (EnchantmentConfig.enchantreducedCooldownRegistry.getBool()) {
         enchantList.add(CrossbowCooldownReduce);
      }

   }

   public static void registerEnchant(Register<Enchantment> event) {
      for(Enchantment enchant : enchantList) {
         MinecraftForge.EVENT_BUS.register(enchant);
         event.getRegistry().register(enchant);
      }

   }
}
