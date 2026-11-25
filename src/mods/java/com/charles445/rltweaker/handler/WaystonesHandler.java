package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.WaystoneReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaystonesHandler {
   WaystoneReflect reflector;

   public WaystonesHandler() {
      try {
         this.reflector = new WaystoneReflect();
         CompatUtil.subscribeEventManually(this.reflector.c_GenerateWaystoneNameEvent, this, ReflectUtil.findMethod(this.getClass(), "onGenerateWaystone"));
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup WaystonesHandler!", var2);
         ErrorUtil.logSilent("Waystones Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onGenerateWaystone(Event event) {
      if (ModConfig.server.waystones.removeWaystoneBiome) {
         try {
            String waystoneName = this.reflector.getWaystoneName(event);
            int dimension = this.reflector.getDimension(event);
            BlockPos waystonePos = this.reflector.getPos(event);
            waystoneName = this.cleanBiome(waystoneName, dimension, waystonePos);
            this.reflector.setWaystoneName(event, waystoneName);
         } catch (Exception var5) {
            RLTweaker.logger.error("Failed to intercept waystone generation!", var5);
         }

      }
   }

   private String cleanBiome(String s, int dimension, BlockPos pos) {
      try {
         World world = DimensionManager.getWorld(dimension);
         if (world != null && !world.isRemote) {
            Block block = world.getBlockState(pos.down()).getBlock();
            if (block != Blocks.GLOWSTONE) {
               return s;
            } else {
               String baseName = this.reflector.getRandomName(dimension);
               String result = "" + baseName;
               Set<String> usedNames = this.reflector.getUsedNames(dimension);

               for(int roman = 1; usedNames.contains(result); ++roman) {
                  result = baseName + " " + this.reflector.toRoman(roman);
               }

               return result;
            }
         } else {
            return s;
         }
      } catch (Exception var10) {
         RLTweaker.logger.error("Failed to intercept waystone generation! Defaulting to uncleaned name for safety.", var10);
         return s;
      }
   }
}
