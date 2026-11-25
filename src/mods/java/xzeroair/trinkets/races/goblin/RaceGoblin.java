package xzeroair.trinkets.races.goblin;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.client.gui.entityPropertiesGui.GuiEntityProperties;
import xzeroair.trinkets.client.gui.hud.mana.ManaHud;
import xzeroair.trinkets.client.model.GoblinEars;
import xzeroair.trinkets.entity.AlphaWolf;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.goblin.config.GoblinConfig;
import xzeroair.trinkets.traits.abilities.AbilityClimbing;
import xzeroair.trinkets.traits.abilities.other.AbilityWolfMount;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class RaceGoblin extends EntityRacePropertiesHandler {
   public static final GoblinConfig serverConfig;
   private final ModelBase ears = new GoblinEars();
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/ears.png");
   public static final ResourceLocation TEXTURE_INNER = new ResourceLocation("xat:textures/inner_ears.png");
   public static final ResourceLocation TEXTURE_OUTER = new ResourceLocation("xat:textures/outer_ears.png");

   public RaceGoblin(@Nonnull EntityLivingBase e) {
      super(e, EntityRaces.goblin);
   }

   public void startTransformation() {
      this.addAbility(new AbilityClimbing());
      this.addAbility(new AbilityWolfMount());
   }

   public void whileTransformed() {
   }

   public void targetedByEnemy(EntityLivingBase enemy) {
      if (serverConfig.friendly_creepers && enemy instanceof EntityCreeper) {
         ((EntityCreeper)enemy).setAttackTarget((EntityLivingBase)null);
      }

   }

   public float isHurt(DamageSource source, float dmg) {
      if (source.isExplosion() && !TrinketHelper.AccessoryCheck(this.entity, ModItems.trinkets.TrinketDamageShield)) {
         float maxHP = this.entity.getHealth();
         float modifier = maxHP * 100.0F / 20.0F * 0.01F;
         float clampModifier = MathHelper.clamp(modifier, 0.01F, 1.0F);
         return dmg * clampModifier;
      } else if (source.isFireDamage()) {
         float modifier = 0.8F;
         if (source.getDamageType().equalsIgnoreCase("Lava")) {
            modifier = 0.5F;
         }

         return dmg * modifier;
      } else {
         return dmg;
      }
   }

   public boolean attackedEntity(EntityLivingBase target, DamageSource source, float dmg) {
      boolean attack = super.attackedEntity(target, source, dmg);
      if (serverConfig.creepers_explode && attack && target instanceof EntityCreeper) {
         if (!((EntityCreeper)target).hasIgnited()) {
            ((EntityCreeper)target).ignite();
         }

         if (((EntityCreeper)target).getCreeperState() == -1) {
            ((EntityCreeper)target).setCreeperState(1);
         }
      }

      return attack;
   }

   @SideOnly(Side.CLIENT)
   public void doRenderLayer(RenderLivingBase renderer, boolean isFake, boolean isSlim, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (TrinketsConfig.CLIENT.rendering) {
         if (this.showTraits()) {
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
            float fscale = 0.34F;
            GlStateManager.scale(0.34F, 0.34F, 0.34F);
            double x = 0.0D;
            double y = -1.2D;
            double z = -0.6D;
            double height = 1.0D;
            double width = 1.0D;
            float u = 0.0F;
            float v = 0.0F;
            int uWidth = 16;
            int vHeight = 16;
            float tileWidth = 64.0F;
            float tileHeight = 32.0F;
            double xR = 0.5D;
            double xL = -0.5D;
            float rot = 30.0F;
            int solidVariant = 2;
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-rot, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
            if (this.getTraitVariant() == 2) {
               DrawingHelper.Draw(TEXTURE, 0.5D, -1.2D, -0.6D, 0.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
               DrawingHelper.Draw(TEXTURE, 0.5D, -1.2D, -0.5999D, 0.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            } else {
               DrawingHelper.Draw(TEXTURE_INNER, 0.5D, -1.2D, -0.6D, 0.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
               DrawingHelper.Draw(TEXTURE_OUTER, 0.5D, -1.2D, -0.6D, 0.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
               DrawingHelper.Draw(TEXTURE_OUTER, 0.5D, -1.2D, -0.5999D, 0.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
            }

            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
            if (this.getTraitVariant() == 2) {
               DrawingHelper.Draw(TEXTURE, -0.5D, -1.2D, -0.6D, 0.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
               DrawingHelper.Draw(TEXTURE, -0.5D, -1.2D, -0.5999D, 0.0F, 16.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            } else {
               DrawingHelper.Draw(TEXTURE_INNER, -0.5D, -1.2D, -0.6D, 0.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
               DrawingHelper.Draw(TEXTURE_OUTER, -0.5D, -1.2D, -0.6D, 0.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
               DrawingHelper.Draw(TEXTURE_OUTER, -0.5D, -1.2D, -0.5999D, 0.0F, 16.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public void doRenderPlayerPre(EntityPlayer entity, double x, double y, double z, RenderPlayer renderer, float partialTick) {
      GuiScreen screen = Minecraft.getMinecraft().currentScreen;
      if (entity != Minecraft.getMinecraft().player || screen == null || screen instanceof GuiChat || screen instanceof GuiEntityProperties || screen instanceof ManaHud) {
         if ((this.isTransforming() || this.isTransformed()) && !this.properties.isNormalSize()) {
            double hScale = (double)this.properties.getHeightValue() * 0.01D;
            double wScale = (double)this.properties.getWidthValue() * 0.01D;
            double xLoc = x / wScale - x;
            double yLoc = y / hScale - y;
            double zLoc = z / wScale - z;
            double yOffset = entity.getYOffset();
            Entity mount = entity.getRidingEntity();
            double mountedOffset = entity.isRiding() && mount != null ? mount.getMountedYOffset() : 0.0D;
            double retMountedOffset = -(mountedOffset + yOffset) - 0.1D;
            if (entity.isRiding() && !(mount instanceof AlphaWolf)) {
               GlStateManager.translate(0.0D, mountedOffset, 0.0D);
               GlStateManager.translate(0.0D, -yOffset, 0.0D);
               GlStateManager.translate(0.0D, retMountedOffset, 0.0D);
            }

            GlStateManager.scale(wScale, hScale, wScale);
            if (entity.isRiding() && !(mount instanceof AlphaWolf)) {
               GlStateManager.translate(0.0D, -retMountedOffset, 0.0D);
               GlStateManager.translate(0.0D, yOffset, 0.0D);
               GlStateManager.translate(0.0D, -mountedOffset, 0.0D);
            }

            GlStateManager.translate(xLoc, yLoc, zLoc);
         }

      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.goblin;
   }
}
