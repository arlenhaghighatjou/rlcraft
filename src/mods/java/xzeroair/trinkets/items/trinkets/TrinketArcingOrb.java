package xzeroair.trinkets.items.trinkets;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityDodge;
import xzeroair.trinkets.traits.abilities.AbilityLightningBolt;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigArcingOrb;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketArcingOrb extends AccessoryBase {
   public final ConfigArcingOrb serverConfig;

   public TrinketArcingOrb(String name) {
      super(name);
      this.serverConfig = TrinketsConfig.SERVER.Items.ARCING_ORB;
      this.setUUID("249e65db-7dea-4825-8489-e6aa99a70be1");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "dodge", this.serverConfig.dodgeAbility && !TrinketsConfig.compat.elenaiDodge);
      TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "dodge.stun", this.serverConfig.dodgeStuns);
      TranslationHelper.KeyEntry key2 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "boltattack", this.serverConfig.attackAbility);
      TranslationHelper.KeyEntry key3 = new TranslationHelper.OptionEntry("dodgecost", this.serverConfig.dodgeAbility, this.serverConfig.dodgeCost);
      TranslationHelper.KeyEntry key4 = new TranslationHelper.OptionEntry("boltcost", this.serverConfig.attackAbility, this.serverConfig.attackCost);
      TranslationHelper.KeyEntry key5 = new TranslationHelper.OptionEntry("boltdamage", this.serverConfig.attackAbility, this.serverConfig.attackDmg);
      TranslationHelper.KeyEntry key6 = new TranslationHelper.KeyBindEntry("arckb", this.serverConfig.attackAbility, ModKeyBindings.ARCING_ORB_ABILITY.getDisplayName());
      return helper.formatAddVariables(translation, key, key1, key2, key3, key4, key5, key6);
   }

   public String[] getAttributeConfig() {
      return this.serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add(new AbilityLightningBolt());
      abilities.add(new AbilityDodge());
   }

   public boolean ItemEnabled() {
      return this.serverConfig.enabled;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }
}
