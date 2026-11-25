package xzeroair.trinkets.items.trinkets;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityWaterAffinity;
import xzeroair.trinkets.traits.abilities.compat.survival.AbilityParasitesImmunity;
import xzeroair.trinkets.traits.abilities.compat.survival.AbilityThirstImmunity;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigSeaStone;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketSeaStone extends AccessoryBase {
   public static final ConfigSeaStone serverConfig;
   public static final ClientConfig.ClientConfigItems.ClientConfigSeaStone clientConfig;

   public TrinketSeaStone(String name) {
      super(name);
      this.setUUID("6029aecd-318e-4b45-8c36-2ddd7f481e36");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "bubbles", serverConfig.underwater_breathing);
      TranslationHelper.KeyEntry key1 = new TranslationHelper.OptionEntry("bubbles", true, serverConfig.underwater_breathing ? "10" : "1");
      TranslationHelper.KeyEntry key2 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "betterswimming", serverConfig.Swim_Tweaks);
      boolean tan = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails || Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty;
      TranslationHelper.KeyEntry key3 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "tanthirst", tan && serverConfig.compat.tan.prevent_thirst);
      return helper.formatAddVariables(translation, key, key1, key2, key3);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add(new AbilityWaterAffinity());
      boolean simplediff = Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty;
      boolean tan = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails || simplediff;
      if (tan && serverConfig.compat.tan.prevent_thirst) {
         abilities.add(new AbilityThirstImmunity());
         if (simplediff) {
            abilities.add(new AbilityParasitesImmunity());
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void playerRenderLayer(ItemStack stack, EntityLivingBase player, RenderPlayer renderer, boolean isSlim, float partialTicks, float scale) {
      if (clientConfig.doRender) {
         float offsetY = 0.16F;
         float offsetZ = 0.14F;
         GlStateManager.pushMatrix();
         if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         renderer.getMainModel().bipedBody.postRender(scale);
         GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.16F, 0.14F);
         if (player.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
            GlStateManager.translate(0.0F, 0.0F, 0.060000002F);
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

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelResourceLocation normal = new ModelResourceLocation(this.getRegistryName().toString(), "inventory");
      ModelResourceLocation worn = new ModelResourceLocation(this.getRegistryName().toString() + "_worn", "inventory");
      ModelBakery.registerItemVariants(this, new ResourceLocation[]{normal, worn});
      ModelLoader.setCustomMeshDefinition(this, (stack) -> {
         return worn;
      });
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.SEA_STONE;
      clientConfig = TrinketsConfig.CLIENT.items.SEA_STONE;
   }
}
