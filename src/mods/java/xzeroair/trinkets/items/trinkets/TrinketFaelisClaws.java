package xzeroair.trinkets.items.trinkets;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityViciousStrike;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigFaelisClaw;
import xzeroair.trinkets.util.helpers.DrawingHelper;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketFaelisClaws extends AccessoryBase {
   public static final ClientConfig.ClientConfigItems.ClientConfigFaelisClaw clientConfig;
   public static final ConfigFaelisClaw serverConfig;
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/claws.png");

   public TrinketFaelisClaws(String name) {
      super(name);
      this.setUUID("4959ec73-142d-4b82-bd0d-cd6cd7431611");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("clawbleed", serverConfig.bleed, serverConfig.bleedDuration);
      return helper.formatAddVariables(translation, key);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      if (serverConfig.bleed) {
         abilities.add(new AbilityViciousStrike());
      }

   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }

   @SideOnly(Side.CLIENT)
   public void playerRenderLayer(ItemStack stack, EntityLivingBase player, RenderPlayer renderer, boolean isSlim, float partialTicks, float scale) {
      if (clientConfig.doRender) {
         int count = TrinketHelper.countAccessories(player, (s) -> {
            return !s.isEmpty() && s.getItem().getRegistryName().compareTo(stack.getItem().getRegistryName()) == 0;
         });
         float offsetX = isSlim ? -12.4F : -18.6F;
         float offsetY = 61.0F;
         float offsetZ = -21.0F;
         float bS = 0.16F;
         boolean flag = serverConfig.compat.baubles.equip_multiple;
         boolean flag1 = flag ? count > 0 : true;
         boolean flag2 = flag ? count > 1 : true;
         if (flag1) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableCull();
            Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            renderer.getMainModel().bipedLeftArm.postRender(scale);
            GlStateManager.scale(scale * 0.16F, scale * 0.16F, scale * 0.16F);
            GlStateManager.translate(-offsetX, 61.0F, -21.0F);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            DrawingHelper.Draw(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 32, 32, 32.0D, 32.0D, 32.0F, 32.0F);
            GlStateManager.popMatrix();
         }

         if (flag2) {
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            renderer.getMainModel().bipedRightArm.postRender(scale);
            GlStateManager.scale(scale * 0.16F, scale * 0.16F, scale * 0.16F);
            GlStateManager.translate(offsetX, 61.0F, -21.0F);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            DrawingHelper.Draw(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 32, 32, 32.0D, 32.0D, 32.0F, 32.0F);
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }

      }
   }

   static {
      clientConfig = TrinketsConfig.CLIENT.items.FAELIS_CLAW;
      serverConfig = TrinketsConfig.SERVER.Items.FAELIS_CLAW;
   }
}
