package vazkii.quark.experimental.lighting;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import vazkii.arl.util.ClientTicker;

public final class ColoredLightSystem {
   private static final List<LightSource> lightSources = new ArrayList();
   private static List<LightSource> currentSources = new ArrayList();
   private static int lastFrame;

   public ColoredLightSystem() {
   }

   public static void tick(Minecraft mc) {
      ForgeModContainer.forgeLightPipelineEnabled = false;
      mc.gameSettings.ambientOcclusion = 0;
      World world = mc.world;
      if (world == null) {
         lightSources.clear();
         currentSources.clear();
      }

      List<LightSource> tempList = new ArrayList(lightSources);
      tempList.removeIf((src) -> {
         return src == null || !src.isValid(world);
      });
      currentSources = tempList;
   }

   public static float[] getLightColor(IBlockAccess world, BlockPos pos) {
      float maxBrightness = 0.0F;
      float addB = 0.0F;
      float addG = 0.0F;
      float addR = 0.0F;
      int time = ClientTicker.ticksInGame;
      if (time != lastFrame) {
         prepareFrame();
      }

      lastFrame = time;

      for(LightSource src : currentSources) {
         BlockPos srcPos = src.pos;
         IBlockState srcState = world.getBlockState(srcPos);
         Block srcBlock = srcState.getBlock();
         if (srcBlock instanceof IColoredLightSource) {
            int srcLight = srcState.getLightValue(world, srcPos);
            float brightness = (float)srcLight / 15.0F;
            int incidence = src.getIncidence(pos);
            if (incidence > 0) {
               float incidenceF = (float)incidence / 15.0F;
               float localBrightness = brightness * incidenceF;
               float[] colors = ((IColoredLightSource)srcBlock).getColoredLight(world, srcPos);
               if (colors.length != 3) {
                  colors = new float[]{1.0F, 1.0F, 1.0F};
               }

               maxBrightness = Math.max(maxBrightness, localBrightness);
               addR += colors[0] * localBrightness;
               addG += colors[1] * localBrightness;
               addB += colors[2] * localBrightness;
            }
         }
      }

      float strongestColor = Math.max(addR, Math.max(addG, addB));
      if (maxBrightness > 0.0F && strongestColor > 0.0F) {
         float lower = 1.0F - maxBrightness;
         addR = addR / strongestColor;
         addG = addG / strongestColor;
         addB = addB / strongestColor;
         addR = MathHelper.clamp(addR, lower, 1.0F);
         addG = MathHelper.clamp(addG, lower, 1.0F);
         addB = MathHelper.clamp(addB, lower, 1.0F);
         return new float[]{addR, addG, addB};
      } else {
         return new float[0];
      }
   }

   private static void prepareFrame() {
      for(LightSource src : currentSources) {
         src.newFrame();
      }

   }

   public static void addLightSource(IBlockAccess access, BlockPos pos, IBlockState state) {
      if (access instanceof World) {
         World world = (World)access;
         ListIterator<LightSource> iterator = lightSources.listIterator();

         while(iterator.hasNext()) {
            LightSource src = (LightSource)iterator.next();
            if (src.pos.equals(pos)) {
               if (src.isValid(world)) {
                  return;
               }

               iterator.remove();
            }
         }

         int brightness = state.getBlock().getLightValue(state, access, pos);
         lightSources.add(new LightSource(world, pos, state, brightness));
      }
   }
}
