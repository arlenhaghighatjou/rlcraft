package xzeroair.trinkets.races.dragon;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.dragon.config.DragonConfig;
import xzeroair.trinkets.traits.abilities.AbilityBlockFinder;
import xzeroair.trinkets.traits.abilities.AbilityFireBreathing;
import xzeroair.trinkets.traits.abilities.AbilityFireImmunity;
import xzeroair.trinkets.traits.abilities.AbilityFlying;
import xzeroair.trinkets.traits.abilities.AbilityNightVision;
import xzeroair.trinkets.traits.abilities.compat.survival.AbilityHeatImmunity;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class RaceDragon extends EntityRacePropertiesHandler {
   public static final DragonConfig serverConfig;
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/dragon_wings.png");
   public static final ResourceLocation TEXTURE_ARMS = new ResourceLocation("xat:textures/dragon_wings_arms.png");
   public static final ResourceLocation TEXTURE_LEATHER = new ResourceLocation("xat:textures/dragon_wings_leather.png");
   protected int tick;
   protected int lastTick = 0;
   protected float armSwing = 0.0F;
   protected int wingFrames = 0;

   public RaceDragon(@Nonnull EntityLivingBase e) {
      super(e, EntityRaces.dragon);
   }

   public void startTransformation() {
      this.addAbility((new AbilityNightVision()).toggleAbility(true));
      this.addAbility(new AbilityFireImmunity());
      if (TrinketsConfig.SERVER.Items.DRAGON_EYE.oreFinder) {
         this.addAbility(new AbilityBlockFinder());
      }

      if (serverConfig.creative_flight) {
         this.addAbility((new AbilityFlying()).setFlightEnabled(serverConfig.creative_flight).setSpeedEnabled(serverConfig.creative_flight_speed).setFlightSpeed((float)serverConfig.flight_speed).setFlightCost(serverConfig.flight_cost));
      }

      if (serverConfig.breath_damage > 0.0F) {
         this.addAbility(new AbilityFireBreathing());
      }

      if ((Trinkets.ToughAsNails || Trinkets.SimpleDifficulty) && serverConfig.compat.tan.immuneToHeat) {
         this.addAbility(new AbilityHeatImmunity());
      }

   }

   public void whileTransformed() {
      if (this.entity.world.isRemote) {
         if (!this.entity.onGround) {
            this.lastTick = this.tick;
            this.tick += 48;
         }

         if (this.tick >= 1210 || this.entity.onGround) {
            this.tick = 0;
            this.lastTick = 0;
         }
      }

   }

   public boolean canFly() {
      return super.canFly() && this.showTraits();
   }

   @SideOnly(Side.CLIENT)
   public void doRenderLayer(RenderLivingBase renderer, boolean isFake, boolean isSlim, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (TrinketsConfig.CLIENT.rendering && this.showTraits()) {
         GlStateManager.pushMatrix();
         if (this.entity.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         if (renderer instanceof RenderPlayer) {
            RenderPlayer rend = (RenderPlayer)renderer;
            rend.getMainModel().bipedBody.postRender(scale);
         }

         GlStateManager.scale(scale, scale, scale);
         GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -2.0F, 0.0F);
         if (this.entity.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
            GlStateManager.translate(-0.4F, -1.0F, 0.0F);
         }

         int solidVariant = 2;
         int base = this.entity.onGround ? 40 : 50;
         int wing = 20;
         int tip = -20;
         int rotX = 0;
         int rotZ = -10;
         double[][] frames = new double[121][10];
         this.wingFrames = frames.length;
         float tickT = (float)this.lastTick + (float)(this.tick - this.lastTick) * partialTicks;
         int angleTick = (int)(tickT * 0.1F);
         this.getWingFrames(angleTick, (double)base, 20.0D, -20.0D, 0.0D, -10.0D, frames);
         if (angleTick >= frames.length) {
            angleTick = frames.length - 1;
         }

         double x = -12.0D;
         double y = isSlim ? -19.0D : -20.0D;
         double z = 0.0D;
         int width = 8;
         int height = 32;
         int uWidth = 16;
         int vHeight = 64;
         int texWidth = 64;
         int texHeight = 64;
         int innerWidth = 16;
         int innerHeight = 32;
         int innerUWidth = 32;
         int innerVHeight = 64;
         int innerTexWidth = 64;
         int innerTexHeight = 64;
         int outerwidth = 8;
         int outHeight = 32;
         int outerUWidth = 16;
         int outerVHeight = 64;
         int outerTexWidth = 64;
         int outerTexHeight = 64;
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         float[] rgb = ColorHelper.getRGBColor(this.getTraitVariant() == 1 ? this.getAltTraitColor() : this.getTraitColor());
         float[] rgb2 = ColorHelper.getRGBColor(this.getTraitVariant() == 1 ? this.getTraitColor() : this.getAltTraitColor());
         float alpha = 1.0F;
         GlStateManager.scale(0.9D, 0.9D, 0.9D);
         GlStateManager.rotate((float)frames[angleTick][0], 0.0F, 1.0F, 0.0F);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -12.0D, y, 0.0D, 48.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_ARMS, -12.0D, y, 0.0D, 48.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_LEATHER, -12.0D, y, 0.0D, 48.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.translate(-12.0D, y, 0.0D);
         GlStateManager.rotate((float)frames[angleTick][1], 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(12.0D, -y, -0.0D);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -28.0D, y, 0.0D, 16.0F, 0.0F, 32, 64, 16.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_ARMS, -28.0D, y, 0.0D, 16.0F, 0.0F, 32, 64, 16.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_LEATHER, -28.0D, y, 0.0D, 16.0F, 0.0F, 32, 64, 16.0D, 32.0D, 64.0F, 64.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.translate(-28.0D, y, 0.0D);
         GlStateManager.rotate((float)(frames[angleTick][1] + (double)((float)frames[angleTick][2])), 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(28.0D, -y, -0.0D);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -36.0D, y, 0.0D, 0.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_ARMS, -36.0D, y, 0.0D, 0.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_LEATHER, -36.0D, y, 0.0D, 0.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.9D, 0.9D, 0.9D);
         GlStateManager.rotate((float)(-frames[angleTick][0]), 0.0F, 1.0F, 0.0F);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -12.0D, y, 0.0D, 48.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_ARMS, -12.0D, y, 0.0D, 48.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_LEATHER, -12.0D, y, 0.0D, 48.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.translate(-12.0D, y, 0.0D);
         GlStateManager.rotate((float)(-frames[angleTick][1]), 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(12.0D, -y, -0.0D);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -28.0D, y, 0.0D, 16.0F, 0.0F, 32, 64, 16.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_ARMS, -28.0D, y, 0.0D, 16.0F, 0.0F, 32, 64, 16.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_LEATHER, -28.0D, y, 0.0D, 16.0F, 0.0F, 32, 64, 16.0D, 32.0D, 64.0F, 64.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.translate(-28.0D, y, 0.0D);
         GlStateManager.rotate((float)(-(frames[angleTick][1] + (double)((float)frames[angleTick][2]))), 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(28.0D, -y, -0.0D);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -36.0D, y, 0.0D, 0.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_ARMS, -36.0D, y, 0.0D, 0.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_LEATHER, -36.0D, y, 0.0D, 0.0F, 0.0F, 16, 64, 8.0D, 32.0D, 64.0F, 64.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   private double[][] getWingFrames(int tickPos, double base, double wing, double tip, double rotX, double rotZ, double[][] frames) {
      frames[0] = new double[]{base, wing, tip, rotX, rotZ};
      int position = 1;
      int s = this.frames(15);

      for(int i1 = 0; i1 < s; ++i1) {
         frames[position][0] = base - (double)i1;
         frames[position][1] = wing - (double)i1;
         frames[position][2] = tip;
         frames[position][3] = rotX;
         frames[position][4] = rotZ;
         ++position;
      }

      int pos1 = position;
      int s1 = this.frames(10);

      for(int i1 = 0; i1 < s1; ++i1) {
         frames[position][0] = frames[pos1 - 1][0] - (double)i1;
         frames[position][1] = frames[pos1 - 1][1] - (double)i1;
         frames[position][2] = frames[pos1 - 1][2];
         frames[position][3] = frames[pos1 - 1][3];
         frames[position][4] = frames[pos1 - 1][4];
         ++position;
      }

      int pos2 = position;
      int s2 = this.frames(10);

      for(int i1 = 0; i1 < s2; ++i1) {
         frames[position][0] = frames[pos2 - 1][0];
         frames[position][1] = frames[pos2 - 1][1] + (double)i1;
         frames[position][2] = frames[pos2 - 1][2];
         frames[position][3] = frames[pos2 - 1][3] + (double)i1;
         frames[position][4] = frames[pos2 - 1][4] - (double)i1;
         ++position;
      }

      int pos3 = position;
      int s3 = this.frames(10);

      for(int i1 = 0; i1 < s3; ++i1) {
         frames[position][0] = frames[pos3 - 1][0] + (double)i1;
         frames[position][1] = frames[pos3 - 1][1] + (double)i1;
         frames[position][2] = frames[pos3 - 1][2];
         frames[position][3] = frames[pos3 - 1][3] + (double)i1;
         frames[position][4] = frames[pos3 - 1][4];
         ++position;
      }

      int pos4 = position;
      int s4 = this.frames(10);

      for(int i1 = 0; i1 < s4; ++i1) {
         frames[position][0] = frames[pos4 - 1][0] + (double)i1;
         frames[position][1] = frames[pos4 - 1][1] + (double)i1;
         frames[position][2] = frames[pos4 - 1][2];
         frames[position][3] = frames[pos4 - 1][3] + (double)i1;
         frames[position][4] = frames[pos4 - 1][4];
         ++position;
      }

      int pos5 = position;
      int s5 = this.frames(10);

      for(int i1 = 0; i1 < s5; ++i1) {
         frames[position][0] = frames[pos5 - 1][0] + (double)i1;
         frames[position][1] = frames[pos5 - 1][1] + (double)i1;
         frames[position][2] = frames[pos5 - 1][2];
         frames[position][3] = frames[pos5 - 1][3];
         frames[position][4] = frames[pos5 - 1][4];
         ++position;
      }

      int pos6 = position;
      int s6 = this.frames(10);

      for(int i1 = 0; i1 < s6; ++i1) {
         frames[position][0] = frames[pos6 - 1][0];
         frames[position][1] = frames[pos6 - 1][1] + (double)i1;
         frames[position][2] = frames[pos6 - 1][2];
         frames[position][3] = frames[pos6 - 1][3];
         frames[position][4] = frames[pos6 - 1][4];
         ++position;
      }

      int pos7 = position;
      int s7 = this.frames(10);

      for(int i1 = 0; i1 < s7; ++i1) {
         frames[position][0] = frames[pos7 - 1][0];
         frames[position][1] = frames[pos7 - 1][1] + (double)i1;
         frames[position][2] = frames[pos7 - 1][2] - (double)i1;
         frames[position][3] = frames[pos7 - 1][3];
         frames[position][4] = frames[pos7 - 1][4];
         ++position;
      }

      int pos8 = position;
      int s8 = this.frames(10);

      for(int i1 = 0; i1 < s8; ++i1) {
         frames[position][0] = frames[pos8 - 1][0];
         frames[position][1] = frames[pos8 - 1][1] - (double)i1;
         frames[position][2] = frames[pos8 - 1][2];
         frames[position][3] = frames[pos8 - 1][3] - (double)i1;
         frames[position][4] = frames[pos8 - 1][4] + (double)i1;
         ++position;
      }

      int pos9 = position;
      int s9 = this.frames(15);

      for(int i1 = 0; i1 < s9; ++i1) {
         frames[position][0] = frames[pos9 - 1][0];
         frames[position][1] = frames[pos9 - 1][1] - (double)i1;
         frames[position][2] = frames[pos9 - 1][2] - (double)i1;
         frames[position][3] = frames[pos9 - 1][3] - (double)i1;
         frames[position][4] = frames[pos9 - 1][4];
         ++position;
      }

      int pos10 = position;
      int s10 = this.frames(10);

      for(int i1 = 0; i1 < s10; ++i1) {
         frames[position][0] = frames[pos10 - 1][0];
         frames[position][1] = frames[pos10 - 1][1] - (double)i1;
         frames[position][2] = frames[pos10 - 1][2] + (double)i1;
         frames[position][3] = frames[pos10 - 1][3];
         frames[position][4] = frames[pos10 - 1][4];
         ++position;
      }

      return frames;
   }

   private double[][] section(int length, int position, int savedPos, int incBase, int incWing, int incTip, int incRotX, int incRotY, double[][] frames) {
      int sL = this.frames(length);

      for(int i1 = 0; i1 < sL; ++i1) {
         frames[position][0] = frames[savedPos - 1][0] + (double)incBase;
         frames[position][1] = frames[savedPos - 1][1] + (double)incWing;
         frames[position][2] = frames[savedPos - 1][2] + (double)incTip;
         frames[position][3] = frames[savedPos - 1][3] + (double)incRotX;
         frames[position][4] = frames[savedPos - 1][4] + (double)incRotY;
      }

      return frames;
   }

   private int frames(int amount) {
      if (this.wingFrames - amount >= 0) {
         this.wingFrames -= amount;
         return amount;
      } else {
         return this.wingFrames > 0 ? this.wingFrames : 0;
      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.dragon;
   }
}
