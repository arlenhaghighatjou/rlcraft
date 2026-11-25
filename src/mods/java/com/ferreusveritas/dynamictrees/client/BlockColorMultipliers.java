package com.ferreusveritas.dynamictrees.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockColorMultipliers {
   private static Map<String, IBlockColor> colorBase = new HashMap();

   public BlockColorMultipliers() {
   }

   public static void register(String label, IBlockColor colorMultiplier) {
      colorBase.put(label, colorMultiplier);
   }

   public static void register(ResourceLocation label, IBlockColor colorMultiplier) {
      colorBase.put(label.toString(), colorMultiplier);
   }

   public static IBlockColor find(String label) {
      return (IBlockColor)colorBase.get(label);
   }

   public static IBlockColor find(ResourceLocation label) {
      return (IBlockColor)colorBase.get(label.toString());
   }

   public static void cleanUp() {
      colorBase = null;
   }
}
