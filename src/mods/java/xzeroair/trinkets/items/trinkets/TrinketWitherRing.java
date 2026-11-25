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
import xzeroair.trinkets.traits.abilities.AbilityWitherAffinity;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigWitherRing;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketWitherRing extends AccessoryBase {
   public static final ConfigWitherRing serverConfig;

   public TrinketWitherRing(String name) {
      super(name);
      this.setUUID("bca63279-4a19-4891-b4b0-a5a2f76e4b90");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "wither", serverConfig.wither);
      TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "leech", serverConfig.leech);
      TranslationHelper.KeyEntry key2 = new TranslationHelper.OptionEntry("witherchance", serverConfig.wither, MathHelper.clamp(1.0F / (float)serverConfig.wither_chance * 100.0F, -2.14748365E9F, 2.14748365E9F) + "%");
      TranslationHelper.KeyEntry key3 = new TranslationHelper.OptionEntry("leechamount", serverConfig.leech, (double)serverConfig.leech_amount * 0.5D);
      return helper.formatAddVariables(translation, key, key1, key2, key3);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add(new AbilityWitherAffinity());
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.WITHER_RING;
   }
}
