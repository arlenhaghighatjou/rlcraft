package com.charles445.simpledifficulty.client.gui;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.RenderUtil;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TemperatureGui {
   private final Minecraft mc = Minecraft.getMinecraft();
   private int updateCounter = 0;
   private final EaglercraftRandom rand = new EaglercraftRandom();
   public static final ResourceLocation ICONS = new ResourceLocation("simpledifficulty:textures/gui/icons.png");
   public static final ResourceLocation TEMPHUD = new ResourceLocation("simpledifficulty:textures/gui/temphud.png");
   private static final int classicTexturePos_X = 0;
   private static final int classicTexturePos_Y = 32;
   private static final int classicTextureWidth = 16;
   private static final int classicTextureHeight = 16;
   private static final int modernTexturePos_X = 0;
   private static final int modernTexturePos_Y = 0;
   private static final int modernTextureWidth = 16;
   private static final int modernTextureHeight = 16;
   private static final int modernFeelPos_X = 0;
   private static final int modernFeelPos_Y = 16;
   private static final int modernFeelWidth = 32;
   private static final int modernFeelHeight = 32;
   private static final int modernArrowPos_X = 0;
   private static final int modernArrowPos_Y = 144;
   private static final int modernArrowFrames = 14;
   private int oldTemperature = -1;
   private int frameCounterClassic = -1;
   private int frameCounterModern = -1;
   private boolean risingTemperature = false;
   private boolean startAnimation = false;
   private boolean shakeSide = false;
   private static final int texturepos_Y_alt_OVR = 80;
   private static final int texturepos_Y_alt_BG = 96;
   private int alternateTemperature = 0;
   private int worldThermometerTemperature = 0;
   private boolean hasThermometer = false;
   private static final int texturepos_X_therm = 0;
   private static final int texturepos_Y_therm = 192;
   private static final int thermometer_per_row = 8;
   private static final int textureWidthTherm = 16;
   private static final int textureHeightTherm = 16;

   public TemperatureGui() {
   }

   public void register(EventBus bus) {
      bus.register(Pre.class, this::onPreRenderGameOverlay);
      bus.register(ClientTickEvent.class, this::onClientTick);
   }

   public void onPreRenderGameOverlay(Pre event) {
      if (event.getType() == ElementType.ALL && QuickConfig.isTemperatureEnabled() && this.mc.playerController.gameIsSurvivalOrAdventure()) {
         this.rand.setSeed((long)(this.updateCounter * 445));
         boolean classic = ModConfig.client.classicHUDTemperature;
         if (classic) {
            this.bind(ICONS);
         } else {
            this.bind(TEMPHUD);
         }

         EntityPlayerSP player = Minecraft.getMinecraft().player;
         ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
         ScaledResolution resolution = event.getResolution();
         GlStateManager.enableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         if (classic) {
            this.renderClassicTemperatureIcon(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getTemperatureLevel());
         } else {
            this.renderTemperatureIcon(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getTemperatureLevel());
         }

         GlStateManager.disableBlend();
         this.bind(Gui.ICONS);
      }

   }

   private int getTempHudFeelBGX(int temperature) {
      int bgx = 0;
      if (temperature >= 6 && temperature < 20) {
         bgx = (temperature / 2 - 2) * 32;
      } else {
         bgx = 0;
      }

      return bgx;
   }

   private int getTempHudFeelBGY(int temperature) {
      return temperature >= 20 ? 32 : 0;
   }

   private int getTempHudCoreBGX(int temperature) {
      int bgx = 0;
      boolean animated = false;
      if (temperature < 6) {
         bgx = 0;
         animated = true;
      } else if (temperature >= 20) {
         bgx = 192;
         animated = true;
      } else {
         bgx = (temperature / 2 + 2) * 16;
      }

      if (animated) {
         if (temperature < 12) {
            bgx += this.updateCounter % 5 * 16;
         } else {
            bgx += this.updateCounter % 4 * 16;
         }
      }

      return bgx;
   }

   private void renderTemperatureIcon(int width, int height, int temperature) {
      TemperatureEnum tempEnum = TemperatureUtil.getTemperatureEnum(temperature);
      int bgXOffset = this.getTempHudCoreBGX(temperature);
      int x = width / 2 - 8;
      int y = height - 54;
      int xOffset = 0;
      int yOffset = 0;
      int shakeFrequency = this.getShakeFrequency(tempEnum, temperature);
      if (shakeFrequency > 0) {
         if (this.updateCounter % shakeFrequency == 0) {
            int shakeCheck = this.updateCounter / shakeFrequency;
            if (shakeCheck % 2 == 0) {
               this.shakeSide = true;
            } else {
               this.shakeSide = false;
            }
         }

         xOffset = this.shakeSide ? 1 : -1;
      }

      if (ModConfig.client.alternateTemp) {
         RenderUtil.drawTexturedModalRect((float)(x - 8 + xOffset), (float)(y - 8 + yOffset), 0 + this.getTempHudFeelBGX(this.alternateTemperature), 16 + this.getTempHudFeelBGY(this.alternateTemperature), 32, 32);
         RenderUtil.drawTexturedModalRect((float)(x + xOffset), (float)(y + yOffset), 0 + bgXOffset, 0, 16, 16);
      } else {
         RenderUtil.drawTexturedModalRect((float)(x + xOffset), (float)(y + yOffset), 0 + bgXOffset, 0, 16, 16);
      }

      this.renderTemperatureChangeAnimation(false, x, y, temperature);
      this.renderThermometer(x, y);
   }

   private void renderClassicTemperatureIcon(int width, int height, int temperature) {
      TemperatureEnum tempEnum = TemperatureUtil.getTemperatureEnum(temperature);
      int bgXOffset = 16 * tempEnum.ordinal();
      int x = width / 2 - 8;
      int y = height - 54;
      int xOffset = 0;
      int yOffset = 0;
      int shakeFrequency = this.getShakeFrequency(tempEnum, temperature);
      if (shakeFrequency > 0) {
         if (this.updateCounter % shakeFrequency == 0) {
            int shakeCheck = this.updateCounter / shakeFrequency;
            if (shakeCheck % 2 == 0) {
               this.shakeSide = true;
            } else {
               this.shakeSide = false;
            }
         }

         xOffset = this.shakeSide ? 1 : -1;
      }

      if (ModConfig.client.alternateTemp) {
         int outsideOffset = 16 * TemperatureUtil.getTemperatureEnum(this.alternateTemperature).ordinal();
         RenderUtil.drawTexturedModalRect((float)(x + xOffset), (float)(y + yOffset), 0 + outsideOffset, 96, 16, 16);
         RenderUtil.drawTexturedModalRect((float)(x + xOffset), (float)(y + yOffset), 0 + bgXOffset, 80, 16, 16);
      } else {
         RenderUtil.drawTexturedModalRect((float)(x + xOffset), (float)(y + yOffset), 0 + bgXOffset, 32, 16, 16);
      }

      this.renderTemperatureChangeAnimation(true, x, y, temperature);
      this.renderThermometer(x, y);
   }

   private void renderTemperatureChangeAnimation(boolean classic, int x, int y, int temperature) {
      if (this.oldTemperature == -1) {
         this.oldTemperature = temperature;
      }

      if (this.oldTemperature != temperature) {
         this.risingTemperature = this.oldTemperature < temperature;
         this.oldTemperature = temperature;
         this.startAnimation = true;
      }

      if (classic) {
         if (this.frameCounterClassic >= 0) {
            int ovrXOffset = 16 * this.frameCounterClassic;
            int ovrYOffset = (this.risingTemperature ? 1 : 2) * 16;
            RenderUtil.drawTexturedModalRect((float)x, (float)y, 0 + ovrXOffset, 32 + ovrYOffset, 16, 16);
         }
      } else if (this.frameCounterModern >= 0) {
         int ovrXOffset = (14 - this.frameCounterModern) * 16;
         int ovrYOffset = (this.risingTemperature ? 1 : 0) * 16;
         RenderUtil.drawTexturedModalRect((float)x, (float)y, 0 + ovrXOffset, 144 + ovrYOffset, 16, 16);
      }

   }

   private void renderThermometer(int x, int y) {
      if (this.hasThermometer && ModConfig.client.thermometer.hudThermometer && ModConfig.client.thermometer.enableThermometer) {
         int therm_position = this.worldThermometerTemperature - TemperatureEnum.FREEZING.getLowerBound();
         int therm_x = therm_position % 8 * 16 + 0;
         int therm_y = therm_position / 8 * 16 + 192;
         int therm_xOffset = ModConfig.client.thermometer.hudThermometerX;
         int therm_yOffset = ModConfig.client.thermometer.hudThermometerY;
         RenderUtil.drawTexturedModalRect((float)(x + therm_xOffset), (float)(y - 18 + therm_yOffset), therm_x, therm_y, 16, 16);
      }

   }

   private int getShakeFrequency(TemperatureEnum tempEnum, int temperature) {
      int shakeFrequency = 0;
      if (tempEnum == TemperatureEnum.FREEZING) {
         if (temperature == TemperatureEnum.FREEZING.getUpperBound()) {
            shakeFrequency = 0;
         } else if (temperature > TemperatureEnum.FREEZING.getMiddle() + 1) {
            shakeFrequency = 2;
         } else {
            shakeFrequency = 1;
         }
      } else if (tempEnum == TemperatureEnum.BURNING) {
         if (temperature == TemperatureEnum.BURNING.getLowerBound()) {
            shakeFrequency = 0;
         } else if (temperature >= TemperatureEnum.BURNING.getMiddle()) {
            shakeFrequency = 1;
         } else {
            shakeFrequency = 2;
         }
      }

      return shakeFrequency;
   }

   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && !this.mc.isGamePaused()) {
         ++this.updateCounter;
         if (this.frameCounterClassic >= 0) {
            --this.frameCounterClassic;
         }

         if (this.frameCounterModern >= 0) {
            --this.frameCounterModern;
         }

         if (this.startAnimation) {
            this.frameCounterClassic = 11;
            this.frameCounterModern = 14;
            this.startAnimation = false;
         }

         if (this.updateCounter % 15 == 12 && QuickConfig.isTemperatureEnabled() && this.mc.player != null) {
            EntityPlayer player = this.mc.player;
            World world = player.getEntityWorld();
            if (ModConfig.client.alternateTemp) {
               this.alternateTemperature = TemperatureUtil.clampTemperature(TemperatureUtil.getPlayerTargetTemperature(player));
            }

            if (ModConfig.client.thermometer.hudThermometer && ModConfig.client.thermometer.enableThermometer) {
               this.worldThermometerTemperature = TemperatureUtil.clampTemperature(WorldUtil.calculateClientWorldEntityTemperature(world, player));
               this.hasThermometer = player.inventory.hasItemStack(new ItemStack(SDItems.thermometer));
            }
         }
      }

   }

   private void bind(ResourceLocation resource) {
      this.mc.getTextureManager().bindTexture(resource);
   }
}
