package xzeroair.trinkets.items.trinkets;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityPoisonAffinity;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigPoisonStone;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketPoisonStone extends AccessoryBase {
   public static final ConfigPoisonStone serverConfig;

   public TrinketPoisonStone(String name) {
      super(name);
      this.setUUID("e86e5b58-1b62-4a54-bba1-6594de844c2e");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "poison", serverConfig.poison);
      TranslationHelper.KeyEntry key1 = new TranslationHelper.OptionEntry("poisonchance", serverConfig.poison, MathHelper.clamp(1.0F / (float)serverConfig.poison_chance * 100.0F, -2.14748365E9F, 2.14748365E9F) + "%");
      TranslationHelper.KeyEntry key2 = new TranslationHelper.OptionEntry("poisonduration", serverConfig.poison, serverConfig.poison_duration / 20 + "s");
      TranslationHelper.KeyEntry key3 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "bonusdamage", serverConfig.bonus_damage);
      float pa = serverConfig.bonus_damage_amount <= 0.0F ? serverConfig.bonus_damage_amount : serverConfig.bonus_damage_amount - 1.0F;
      TranslationHelper.KeyEntry key4 = new TranslationHelper.OptionEntry("damagemultiplier", serverConfig.bonus_damage, helper.translateAttributeValue(1, (double)pa));
      return helper.formatAddVariables(translation, key, key1, key2, key3, key4);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add(new AbilityPoisonAffinity());
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.POISON_STONE;
   }
}
