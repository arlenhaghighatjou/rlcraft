package xzeroair.trinkets.races.fairy;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.client.model.Wings;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.fairy.config.FairyConfig;
import xzeroair.trinkets.traits.abilities.AbilityClimbing;
import xzeroair.trinkets.traits.abilities.AbilityFlying;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class RaceFairy extends EntityRacePropertiesHandler {
   public static final FairyConfig serverConfig;
   public static final ClientConfig.ClientConfigItems.ClientConfigFairyRing clientConfig;
   public static List<String> disallowedMounts;
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/fairy_wings.png");
   int tick = 0;
   float armSwing = 0.0F;
   private final ModelBase wings = new Wings();

   public RaceFairy(EntityLivingBase e) {
      super(e, EntityRaces.fairy);
      disallowedMounts = Arrays.asList(serverConfig.mountBlacklist);
   }

   public void startTransformation() {
      if (serverConfig.creative_flight) {
         this.addAbility((new AbilityFlying()).setFlightEnabled(serverConfig.creative_flight).setSpeedEnabled(serverConfig.creative_flight_speed).setFlightSpeed((float)serverConfig.flight_speed).setFlightCost(serverConfig.flight_cost));
      }

      if (serverConfig.climbing) {
         this.addAbility(new AbilityClimbing());
      }

   }

   public void whileTransformed() {
      if (!this.entity.world.isRemote && this.entity.isRiding()) {
         Entity mount = this.entity.getRidingEntity();
         if (mount != null && !this.mountEntity(mount)) {
            this.entity.dismountRidingEntity();
         }
      }

   }

   public boolean canFly() {
      return super.canFly() && this.showTraits();
   }

   public boolean mountEntity(Entity mount) {
      if (this.isCreativePlayer()) {
         return true;
      } else if (!serverConfig.canMount) {
         return false;
      } else if (!disallowedMounts.isEmpty()) {
         try {
            ResourceLocation regName = EntityRegistry.getEntry(mount.getClass()).getRegistryName();
            String modID = regName.getResourceDomain();
            String entityID = regName.getResourcePath();
            boolean exists = disallowedMounts.contains(modID + ":*") || disallowedMounts.contains(regName.toString());
            if (exists) {
               if (!serverConfig.canControlBoats && mount instanceof EntityBoat) {
                  EntityBoat boat = (EntityBoat)mount;
                  Entity controller = boat.getControllingPassenger();
                  if (controller == null || controller == this.entity) {
                     return false;
                  }
               }

               return serverConfig.whitelist;
            }

            if (!serverConfig.canControlBoats && mount instanceof EntityBoat) {
               EntityBoat boat = (EntityBoat)mount;
               Entity controller = boat.getControllingPassenger();
               if (controller == null || controller == this.entity) {
                  return false;
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         return !serverConfig.whitelist;
      } else {
         if (!serverConfig.canControlBoats && mount instanceof EntityBoat) {
            EntityBoat boat = (EntityBoat)mount;
            Entity controller = boat.getControllingPassenger();
            if (controller == null || controller == this.entity) {
               return false;
            }
         }

         return true;
      }
   }

   @SideOnly(Side.CLIENT)
   public void doRenderLayer(RenderLivingBase renderer, boolean isFake, boolean isSlim, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (TrinketsConfig.CLIENT.rendering) {
         if (this.showTraits()) {
            GlStateManager.pushMatrix();
            if (!isFake && this.getTraitVariant() != 2) {
               float flap = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F);
               int angle = 54;
               if (!this.entity.onGround || flap != this.armSwing) {
                  this.tick += 6;
               }

               if (this.tick > 54 || flap == this.armSwing && this.entity.onGround) {
                  this.tick = 0;
               }

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

               double x = 18.0D;
               double y = 0.0D;
               double z = -1.0D;
               int barWidth = 16;
               int barHeight = 16;
               int barCutoffWidth = 36;
               int barCutoffHeight = 42;
               int texWidth = 36;
               int texHeight = 42;
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               GlStateManager.disableLighting();
               GlStateManager.disableCull();
               GlStateManager.enableBlend();
               GlStateManager.pushMatrix();
               GlStateManager.rotate((float)(54 - this.tick), 0.0F, 1.0F, 0.0F);
               GlStateManager.translate(-1.0F, 0.0F, 0.0F);
               float[] rgb = ColorHelper.getRGBColor(this.getTraitVariant() == 1 ? this.getAltTraitColor() : this.getTraitColor());
               DrawingHelper.Draw(TEXTURE, -18.0D, 0.0D, -1.0D, 0.0F, 0.0F, 36, 42, 16.0D, 16.0D, 36.0F, 42.0F, rgb[0], rgb[1], rgb[2], 1.0F);
               GlStateManager.popMatrix();
               GlStateManager.pushMatrix();
               GlStateManager.rotate((float)(-54 + this.tick), 0.0F, 1.0F, 0.0F);
               GlStateManager.translate(-1.0F, 0.0F, 0.0F);
               DrawingHelper.Draw(TEXTURE, -18.0D, 0.0D, 1.0D, 0.0F, 0.0F, 36, 42, 16.0D, 16.0D, 36.0F, 42.0F, rgb[0], rgb[1], rgb[2], 1.0F);
               GlStateManager.popMatrix();
               GlStateManager.enableLighting();
               GlStateManager.enableCull();
               GlStateManager.disableBlend();
               this.armSwing = flap;
            } else {
               GlStateManager.pushMatrix();
               if (renderer instanceof RenderPlayer) {
                  RenderPlayer r = (RenderPlayer)renderer;
                  r.getMainModel().bipedBody.postRender(scale);
               }

               if (this.entity.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
                  GlStateManager.translate(0.0F, -0.1F, 0.06F);
                  GlStateManager.scale(1.1F, 1.1F, 1.1F);
               }

               if (this.entity.isSneaking()) {
                  GlStateManager.translate(0.0F, 0.0F, -0.1F);
               }

               GlStateManager.scale(scale, scale, scale);
               this.wings.render(this.entity, this.entity.limbSwing, this.entity.limbSwingAmount, (float)this.entity.ticksExisted, this.entity.rotationYaw, this.entity.rotationPitch, 1.0F);
               GlStateManager.popMatrix();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
         }

      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.fairy;
      clientConfig = TrinketsConfig.CLIENT.items.FAIRY_RING;
      disallowedMounts = Arrays.asList(serverConfig.mountBlacklist);
   }
}
