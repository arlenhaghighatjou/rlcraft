package hunternif.mc.atlas.registry;

import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hunternif.mc.atlas.util.BitMatrix;
import hunternif.mc.atlas.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.IOUtils;

public class MarkerType extends IRegistryEntry.Impl {
   private ResourceLocation[] icons;
   private BitMatrix[] iconPixels;
   private int[] iconSizes = null;
   private int viewSize = 2;
   private int clipMin = -1000;
   private int clipMax = 1000;
   private boolean alwaysShow = false;
   private boolean isTile = false;
   private boolean isTechnical = false;
   private double centerX = 0.5D;
   private double centerY = 0.5D;
   private boolean isFromJson = false;
   private final MarkerType.JSONData data = new MarkerType.JSONData(this);
   private int iconIndex = 0;

   public MarkerType(ResourceLocation loc, ResourceLocation... icons) {
      if (loc == null) {
         throw new IllegalArgumentException("MarkerType registry name can't be null");
      } else {
         this.setRegistryName(loc);
         this.icons = icons;
      }
   }

   public boolean isTechnical() {
      return this.isTechnical;
   }

   public boolean shouldHide(boolean isHidingMarkers, int scaleIndex) {
      return this.shouldClip(scaleIndex) || !this.alwaysShow && isHidingMarkers;
   }

   private boolean shouldClip(int scaleIndex) {
      return scaleIndex < this.clipMin || scaleIndex > this.clipMax;
   }

   public boolean shouldHover(double x, double y) {
      if (!this.isTechnical() && x <= 1.0D && x >= 0.0D && y <= 1.0D && y >= 0.0D) {
         if (this.iconPixels != null && this.iconPixels.length != 0 && this.iconIndex >= 0) {
            int iconX = (int)((double)this.iconPixels[this.iconIndex].getWidth() * x);
            int iconY = (int)((double)this.iconPixels[this.iconIndex].getHeight() * y);
            return this.iconPixels[this.iconIndex].get(iconX, iconY);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private int viewSize() {
      return this.viewSize;
   }

   private boolean isTile() {
      return this.isTile;
   }

   private double getCenterX() {
      return this.centerX;
   }

   private double getCenterY() {
      return this.centerY;
   }

   public ResourceLocation getIcon() {
      return this.icons.length != 0 && this.iconIndex >= 0 ? this.icons[this.iconIndex] : TextureMap.LOCATION_MISSING_TEXTURE;
   }

   public ResourceLocation[] getAllIcons() {
      return this.icons;
   }

   public void calculateMip(double scale, double mapScale, double screenScale) {
      int size = (int)(16.0D * scale * (double)this.viewSize());
      if (this.isTile) {
         size = (int)((double)size * mapScale);
      }

      if (this.icons.length > 1) {
         int smallestSide = (int)((double)size * screenScale);
         int closestValue = Integer.MAX_VALUE;
         int closestIndex = -1;

         for(int i = 0; i < this.iconSizes.length; ++i) {
            if (this.iconSizes[i] < closestValue && this.iconSizes[i] >= smallestSide) {
               closestValue = this.iconSizes[i];
               closestIndex = i;
            }
         }

         if (closestIndex > 0) {
            this.iconIndex = closestIndex;
         }
      }

   }

   public void resetMip() {
      this.iconIndex = 0;
   }

   public MarkerRenderInfo getRenderInfo(double scale, double mapScale, double screenScale) {
      boolean isTile = this.isTile();
      int size = (int)(16.0D * scale * (double)this.viewSize());
      if (isTile) {
         size = (int)((double)size * mapScale);
      }

      int x = -((int)((double)size * this.getCenterX()));
      int y = -((int)((double)size * this.getCenterY()));
      ResourceLocation icon = this.getIcon();
      return new MarkerRenderInfo(icon, (double)x, (double)y, size, size);
   }

   public void initMips() {
      this.iconSizes = new int[this.icons.length];
      this.iconPixels = new BitMatrix[this.icons.length];
      int ALPHA_THRESHOLD = 8;

      for(int i = 0; i < this.icons.length; ++i) {
         this.iconSizes[i] = -1;
         if (this.icons[i] == null) {
            Log.warn("Marker %s -- Texture location is null at index %d!", this.getRegistryName().toString(), i);
         }

         IResource iresource = null;

         try {
            iresource = Minecraft.getMinecraft().getResourceManager().getResource(this.icons[i]);
            ImageData bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            this.iconSizes[i] = Math.min(bufferedimage.width, bufferedimage.height);
            BitMatrix matrix = new BitMatrix(bufferedimage.width, bufferedimage.height, false);

            for(int x = 0; x < bufferedimage.width; ++x) {
               for(int y = 0; y < bufferedimage.height; ++y) {
                  int color = bufferedimage.pixels[y * bufferedimage.width + x];
                  int alpha = color >> 24 & 255;
                  if (alpha >= ALPHA_THRESHOLD) {
                     matrix.set(x, y, true);
                     matrix.set(x - 1, y, true);
                     matrix.set(x + 1, y, true);
                     matrix.set(x, y - 1, true);
                     matrix.set(x, y + 1, true);
                     matrix.set(x + 1, y + 1, true);
                     matrix.set(x - 1, y - 1, true);
                     matrix.set(x + 1, y - 1, true);
                     matrix.set(x - 1, y + 1, true);
                  }
               }
            }

            this.iconPixels[i] = matrix;
         } catch (IOException var13) {
            Log.warn(var13, "Marker %s -- Error getting texture size data for index %d - %s", this.getRegistryName().toString(), i, this.icons[i].toString());
         } finally {
            IOUtils.closeQuietly(iresource);
         }
      }

   }

   public MarkerType setSize(int value) {
      this.viewSize = value;
      return this;
   }

   public MarkerType setIsTile(boolean value) {
      this.isTile = value;
      return this;
   }

   public MarkerType setAlwaysShow(boolean value) {
      this.alwaysShow = value;
      return this;
   }

   public MarkerType setClip(int min, int max) {
      this.clipMin = Math.min(min, max);
      this.clipMax = Math.max(min, max);
      return this;
   }

   public MarkerType setCenter(double x, double y) {
      this.centerX = x;
      this.centerY = y;
      return this;
   }

   public MarkerType setIsTechnical(boolean value) {
      this.isTechnical = value;
      return this;
   }

   public MarkerType setIsFromJson(boolean value) {
      this.isFromJson = value;
      return this;
   }

   public MarkerType.JSONData getJSONData() {
      return this.data;
   }

   public static class JSONData {
      static final String ICONS = "textures";
      static final String SIZE = "size";
      static final String CLIP_MIN = "clipMin";
      static final String CLIP_MAX = "clipMax";
      static final String ALWAYS_SHOW = "alwaysShow";
      static final String IS_TILE = "isTile";
      static final String IS_TECH = "isTechnical";
      static final String CENTER_X = "centerX";
      static final String CENTER_Y = "centerY";
      static final String NONE = "NONE";
      private final MarkerType type;
      ResourceLocation[] icons;
      Integer viewSize = null;
      Integer clipMin = null;
      Integer clipMax = null;
      Boolean alwaysShow = null;
      Boolean isTile = null;
      Boolean isTechnical = null;
      Double centerX = null;
      Double centerY = null;

      JSONData(MarkerType type) {
         this.type = type;
      }

      public void saveTo(JsonObject object) {
         if (this.icons != null) {
            JsonArray arr = new JsonArray();

            for(ResourceLocation loc : this.icons) {
               arr.add(new JsonPrimitive(loc.toString()));
            }

            object.add("textures", arr);
         }

         if (this.viewSize != null) {
            object.addProperty("size", this.viewSize);
         }

         if (this.clipMin != null) {
            object.addProperty("clipMin", this.clipMin);
         }

         if (this.clipMax != null) {
            object.addProperty("clipMax", this.clipMax);
         }

         if (this.alwaysShow != null) {
            object.addProperty("alwaysShow", this.alwaysShow);
         }

         if (this.isTile != null) {
            object.addProperty("isTile", this.isTile);
         }

         if (this.isTechnical != null) {
            object.addProperty("isTechnical", this.isTechnical);
         }

         if (this.centerX != null) {
            object.addProperty("centerX", this.centerX);
         }

         if (this.centerY != null) {
            object.addProperty("centerY", this.centerY);
         }

      }

      public void readFrom(JsonObject object) {
         if (object.entrySet().size() != 0) {
            String typeName = this.type.getRegistryName().toString();
            String workingOn = "NONE";

            try {
               if (object.has("textures") && object.get("textures").isJsonArray()) {
                  workingOn = "textures";
                  List<ResourceLocation> list = new ArrayList();
                  int i = 0;

                  for(JsonElement elem : object.get("textures").getAsJsonArray()) {
                     if (elem.isJsonPrimitive()) {
                        list.add(new ResourceLocation(elem.getAsString()));
                     } else {
                        Log.warn("Loading marker %s from JSON: Texture item %d isn't a primitive", typeName, i);
                     }

                     ++i;
                  }

                  this.icons = (ResourceLocation[])list.toArray(new ResourceLocation[0]);
                  workingOn = "NONE";
               }

               if (object.has("size") && object.get("size").isJsonPrimitive()) {
                  workingOn = "size";
                  this.viewSize = object.get("size").getAsInt();
                  workingOn = "NONE";
               }

               if (object.has("clipMin") && object.get("clipMin").isJsonPrimitive()) {
                  workingOn = "clipMin";
                  this.clipMin = object.get("clipMin").getAsInt();
                  workingOn = "NONE";
               }

               if (object.has("clipMax") && object.get("clipMax").isJsonPrimitive()) {
                  workingOn = "clipMax";
                  this.clipMax = object.get("clipMax").getAsInt();
                  workingOn = "NONE";
               }

               if (object.has("alwaysShow") && object.get("alwaysShow").isJsonPrimitive()) {
                  workingOn = "alwaysShow";
                  this.alwaysShow = object.get("alwaysShow").getAsBoolean();
                  workingOn = "NONE";
               }

               if (object.has("isTile") && object.get("isTile").isJsonPrimitive()) {
                  workingOn = "isTile";
                  this.isTile = object.get("isTile").getAsBoolean();
                  workingOn = "NONE";
               }

               if (object.has("isTechnical") && object.get("isTechnical").isJsonPrimitive()) {
                  workingOn = "isTechnical";
                  this.isTechnical = object.get("isTechnical").getAsBoolean();
                  workingOn = "NONE";
               }

               if (object.has("centerX") && object.get("centerX").isJsonPrimitive()) {
                  workingOn = "centerX";
                  this.centerX = object.get("centerX").getAsDouble();
                  workingOn = "NONE";
               }

               if (object.has("centerY") && object.get("centerY").isJsonPrimitive()) {
                  workingOn = "centerY";
                  this.centerY = object.get("centerY").getAsDouble();
                  workingOn = "NONE";
               }
            } catch (ClassCastException var8) {
               Log.warn(var8, "Loading marker $s from JSON: Parsing element %s: element was wrong type!", typeName, workingOn);
            } catch (NumberFormatException var9) {
               Log.warn(var9, "Loading marker $s from JSON: Parsing element %s: element was an invalid number!", typeName, workingOn);
            }

            if (this.icons != null) {
               this.type.icons = this.icons;
            }

            if (this.viewSize != null) {
               this.type.viewSize = this.viewSize.intValue();
            }

            if (this.clipMin != null) {
               this.type.clipMin = this.clipMin.intValue();
            }

            if (this.clipMax != null) {
               this.type.clipMax = this.clipMax.intValue();
            }

            if (this.alwaysShow != null) {
               this.type.alwaysShow = this.alwaysShow.booleanValue();
            }

            if (this.isTile != null) {
               this.type.isTile = this.isTile.booleanValue();
            }

            if (this.isTechnical != null) {
               this.type.isTechnical = this.isTechnical.booleanValue();
            }

            if (this.centerX != null) {
               this.type.centerX = this.centerX.doubleValue();
            }

            if (this.centerY != null) {
               this.type.centerY = this.centerY.doubleValue();
            }

         }
      }
   }
}
