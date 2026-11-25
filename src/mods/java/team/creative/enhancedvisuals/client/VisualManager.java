package team.creative.enhancedvisuals.client;

import com.creativemd.creativecore.common.config.premade.IntMinMax;
import com.creativemd.creativecore.common.config.premade.curve.Curve;
import com.creativemd.creativecore.common.config.premade.curve.DecimalCurve;
import com.creativemd.creativecore.common.utils.type.HashMapList;
import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import team.creative.enhancedvisuals.EnhancedVisuals;
import team.creative.enhancedvisuals.api.Particle;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualCategory;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.common.event.EVEvents;
import team.creative.enhancedvisuals.common.visual.VisualRegistry;

public class VisualManager {
   private static Minecraft mc = Minecraft.getMinecraft();
   public static EaglercraftRandom rand = new EaglercraftRandom();
   private static HashMapList<VisualCategory, Visual> visuals = new HashMapList();

   public VisualManager() {
   }

   public static void onTick(@Nullable EntityPlayer player) {
      boolean areEyesInWater = player != null && EVEvents.areEyesInWater(player);
      synchronized(visuals) {
         Iterator<Visual> iterator = visuals.iterator();

         while(iterator.hasNext()) {
            Visual visual = (Visual)iterator.next();
            int factor = 1;
            if (areEyesInWater && visual.isAffectedByWater()) {
               factor = EnhancedVisuals.CONFIG.waterSubstractFactor;
            }

            for(int i = 0; i < factor; ++i) {
               if (!visual.tick()) {
                  visual.removeFromDisplay();
                  iterator.remove();
                  break;
               }
            }
         }

         for(VisualHandler handler : VisualRegistry.handlers()) {
            if (handler.isEnabled(player)) {
               handler.tick(player);
            }
         }
      }

      if (player != null && !player.isEntityAlive()) {
         clearParticles();
      }

   }

   public static Collection<Visual> visuals(VisualCategory category) {
      return visuals.getValues(category);
   }

   public static void clearParticles() {
      synchronized(visuals) {
         visuals.removeKey(VisualCategory.particle);
      }
   }

   public static void add(Visual visual) {
      if (!visual.type.disabled) {
         visual.addToDisplay();
         visuals.add(visual.getCategory(), visual);
      }

   }

   public static Visual addVisualFadeOut(VisualType vt, VisualHandler handler, IntMinMax time) {
      return addVisualFadeOut(vt, handler, new DecimalCurve(0.0D, 1.0D, (double)time.next(rand), 0.0D));
   }

   public static Visual addVisualFadeOut(VisualType vt, VisualHandler handler, int time) {
      return addVisualFadeOut(vt, handler, new DecimalCurve(0.0D, 1.0D, (double)time, 0.0D));
   }

   public static Visual addVisualFadeOut(VisualType vt, VisualHandler handler, Curve curve) {
      Visual v = new Visual(vt, handler, curve, vt.getVariantAmount() > 1 ? rand.nextInt(vt.getVariantAmount()) : 0);
      add(v);
      return v;
   }

   public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, IntMinMax time, boolean rotate) {
      addParticlesFadeOut(vt, handler, count, new DecimalCurve(0.0D, 1.0D, (double)time.next(rand), 0.0D), rotate, (Color)null);
   }

   public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, IntMinMax time, boolean rotate, Color color) {
      addParticlesFadeOut(vt, handler, count, new DecimalCurve(0.0D, 1.0D, (double)time.next(rand), 0.0D), rotate, color);
   }

   public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, int time, boolean rotate) {
      addParticlesFadeOut(vt, handler, count, new DecimalCurve(0.0D, 1.0D, (double)time, 0.0D), rotate, (Color)null);
   }

   public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, Curve curve, boolean rotate, Color color) {
      if (!vt.disabled) {
         for(int i = 0; i < count; ++i) {
            ScaledResolution resolution = new ScaledResolution(mc);
            int screenWidth = resolution.getScaledWidth();
            int screenHeight = resolution.getScaledHeight();
            int width = vt.getWidth(screenWidth);
            int height = vt.getHeight(screenHeight);
            if (vt.scaleVariants()) {
               double scale = vt.randomScale(rand);
               width = (int)((double)width * scale);
               height = (int)((double)height * scale);
            }

            Particle particle = new Particle(vt, handler, curve, generateOffset(rand, screenWidth, width), generateOffset(rand, screenHeight, height), width, height, vt.canRotate() && rotate ? rand.nextFloat() * 360.0F : 0.0F, rand.nextInt(vt.getVariantAmount()));
            if (color != null) {
               particle.color = color;
            }

            add(particle);
         }

      }
   }

   public static int generateOffset(EaglercraftRandom rand, int dimensionLength, int spacingBuffer) {
      int half = dimensionLength / 2;
      float multiplier = (float)(1.0D - Math.pow(rand.nextDouble(), 2.0D));
      float textureCenterPosition = rand.nextInt(2) == 0 ? (float)half + (float)half * multiplier : (float)half - (float)half * multiplier;
      return (int)(textureCenterPosition - (float)spacingBuffer / 2.0F);
   }
}
