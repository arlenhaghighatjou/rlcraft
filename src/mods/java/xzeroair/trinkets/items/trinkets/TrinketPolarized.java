package xzeroair.trinkets.items.trinkets;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.Trinket.TrinketProperties;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.AbilityMagnetic;
import xzeroair.trinkets.traits.abilities.AbilityRepel;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IToggleAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigPolarizedStone;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketPolarized extends AccessoryBase {
   public static final ConfigPolarizedStone serverConfig;

   public TrinketPolarized(String name) {
      super(name);
      this.setUUID("1ed98d9e-3075-45e0-b6f7-fcdff24caed4");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "collect");
      TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "collectxp", serverConfig.collectXP);
      TranslationHelper.KeyEntry key2 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "repel", serverConfig.repell);
      TrinketProperties prop = Capabilities.getTrinketProperties(stack);
      boolean main = prop == null ? false : prop.mainAbility();
      boolean alt = prop == null ? false : prop.altAbility();
      TranslationHelper.KeyEntry key3 = new TranslationHelper.OptionEntry("collecttoggle", serverConfig.collectXP, helper.toggleCheckTranslation(main));
      TranslationHelper.KeyEntry key4 = new TranslationHelper.OptionEntry("repeltoggle", serverConfig.repell, helper.toggleCheckTranslation(alt));
      TranslationHelper.KeyEntry key5 = new TranslationHelper.KeyBindEntry("magnetkb", ModKeyBindings.POLARIZED_STONE_ABILITY.getDisplayName());
      TranslationHelper.KeyEntry key6 = new TranslationHelper.KeyBindEntry("auxkb", ModKeyBindings.AUX_KEY.getDisplayName());
      return helper.formatAddVariables(translation, key, key1, key2, key3, key4, key5, key6);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add(new AbilityMagnetic());
      if (serverConfig.repell) {
         abilities.add(new AbilityRepel());
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
      ItemStack stack = player.getHeldItem(handIn);
      boolean client = player.world.isRemote;
      boolean sneak = player.isSneaking();
      TrinketProperties prop = Capabilities.getTrinketProperties(stack);
      if (prop != null) {
         EntityProperties entityProps = Capabilities.getEntityProperties(player);
         TranslationHelper helper = TranslationHelper.INSTANCE;
         if (!sneak) {
            boolean toggle = prop.mainAbility();
            if (client) {
               String magnetMode = (new TextComponentTranslation(stack.getUnlocalizedName() + ".magnetmode", new Object[0])).getFormattedText();
               TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("collecttoggle", serverConfig.collectXP, helper.toggleCheckTranslation(!toggle));
               player.sendStatusMessage(new TextComponentString(helper.formatAddVariables(magnetMode, key)), true);
            }

            Capabilities.getEntityProperties(player, (props) -> {
               if (entityProps != null) {
                  AbilityHandler.AbilityHolder magnet = entityProps.getAbilityHandler().getAbilityHolder("xat:magnetic");
                  if (magnet != null && this.getRegistryName().toString().contentEquals(magnet.getSourceID())) {
                     IAbilityInterface ability = magnet.getAbility();
                     if (ability instanceof IToggleAbility) {
                        ((IToggleAbility)ability).toggleAbility(!toggle);
                     }
                  }
               }

            });
            prop.toggleMainAbility(!toggle);
         } else {
            boolean toggle = prop.altAbility();
            if (client) {
               String repelMode = (new TextComponentTranslation(stack.getUnlocalizedName() + ".repelmode", new Object[0])).getFormattedText();
               TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("repeltoggle", serverConfig.repell, helper.toggleCheckTranslation(!toggle));
               player.sendStatusMessage(new TextComponentString(helper.formatAddVariables(repelMode, key)), true);
            }

            Capabilities.getEntityProperties(player, (props) -> {
               if (entityProps != null) {
                  AbilityHandler.AbilityHolder repel = entityProps.getAbilityHandler().getAbilityHolder("xat:repel");
                  if (repel != null && this.getRegistryName().toString().contentEquals(repel.getSourceID())) {
                     IAbilityInterface ability = repel.getAbility();
                     if (ability instanceof IToggleAbility) {
                        ((IToggleAbility)ability).toggleAbility(!toggle);
                     }
                  }
               }

            });
            prop.toggleAltAbility(!toggle);
         }
      }

      player.setActiveHand(handIn);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
   }

   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + 0;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelResourceLocation normal = new ModelResourceLocation(this.getRegistryName().toString(), "inventory");
      ModelResourceLocation magnet = new ModelResourceLocation(this.getRegistryName().toString() + "_magnet", "inventory");
      ModelResourceLocation repel = new ModelResourceLocation(this.getRegistryName().toString() + "_repell", "inventory");
      ModelResourceLocation both = new ModelResourceLocation(this.getRegistryName().toString() + "_both", "inventory");
      ModelBakery.registerItemVariants(this, new ResourceLocation[]{normal, magnet, repel, both});
      ModelLoader.setCustomMeshDefinition(this, (stack) -> {
         TrinketProperties prop = Capabilities.getTrinketProperties(stack);
         boolean main = prop != null && prop.mainAbility();
         boolean alt = prop != null && prop.altAbility();
         if (main && alt) {
            return both;
         } else if (main) {
            return magnet;
         } else {
            return alt ? repel : normal;
         }
      });
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.POLARIZED_STONE;
   }
}
