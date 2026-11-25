package com.teamacronymcoders.base.client;

import com.teamacronymcoders.base.util.Platform;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientHelper {
   public static String shiftForInfo = TextFormatting.GRAY + "Hold " + TextFormatting.GREEN + "SHIFT" + TextFormatting.GRAY + " for more info.";

   public ClientHelper() {
   }

   public static boolean isShiftKeyDown() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }

   public static Minecraft mc() {
      return FMLClientHandler.instance().getClient();
   }

   public static FontRenderer fontRenderer() {
      return mc().fontRenderer;
   }

   public static EntityPlayerSP player() {
      return mc().player;
   }

   public static WorldClient world() {
      return mc().world;
   }

   public static Entity viewEntity() {
      return mc().getRenderViewEntity();
   }

   public static TextureManager textureManager() {
      return mc().getTextureManager();
   }

   public static RayTraceResult rayTrace() {
      return mc().objectMouseOver;
   }

   public static GuiScreen screen() {
      return mc().currentScreen;
   }

   public static GameSettings settings() {
      return mc().gameSettings;
   }

   public static EntityRenderer entityRenderer() {
      return mc().entityRenderer;
   }

   public static RenderManager renderManager() {
      return mc().getRenderManager();
   }

   public static ScaledResolution resolution() {
      return new ScaledResolution(mc());
   }

   public static ItemModelMesher getItemModelMesher() {
      return mc().getRenderItem().getItemModelMesher();
   }

   @Nullable
   public static IResource getResource(ResourceLocation resourceLocation) {
      IResource iResource = null;

      try {
         iResource = mc().getResourceManager().getResource(resourceLocation);
      } catch (IOException var3) {
         Platform.attemptLogExceptionToCurrentMod(var3);
      }

      return iResource;
   }
}
