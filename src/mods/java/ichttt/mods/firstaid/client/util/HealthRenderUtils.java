package ichttt.mods.firstaid.client.util;

import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.client.gui.FlashStateManager;
import ichttt.mods.firstaid.common.EventHandler;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HealthRenderUtils {
   public static final ResourceLocation GUI_LOCATION = new ResourceLocation("firstaid", "textures/gui/show_wounds.png");
   public static final DecimalFormat TEXT_FORMAT = new DecimalFormat("0.0");
   private static final Object2IntOpenHashMap<EnumPlayerPart> prevHealth = new Object2IntOpenHashMap();
   private static final EnumMap<EnumPlayerPart, FlashStateManager> flashStates = new EnumMap(EnumPlayerPart.class);

   public HealthRenderUtils() {
   }

   public static void drawHealthString(AbstractDamageablePart damageablePart, float xTranslation, float yTranslation, boolean allowSecondLine) {
      float absorption = damageablePart.getAbsorption();
      String text = TEXT_FORMAT.format((double)damageablePart.currentHealth) + "/" + damageablePart.getMaxHealth();
      if (absorption > 0.0F) {
         String line2 = "+ " + TEXT_FORMAT.format((double)absorption);
         if (allowSecondLine) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(line2, xTranslation, yTranslation + 5.0F, 16777215);
            yTranslation -= 5.0F;
         } else {
            text = text + " " + line2;
         }
      }

      Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, xTranslation, yTranslation, 16777215);
   }

   private static void updatePrev(EnumPlayerPart part, int current, boolean playerDead) {
      if (!playerDead) {
         prevHealth.put(part, current);
      } else {
         prevHealth.clear();
      }

   }

   public static boolean healthChanged(AbstractDamageablePart damageablePart, boolean playerDead) {
      int current = (int)Math.ceil((double)damageablePart.currentHealth);
      FlashStateManager activeFlashState = (FlashStateManager)Objects.requireNonNull(flashStates.get(damageablePart.part));
      if (prevHealth.containsKey(damageablePart.part)) {
         int prev = prevHealth.getInt(damageablePart.part);
         updatePrev(damageablePart.part, current, playerDead);
         if (prev != current) {
            activeFlashState.setActive(Minecraft.getSystemTime());
            return true;
         } else {
            return false;
         }
      } else {
         activeFlashState.setActive(Minecraft.getSystemTime());
         updatePrev(damageablePart.part, current, playerDead);
         return true;
      }
   }

   public static boolean drawAsString(AbstractDamageablePart damageablePart, boolean allowSecondLine) {
      int maxHealth = getMaxHearts((float)damageablePart.getMaxHealth());
      int maxExtraHealth = getMaxHearts(damageablePart.getAbsorption());
      return maxHealth + maxExtraHealth > 8 && allowSecondLine || maxHealth + maxExtraHealth > 12;
   }

   public static void drawHealth(AbstractDamageablePart damageablePart, float xTranslation, float yTranslation, Gui gui, boolean allowSecondLine) {
      int maxHealth = getMaxHearts((float)damageablePart.getMaxHealth());
      int maxExtraHealth = getMaxHearts(damageablePart.getAbsorption());
      int current = (int)Math.ceil((double)damageablePart.currentHealth);
      FlashStateManager activeFlashState = (FlashStateManager)Objects.requireNonNull(flashStates.get(damageablePart.part));
      if (drawAsString(damageablePart, allowSecondLine)) {
         drawHealthString(damageablePart, xTranslation, yTranslation, allowSecondLine);
      } else {
         int yTexture = damageablePart.canCauseDeath ? 45 : 0;
         int absorption = (int)Math.ceil((double)damageablePart.getAbsorption());
         boolean highlight = activeFlashState.update(Minecraft.getSystemTime());
         Minecraft mc = Minecraft.getMinecraft();
         int regen = -1;
         if (FirstAidConfig.externalHealing.allowOtherHealingItems && mc.player.isPotionActive(MobEffects.REGENERATION)) {
            regen = mc.ingameGUI.updateCounter / 2 % 15;
         }

         boolean low = (float)(current + absorption) < 1.25F;
         mc.getTextureManager().bindTexture(Gui.ICONS);
         GlStateManager.pushMatrix();
         GlStateManager.translate(xTranslation, yTranslation, 0.0F);
         boolean drawSecondLine = allowSecondLine;
         if (allowSecondLine) {
            drawSecondLine = maxHealth + maxExtraHealth > 4;
         }

         if (drawSecondLine) {
            int maxHealth2 = 0;
            if (maxHealth > 4) {
               maxHealth2 = maxHealth - 4;
               maxHealth = 4;
            }

            int maxExtraHealth2 = Math.max(0, maxExtraHealth - (4 - maxHealth));
            maxExtraHealth -= maxExtraHealth2;
            int current2 = 0;
            if (current > 8) {
               current2 = current - 8;
               current = 8;
            }

            int absorption2 = absorption - maxExtraHealth * 2;
            absorption -= absorption2;
            GlStateManager.translate(0.0F, 5.0F, 0.0F);
            GlStateManager.pushMatrix();
            renderLine(regen, low, yTexture, maxHealth2, maxExtraHealth2, current2, absorption2, gui, highlight);
            regen -= maxHealth2 + maxExtraHealth;
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, -10.0F, 0.0F);
         }

         renderLine(regen, low, yTexture, maxHealth, maxExtraHealth, current, absorption, gui, highlight);
         GlStateManager.popMatrix();
      }
   }

   private static void renderLine(int regen, boolean low, int yTexture, int maxHealth, int maxExtraHearts, int current, int absorption, Gui gui, boolean highlight) {
      GlStateManager.pushMatrix();
      Int2IntMap map = new Int2IntArrayMap();
      if (low) {
         for(int i = 0; i < maxHealth + maxExtraHearts; ++i) {
            map.put(i, EventHandler.rand.nextInt(2));
         }
      }

      renderMax(regen, map, maxHealth, yTexture, gui, highlight);
      if (maxExtraHearts > 0) {
         if (maxHealth != 0) {
            GlStateManager.translate((float)(2 + 9 * maxHealth), 0.0F, 0.0F);
         }

         renderMax(regen - maxHealth, map, maxExtraHearts, yTexture, gui, false);
      }

      GlStateManager.popMatrix();
      GlStateManager.translate(0.0F, 0.0F, 1.0F);
      renderCurrentHealth(regen, map, current, yTexture, gui);
      if (absorption > 0) {
         int offset = maxHealth * 9 + (maxHealth == 0 ? 0 : 2);
         GlStateManager.translate((float)offset, 0.0F, 0.0F);
         renderAbsorption(regen - maxHealth, map, absorption, yTexture, gui);
      }

   }

   public static int getMaxHearts(float value) {
      int maxCurrentHearts = (int)Math.ceil((double)value);
      if (maxCurrentHearts % 2 != 0) {
         ++maxCurrentHearts;
      }

      return maxCurrentHearts >> 1;
   }

   private static void renderMax(int regen, Int2IntFunction function, int max, int yTexture, Gui gui, boolean highlight) {
      int BACKGROUND = highlight ? 25 : 16;
      renderTexturedModalRects(regen, function, max, false, BACKGROUND, BACKGROUND, yTexture, gui);
   }

   private static void renderCurrentHealth(int regen, Int2IntFunction function, int current, int yTexture, Gui gui) {
      boolean renderLastHalf = false;
      int render = current >> 1;
      if (current % 2 != 0) {
         renderLastHalf = true;
         ++render;
      }

      renderTexturedModalRects(regen, function, render, renderLastHalf, 61, 52, yTexture, gui);
   }

   private static void renderAbsorption(int regen, Int2IntFunction function, int absorption, int yTexture, Gui gui) {
      boolean renderLastHalf = false;
      int render = absorption >> 1;
      if (absorption % 2 != 0) {
         renderLastHalf = true;
         ++render;
      }

      if (render > 0) {
         renderTexturedModalRects(regen, function, render, renderLastHalf, 169, 160, yTexture, gui);
      }

   }

   private static void renderTexturedModalRects(int regen, Int2IntFunction function, int toDraw, boolean lastOneHalf, int halfTextureX, int textureX, int textureY, Gui gui) {
      if (toDraw != 0) {
         if (toDraw < 0) {
            throw new IllegalArgumentException("Cannot draw negative amount of icons " + toDraw);
         } else {
            for(int i = 0; i < toDraw; ++i) {
               boolean renderHalf = lastOneHalf && i + 1 == toDraw;
               gui.drawTexturedModalRect(9.0F * (float)i, (float)((i == regen ? -2 : 0) - function.get(i)), renderHalf ? halfTextureX : textureX, textureY, 9, 9);
            }

         }
      }
   }

   static {
      for(EnumPlayerPart part : EnumPlayerPart.VALUES) {
         flashStates.put(part, new FlashStateManager());
      }

   }
}
