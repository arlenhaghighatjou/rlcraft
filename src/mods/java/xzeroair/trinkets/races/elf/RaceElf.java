package xzeroair.trinkets.races.elf;

import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.attributes.JumpAttribute;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.client.model.ElfEars;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.elf.config.ElfConfig;
import xzeroair.trinkets.traits.abilities.AbilityChargedShot;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.AttributeHelper;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class RaceElf extends EntityRacePropertiesHandler {
   public static final ElfConfig serverConfig;
   protected UpdatingAttribute bonusSpeed = (new UpdatingAttribute(UUID.fromString("628dedc0-5f63-4b45-bccb-ecb0fe881b49"), SharedMonsterAttributes.MOVEMENT_SPEED)).setSavedInNBT(false);
   protected UpdatingAttribute bonusAtkSpeed = (new UpdatingAttribute(UUID.fromString("628dedc0-5f63-4b45-bccb-ecb0fe881b49"), SharedMonsterAttributes.ATTACK_SPEED)).setSavedInNBT(false);
   protected UpdatingAttribute jump = (new UpdatingAttribute(UUID.fromString("628dedc0-5f63-4b45-bccb-ecb0fe881b49"), JumpAttribute.Jump)).setSavedInNBT(false);
   private ModelBase ears = new ElfEars();
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/ears.png");
   public static final ResourceLocation TEXTURE_INNER = new ResourceLocation("xat:textures/inner_ears.png");
   public static final ResourceLocation TEXTURE_OUTER = new ResourceLocation("xat:textures/outer_ears.png");

   public RaceElf(@Nonnull EntityLivingBase e) {
      super(e, EntityRaces.elf);
   }

   public void startTransformation() {
      if (serverConfig.charge_shot) {
         this.addAbility(new AbilityChargedShot());
      }

   }

   public void whileTransformed() {
      if (!this.entity.world.isRemote) {
         try {
            if (this.entity.world.getBiome(this.entity.getPosition()) != null) {
               Set<Type> biomeType = BiomeDictionary.getTypes(this.entity.world.getBiome(this.entity.getPosition()));
               if (biomeType.contains(Type.FOREST)) {
                  this.bonusSpeed.addModifier(this.entity, 0.2D, 2);
                  this.bonusAtkSpeed.addModifier(this.entity, 0.5D, 2);
                  this.jump.addModifier(this.entity, 0.2D, 2);
               } else {
                  this.bonusSpeed.removeModifier(this.entity);
                  this.bonusAtkSpeed.removeModifier(this.entity);
                  this.jump.removeModifier(this.entity);
               }
            }
         } catch (Exception var2) {
            ;
         }

      }
   }

   public void endTransformation() {
      AttributeHelper.removeAttributes(this.entity, UUID.fromString("628dedc0-5f63-4b45-bccb-ecb0fe881b49"));
   }

   @SideOnly(Side.CLIENT)
   public void doRenderLayer(RenderLivingBase renderer, boolean isFake, boolean isSlim, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (TrinketsConfig.CLIENT.rendering && this.showTraits()) {
         this.ears = new ElfEars();
         GlStateManager.pushMatrix();
         if (this.entity.isSneaking()) {
            GlStateManager.translate(0.0D, 0.2D, 0.0D);
         }

         if (renderer instanceof RenderPlayer) {
            RenderPlayer rend = (RenderPlayer)renderer;
            rend.getMainModel().bipedHead.postRender(scale);
         }

         if (this.entity.hasItemInSlot(EntityEquipmentSlot.HEAD)) {
            GlStateManager.translate(0.0F, -0.02F, -0.045F);
            GlStateManager.scale(1.1F, 1.1F, 1.1F);
         }

         float[] rgb = ColorHelper.getRGBColor(this.getTraitVariant() == 1 ? this.getAltTraitColor() : this.getTraitColor());
         float[] rgb2 = ColorHelper.getRGBColor(this.getTraitVariant() == 1 ? this.getTraitColor() : this.getAltTraitColor());
         float fscale = 0.3F;
         GlStateManager.scale(0.3F, 0.3F, 0.3F);
         double x = 0.0D;
         double y = -1.5D;
         double height = 1.0D;
         double width = 1.0D;
         double z = -0.4D;
         float u = 16.0F;
         float v = 0.0F;
         int uWidth = 16;
         int vHeight = 16;
         float tileWidth = 64.0F;
         float tileHeight = 32.0F;
         double xR = 0.72D;
         double xL = -0.72D;
         float rot = 26.0F;
         int solidVariant = 2;
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.rotate(-rot, 0.0F, 1.0F, 0.0F);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, 0.72D, -1.5D, -0.4D, 16.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE, 0.72D, -1.5D, -0.39990000000000003D, 16.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_INNER, 0.72D, -1.5D, -0.4D, 16.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, 0.72D, -1.5D, -0.4D, 16.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, 0.72D, -1.5D, -0.39990000000000003D, 16.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.rotate(rot * 2.0F, 0.0F, 1.0F, 0.0F);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -0.72D, -1.5D, -0.4D, 16.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE, -0.72D, -1.5D, -0.39990000000000003D, 16.0F, 16.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_INNER, -0.72D, -1.5D, -0.4D, 16.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, -0.72D, -1.5D, -0.4D, 16.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, -0.72D, -1.5D, -0.39990000000000003D, 16.0F, 16.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.elf;
   }
}
