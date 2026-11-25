package ichttt.mods.firstaid.client;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.client.gui.FlashStateManager;
import ichttt.mods.firstaid.client.gui.GuiHealthScreen;
import ichttt.mods.firstaid.client.util.HealthRenderUtils;
import ichttt.mods.firstaid.client.util.PlayerModelRenderer;
import ichttt.mods.firstaid.common.util.CommonUtils;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HUDHandler implements ISelectiveResourceReloadListener {
   public static final HUDHandler INSTANCE = new HUDHandler();
   private static final int FADE_TIME = 30;
   private final Map<EnumPlayerPart, String> TRANSLATION_MAP = new EnumMap(EnumPlayerPart.class);
   private final FlashStateManager flashStateManager = new FlashStateManager();
   private int maxLength;
   public int ticker = -1;

   public HUDHandler() {
   }

   public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
      if (resourcePredicate.test(VanillaResourceType.LANGUAGES)) {
         this.buildTranslationTable();
      }
   }

   private synchronized void buildTranslationTable() {
      FirstAid.LOGGER.debug("Building GUI translation table");
      this.TRANSLATION_MAP.clear();
      this.maxLength = 0;

      for(EnumPlayerPart part : EnumPlayerPart.VALUES) {
         String translated = I18n.format("gui." + part.toString().toLowerCase(Locale.ENGLISH), new Object[0]);
         this.maxLength = Math.max(this.maxLength, Minecraft.getMinecraft().fontRenderer.getStringWidth(translated));
         this.TRANSLATION_MAP.put(part, translated);
      }

   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void renderOverlay(ScaledResolution scaledResolution, float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.mcProfiler.startSection("prepare");
      if (mc.player != null) {
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)Objects.requireNonNull(mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null));
         if (FirstAid.isSynced) {
            if (this.TRANSLATION_MAP.isEmpty()) {
               this.buildTranslationTable();
            }

            int visibleTicks = FirstAidConfig.overlay.displayMode.visibleDurationTicks;
            if (visibleTicks != -1) {
               visibleTicks += 30;
            }

            boolean playerDead = damageModel.isDead(mc.player);

            for(AbstractDamageablePart damageablePart : damageModel) {
               if (HealthRenderUtils.healthChanged(damageablePart, playerDead)) {
                  if (visibleTicks != -1) {
                     this.ticker = Math.max(this.ticker, visibleTicks);
                  }

                  if (FirstAidConfig.overlay.displayMode.flash) {
                     this.flashStateManager.setActive(Minecraft.getSystemTime());
                  }
               }
            }

            if (FirstAidConfig.overlay.overlayMode != FirstAidConfig.Overlay.OverlayMode.OFF && (!GuiHealthScreen.isOpen || FirstAidConfig.overlay.overlayMode.isPlayerModel()) && CommonUtils.isSurvivalOrAdventure(mc.player)) {
               if (visibleTicks == -1 || this.ticker >= 0) {
                  mc.getTextureManager().bindTexture(Gui.ICONS);
                  Gui gui = mc.ingameGUI;
                  int xOffset = FirstAidConfig.overlay.xOffset;
                  int yOffset = FirstAidConfig.overlay.yOffset;
                  boolean playerModel = FirstAidConfig.overlay.overlayMode.isPlayerModel();
                  switch(FirstAidConfig.overlay.pos) {
                  case TOP_LEFT:
                     if (playerModel) {
                        ++xOffset;
                     }
                     break;
                  case TOP_RIGHT:
                     xOffset = scaledResolution.getScaledWidth() - xOffset - (playerModel ? 34 : damageModel.getMaxRenderSize() + this.maxLength);
                     break;
                  case BOTTOM_LEFT:
                     if (playerModel) {
                        ++xOffset;
                     }

                     yOffset = scaledResolution.getScaledHeight() - yOffset - (playerModel ? 66 : 80);
                     break;
                  case BOTTOM_RIGHT:
                     xOffset = scaledResolution.getScaledWidth() - xOffset - (playerModel ? 34 : damageModel.getMaxRenderSize() + this.maxLength);
                     yOffset = scaledResolution.getScaledHeight() - yOffset - (playerModel ? 62 : 80);
                     break;
                  default:
                     throw new RuntimeException("Invalid config option for position: " + FirstAidConfig.overlay.pos);
                  }

                  if (!(mc.currentScreen instanceof GuiChat) || FirstAidConfig.overlay.pos != FirstAidConfig.Overlay.Position.BOTTOM_LEFT) {
                     if (!mc.gameSettings.showDebugInfo || FirstAidConfig.overlay.pos != FirstAidConfig.Overlay.Position.TOP_LEFT) {
                        boolean enableAlphaBlend = visibleTicks != -1 && this.ticker < 30;
                        int alpha = enableAlphaBlend ? MathHelper.clamp((int)((float)(30 - this.ticker) * 255.0F / 30.0F), FirstAidConfig.overlay.alpha, 250) : FirstAidConfig.overlay.alpha;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)xOffset, (float)yOffset, 0.0F);
                        if (enableAlphaBlend) {
                           GlStateManager.enableBlend();
                           GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
                        }

                        mc.mcProfiler.endStartSection("render");
                        if (FirstAidConfig.overlay.overlayMode.isPlayerModel()) {
                           boolean fourColors = FirstAidConfig.overlay.overlayMode == FirstAidConfig.Overlay.OverlayMode.PLAYER_MODEL_4_COLORS || FirstAidConfig.overlay.overlayMode == FirstAidConfig.Overlay.OverlayMode.PLAYER_MODEL_OLD_4_COLORS;
                           boolean oldModel = FirstAidConfig.overlay.overlayMode == FirstAidConfig.Overlay.OverlayMode.PLAYER_MODEL_OLD_4_COLORS || FirstAidConfig.overlay.overlayMode == FirstAidConfig.Overlay.OverlayMode.PLAYER_MODEL_OLD_COLORS;
                           PlayerModelRenderer.renderPlayerHealth(damageModel, fourColors, oldModel, gui, this.flashStateManager.update(Minecraft.getSystemTime()), (float)alpha, partialTicks);
                        } else {
                           int xTranslation = this.maxLength;

                           for(AbstractDamageablePart part : damageModel) {
                              mc.fontRenderer.drawStringWithShadow((String)this.TRANSLATION_MAP.get(part.part), 0.0F, 0.0F, 16777215 - (alpha << 24 & -16777215));
                              if (FirstAidConfig.overlay.overlayMode == FirstAidConfig.Overlay.OverlayMode.NUMBERS) {
                                 HealthRenderUtils.drawHealthString(part, (float)xTranslation, 0.0F, false);
                              } else {
                                 HealthRenderUtils.drawHealth(part, (float)xTranslation, 0.0F, gui, false);
                              }

                              GlStateManager.translate(0.0F, 10.0F, 0.0F);
                           }
                        }

                        if (enableAlphaBlend) {
                           GlStateManager.disableBlend();
                        }

                        GlStateManager.popMatrix();
                     }
                  }
               }
            }
         }
      }
   }
}
