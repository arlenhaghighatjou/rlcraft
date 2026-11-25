package xzeroair.trinkets.items.foods;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.items.base.FoodBase;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class Mana_Reagent extends FoodBase {
   public Mana_Reagent(String name) {
      super(name, 2, 1.0F);
      this.setAlwaysEdible();
      this.setUUID("5569090a-43b2-468e-918a-05df8570277c");
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 32;
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("mpmax", "10");
      return helper.formatAddVariables(translation, key);
   }

   public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
      MagicStats magic = Capabilities.getMagicStats(entity);
      if (magic != null) {
         magic.setBonusMana(magic.getBonusMana() - 1.0D);
      }

      if (TrinketsConfig.SERVER.mana.reagentHarmful) {
         if (!entity.isPotionActive(MobEffects.POISON)) {
            entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 300, 1, false, false));
         }

         if (!entity.isPotionActive(MobEffects.WEAKNESS)) {
            entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 1, false, false));
         }
      }

      return super.onItemUseFinish(stack, world, entity);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
      return super.onItemRightClick(worldIn, player, handIn);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.EAT;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      super.registerModels();
   }
}
