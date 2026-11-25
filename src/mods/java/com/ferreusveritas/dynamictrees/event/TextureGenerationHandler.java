package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.client.ThickRingTextureAtlasSprite;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TextureGenerationHandler {
   private static final Map<ResourceLocation, ResourceLocation> thickRingTextures = new HashMap();

   public TextureGenerationHandler() {
   }

   public static ResourceLocation addRingTextureLocation(ResourceLocation ringsRes) {
      ResourceLocation outputRes = new ResourceLocation(ringsRes.getResourceDomain(), ringsRes.getResourcePath() + "_thick");
      thickRingTextures.put(ringsRes, outputRes);
      return outputRes;
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void onTextureStitchEvent(Pre event) {
      IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();

      for(Entry<ResourceLocation, ResourceLocation> entry : thickRingTextures.entrySet()) {
         if (entry.getKey() instanceof TextureGenerationHandler.DualResourceLocation) {
            dualStitch(event, (TextureGenerationHandler.DualResourceLocation)entry.getKey(), (ResourceLocation)entry.getValue());
         } else {
            ResourceLocation textureLocation = new ResourceLocation(((ResourceLocation)entry.getValue()).getResourceDomain(), String.format("%s/%s%s", event.getMap().getBasePath(), ((ResourceLocation)entry.getValue()).getResourcePath(), ".png"));

            IResource resource;
            try {
               resource = resourceManager.getResource(textureLocation);
            } catch (IOException var7) {
               resource = null;
            }

            if (resource != null) {
               event.getMap().registerSprite((ResourceLocation)entry.getValue());
            } else {
               event.getMap().setTextureEntry(new ThickRingTextureAtlasSprite((ResourceLocation)entry.getValue(), (ResourceLocation)entry.getKey()));
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void dualStitch(Pre event, TextureGenerationHandler.DualResourceLocation key, ResourceLocation value) {
      event.getMap().setTextureEntry(new ThickRingTextureAtlasSprite(value, key, key.getAlternate()));
   }

   public static class DualResourceLocation extends ResourceLocation {
      private final ResourceLocation alternate;

      public DualResourceLocation(ResourceLocation primary, ResourceLocation alternate) {
         super(primary.getResourceDomain(), primary.getResourcePath());
         this.alternate = alternate;
      }

      public ResourceLocation getAlternate() {
         return this.alternate;
      }
   }
}
