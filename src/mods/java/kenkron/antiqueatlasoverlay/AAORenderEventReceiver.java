package kenkron.antiqueatlasoverlay;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.SubTile;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.TileRenderIterator;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import hunternif.mc.atlas.util.Rect;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber(
   modid = "antiqueatlasoverlay"
)
public class AAORenderEventReceiver {
   private static final int CHUNK_SIZE = 16;
   private static int screenScale = 1;
   private static ScaledResolution res;

   public AAORenderEventReceiver() {
   }

   private static Integer getPlayerAtlas(EntityPlayer player) {
      if (!SettingsConfig.gameplay.itemNeeded) {
         return player.getUniqueID().hashCode();
      } else {
         ItemStack stack = player.getHeldItemOffhand();
         if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            return stack.getItemDamage();
         } else {
            for(int i = 0; i < 9; ++i) {
               stack = player.inventory.getStackInSlot(i);
               if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                  return stack.getItemDamage();
               }
            }

            return null;
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public static void eventHandler(Post event) {
      if (event.getType() == ElementType.ALL) {
         if (AAOConfig.appearance.enabled) {
            if (!(Minecraft.getMinecraft().currentScreen instanceof GuiAtlas)) {
               EntityPlayerSP player = Minecraft.getMinecraft().player;
               Integer atlas = null;
               if (AAOConfig.appearance.requiresHold) {
                  ItemStack stack = player.getHeldItemMainhand();
                  ItemStack stack2 = player.getHeldItemOffhand();
                  if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                     atlas = stack.getItemDamage();
                  } else if (!stack2.isEmpty() && stack2.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                     atlas = stack2.getItemDamage();
                  }
               } else {
                  atlas = getPlayerAtlas(player);
               }

               if (atlas != null) {
                  int gameWidth = event.getResolution().getScaledWidth();
                  int gameHeight = event.getResolution().getScaledHeight();
                  Rect bounds = (new Rect()).setOrigin(AAOConfig.position.xPosition, AAOConfig.position.yPosition);
                  if (AAOConfig.position.alignRight) {
                     bounds.minX = gameWidth - (AAOConfig.position.width + AAOConfig.position.xPosition);
                  }

                  if (AAOConfig.position.alignBottom) {
                     bounds.minY = gameHeight - (AAOConfig.position.height + AAOConfig.position.yPosition);
                  }

                  bounds.setSize(AAOConfig.position.width, AAOConfig.position.height);
                  res = event.getResolution();
                  drawMinimap(bounds, atlas.intValue(), player.getPositionVector(), player.getRotationYawHead(), player.dimension);
               }

            }
         }
      }
   }

   private static void drawMinimap(Rect shape, int atlasID, Vec3d position, float rotation, int dimension) {
      screenScale = (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.alphaFunc(516, 0.0F);
      AtlasRenderHelper.drawFullTexture(Textures.BOOK, (double)shape.minX, (double)shape.minY, shape.getWidth(), shape.getHeight());
      Rect innerShape = new Rect(shape.minX + Math.round(AAOConfig.appearance.borderX * (float)shape.getWidth()), shape.minY + Math.round(AAOConfig.appearance.borderY * (float)shape.getHeight()), shape.maxX - Math.round(AAOConfig.appearance.borderX * (float)shape.getWidth()), shape.maxY - Math.round(AAOConfig.appearance.borderY * (float)shape.getHeight()));
      drawTiles(innerShape, atlasID, position, dimension);
      if (AAOConfig.appearance.markerSize > 0) {
         drawMarkers(innerShape, atlasID, position, dimension);
         int shapeMiddleX = (shape.minX + shape.maxX) / 2;
         int shapeMiddleY = (shape.minY + shape.maxY) / 2;
         drawPlayer((float)shapeMiddleX, (float)shapeMiddleY, rotation);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, (double)shape.minX, (double)shape.minY, shape.getWidth(), shape.getHeight());
      GlStateManager.disableBlend();
   }

   private static void drawTiles(Rect shape, int atlasID, Vec3d position, int dimension) {
      GL11.glEnable(3089);
      glScissorGUI(shape);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, Minecraft.getMinecraft().world).getDimensionData(dimension);
      TileRenderIterator iter = new TileRenderIterator(biomeData);
      Rect iteratorScope = getChunkCoverage(position, shape);
      iter.setScope(iteratorScope);
      iter.setStep(1);
      Vec3d chunkPosition = new Vec3d(position.x / 16.0D, position.y / 16.0D, position.z / 16.0D);
      int shapeMiddleX = (shape.minX + shape.maxX) / 2;
      int shapeMiddleY = (shape.minY + shape.maxY) / 2;
      SetTileRenderer renderer = new SetTileRenderer(AAOConfig.appearance.tileSize / 2);

      while(iter.hasNext()) {
         for(SubTile subtile : iter.next()) {
            if (subtile != null && subtile.tile != null) {
               float relativeChunkPositionX = (float)((double)subtile.x / 2.0D + (double)iteratorScope.minX - chunkPosition.x);
               float relativeChunkPositionY = (float)((double)subtile.y / 2.0D + (double)iteratorScope.minY - chunkPosition.z);
               renderer.addTileCorner(BiomeTextureMap.instance().getTexture(subtile.tile), shapeMiddleX + (int)Math.floor((double)(relativeChunkPositionX * (float)AAOConfig.appearance.tileSize)), shapeMiddleY + (int)Math.floor((double)(relativeChunkPositionY * (float)AAOConfig.appearance.tileSize)), subtile.getTextureU(), subtile.getTextureV());
            }
         }
      }

      renderer.draw();
      GL11.glDisable(3089);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void drawMarkers(Rect shape, int atlasID, Vec3d position, int dimension) {
      GL11.glEnable(3089);
      glScissorGUI(shape);
      DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, Minecraft.getMinecraft().world).getDimensionData(dimension);
      DimensionMarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData.getData().getMarkersDataInDimension(dimension);
      drawMarkersData(globalMarkersData, shape, biomeData, position);
      MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, Minecraft.getMinecraft().world);
      DimensionMarkersData localMarkersData = null;
      if (markersData != null) {
         localMarkersData = markersData.getMarkersDataInDimension(dimension);
      }

      drawMarkersData(localMarkersData, shape, biomeData, position);
      GL11.glDisable(3089);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void drawPlayer(float x, float y, float rotation) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, 0.0F);
      GlStateManager.rotate(180.0F + rotation, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate((float)(-AAOConfig.appearance.playerIconWidth / 2), (float)(-AAOConfig.appearance.playerIconHeight / 2), 0.0F);
      AtlasRenderHelper.drawFullTexture(Textures.PLAYER, 0.0D, 0.0D, AAOConfig.appearance.playerIconWidth, AAOConfig.appearance.playerIconHeight);
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void drawMarkersData(DimensionMarkersData markersData, Rect shape, DimensionData biomeData, Vec3d position) {
      Rect markerShape = new Rect(shape.minX - AAOConfig.appearance.markerSize / 2, shape.minY - AAOConfig.appearance.markerSize / 2, shape.maxX + AAOConfig.appearance.markerSize / 2, shape.maxY + AAOConfig.appearance.markerSize / 2);
      Rect mcchunks = getChunkCoverage(position, markerShape);
      Rect chunks = new Rect((int)Math.floor((double)(mcchunks.minX / 8)), (int)Math.floor((double)(mcchunks.minY / 8)), (int)Math.ceil((double)(mcchunks.maxX / 8)), (int)Math.ceil((double)(mcchunks.maxY / 8)));
      int shapeMiddleX = (shape.minX + shape.maxX) / 2;
      int shapeMiddleY = (shape.minY + shape.maxY) / 2;

      for(int x = chunks.minX; x <= chunks.maxX; ++x) {
         for(int z = chunks.minY; z <= chunks.maxY; ++z) {
            List<Marker> markers = markersData.getMarkersAtChunk(Math.round((float)x), Math.round((float)z));
            if (markers != null) {
               for(Marker marker : markers) {
                  int relativeChunkPositionX = AAOConfig.appearance.tileSize * (2 * (marker.getX() / 2) - 2 * (int)Math.floor(position.x / 2.0D)) / 16;
                  int relativeChunkPositionY = AAOConfig.appearance.tileSize * (2 * (marker.getZ() / 2) - 2 * (int)Math.floor(position.z / 2.0D)) / 16;
                  int guiX = (int)Math.floor((double)(shapeMiddleX - AAOConfig.appearance.markerSize / 2 + relativeChunkPositionX));
                  int guiY = (int)Math.floor((double)(shapeMiddleY - AAOConfig.appearance.markerSize / 2 + relativeChunkPositionY));
                  renderMarker(marker, guiX, guiY, biomeData);
               }
            }
         }
      }

   }

   private static void renderMarker(Marker marker, int x, int y, DimensionData biomeData) {
      if (marker.isVisibleAhead() || biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         MarkerType m = MarkerRegistry.find(marker.getType());
         if (m != null) {
            MarkerRenderInfo info = m.getRenderInfo(1.0D, (double)AAOConfig.appearance.tileSize, (double)screenScale);
            AtlasRenderHelper.drawFullTexture(info.tex, (double)x, (double)y, AAOConfig.appearance.markerSize, AAOConfig.appearance.markerSize);
         }
      }
   }

   private static Rect getChunkCoverage(Vec3d position, Rect windowShape) {
      int minChunkX = (int)Math.floor(position.x / 16.0D - (double)((float)windowShape.getWidth() / (2.0F * (float)AAOConfig.appearance.tileSize)));
      --minChunkX;
      int minChunkY = (int)Math.floor(position.z / 16.0D - (double)((float)windowShape.getHeight() / (2.0F * (float)AAOConfig.appearance.tileSize)));
      --minChunkY;
      int maxChunkX = (int)Math.ceil(position.x / 16.0D + (double)((float)windowShape.getWidth() / (2.0F * (float)AAOConfig.appearance.tileSize)));
      ++maxChunkX;
      int maxChunkY = (int)Math.ceil(position.z / 16.0D + (double)((float)windowShape.getHeight() / (2.0F * (float)AAOConfig.appearance.tileSize)));
      ++maxChunkY;
      return new Rect(minChunkX, minChunkY, maxChunkX, maxChunkY);
   }

   private static void glScissorGUI(Rect shape) {
      int mcHeight = Minecraft.getMinecraft().displayHeight;
      float scissorScaleX = (float)Minecraft.getMinecraft().displayWidth * 1.0F / (float)res.getScaledWidth();
      float scissorScaleY = (float)mcHeight * 1.0F / (float)res.getScaledHeight();
      GL11.glScissor((int)((float)shape.minX * scissorScaleX), (int)((float)mcHeight - (float)shape.maxY * scissorScaleY), (int)((float)shape.getWidth() * scissorScaleX), (int)((float)shape.getHeight() * scissorScaleY));
   }
}
