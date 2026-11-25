package xzeroair.trinkets.items.trinkets;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.init.Elements;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityBlockFinder;
import xzeroair.trinkets.traits.abilities.AbilityFireImmunity;
import xzeroair.trinkets.traits.abilities.AbilityFrostWalker;
import xzeroair.trinkets.traits.abilities.AbilityIceImmunity;
import xzeroair.trinkets.traits.abilities.AbilityNightVision;
import xzeroair.trinkets.traits.abilities.compat.survival.AbilityColdImmunity;
import xzeroair.trinkets.traits.abilities.compat.survival.AbilityHeatImmunity;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.elements.Element;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigDragonsEye;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketDragonsEye extends AccessoryBase {
   public static final ConfigDragonsEye serverConfig;
   public static final ClientConfig.ClientConfigItems.ClientConfigDragonsEye clientConfig;

   public TrinketDragonsEye(String name) {
      super(name);
      this.setUUID("6a345136-49b7-4b71-88dc-87301e329ac1");
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == this.getCreativeTab()) {
         ItemStack fire = new ItemStack(this, 1, 0);
         items.add(fire);
         if (serverConfig.compat.iaf.ICE_VARIANT) {
            ItemStack ice = new ItemStack(this, 1, 0);
            Capabilities.getTrinketProperties(ice, (prop) -> {
               prop.setVariant(1);
               prop.getElementAttributes().setPrimaryElement(Elements.ICE);
            });
            items.add(ice);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "treasurefinder", serverConfig.oreFinder);
      String oreTarget = "NONE";

      try {
         EntityPlayer player = Minecraft.getMinecraft().player;
         IAbilityInterface ability = (IAbilityInterface)Capabilities.getEntityProperties(player, (Object)null, (prop, a) -> {
            return prop.getAbilityHandler().getAbility("xat:block_detection");
         });
         if (ability instanceof AbilityBlockFinder) {
            AbilityBlockFinder finder = (AbilityBlockFinder)ability;
            String target = finder.parseTargetName(finder.getTreasure());
            if (!target.isEmpty()) {
               oreTarget = target;
            }
         }
      } catch (Exception var20) {
         oreTarget = "ERROR";
      }

      TranslationHelper.KeyEntry key1 = new TranslationHelper.OptionEntry("target", serverConfig.oreFinder, oreTarget.trim());
      TranslationHelper.KeyEntry keybind1 = new TranslationHelper.KeyBindEntry("denvkb", ModKeyBindings.DRAGONS_EYE_ABILITY.getDisplayName());
      TranslationHelper.KeyEntry keybind2 = new TranslationHelper.KeyBindEntry("deofkb", ModKeyBindings.DRAGONS_EYE_TARGET.getDisplayName());
      boolean tan = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails || Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty;
      Element element = this.getPrimaryElement(stack);
      boolean isIceVariant = element == Elements.ICE;
      TranslationHelper.KeyEntry key2 = new TranslationHelper.OptionEntry("variantresist", (new TextComponentTranslation(isIceVariant ? "xat.effect.ice_resistance" : "effect.fireResistance", new Object[0])).getFormattedText());
      TranslationHelper.KeyEntry TANHot = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "heatimmune", tan && serverConfig.compat.tan.immuneToHeat);
      TranslationHelper.KeyEntry TANCold = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "coldimmune", tan && serverConfig.compat.tan.immuneToHeat);
      TranslationHelper.KeyEntry key3 = new TranslationHelper.OptionEntry("typeimmune", (new TextComponentTranslation(isIceVariant ? TANCold.option() : TANHot.option(), new Object[0])).getFormattedText());
      TranslationHelper.KeyEntry IAFFrostWalker = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack) + ".compat.iaf.ice", "frostwalker", isIceVariant && serverConfig.compat.iaf.ICE_VARIANT && serverConfig.compat.iaf.FROST_WALKER);
      return helper.formatAddVariables(translation, key, key1, keybind1, keybind2, key2, TANHot, TANCold, key3, IAFFrostWalker).replace("#underline:", "");
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add((new AbilityNightVision()).toggleAbility(true));
      Element element = this.getPrimaryElement(stack);
      boolean isIceVariant = element == Elements.ICE;
      boolean tan = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails || Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty;
      if (serverConfig.compat.iaf.ICE_VARIANT && isIceVariant) {
         abilities.add(new AbilityIceImmunity());
         if (serverConfig.compat.iaf.FROST_WALKER) {
            abilities.add(new AbilityFrostWalker());
         }

         if (tan && serverConfig.compat.tan.immuneToCold) {
            abilities.add(new AbilityColdImmunity());
         }
      } else {
         abilities.add(new AbilityFireImmunity());
         if (tan && serverConfig.compat.tan.immuneToHeat) {
            abilities.add(new AbilityHeatImmunity());
         }
      }

      if (serverConfig.oreFinder) {
         abilities.add(new AbilityBlockFinder());
      }

   }

   public String getItemStackDisplayName(ItemStack stack) {
      Element element = this.getPrimaryElement(stack);
      String displayName;
      if (element == Elements.ICE) {
         displayName = (new TextComponentTranslation(this.getUnlocalizedName(stack) + ".ice.name", new Object[0])).getFormattedText();
      } else {
         displayName = super.getItemStackDisplayName(stack);
      }

      return displayName.trim();
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelResourceLocation normal = new ModelResourceLocation(this.getRegistryName().toString(), "inventory");
      ModelResourceLocation iceVariant = new ModelResourceLocation(this.getRegistryName().toString() + "_ice", "inventory");
      ModelResourceLocation fireVariant = new ModelResourceLocation(this.getRegistryName().toString() + "_fire", "inventory");
      ModelBakery.registerItemVariants(this, new ResourceLocation[]{normal, fireVariant, iceVariant});
      ModelLoader.setCustomMeshDefinition(this, (stack) -> {
         Element element = this.getPrimaryElement(stack);
         return element == Elements.ICE ? iceVariant : normal;
      });
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.DRAGON_EYE;
      clientConfig = TrinketsConfig.CLIENT.items.DRAGON_EYE;
   }
}
