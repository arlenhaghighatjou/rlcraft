package xzeroair.trinkets.races.faelis;

import java.util.Iterator;
import java.util.TreeMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.attributes.JumpAttribute;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.statushandler.StatusHandler;
import xzeroair.trinkets.client.model.FaelisEars;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.faelis.config.FaelisConfig;
import xzeroair.trinkets.traits.abilities.AbilityClimbing;
import xzeroair.trinkets.traits.statuseffects.StatusEffectsEnum;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.helpers.AttributeHelper;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class RaceFaelis extends EntityRacePropertiesHandler {
   public static FaelisConfig serverConfig;
   protected UpdatingAttribute movement = (new UpdatingAttribute(EaglercraftUUID.fromString("1c9ba72a-a558-4ccc-a997-777bf3a9859a"), SharedMonsterAttributes.MOVEMENT_SPEED)).setSavedInNBT(false);
   protected UpdatingAttribute jump = (new UpdatingAttribute(EaglercraftUUID.fromString("1c9ba72a-a558-4ccc-a997-777bf3a9859a"), JumpAttribute.Jump)).setSavedInNBT(false);
   private FaelisEars ears = new FaelisEars();
   public static final ResourceLocation TEXTURE = new ResourceLocation("xat:textures/ears.png");
   public static final ResourceLocation TEXTURE_INNER = new ResourceLocation("xat:textures/inner_ears.png");
   public static final ResourceLocation TEXTURE_OUTER = new ResourceLocation("xat:textures/outer_ears.png");

   public RaceFaelis(@Nonnull EntityLivingBase e) {
      super(e, EntityRaces.faelis);
   }

   public void startTransformation() {
      this.addAbility(new AbilityClimbing());
   }

   public void whileTransformed() {
      if (!this.entity.world.isRemote) {
         boolean hasMilkBuff = false;
         if (TrinketsConfig.SERVER.races.faelis.penalties) {
            double amount = 0.0D;
            StatusHandler status = Capabilities.getStatusHandler(this.entity);
            if (status != null) {
               if (status.getActiveEffects().containsKey(StatusEffectsEnum.Invigorated.getName())) {
                  hasMilkBuff = true;
               } else {
                  hasMilkBuff = false;
               }
            }

            try {
               label64:
               for(ItemStack stack : this.entity.getArmorInventoryList()) {
                  if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor && TrinketsConfig.SERVER.races.faelis.penalties) {
                     TreeMap<String, ConfigHelper.ArmorEntry> ArmorWeightValues = ConfigHelper.TrinketConfigStorage.ArmorWeightValues;
                     Iterator var8 = ArmorWeightValues.values().iterator();

                     ConfigHelper.ArmorEntry entry;
                     while(true) {
                        if (!var8.hasNext()) {
                           continue label64;
                        }

                        entry = (ConfigHelper.ArmorEntry)var8.next();
                        if (entry.doesItemMatchEntry(stack) && entry.getWeight() != 0.0D) {
                           break;
                        }
                     }

                     amount -= entry.getWeight();
                  }
               }
            } catch (Exception var10) {
               ;
            }

            if (amount != 0.0D && !hasMilkBuff) {
               this.movement.addModifier(this.entity, amount, 2);
               this.jump.addModifier(this.entity, amount, 2);
            } else {
               this.movement.removeModifier(this.entity);
               this.jump.removeModifier(this.entity);
            }
         } else {
            this.movement.removeModifier(this.entity);
            this.jump.removeModifier(this.entity);
         }

      }
   }

   public void endTransformation() {
      AttributeHelper.removeAttributes(this.entity, EaglercraftUUID.fromString("1c9ba72a-a558-4ccc-a997-777bf3a9859a"));
   }

   public float hurtEntity(EntityLivingBase target, DamageSource source, float dmg) {
      if (!(source instanceof EntityDamageSourceIndirect) && !source.isExplosion() && !source.isMagicDamage() && !source.isProjectile() && dmg > 0.0F) {
         ItemStack stack1 = this.entity.getHeldItemMainhand();
         ItemStack stack2 = this.entity.getHeldItemOffhand();
         if (stack1.isEmpty() && stack2.isEmpty()) {
            dmg = (float)((double)dmg + serverConfig.bonus);
         }
      }

      return dmg;
   }

   @SideOnly(Side.CLIENT)
   public void doRenderLayer(RenderLivingBase renderer, boolean isFake, boolean isSlim, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (TrinketsConfig.CLIENT.rendering && this.showTraits()) {
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
         double y = -2.5D;
         double z = -0.72D;
         double height = 1.0D;
         double width = 1.0D;
         float u = 32.0F;
         float v = 0.0F;
         int uWidth = 16;
         int vHeight = 16;
         float tileWidth = 64.0F;
         float tileHeight = 32.0F;
         double xR = -0.3D;
         double xL = 0.3D;
         float rot = 26.0F;
         int solidVariant = 2;
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.rotate(-rot, 0.0F, 1.0F, 0.0F);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, -0.3D, -2.5D, -0.72D, 32.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE, -0.3D, -2.5D, -0.7199D, 32.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_INNER, -0.3D, -2.5D, -0.72D, 32.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, -0.3D, -2.5D, -0.72D, 32.0F, 0.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, -0.3D, -2.5D, -0.7199D, 32.0F, 16.0F, 16, 16, 1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.rotate(rot * 2.0F, 0.0F, 1.0F, 0.0F);
         if (this.getTraitVariant() == 2) {
            DrawingHelper.Draw(TEXTURE, 0.3D, -2.5D, -0.72D, 32.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE, 0.3D, -2.5D, -0.7199D, 32.0F, 16.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
         } else {
            DrawingHelper.Draw(TEXTURE_INNER, 0.3D, -2.5D, -0.72D, 32.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb[0], rgb[1], rgb[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, 0.3D, -2.5D, -0.72D, 32.0F, 0.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
            DrawingHelper.Draw(TEXTURE_OUTER, 0.3D, -2.5D, -0.7199D, 32.0F, 16.0F, 16, 16, -1.0D, 1.0D, 64.0F, 32.0F, rgb2[0], rgb2[1], rgb2[2], 1.0F);
         }

         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.faelis;
   }
}
