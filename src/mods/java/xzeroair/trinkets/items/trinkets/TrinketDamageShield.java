package xzeroair.trinkets.items.trinkets;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityResistance;
import xzeroair.trinkets.traits.abilities.compat.firstaid.AbilityIgnoreHeadshot;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigDamageShield;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketDamageShield extends AccessoryBase {
   public static final ConfigDamageShield serverConfig;
   public static final ClientConfig.ClientConfigItems.ClientConfigDamageShield clientConfig;

   public TrinketDamageShield(String name) {
      super(name);
      this.setUUID("c0885371-20dd-4c56-86eb-78f24d9fe777");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "explosionresist", serverConfig.explosion_resist);
      TranslationHelper.KeyEntry key1 = new TranslationHelper.OptionEntry("explosionresistamount", serverConfig.explosion_resist, 100.0F - serverConfig.explosion_amount * 100.0F + "%");
      TranslationHelper.KeyEntry key2 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "damageignored", serverConfig.damage_ignore);
      String hits = "0";

      try {
         EntityPlayer player = Minecraft.getMinecraft().player;
         IAbilityInterface ability = (IAbilityInterface)Capabilities.getEntityProperties(player, (Object)null, (prop, a) -> {
            return prop.getAbilityHandler().getAbility("xat:safe_guard");
         });
         if (ability instanceof AbilityResistance) {
            AbilityResistance safeGuard = (AbilityResistance)ability;
            hits = safeGuard.getHitCount() + "";
         }
      } catch (Exception var18) {
         hits = "ERROR";
      }

      TranslationHelper.KeyEntry key3 = new TranslationHelper.OptionEntry("damageignoredhitcount", serverConfig.damage_ignore, hits);
      TranslationHelper.KeyEntry key4 = new TranslationHelper.OptionEntry("cfghitcount", serverConfig.damage_ignore, serverConfig.hits);
      String effect = "ERROR";

      try {
         Potion peffect = Potion.getPotionFromResourceLocation(serverConfig.potionEffect);
         if (peffect != null) {
            effect = (new TextComponentTranslation(peffect.getName(), new Object[0])).getUnformattedText();
         }
      } catch (Exception var17) {
         ;
      }

      TranslationHelper.KeyEntry key5 = new TranslationHelper.OptionEntry("soheffect", true, effect);
      TranslationHelper.KeyEntry FirstAid = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "headshots", serverConfig.compat.firstaid.chance_ignore);
      TranslationHelper.KeyEntry key6 = new TranslationHelper.OptionEntry("headshotchance", serverConfig.compat.firstaid.chance_ignore, MathHelper.clamp(1.0F / (float)serverConfig.compat.firstaid.chance_headshots * 100.0F, -2.14748365E9F, 2.14748365E9F) + "%");
      return helper.formatAddVariables(translation, key, key1, key2, key3, key4, FirstAid, key5, key6);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add(new AbilityResistance());
      if (Trinkets.FirstAid && serverConfig.compat.firstaid.chance_ignore) {
         abilities.add(new AbilityIgnoreHeadshot());
      }

   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.onUpdate(stack, world, entity, itemSlot, isSelected);
      Capabilities.getTrinketProperties(stack, (prop) -> {
         prop.setVariant(0);
      });
   }

   public void eventPlayerTick(ItemStack stack, EntityPlayer player) {
      super.eventPlayerTick(stack, player);
      if (TrinketsConfig.SERVER.misc.retrieveVIP) {
         Capabilities.getVipStatus(player, (status) -> {
            Capabilities.getTrinketProperties(stack, (prop) -> {
               prop.setVariant(status.getStatus());
            });
         });
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelResourceLocation normal = new ModelResourceLocation(this.getRegistryName().toString(), "inventory");
      ModelResourceLocation bro = new ModelResourceLocation(this.getRegistryName().toString() + "_bro", "inventory");
      ModelResourceLocation panda = new ModelResourceLocation(this.getRegistryName().toString() + "_panda", "inventory");
      ModelResourceLocation vip = new ModelResourceLocation(this.getRegistryName().toString() + "_vip", "inventory");
      ModelResourceLocation artsy = new ModelResourceLocation(this.getRegistryName().toString() + "_artsy", "inventory");
      ModelResourceLocation twilight = new ModelResourceLocation(this.getRegistryName().toString() + "_twilight", "inventory");
      ModelBakery.registerItemVariants(this, new ResourceLocation[]{normal, bro, panda, vip, artsy, twilight});
      ModelLoader.setCustomMeshDefinition(this, (stack) -> {
         int variant = ((Integer)Capabilities.getTrinketProperties(stack, Integer.valueOf(0), (prop, var) -> {
            return prop.getVariant();
         })).intValue();
         switch(variant) {
         case 0:
            return normal;
         case 1:
            return vip;
         case 2:
            return bro;
         case 3:
            return panda;
         case 4:
            return artsy;
         case 5:
            return twilight;
         default:
            return vip;
         }
      });
   }

   @SideOnly(Side.CLIENT)
   public void playerRenderLayer(ItemStack stack, EntityLivingBase player, RenderPlayer renderer, boolean isSlim, float partialTicks, float scale) {
      if (clientConfig.doRender) {
         float offsetX = 0.17F;
         float offsetY = 0.22F;
         float offsetZ = 0.16F;
         GlStateManager.pushMatrix();
         if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         renderer.getMainModel().bipedBody.postRender(scale);
         GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.17F, -0.22F, 0.16F);
         if (player.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
            GlStateManager.translate(0.030000001F, 0.0F, 0.040000007F);
         }

         float bS = 3.0F;
         GlStateManager.scale(scale * 3.0F, scale * 3.0F, scale * 3.0F);
         Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
         GlStateManager.popMatrix();
      }
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.DAMAGE_SHIELD;
      clientConfig = TrinketsConfig.CLIENT.items.DAMAGE_SHIELD;
   }
}
