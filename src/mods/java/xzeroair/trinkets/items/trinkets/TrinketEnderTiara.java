package xzeroair.trinkets.items.trinkets;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import net.minecraft.client.model.ModelBiped;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.client.model.BipedJsonModel;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityEnderQueen;
import xzeroair.trinkets.traits.abilities.compat.survival.AbilityColdImmunity;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigEnderCrown;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketEnderTiara extends AccessoryBase {
   public static final ConfigEnderCrown serverConfig;
   public static final ClientConfig.ClientConfigItems.ClientConfigEnderCrown clientConfig;
   private BipedJsonModel tiara;

   public TrinketEnderTiara(String name) {
      super(name);
      this.setUUID("a45dbc1c-17e9-40b4-b6a3-09dea74355b7");
   }

   @SideOnly(Side.CLIENT)
   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      TranslationHelper.KeyEntry key = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "damageignored", serverConfig.dmgChance);
      TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "endermenchance", serverConfig.spawnChance);
      TranslationHelper.KeyEntry key2 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "endermenfollow", serverConfig.Follow);
      TranslationHelper.KeyEntry key3 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "waterhurts", serverConfig.water_hurts);
      TranslationHelper.KeyEntry key4 = new TranslationHelper.OptionEntry("chance", serverConfig.dmgChance || serverConfig.spawnChance, MathHelper.clamp(1.0F / (float)serverConfig.chance * 100.0F, -2.14748365E9F, 2.14748365E9F) + "%");
      boolean tan = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails || Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty;
      TranslationHelper.KeyEntry TAN = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "coldimmune", tan && serverConfig.compat.tan.immuneToCold);
      return helper.formatAddVariables(translation, key, key1, key2, key3, key4, TAN);
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      abilities.add(new AbilityEnderQueen());
      boolean tan = Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails || Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty;
      if (tan && serverConfig.compat.tan.immuneToCold) {
         abilities.add(new AbilityColdImmunity());
      }

   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      return super.getAttributeModifiers(slot, stack);
   }

   public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
      return EntityEquipmentSlot.HEAD;
   }

   public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
      boolean alreadyEquipped = entity instanceof EntityLivingBase && TrinketHelper.AccessoryCheck((EntityLivingBase)entity, this);
      return !alreadyEquipped && super.isValidArmor(stack, armorType, entity);
   }

   public boolean canEquipAccessory(ItemStack stack, EntityLivingBase player) {
      boolean alreadyEquipped = false;

      try {
         alreadyEquipped = TrinketHelper.getSlotInfoForItemFromEquipment(player, (s) -> {
            return s != null && !s.isEmpty() && s.isItemEqual(stack);
         }) == null;
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return alreadyEquipped && super.canEquipAccessory(stack, player);
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
      if (!itemStack.isEmpty()) {
         BipedJsonModel model = this.tiara;
         model.tranformType = TransformType.HEAD;
         model.equippedAsAccessory = false;
         model.setModelAttributes(_default);
         model.isSneak = _default.isSneak;
         model.isRiding = _default.isRiding;
         model.isChild = _default.isChild;
         model.rightArmPose = _default.rightArmPose;
         model.leftArmPose = _default.leftArmPose;
         return model;
      } else {
         return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
      }
   }

   @SideOnly(Side.CLIENT)
   public void playerRenderLayer(ItemStack stack, EntityLivingBase player, RenderPlayer renderer, boolean isSlim, float partialTicks, float scale) {
      if (clientConfig.doRender) {
         if (this.tiara == null) {
            this.tiara = new BipedJsonModel(new ResourceLocation(this.getRegistryName().toString() + "_model"));
         }

         GlStateManager.pushMatrix();
         BipedJsonModel model = this.tiara;
         model.equippedAsAccessory = true;
         model.tranformType = TransformType.HEAD;
         double sneakOffset = player.isSneaking() ? 0.2D : 0.0D;
         boolean hasHelmet = player.hasItemInSlot(EntityEquipmentSlot.HEAD);
         float hScale = hasHelmet ? 1.2F : 1.0F;
         double helmetOffsetY = hasHelmet ? 0.12D : 0.0D;
         double helmetOffsetZ = hasHelmet ? -0.04D : 0.0D;
         GlStateManager.translate(0.0D, sneakOffset, 0.0D);
         renderer.getMainModel().bipedHead.postRender(scale);
         GlStateManager.translate(0.0D, 0.19D, -0.04D);
         GlStateManager.scale(scale * 10.0F, scale * 10.0F, scale * 10.0F);
         GlStateManager.translate(0.0D, helmetOffsetY, helmetOffsetZ);
         GlStateManager.scale(hScale, hScale, hScale);
         model.render(player, player.limbSwing, player.limbSwingAmount, (float)player.ticksExisted, player.rotationYaw, player.rotationPitch, scale);
         GlStateManager.popMatrix();
      }
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelResourceLocation normal = new ModelResourceLocation(this.getRegistryName().toString(), "inventory");
      ModelResourceLocation model = new ModelResourceLocation(this.getRegistryName().toString() + "_model", "inventory");
      ModelBakery.registerItemVariants(this, new ResourceLocation[]{normal, model});
      ModelLoader.setCustomMeshDefinition(this, (stack) -> {
         return (ModelResourceLocation)Capabilities.getTrinketProperties(stack, normal, (prop, tex) -> {
            return prop.getSlotInfo().getHandlerType().equals(TrinketHelper.SlotInformation.ItemHandlerType.HEAD) ? model : tex;
         });
      });
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.ENDER_CROWN;
      clientConfig = TrinketsConfig.CLIENT.items.ENDER_CROWN;
   }
}
