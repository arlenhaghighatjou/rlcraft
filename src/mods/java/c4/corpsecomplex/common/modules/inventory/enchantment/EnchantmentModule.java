package c4.corpsecomplex.common.modules.inventory.enchantment;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class EnchantmentModule extends Submodule {
   @ObjectHolder("corpsecomplex:soulbound")
   public static EnchantmentSoulbound soulbound;
   public static boolean registerEnchant;
   public static String rarity;
   public static double levelDrop;
   public static double baseSave;
   public static double extraPerLevel;
   static boolean allowedOnBooks;
   static boolean canApplyEnchantingTable;
   static int maxLevel;
   private static boolean cfgEnabled;

   public EnchantmentModule(Module parentModule) {
      super(parentModule, "Soulbinding");
      this.configCategory.setComment("Enable and customize the soulbinding enchantment");
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable Soulbinding Enchantment", false, "Set to true to enable Soulbinding enchantment", true);
      canApplyEnchantingTable = this.getBool("Can Apply at Enchanting Table", true, "Set to true to allow enchanting Soulbinding at the enchantment table", false);
      rarity = this.getString("Rarity", "VERY_RARE", "The rarity of the enchantment (COMMON, UNCOMMON, RARE, or VERY_RARE)", new String[]{"COMMON", "UNCOMMON", "RARE", "VERY_RARE"}, true);
      maxLevel = this.getInt("Max Level", 1, 1, 5, "The max level of the enchantment", true);
      levelDrop = this.getDouble("Chance to Drop Level on Saved Item", 0.0D, 0.0F, 1.0F, "The percent chance that the item will drop a level in the enchantment on death when kept", false);
      baseSave = this.getDouble("Base Save Probability", 1.0D, 0.0F, 1.0F, "The base percent chance that the enchantment will save an item on death regardless of level", false);
      extraPerLevel = this.getDouble("Extra Save Probability per Level", 0.0D, 0.0F, 1.0F, "The percent chance increase that each level in the enchantment will give to saving an item on death", false);
      allowedOnBooks = this.getBool("Allowed on Books", true, "Set to true to allow enchanting Soulbinding on books", false);
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Arrays.asList("Enable Soulbinding Enchantment", "Max Level", "Chance to Drop Level on Saved Item", "Base Save Probability", "Extra Save Probability per Level", "Can Apply at Enchanting Table", "Allowed on Books"));
   }

   public void setEnabled() {
      this.enabled = registerEnchant = cfgEnabled && this.parentModule.enabled;
   }
}
