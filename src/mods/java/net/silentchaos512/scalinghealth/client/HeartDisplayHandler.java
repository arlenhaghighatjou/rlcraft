package net.silentchaos512.scalinghealth.client;

import java.awt.Color;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.lib.config.ConfigBase;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.config.Config;

public final class HeartDisplayHandler extends Gui {
   public static final HeartDisplayHandler INSTANCE = new HeartDisplayHandler();
   private static final float COLOR_CHANGE_PERIOD = 150.0F;
   private static final ResourceLocation TEXTURE = new ResourceLocation("scalinghealth", "textures/gui/hud.png");
   private long lastSystemTime = 0L;
   private int playerHealth = 0;
   private int lastPlayerHealth = 0;
   private EaglercraftRandom rand = new EaglercraftRandom();

   private HeartDisplayHandler() {
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onHealthBar(Pre event) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayer player = mc.player;
      if (event.getType() == ElementType.TEXT && mc.playerController.gameIsSurvivalOrAdventure()) {
         if (Config.Client.Hearts.textStyle != HeartDisplayHandler.TextStyle.DISABLED) {
            this.renderHealthText(event, player.getHealth(), player.getMaxHealth(), -91 + Config.Client.Hearts.textOffsetX, -38 + Config.Client.Hearts.textOffsetY, Config.Client.Hearts.textStyle, Config.Client.Hearts.textColor);
         }

         if (Config.Client.Hearts.absorbTextStyle != HeartDisplayHandler.TextStyle.DISABLED && player.getAbsorptionAmount() > 0.0F) {
            this.renderHealthText(event, player.getAbsorptionAmount(), 0.0F, -91 + Config.Client.Hearts.absorbTextOffsetX, -49 + Config.Client.Hearts.absorbTextOffsetY, Config.Client.Hearts.absorbTextStyle, Config.Client.Hearts.absorbTextColor);
         }
      }

      if (event.getType() == ElementType.HEALTH && Config.Client.Hearts.customHeartRendering) {
         event.setCanceled(true);
         this.renderHearts(event, mc, player);
      }

   }

   private void renderHearts(RenderGameOverlayEvent event, Minecraft mc, EntityPlayer player) {
      boolean hardcoreMode = mc.world.getWorldInfo().isHardcoreModeEnabled();
      int width = event.getResolution().getScaledWidth();
      int height = event.getResolution().getScaledHeight();
      GlStateManager.enableBlend();
      int health = MathHelper.ceil(player.getHealth());
      boolean highlight = player.hurtResistantTime / 3 % 2 == 1;
      int updateCounter = ClientTicks.ticksInGame;
      if (health < this.playerHealth && player.hurtResistantTime > 0 || health > this.playerHealth && player.hurtResistantTime > 0) {
         this.lastSystemTime = Minecraft.getSystemTime();
      }

      if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
         this.playerHealth = health;
         this.lastPlayerHealth = health;
         this.lastSystemTime = Minecraft.getSystemTime();
      }

      this.playerHealth = health;
      int healthLast = this.lastPlayerHealth;
      IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
      float healthMax = Math.min((float)attrMaxHealth.getAttributeValue(), 20.0F);
      float absorb = (float)MathHelper.ceil(player.getAbsorptionAmount());
      int healthRows = absorb > 0.0F ? 2 : 1;
      int rowHeight = Math.max(10 - (healthRows - 2), 3);
      this.rand.setSeed((long)(updateCounter * 312871));
      int[] lowHealthBob = new int[10];

      for(int i = 0; i < lowHealthBob.length; ++i) {
         lowHealthBob[i] = this.rand.nextInt(2);
      }

      int left = width / 2 - 91;
      int top = height - GuiIngameForge.left_height;
      GuiIngameForge.left_height += healthRows * rowHeight;
      if (rowHeight != 10) {
         GuiIngameForge.left_height += 10 - rowHeight;
      }

      int regen = -1;
      if (player.isPotionActive(MobEffects.REGENERATION)) {
         regen = updateCounter % 25;
      }

      int TOP = 9 * (hardcoreMode ? 5 : 0);
      int BACKGROUND = highlight ? 25 : 16;
      int MARGIN = 16;
      this.drawVanillaHearts(health, highlight, healthLast, healthMax, rowHeight, left, top, regen, lowHealthBob, TOP, BACKGROUND, MARGIN);
      int potionOffset = hardcoreMode ? 27 : 0;
      mc.renderEngine.bindTexture(TEXTURE);
      health = MathHelper.ceil(player.getHealth());
      int rowCount = getCustomHeartRowCount(health);
      int maxHealthRows = getCustomHeartRowCount((int)player.getMaxHealth());

      for(int row = Math.max(0, rowCount - 2); row < rowCount; ++row) {
         int actualRow = row + (Config.Client.Hearts.replaceVanillaRow ? 0 : 1);
         int renderHearts = Math.min((health - 20 * actualRow) / 2, 10);
         int rowColor = this.getColorForRow(row, false);

         int j;
         for(j = 0; j < renderHearts; ++j) {
            int y = top + (j == regen ? -2 : 0);
            if (health <= 4) {
               y += lowHealthBob[MathHelper.clamp(j, 0, lowHealthBob.length - 1)];
            }

            this.drawTexturedModalRect(left + 8 * j, y, 0, potionOffset, 9, 9, rowColor);
         }

         boolean anythingDrawn = j > 0;
         if (health % 2 == 1 && renderHearts < 10) {
            int y = top + (j == regen ? -2 : 0);
            if (health <= 4) {
               y += lowHealthBob[MathHelper.clamp(j, 0, lowHealthBob.length - 1)];
            }

            this.drawTexturedModalRect(left + 8 * renderHearts, y, 9, potionOffset, 9, 9, rowColor);
            anythingDrawn = true;
         }

         if (Config.Client.Hearts.lastHeartOutline && anythingDrawn && row == maxHealthRows - 1) {
            j = (int)Math.ceil((double)(player.getMaxHealth() % 20.0F / 2.0F)) - 1;
            if (j < 0) {
               j += 10;
            }

            int y = top + (j == regen ? -2 : 0);
            if (health <= 4) {
               y += lowHealthBob[MathHelper.clamp(j, 0, lowHealthBob.length - 1)];
            }

            this.drawTexturedModalRect(left + 8 * j, y, 17, 9, 9, 9, Config.Client.Hearts.lastHeartOutlineColor);
         }
      }

      for(int i = 0; i < 10 && (double)i < Math.ceil((double)((float)health / 2.0F)); ++i) {
         int y = top + (i == regen ? -2 : 0);
         if (health <= 4) {
            y += lowHealthBob[MathHelper.clamp(i, 0, lowHealthBob.length - 1)];
         }

         if (this.showEffectHearts(player)) {
            int color = this.effectHeartColor(player);
            this.drawTexturedModalRect(left + 8 * i, y, 0, 54, 9, 9, color);
         }

         if (!hardcoreMode) {
            this.drawTexturedModalRect(left + 8 * i, y, 17, 0, 9, 9, -855638017);
         }
      }

      if (Config.Client.Hearts.absorptionStyle != HeartDisplayHandler.AbsorptionHeartStyle.VANILLA) {
         int absorbCeil = (int)Math.ceil((double)absorb);
         rowCount = (int)Math.ceil((double)(absorb / 20.0F));
         int texX = 17;
         int texY = Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.SHIELD ? 45 : 54;

         for(int i = 0; i < 10 && (float)i < absorb / 2.0F; ++i) {
            int y = top - 10 + (i == regen - 10 ? -2 : 0);
            this.drawTexturedModalRect(left + 8 * i, y, texX, texY, 9, 9, 16777215);
         }

         texX = Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.SHIELD ? 26 : 0;
         texY = Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.SHIELD ? 0 : potionOffset;

         for(int i = Math.max(0, rowCount - 2); i < rowCount; ++i) {
            int renderHearts = Math.min((absorbCeil - 20 * i) / 2, 10);
            int rowColor = this.getColorForRow(i, true);

            int x;
            for(x = 0; x < renderHearts; ++x) {
               int y = top - 10 + (x == regen - 10 ? -2 : 0);
               this.drawTexturedModalRect(left + 8 * x, y, texX, texY, 9, 9, rowColor);
            }

            boolean anythingDrawn = x > 0;
            if (absorbCeil % 2 == 1 && renderHearts < 10) {
               int y = top - 10 + (x == regen - 10 ? -2 : 0);
               this.drawTexturedModalRect(left + 8 * renderHearts, y, texX + 9, texY, 9, 9, rowColor);
               anythingDrawn = true;
            }
         }

         for(int i = 0; i < 10 && (float)i < absorb / 2.0F; ++i) {
            int y = top - 10 + (i == regen - 10 ? -2 : 0);
            if (Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.SHIELD) {
               this.drawTexturedModalRect(left + 8 * i, y, 17, 36, 9, 9, 16777215);
            } else if (Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.GOLD_OUTLINE) {
               this.drawTexturedModalRect(left + 8 * i, y, 17, 27, 9, 9, 16777215);
            }

            if (!hardcoreMode || Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.SHIELD) {
               this.drawTexturedModalRect(left + 8 * i, y, 17, 0, 9, 9, -855638017);
            }
         }
      }

      GlStateManager.disableBlend();
      mc.renderEngine.bindTexture(Gui.ICONS);
   }

   private static int getCustomHeartRowCount(int health) {
      return Config.Client.Hearts.replaceVanillaRow ? MathHelper.ceil((float)health / 20.0F) : health / 20;
   }

   private void drawVanillaHearts(int health, boolean highlight, int healthLast, float healthMax, int rowHeight, int left, int top, int regen, int[] lowHealthBob, int TOP, int BACKGROUND, int MARGIN) {
      float absorb = (float)MathHelper.ceil(Minecraft.getMinecraft().player.getAbsorptionAmount());
      float absorbRemaining = absorb;
      float healthTotal = (float)health + absorb;
      int iStart = MathHelper.ceil((healthMax + (Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.VANILLA ? absorb : 0.0F)) / 2.0F) - 1;

      for(int i = iStart; i >= 0; --i) {
         int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
         int x = left + i % 10 * 8;
         int y = top - row * rowHeight;
         if (health <= 4) {
            y += lowHealthBob[MathHelper.clamp(i, 0, lowHealthBob.length - 1)];
         }

         if (i == regen) {
            y -= 2;
         }

         this.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
         if (highlight) {
            if (i * 2 + 1 < healthLast) {
               this.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9);
            } else if (i * 2 + 1 == healthLast) {
               this.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9);
            }
         }

         if (absorbRemaining > 0.0F && Config.Client.Hearts.absorptionStyle == HeartDisplayHandler.AbsorptionHeartStyle.VANILLA) {
            if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
               this.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
               --absorbRemaining;
            } else {
               if ((float)(i * 2 + 1) < healthTotal) {
                  this.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9);
               }

               absorbRemaining -= 2.0F;
            }
         } else if (i * 2 + 1 < health) {
            this.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9);
         } else if (i * 2 + 1 == health) {
            this.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9);
         }
      }

   }

   private void renderHealthText(RenderGameOverlayEvent event, float current, float max, int offsetX, int offsetY, HeartDisplayHandler.TextStyle style, HeartDisplayHandler.TextColor styleColor) {
      float scale = style.scale;
      int width = event.getResolution().getScaledWidth();
      int height = event.getResolution().getScaledHeight();
      int left = (int)((float)(width / 2 + offsetX) / scale);
      int top = (int)(((float)(height + offsetY) + 1.0F / scale) / scale);
      String healthString = style.textFor(current, max);
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      int stringWidth = fontRenderer.getStringWidth(healthString);
      float divisor = max == 0.0F ? current : max;
      int color;
      switch(styleColor) {
      case GREEN_TO_RED:
         color = Color.HSBtoRGB(0.34F * current / divisor, 0.7F, 1.0F);
         break;
      case PSYCHEDELIC:
         color = Color.HSBtoRGB((float)ClientTicks.ticksInGame % 150.0F / 150.0F, 0.55F * current / divisor, 1.0F);
         break;
      case SOLID:
         color = Config.Client.Hearts.textSolidColor;
         break;
      case WHITE:
      default:
         color = 14540253;
      }

      GlStateManager.pushMatrix();
      GlStateManager.scale(scale, scale, 1.0F);
      fontRenderer.drawStringWithShadow(healthString, (float)(left - stringWidth - 2), (float)top, color);
      GlStateManager.popMatrix();
   }

   private void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int color) {
      float a = (float)(color >> 24 & 255) / 255.0F;
      if (a <= 0.0F) {
         a = 1.0F;
      }

      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      GlStateManager.color(r, g, b, a);
      this.drawTexturedModalRect(x, y, textureX, textureY, width, height);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private int getColorForRow(int row, boolean absorption) {
      int[] colors = absorption ? Config.Client.Hearts.absorptionHeartColors : Config.Client.Hearts.heartColors;
      int index = Config.Client.Hearts.heartColorLooping ? row % colors.length : MathHelper.clamp(row, 0, colors.length - 1);
      return colors[index];
   }

   private boolean showEffectHearts(EntityPlayer player) {
      return player.isPotionActive(MobEffects.POISON) || player.isPotionActive(MobEffects.WITHER);
   }

   private int effectHeartColor(EntityPlayer player) {
      if (player.isPotionActive(MobEffects.WITHER)) {
         return 6700615;
      } else {
         return player.isPotionActive(MobEffects.POISON) ? 5149489 : 16777215;
      }
   }

   public static enum AbsorptionHeartStyle {
      SHIELD,
      GOLD_OUTLINE,
      VANILLA;

      public static final String COMMENT = "Determines how absorption hearts should be rendered.";

      private AbsorptionHeartStyle() {
      }

      public static HeartDisplayHandler.AbsorptionHeartStyle loadDromConfig(ConfigBase config) {
         return (HeartDisplayHandler.AbsorptionHeartStyle)config.loadEnum("Absorption Heart Style", "main.client", HeartDisplayHandler.AbsorptionHeartStyle.class, SHIELD, "Determines how absorption hearts should be rendered.");
      }
   }

   public static enum TextColor {
      GREEN_TO_RED,
      WHITE,
      PSYCHEDELIC,
      SOLID;

      public static final String COMMENT = "Determines the color of the text next to your hearts. GREEN_TO_RED displays green at full health, and moves to red as you lose health (does not work with absorption). WHITE will just be good old fashioned white text. Set to PSYCHEDELIC if you want to taste the rainbow. SOLID is a fixed color";

      private TextColor() {
      }

      public static HeartDisplayHandler.TextColor loadFromConfig(ConfigBase config, String name, HeartDisplayHandler.TextColor defaultValue) {
         return (HeartDisplayHandler.TextColor)config.loadEnum(name, "main.client", HeartDisplayHandler.TextColor.class, defaultValue, "Determines the color of the text next to your hearts. GREEN_TO_RED displays green at full health, and moves to red as you lose health (does not work with absorption). WHITE will just be good old fashioned white text. Set to PSYCHEDELIC if you want to taste the rainbow. SOLID is a fixed color");
      }
   }

   public static enum TextStyle {
      DISABLED(1.0F) {
         public String textFor(float current, float max) {
            return "";
         }
      },
      ROWS(0.65F) {
         public String textFor(float current, float max) {
            return (int)Math.ceil((double)(current / 20.0F)) + "x";
         }
      },
      HEALTH_AND_MAX(0.5F) {
         public String textFor(float current, float max) {
            return max == 0.0F ? HEALTH_ONLY.textFor(current, max) : Math.round(current) + "/" + Math.round(max);
         }
      },
      HEALTH_ONLY(0.5F) {
         public String textFor(float current, float max) {
            return String.valueOf(Math.round(current));
         }
      };

      private static final String COMMENT = "Determines what the text next to your hearts will display. DISABLED will display nothing, ROWS will display the number of remaining rows that have health left, and HEALTH_AND_MAX will display your actual health and max health values (for absorption, there is no max value). HEALTH_ONLY displays just the current amount.";
      private final float scale;

      private TextStyle(float scale) {
         this.scale = scale;
      }

      public static HeartDisplayHandler.TextStyle loadFromConfig(ConfigBase config, String name, HeartDisplayHandler.TextStyle defaultValue) {
         return (HeartDisplayHandler.TextStyle)config.loadEnum(name, "main.client", HeartDisplayHandler.TextStyle.class, defaultValue, "Determines what the text next to your hearts will display. DISABLED will display nothing, ROWS will display the number of remaining rows that have health left, and HEALTH_AND_MAX will display your actual health and max health values (for absorption, there is no max value). HEALTH_ONLY displays just the current amount.");
      }

      public abstract String textFor(float var1, float var2);
   }
}
