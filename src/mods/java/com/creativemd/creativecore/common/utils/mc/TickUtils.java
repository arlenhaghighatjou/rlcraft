package com.creativemd.creativecore.common.utils.mc;

import com.creativemd.creativecore.client.CreativeCoreClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TickUtils {
   public TickUtils() {
   }

   @SideOnly(Side.CLIENT)
   private static float getPartialTickTimeClient() {
      return CreativeCoreClient.getRenderPartialTicks();
   }

   public static float getPartialTickTime() {
      return FMLCommonHandler.instance().getSide().isClient() ? getPartialTickTimeClient() : 1.0F;
   }
}
