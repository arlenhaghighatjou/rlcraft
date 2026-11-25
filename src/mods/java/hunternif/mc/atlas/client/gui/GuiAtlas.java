package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.SubTile;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.TileRenderIterator;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.atlas.client.gui.core.GuiCursor;
import hunternif.mc.atlas.client.gui.core.GuiStates;
import hunternif.mc.atlas.client.gui.core.IButtonListener;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.event.MarkerClickedEvent;
import hunternif.mc.atlas.event.MarkerHoveredEvent;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import hunternif.mc.atlas.util.AtlasTooLargeException;
import hunternif.mc.atlas.util.ExportImageUtil;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.MathUtil;
import hunternif.mc.atlas.util.Rect;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.common.MinecraftForge;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAtlas extends GuiComponent {
   public static final int WIDTH = 310;
   public static final int HEIGHT = 218;
   private static final int CONTENT_X = 17;
   private static final int CONTENT_Y = 11;
   private static final int MAP_WIDTH = 276;
   private static final int MAP_HEIGHT = 194;
   private static final float PLAYER_ROTATION_STEPS = 16.0F;
   private static final int PLAYER_ICON_WIDTH = 7;
   private static final int PLAYER_ICON_HEIGHT = 8;
   public static final int MARKER_SIZE = 32;
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
   private static final double MIN_SCALE_THRESHOLD = 0.5D;
   private final long[] renderTimes = new long[30];
   private int renderTimesIndex = 0;
   private final GuiStates state = new GuiStates();
   private final GuiStates.IState NORMAL = new GuiStates.SimpleState();
   private final GuiStates.IState HIDING_MARKERS = new GuiStates.IState() {
      public void onEnterState() {
         GuiAtlas.this.btnShowMarkers.setSelected(false);
         GuiAtlas.this.btnShowMarkers.setTitle(I18n.format("gui.antiqueatlas.showMarkers", new Object[0]));
         GuiAtlas.this.btnShowMarkers.setIconTexture(Textures.ICON_SHOW_MARKERS);
      }

      public void onExitState() {
         GuiAtlas.this.btnShowMarkers.setSelected(false);
         GuiAtlas.this.btnShowMarkers.setTitle(I18n.format("gui.antiqueatlas.hideMarkers", new Object[0]));
         GuiAtlas.this.btnShowMarkers.setIconTexture(Textures.ICON_HIDE_MARKERS);
      }
   };
   private final GuiStates.IState PLACING_MARKER = new GuiStates.IState() {
      public void onEnterState() {
         GuiAtlas.this.btnMarker.setSelected(true);
      }

      public void onExitState() {
         GuiAtlas.this.btnMarker.setSelected(false);
      }
   };
   private final GuiStates.IState DELETING_MARKER = new GuiStates.IState() {
      public void onEnterState() {
         GuiAtlas.this.mc.mouseHelper.grabMouseCursor();
         GuiAtlas.this.addChild(GuiAtlas.this.eraser);
         GuiAtlas.this.btnDelMarker.setSelected(true);
      }

      public void onExitState() {
         GuiAtlas.this.mc.mouseHelper.ungrabMouseCursor();
         GuiAtlas.this.removeChild(GuiAtlas.this.eraser);
         GuiAtlas.this.btnDelMarker.setSelected(false);
      }
   };
   private final GuiCursor eraser = new GuiCursor();
   private final GuiStates.IState EXPORTING_IMAGE = new GuiStates.IState() {
      public void onEnterState() {
         GuiAtlas.this.btnExportPng.setSelected(true);
      }

      public void onExitState() {
         GuiAtlas.this.btnExportPng.setSelected(false);
      }
   };
   private final GuiArrowButton btnUp;
   private final GuiArrowButton btnDown;
   private final GuiArrowButton btnLeft;
   private final GuiArrowButton btnRight;
   private final GuiBookmarkButton btnExportPng;
   private final GuiBookmarkButton btnMarker;
   private final GuiBookmarkButton btnDelMarker;
   private final GuiBookmarkButton btnShowMarkers;
   private final GuiPositionButton btnPosition;
   private static final int BUTTON_PAUSE = 8;
   private static final int navigateStep = 24;
   private GuiComponentButton selectedButton = null;
   private long timeButtonPressed = 0L;
   private boolean isDragging = false;
   private int dragMouseX;
   private int dragMouseY;
   private int dragMapOffsetX;
   private int dragMapOffsetY;
   private int mapOffsetX;
   private int mapOffsetY;
   private boolean followPlayer = true;
   private final GuiScaleBar scaleBar = new GuiScaleBar();
   private double mapScale;
   private int tileHalfSize;
   private int tile2ChunkScale;
   private DimensionMarkersData localMarkersData;
   private Marker hoveredMarker;
   private final GuiMarkerFinalizer markerFinalizer = new GuiMarkerFinalizer();
   private final GuiBlinkingMarker blinkingIcon = new GuiBlinkingMarker();
   private EntityPlayer player;
   private ItemStack stack;
   private DimensionData biomeData;
   private int screenScale;
   private final ProgressBarOverlay progressBar = new ProgressBarOverlay(100, 2);
   private long lastUpdateMillis = System.currentTimeMillis();
   private int scaleAlpha = 255;
   private int scaleClipIndex = 0;
   private int zoomLevelOne = 8;
   private int zoomLevel;
   private String[] zoomNames;
   private Thread exportThread;

   public GuiAtlas() {
      this.zoomLevel = this.zoomLevelOne;
      this.zoomNames = new String[]{"256", "128", "64", "32", "16", "8", "4", "2", "1", "1/2", "1/4", "1/8", "1/16", "1/32", "1/64", "1/128", "1/256"};
      this.setSize(310, 218);
      this.setMapScale(0.5D);
      this.followPlayer = true;
      this.setInterceptKeyboard(true);
      this.btnUp = GuiArrowButton.up();
      this.addChild(this.btnUp).offsetGuiCoords(148, 10);
      this.btnDown = GuiArrowButton.down();
      this.addChild(this.btnDown).offsetGuiCoords(148, 194);
      this.btnLeft = GuiArrowButton.left();
      this.addChild(this.btnLeft).offsetGuiCoords(15, 100);
      this.btnRight = GuiArrowButton.right();
      this.addChild(this.btnRight).offsetGuiCoords(283, 100);
      this.btnPosition = new GuiPositionButton();
      this.btnPosition.setEnabled(!this.followPlayer);
      this.addChild(this.btnPosition).offsetGuiCoords(283, 194);
      IButtonListener positionListener = (button) -> {
         this.selectedButton = button;
         if (button.equals(this.btnPosition)) {
            this.followPlayer = true;
            this.btnPosition.setEnabled(false);
         } else {
            this.navigateByButton(this.selectedButton);
            this.timeButtonPressed = this.player.getEntityWorld().getTotalWorldTime();
         }

      };
      this.btnUp.addListener(positionListener);
      this.btnDown.addListener(positionListener);
      this.btnLeft.addListener(positionListener);
      this.btnRight.addListener(positionListener);
      this.btnPosition.addListener(positionListener);
      this.btnExportPng = new GuiBookmarkButton(1, Textures.ICON_EXPORT, I18n.format("gui.antiqueatlas.exportImage", new Object[0])) {
         public boolean isEnabled() {
            return !ExportImageUtil.isExporting;
         }
      };
      this.addChild(this.btnExportPng).offsetGuiCoords(300, 75);
      this.btnExportPng.addListener((button) -> {
         if (this.stack != null || !SettingsConfig.gameplay.itemNeeded) {
            this.exportThread = new Thread(() -> {
               this.exportImage(this.getAtlasID());
            }, "Atlas file export thread");
            this.exportThread.start();
         }

      });
      this.btnMarker = new GuiBookmarkButton(0, Textures.ICON_ADD_MARKER, I18n.format("gui.antiqueatlas.addMarker", new Object[0]));
      this.addChild(this.btnMarker).offsetGuiCoords(300, 14);
      this.btnMarker.addListener((button) -> {
         if (this.stack != null || !SettingsConfig.gameplay.itemNeeded) {
            if (this.state.is(this.PLACING_MARKER)) {
               this.selectedButton = null;
               this.state.switchTo(this.NORMAL);
            } else {
               this.selectedButton = button;
               this.state.switchTo(this.PLACING_MARKER);
               if (GuiScreen.isShiftKeyDown()) {
                  this.markerFinalizer.setMarkerData(this.player.getEntityWorld(), this.getAtlasID(), this.player.dimension, (int)this.player.posX, (int)this.player.posZ);
                  this.addChild(this.markerFinalizer);
                  this.blinkingIcon.setTexture(this.markerFinalizer.selectedType.getIcon(), 32, 32);
                  this.addChildBehind(this.markerFinalizer, this.blinkingIcon).setRelativeCoords(this.worldXToScreenX((int)this.player.posX) - this.getGuiX() - 16, this.worldZToScreenY((int)this.player.posZ) - this.getGuiY() - 16);
                  this.setInterceptKeyboard(true);
                  KeyBinding.unPressAllKeys();
                  this.selectedButton = null;
                  this.state.switchTo(this.NORMAL);
               }
            }
         }

      });
      this.btnDelMarker = new GuiBookmarkButton(2, Textures.ICON_DELETE_MARKER, I18n.format("gui.antiqueatlas.delMarker", new Object[0]));
      this.addChild(this.btnDelMarker).offsetGuiCoords(300, 33);
      this.btnDelMarker.addListener((button) -> {
         if (this.stack != null || !SettingsConfig.gameplay.itemNeeded) {
            if (this.state.is(this.DELETING_MARKER)) {
               this.selectedButton = null;
               this.state.switchTo(this.NORMAL);
            } else {
               this.selectedButton = button;
               this.state.switchTo(this.DELETING_MARKER);
            }
         }

      });
      this.btnShowMarkers = new GuiBookmarkButton(3, Textures.ICON_HIDE_MARKERS, I18n.format("gui.antiqueatlas.hideMarkers", new Object[0]));
      this.addChild(this.btnShowMarkers).offsetGuiCoords(300, 52);
      this.btnShowMarkers.addListener((button) -> {
         if (this.stack != null || !SettingsConfig.gameplay.itemNeeded) {
            this.selectedButton = null;
            this.state.switchTo(this.state.is(this.HIDING_MARKERS) ? this.NORMAL : this.HIDING_MARKERS);
         }

      });
      this.addChild(this.scaleBar).offsetGuiCoords(20, 198);
      this.scaleBar.setMapScale(1.0D);
      this.markerFinalizer.addListener(this.blinkingIcon);
      this.eraser.setTexture(Textures.ERASER, 12, 14, 2, 11);
   }

   public GuiAtlas prepareToOpen(ItemStack stack) {
      this.stack = stack;
      return this.prepareToOpen();
   }

   public GuiAtlas prepareToOpen() {
      this.player = Minecraft.getMinecraft().player;
      this.updateAtlasData();
      if (!this.followPlayer && SettingsConfig.gameplay.doSaveBrowsingPos) {
         this.loadSavedBrowsingPosition();
      }

      return this;
   }

   public void loadSavedBrowsingPosition() {
      this.setMapScale(this.biomeData.getBrowsingZoom());
      this.mapOffsetX = this.biomeData.getBrowsingX();
      this.mapOffsetY = this.biomeData.getBrowsingY();
      this.isDragging = false;
   }

   public void initGui() {
      super.initGui();
      if (!this.state.equals(this.EXPORTING_IMAGE)) {
         this.state.switchTo(this.NORMAL);
      }

      Keyboard.enableRepeatEvents(true);
      this.screenScale = (new ScaledResolution(this.mc)).getScaleFactor();
      this.setCentered();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseState) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseState);
      if (!this.state.is(this.EXPORTING_IMAGE)) {
         int mapX = (this.width - 276) / 2;
         int mapY = (this.height - 194) / 2;
         boolean isMouseOverMap = mouseX >= mapX && mouseX <= mapX + 276 && mouseY >= mapY && mouseY <= mapY + 194;
         if (!this.state.is(this.NORMAL) && !this.state.is(this.HIDING_MARKERS)) {
            int atlasID = this.getAtlasID();
            if (this.state.is(this.PLACING_MARKER) && isMouseOverMap && mouseState == 0) {
               this.markerFinalizer.setMarkerData(this.player.getEntityWorld(), atlasID, this.player.dimension, this.screenXToWorldX(mouseX), this.screenYToWorldZ(mouseY));
               this.addChild(this.markerFinalizer);
               this.blinkingIcon.setTexture(this.markerFinalizer.selectedType.getIcon(), 32, 32);
               this.addChildBehind(this.markerFinalizer, this.blinkingIcon).setRelativeCoords(mouseX - this.getGuiX() - 16, mouseY - this.getGuiY() - 16);
               this.setInterceptKeyboard(true);
               KeyBinding.unPressAllKeys();
            } else if (this.state.is(this.DELETING_MARKER) && this.hoveredMarker != null && !this.hoveredMarker.isGlobal() && isMouseOverMap && mouseState == 0) {
               AtlasAPI.markers.deleteMarker(this.player.getEntityWorld(), atlasID, this.hoveredMarker.getId());
            }

            this.state.switchTo(this.NORMAL);
         } else if (isMouseOverMap && this.selectedButton == null && (this.hoveredMarker == null || !MinecraftForge.EVENT_BUS.post(new MarkerClickedEvent(this.player, this.hoveredMarker, mouseState)))) {
            this.isDragging = true;
            this.dragMouseX = mouseX;
            this.dragMouseY = mouseY;
            this.dragMapOffsetX = this.mapOffsetX;
            this.dragMapOffsetY = this.mapOffsetY;
         }

      }
   }

   private void exportImage(int atlasID) {
      boolean showMarkers = !this.state.is(this.HIDING_MARKERS);
      this.state.switchTo(this.EXPORTING_IMAGE);
      ExportImageUtil.isExporting = true;
      File screenshot_folder = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
      if (!screenshot_folder.isDirectory()) {
         screenshot_folder.mkdir();
      }

      String outputname = "atlas-" + DATE_FORMAT.format(new Date());
      File file = new File(screenshot_folder, outputname + ".png");

      for(int i = 1; file.exists(); ++i) {
         file = new File(screenshot_folder, outputname + "_" + i + ".png");
      }

      if (file != null) {
         try {
            Log.info("Exporting image from Atlas #%d to file %s", atlasID, file.getAbsolutePath());
            ExportImageUtil.exportPngImage(this.biomeData, this.localMarkersData, file, showMarkers);
            Log.info("Finished exporting image");
            printSuccessToChat(file);
         } catch (AtlasTooLargeException var13) {
            Log.warn(var13, "Atlas is too large");
            printLocalizedMessageToChat("gui.antiqueatlas.export.atlasTooLarge");
            ExportImageUtil.isExporting = false;
         } catch (OutOfMemoryError var14) {
            Log.warn(var14, "Image is too large, trying to export in strips");

            try {
               ExportImageUtil.exportPngImageTooLarge(this.biomeData, this.localMarkersData, file, showMarkers);
            } catch (OutOfMemoryError var12) {
               int minX = (this.biomeData.getScope().minX - 1) * 16;
               int minY = (this.biomeData.getScope().minY - 1) * 16;
               int outWidth = (this.biomeData.getScope().maxX + 2) * 16 - minX;
               int outHeight = (this.biomeData.getScope().maxY + 2) * 16 - minY;
               Log.error(var12, "Image is STILL too large, how massive is this map?! Answer: (%dx%d)", outWidth, outHeight);
               printLocalizedMessageToChat("gui.antiqueatlas.export.tooLarge");
               ExportImageUtil.isExporting = false;
            }
         }
      }

      ExportImageUtil.isExporting = false;
      this.state.switchTo(showMarkers ? this.NORMAL : this.HIDING_MARKERS);
   }

   public void handleKeyboardInput() throws IOException {
      super.handleKeyboardInput();
      if (Keyboard.getEventKeyState()) {
         int key = Keyboard.getEventKey();
         if (key == 200) {
            this.navigateMap(0, 24);
         } else if (key == 208) {
            this.navigateMap(0, -24);
         } else if (key == 203) {
            this.navigateMap(24, 0);
         } else if (key == 205) {
            this.navigateMap(-24, 0);
         } else if (key != 78 && key != 13) {
            if (key != 74 && key != 12) {
               if (key == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
                  this.close();
               } else {
                  KeyBinding[] hotbarKeys = this.mc.gameSettings.keyBindsHotbar;

                  for(KeyBinding bind : hotbarKeys) {
                     if (key == bind.getKeyCode()) {
                        this.close();
                        break;
                     }
                  }
               }
            } else {
               this.setMapScale(this.mapScale / 2.0D);
            }
         } else {
            this.setMapScale(this.mapScale * 2.0D);
         }
      }

   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int wheelMove = Mouse.getEventDWheel();
      if (wheelMove != 0) {
         wheelMove = wheelMove > 0 ? 1 : -1;
         if (SettingsConfig.userInterface.doReverseWheelZoom) {
            wheelMove *= -1;
         }

         int mouseOffsetX = this.mc.displayWidth / this.screenScale / 2 - this.getMouseX();
         int mouseOffsetY = this.mc.displayHeight / this.screenScale / 2 - this.getMouseY();
         double newScale = this.mapScale * Math.pow(2.0D, (double)wheelMove);
         int addOffsetX = 0;
         int addOffsetY = 0;
         if (Math.abs(mouseOffsetX) < 138 && Math.abs(mouseOffsetY) < 97) {
            addOffsetX = mouseOffsetX * wheelMove;
            addOffsetY = mouseOffsetY * wheelMove;
            if (wheelMove > 0) {
               addOffsetX = (int)((double)addOffsetX * (this.mapScale / newScale));
               addOffsetY = (int)((double)addOffsetY * (this.mapScale / newScale));
            }
         }

         this.setMapScale(newScale, addOffsetX, addOffsetY);
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int mouseState) {
      super.mouseReleased(mouseX, mouseY, mouseState);
      if (mouseState != -1) {
         this.selectedButton = null;
         this.isDragging = false;
      }

   }

   protected void mouseClickMove(int mouseX, int mouseY, int lastMouseButton, long timeSinceMouseClick) {
      super.mouseClickMove(mouseX, mouseY, lastMouseButton, timeSinceMouseClick);
      if (this.isDragging) {
         this.followPlayer = false;
         this.btnPosition.setEnabled(true);
         this.mapOffsetX = this.dragMapOffsetX + mouseX - this.dragMouseX;
         this.mapOffsetY = this.dragMapOffsetY + mouseY - this.dragMouseY;
      }

   }

   public void updateScreen() {
      super.updateScreen();
      if (this.player != null) {
         if (this.followPlayer) {
            this.mapOffsetX = (int)(-this.player.posX * this.mapScale);
            this.mapOffsetY = (int)(-this.player.posZ * this.mapScale);
         }

         if (this.player.getEntityWorld().getTotalWorldTime() > this.timeButtonPressed + 8L) {
            this.navigateByButton(this.selectedButton);
         }

         this.updateAtlasData();
      }
   }

   private void updateAtlasData() {
      int atlasID = this.getAtlasID();
      this.biomeData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, this.player.getEntityWorld()).getDimensionData(this.player.dimension);
      MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, this.player.getEntityWorld());
      if (markersData != null) {
         this.localMarkersData = markersData.getMarkersDataInDimension(this.player.dimension);
      } else {
         this.localMarkersData = null;
      }

   }

   private void navigateByButton(GuiComponentButton btn) {
      if (btn != null) {
         if (btn.equals(this.btnUp)) {
            this.navigateMap(0, 24);
         } else if (btn.equals(this.btnDown)) {
            this.navigateMap(0, -24);
         } else if (btn.equals(this.btnLeft)) {
            this.navigateMap(24, 0);
         } else if (btn.equals(this.btnRight)) {
            this.navigateMap(-24, 0);
         }

      }
   }

   private void navigateMap(int dx, int dy) {
      this.mapOffsetX += dx;
      this.mapOffsetY += dy;
      this.followPlayer = false;
      this.btnPosition.setEnabled(true);
   }

   public void setMapScale(double scale) {
      this.setMapScale(scale, 0, 0);
   }

   private void setMapScale(double scale, int addOffsetX, int addOffsetY) {
      double oldScale = this.mapScale;
      this.mapScale = Math.min(Math.max(scale, SettingsConfig.userInterface.minScale), SettingsConfig.userInterface.maxScale);
      if (oldScale != this.mapScale) {
         if (this.mapScale >= 0.5D) {
            this.tileHalfSize = (int)Math.round(8.0D * this.mapScale);
            this.tile2ChunkScale = 1;
         } else {
            this.tileHalfSize = (int)Math.round(4.0D);
            this.tile2ChunkScale = (int)Math.round(0.5D / this.mapScale);
         }

         this.scaleBar.setMapScale(this.mapScale * 2.0D);
         this.mapOffsetX = (int)((double)(this.mapOffsetX + addOffsetX) * (this.mapScale / oldScale));
         this.mapOffsetY = (int)((double)(this.mapOffsetY + addOffsetY) * (this.mapScale / oldScale));
         this.dragMapOffsetX = (int)((double)this.dragMapOffsetX * (this.mapScale / oldScale));
         this.dragMapOffsetY = (int)((double)this.dragMapOffsetY * (this.mapScale / oldScale));
         this.scaleClipIndex = MathHelper.log2((int)(this.mapScale * 8192.0D)) + 1 - 13;
         this.zoomLevel = -this.scaleClipIndex + this.zoomLevelOne;
         this.scaleAlpha = 255;
         if (this.followPlayer && (addOffsetX != 0 || addOffsetY != 0)) {
            this.followPlayer = false;
            this.btnPosition.setEnabled(true);
         }

      }
   }

   public void drawScreen(int mouseX, int mouseY, float par3) {
      long currentMillis = System.currentTimeMillis();
      long deltaMillis = currentMillis - this.lastUpdateMillis;
      this.lastUpdateMillis = currentMillis;
      if (SettingsConfig.performance.debugRender) {
         this.renderTimes[this.renderTimesIndex++] = System.currentTimeMillis();
         if (this.renderTimesIndex == this.renderTimes.length) {
            this.renderTimesIndex = 0;
            double elapsed = 0.0D;

            for(int i = 0; i < this.renderTimes.length - 1; ++i) {
               elapsed += (double)(this.renderTimes[i + 1] - this.renderTimes[i]);
            }

            System.out.printf("GuiAtlas avg. render time: %.3f\n", elapsed / (double)this.renderTimes.length);
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.alphaFunc(516, 0.0F);
      AtlasRenderHelper.drawFullTexture(Textures.BOOK, (double)this.getGuiX(), (double)this.getGuiY(), 310, 218);
      if ((this.stack != null || !SettingsConfig.gameplay.itemNeeded) && this.biomeData != null) {
         if (this.state.is(this.DELETING_MARKER)) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
         }

         GL11.glEnable(3089);
         GL11.glScissor((this.getGuiX() + 17) * this.screenScale, this.mc.displayHeight - (this.getGuiY() + 11 + 194) * this.screenScale, 276 * this.screenScale, 194 * this.screenScale);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         int mapStartX = MathUtil.roundToBase((int)Math.floor(-(138.0D + (double)this.mapOffsetX + (double)(2 * this.tileHalfSize)) / this.mapScale / 16.0D), this.tile2ChunkScale);
         int mapStartZ = MathUtil.roundToBase((int)Math.floor(-(97.0D + (double)this.mapOffsetY + (double)(2 * this.tileHalfSize)) / this.mapScale / 16.0D), this.tile2ChunkScale);
         int mapEndX = MathUtil.roundToBase((int)Math.ceil((138.0D - (double)this.mapOffsetX + (double)(2 * this.tileHalfSize)) / this.mapScale / 16.0D), this.tile2ChunkScale);
         int mapEndZ = MathUtil.roundToBase((int)Math.ceil((97.0D - (double)this.mapOffsetY + (double)(2 * this.tileHalfSize)) / this.mapScale / 16.0D), this.tile2ChunkScale);
         int mapStartScreenX = this.getGuiX() + 155 + (int)((double)(mapStartX << 4) * this.mapScale) + this.mapOffsetX;
         int mapStartScreenY = this.getGuiY() + 109 + (int)((double)(mapStartZ << 4) * this.mapScale) + this.mapOffsetY;
         TileRenderIterator iter = new TileRenderIterator(this.biomeData);
         iter.setScope((new Rect()).setOrigin(mapStartX, mapStartZ).set(mapStartX, mapStartZ, mapEndX, mapEndZ));
         iter.setStep(this.tile2ChunkScale);

         while(iter.hasNext()) {
            for(SubTile subtile : iter.next()) {
               if (subtile != null && subtile.tile != null) {
                  AtlasRenderHelper.drawAutotileCorner(BiomeTextureMap.instance().getTexture(subtile.tile), mapStartScreenX + subtile.x * this.tileHalfSize, mapStartScreenY + subtile.y * this.tileHalfSize, (double)subtile.getTextureU(), (double)subtile.getTextureV(), this.tileHalfSize);
               }
            }
         }

         int markersStartX = MathUtil.roundToBase(mapStartX, 8) / 8 - 1;
         int markersStartZ = MathUtil.roundToBase(mapStartZ, 8) / 8 - 1;
         int markersEndX = MathUtil.roundToBase(mapEndX, 8) / 8 + 1;
         int markersEndZ = MathUtil.roundToBase(mapEndZ, 8) / 8 + 1;
         double iconScale = this.getIconScale();
         if (this.localMarkersData != null) {
            for(int x = markersStartX; x <= markersEndX; ++x) {
               for(int z = markersStartZ; z <= markersEndZ; ++z) {
                  List<Marker> markers = this.localMarkersData.getMarkersAtChunk(x, z);
                  if (markers != null) {
                     for(Marker marker : markers) {
                        this.renderMarker(marker, iconScale);
                     }
                  }
               }
            }
         }

         GL11.glDisable(3089);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, (double)this.getGuiX(), (double)this.getGuiY(), 310, 218);
         this.renderScaleOverlay(deltaMillis);
         iconScale = this.getIconScale();
         if (!this.state.is(this.HIDING_MARKERS)) {
            int playerOffsetX = (int)(this.player.posX * this.mapScale) + this.mapOffsetX;
            int playerOffsetZ = (int)(this.player.posZ * this.mapScale) + this.mapOffsetY;
            if (playerOffsetX < -138) {
               playerOffsetX = -138;
            }

            if (playerOffsetX > 138) {
               playerOffsetX = 138;
            }

            if (playerOffsetZ < -97) {
               playerOffsetZ = -97;
            }

            if (playerOffsetZ > 95) {
               playerOffsetZ = 95;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, this.state.is(this.PLACING_MARKER) ? 0.5F : 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(this.getGuiX() + 155 + playerOffsetX), (float)(this.getGuiY() + 109 + playerOffsetZ), 0.0F);
            float playerRotation = (float)Math.round(this.player.rotationYaw / 360.0F * 16.0F) / 16.0F * 360.0F;
            GlStateManager.rotate(180.0F + playerRotation, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-3.0D * iconScale, -4.0D * iconScale, 0.0D);
            AtlasRenderHelper.drawFullTexture(Textures.PLAYER, 0.0D, 0.0D, (int)Math.round(7.0D * iconScale), (int)Math.round(8.0D * iconScale));
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         super.drawScreen(mouseX, mouseY, par3);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         if (this.state.is(this.PLACING_MARKER)) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
            this.markerFinalizer.selectedType.calculateMip(iconScale, this.mapScale, (double)this.screenScale);
            MarkerRenderInfo renderInfo = this.markerFinalizer.selectedType.getRenderInfo(iconScale, this.mapScale, (double)this.screenScale);
            this.markerFinalizer.selectedType.resetMip();
            AtlasRenderHelper.drawFullTexture(renderInfo.tex, (double)mouseX + renderInfo.x, (double)mouseY + renderInfo.y, renderInfo.width, renderInfo.height);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         if (this.state.is(this.EXPORTING_IMAGE)) {
            this.drawDefaultBackground();
            this.progressBar.draw((this.width - 100) / 2, this.height / 2 - 34);
         }

      }
   }

   private void renderScaleOverlay(long deltaMillis) {
      if (this.scaleAlpha > 0) {
         GlStateManager.translate((float)(this.getGuiX() + 310 - 13), (float)(this.getGuiY() + 12), 0.0F);
         String text = "x";
         int textWidth;
         int xWidth = textWidth = this.fontRenderer.getStringWidth(text);
         ++xWidth;
         this.fontRenderer.drawString(text, -textWidth, 0, this.scaleAlpha << 24);
         text = this.zoomNames[this.zoomLevel];
         if (text.contains("/")) {
            String[] parts = text.split("/");
            text = parts[0];
            int centerXtranslate = Math.max(this.fontRenderer.getStringWidth(parts[0]), this.fontRenderer.getStringWidth(parts[1])) / 2;
            GlStateManager.translate((float)(-xWidth - centerXtranslate), (float)(-this.fontRenderer.FONT_HEIGHT / 2), 0.0F);
            GlStateManager.disableTexture2D();
            Tessellator t = Tessellator.getInstance();
            WorldRenderer vb = t.getBuffer();
            vb.begin(7, DefaultVertexFormats.POSITION);
            vb.pos((double)centerXtranslate, (double)(this.fontRenderer.FONT_HEIGHT - 1), 0.0D).endVertex();
            vb.pos((double)(-centerXtranslate - 1), (double)(this.fontRenderer.FONT_HEIGHT - 1), 0.0D).endVertex();
            vb.pos((double)(-centerXtranslate - 1), (double)this.fontRenderer.FONT_HEIGHT, 0.0D).endVertex();
            vb.pos((double)centerXtranslate, (double)this.fontRenderer.FONT_HEIGHT, 0.0D).endVertex();
            t.draw();
            GlStateManager.enableTexture2D();
            textWidth = this.fontRenderer.getStringWidth(text);
            this.fontRenderer.drawString(text, -textWidth / 2, 0, this.scaleAlpha << 24);
            text = parts[1];
            GlStateManager.translate(0.0F, (float)(this.fontRenderer.FONT_HEIGHT + 1), 0.0F);
            textWidth = this.fontRenderer.getStringWidth(text);
            this.fontRenderer.drawString(text, -textWidth / 2, 0, this.scaleAlpha << 24);
            GlStateManager.translate((float)(xWidth + centerXtranslate), (float)(-this.fontRenderer.FONT_HEIGHT / 2 - 2), 0.0F);
         } else {
            textWidth = this.fontRenderer.getStringWidth(text);
            this.fontRenderer.drawString(text, -textWidth - xWidth + 1, 1, this.scaleAlpha << 24);
         }

         GlStateManager.translate((float)(-(this.getGuiX() + 310 - 13)), (float)(-(this.getGuiY() + 12)), 0.0F);
         int deltaScaleAlpha = (int)((double)deltaMillis * 0.256D);
         if (deltaScaleAlpha == 0) {
            deltaScaleAlpha = 1;
         }

         this.scaleAlpha -= 20 * deltaScaleAlpha;
         if (this.scaleAlpha < 0) {
            this.scaleAlpha = 0;
         }
      }

   }

   private void renderMarker(Marker marker, double scale) {
      MarkerType type = MarkerRegistry.find(marker.getType());
      if (type != null) {
         if (!type.shouldHide(this.state.is(this.HIDING_MARKERS), this.scaleClipIndex)) {
            int markerX = this.worldXToScreenX(marker.getX());
            int markerY = this.worldZToScreenY(marker.getZ());
            if (marker.isVisibleAhead() || this.biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
               type.calculateMip(scale, this.mapScale, (double)this.screenScale);
               MarkerRenderInfo info = type.getRenderInfo(scale, this.mapScale, (double)this.screenScale);
               boolean mouseIsOverMarker = type.shouldHover(((double)this.getMouseX() - ((double)markerX + info.x)) / (double)info.width, ((double)this.getMouseY() - ((double)markerY + info.y)) / (double)info.height);
               type.resetMip();
               if (mouseIsOverMarker) {
                  GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
                  this.hoveredMarker = marker;
                  MinecraftForge.EVENT_BUS.post(new MarkerHoveredEvent(this.player, marker));
               } else {
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  if (this.hoveredMarker == marker) {
                     this.hoveredMarker = null;
                  }
               }

               if (this.state.is(this.PLACING_MARKER)) {
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
               } else if (this.state.is(this.DELETING_MARKER) && marker.isGlobal()) {
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
               } else {
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               }

               if (SettingsConfig.performance.debugRender) {
                  System.out.println("Rendering Marker: " + info.tex);
               }

               AtlasRenderHelper.drawFullTexture(info.tex, (double)markerX + info.x, (double)markerY + info.y, info.width, info.height);
               if (this.isMouseOver && mouseIsOverMarker && marker.getLabel().length() > 0) {
                  this.drawTooltip(Collections.singletonList(marker.getLocalizedLabel()), this.mc.fontRenderer);
               }

            }
         }
      }
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void onGuiClosed() {
      super.onGuiClosed();
      this.removeChild(this.markerFinalizer);
      this.removeChild(this.blinkingIcon);
      Keyboard.enableRepeatEvents(false);
      this.biomeData.setBrowsingPosition(this.mapOffsetX, this.mapOffsetY, this.mapScale);
      PacketDispatcher.sendToServer(new BrowsingPositionPacket(this.getAtlasID(), this.player.dimension, this.mapOffsetX, this.mapOffsetY, this.mapScale));
   }

   private int screenXToWorldX(int mouseX) {
      return (int)Math.round((double)(mouseX - this.width / 2 - this.mapOffsetX) / this.mapScale);
   }

   private int screenYToWorldZ(int mouseY) {
      return (int)Math.round((double)(mouseY - this.height / 2 - this.mapOffsetY) / this.mapScale);
   }

   private int worldXToScreenX(int x) {
      return (int)Math.round((double)x * this.mapScale + (double)(this.width / 2) + (double)this.mapOffsetX);
   }

   private int worldZToScreenY(int z) {
      return (int)Math.round((double)z * this.mapScale + (double)(this.height / 2) + (double)this.mapOffsetY);
   }

   protected void onChildClosed(GuiComponent child) {
      if (child.equals(this.markerFinalizer)) {
         this.setInterceptKeyboard(true);
         this.removeChild(this.blinkingIcon);
      }

   }

   public void updateL18n() {
      this.btnExportPng.setTitle(I18n.format("gui.antiqueatlas.exportImage", new Object[0]));
      this.btnMarker.setTitle(I18n.format("gui.antiqueatlas.addMarker", new Object[0]));
   }

   private double getIconScale() {
      return SettingsConfig.userInterface.doScaleMarkers ? (this.mapScale < 0.5D ? 0.5D : (this.mapScale > 1.0D ? 2.0D : 1.0D)) : 1.0D;
   }

   private int getAtlasID() {
      return SettingsConfig.gameplay.itemNeeded ? this.stack.getItemDamage() : this.player.getUniqueID().hashCode();
   }

   private static void printSuccessToChat(File file) {
      ITextComponent text = new TextComponentString(file.getName());

      String path;
      try {
         path = file.getCanonicalFile().getAbsolutePath();
      } catch (IOException var4) {
         path = file.getAbsolutePath();
      }

      text.getStyle().setClickEvent(new ClickEvent(Action.OPEN_FILE, path));
      text.getStyle().setUnderlined(Boolean.valueOf(true));
      TextComponentTranslation post = new TextComponentTranslation("gui.antiqueatlas.export.success", new Object[]{text});
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(post);
   }

   private static void printLocalizedMessageToChat(String translationKey) {
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(translationKey, new Object[0]));
   }
}
