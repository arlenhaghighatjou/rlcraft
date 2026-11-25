package xzeroair.trinkets.items.trinkets;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityNullKinetic;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigInertiaNull;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketInertiaNull extends AccessoryBase {
   public static final ConfigInertiaNull serverConfig;

   public TrinketInertiaNull(String name) {
      super(name);
      this.setUUID("8192af5d-98de-4c1e-a125-e99864b99634");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("inertianullfd", true, 100.0F - serverConfig.falldamage_amount * 100.0F + "%");
      return helper.formatAddVariables(translation, key);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      float fallMultiplier = serverConfig.fall_damage ? serverConfig.falldamage_amount : 0.0F;
      abilities.add((new AbilityNullKinetic()).setFallMultiplier(fallMultiplier));
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.INERTIA_NULL;
   }
}
