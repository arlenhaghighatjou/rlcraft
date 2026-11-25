package net.mcft.copy.backpacks.client.gui;

import java.io.File;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.profiler.Profiler;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class GuiEntityRender extends GuiElementBase {
   private static final int RESERVED_SPACE = 16;
   private Entity _entity;
   private float _yawDefault;
   private float _yaw;
   private float _yawMin;
   private float _yawMax;
   private float _pitchDefault;
   private float _pitch;
   private float _pitchMin;
   private float _pitchMax;
   private float _centerX;
   private float _centerY;
   private float _zoom;
   private int _colorBackground;
   private int _colorBorder;

   public GuiEntityRender(Entity entity) {
      this(80, 140, entity);
   }

   public GuiEntityRender(int width, int height) {
      this(width, height, (Entity)null);
   }

   public GuiEntityRender(int width, int height, Entity entity) {
      this(0, 0, width, height, entity);
   }

   public GuiEntityRender(int x, int y, int width, int height, Entity entity) {
      this._entity = null;
      this._yawDefault = 0.0F;
      this._yaw = 0.0F;
      this._yawMin = -180.0F;
      this._yawMax = 180.0F;
      this._pitchDefault = 0.0F;
      this._pitch = 15.0F;
      this._pitchMin = -25.0F;
      this._pitchMax = 25.0F;
      this._centerX = 0.5F;
      this._centerY = 0.5F;
      this._zoom = 1.0F;
      this._colorBackground = GuiElementBase.Color.BLACK;
      this._colorBorder = -6250336;
      this.setPosition(x, y);
      this.setSize(width, height);
      this.setEntity(entity);
   }

   public GuiEntityRender(Class<? extends Entity> entityClass) {
      this(80, 140, entityClass);
   }

   public GuiEntityRender(int width, int height, Class<? extends Entity> entityClass) {
      this(0, 0, width, height, entityClass);
   }

   public GuiEntityRender(int x, int y, int width, int height, Class<? extends Entity> entityClass) {
      this._entity = null;
      this._yawDefault = 0.0F;
      this._yaw = 0.0F;
      this._yawMin = -180.0F;
      this._yawMax = 180.0F;
      this._pitchDefault = 0.0F;
      this._pitch = 15.0F;
      this._pitchMin = -25.0F;
      this._pitchMax = 25.0F;
      this._centerX = 0.5F;
      this._centerY = 0.5F;
      this._zoom = 1.0F;
      this._colorBackground = GuiElementBase.Color.BLACK;
      this._colorBorder = -6250336;
      this.setPosition(x, y);
      this.setSize(width, height);
      this.setEntity(entityClass);
   }

   public Entity getEntity() {
      return this._entity;
   }

   public void setEntity(Entity value) {
      this._entity = value;
   }

   public void setEntity(Class<? extends Entity> value) {
      try {
         this.setEntity(value != null ? (Entity)value.getConstructor(World.class).newInstance(GuiEntityRender.DummyWorld.INSTANCE) : null);
      } catch (ReflectiveOperationException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void setYaw(float value, float min, float max) {
      this.setYaw(value);
      this.setYawControl(min, max);
   }

   public void setYaw(float value) {
      this._yawDefault = value;
   }

   public void setYawControl(float min, float max) {
      this._yawMin = min;
      this._yawMax = max;
   }

   public void setPitch(float value, float min, float max) {
      this.setPitch(value);
      this.setPitchControl(min, max);
   }

   public void setPitch(float value) {
      this._pitchDefault = value;
   }

   public void setPitchControl(float min, float max) {
      this._pitchMin = min;
      this._pitchMax = max;
   }

   public void setCenter(float x, float y) {
      this._centerX = x;
      this._centerY = y;
   }

   public void setZoom(float value) {
      this._zoom = value;
   }

   public void setBackgroundColor(int value) {
      this._colorBackground = value;
   }

   public void setBorderColor(int value) {
      this._colorBorder = value;
   }

   public boolean canDrag() {
      return true;
   }

   public void onDragged(int mouseX, int mouseY, int deltaX, int deltaY, int startX, int startY) {
      this._yaw += (float)(deltaX * 2);
      this._pitch += (float)deltaY;
      boolean yawWrap = this._yawMax - this._yawMin >= 360.0F;
      boolean pitchWrap = this._pitchMax - this._pitchMin >= 360.0F;
      if (this._yaw < this._yawMin) {
         this._yaw = yawWrap ? this._yaw + 360.0F : this._yawMin;
      }

      if (this._yaw > this._yawMax) {
         this._yaw = yawWrap ? this._yaw - 360.0F : this._yawMax;
      }

      if (this._pitch < this._pitchMin) {
         this._pitch = pitchWrap ? this._pitch + 360.0F : this._pitchMin;
      }

      if (this._pitch > this._pitchMax) {
         this._pitch = pitchWrap ? this._pitch - 360.0F : this._pitchMax;
      }

   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      int w = this.getWidth();
      int h = this.getHeight();
      enableBlendAlphaStuffs();
      setRenderColorARGB(this._colorBackground);
      drawRect(1, 1, w - 2, h - 2);
      setRenderColorARGB(this._colorBorder);
      drawOutline(0, 0, w, h);
      disableBlendAlphaStuffs();
      Entity entity = this.getEntity();
      if (entity != null) {
         AxisAlignedBB bbox = entity.getRenderBoundingBox();
         float entityWidth = (float)(bbox.maxX - bbox.minX);
         float entityHeight = (float)(bbox.maxY - bbox.minY);
         float scale = Math.min((float)(w - 16) / entityWidth, (float)(h - 16) / entityHeight) * this._zoom;
         float yaw = this._yawDefault + this._yaw;
         float pitch = this._pitchDefault + this._pitch;
         this.getContext().pushScissor(this, 1, 1, w - 2, h - 2);
         if (entity.getEntityWorld() == null) {
            entity.setWorld(GuiEntityRender.DummyWorld.INSTANCE);
         }

         if (getMC().player == null) {
            World world = entity.getEntityWorld();
            if (!(world instanceof GuiEntityRender.DummyWorld)) {
               return;
            }

            getMC().player = ((GuiEntityRender.DummyWorld)world).player;
            getMC().player.setWorld(world);
         }

         setRenderColorARGB(GuiElementBase.Color.WHITE);
         GlStateManager.enableDepth();
         GlStateManager.enableColorMaterial();
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)w * this._centerX, (float)h * this._centerY, 100.0F);
         GlStateManager.scale(-scale, scale, scale);
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -(entityHeight / 2.0F), 0.0F);
         GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
         RenderHelper.enableStandardItemLighting();
         RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
         rendermanager.setPlayerViewY(180.0F);
         rendermanager.setRenderShadow(false);
         rendermanager.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
         rendermanager.setRenderShadow(true);
         GlStateManager.popMatrix();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.disableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.disableDepth();
         if (getMC().player instanceof GuiEntityRender.DummyPlayerSP) {
            getMC().player = null;
         }

         this.getContext().popScissor();
      }
   }

   public static class DummyPlayerSP extends EntityPlayerSP {
      public DummyPlayerSP(World world) {
         super(GuiElementBase.getMC(), world, new NetHandlerPlayClient(GuiElementBase.getMC(), (GuiScreen)null, (NetworkManager)null, GuiElementBase.getMC().getSession().getProfile()), (StatisticsManager)null, (RecipeBook)null);
      }
   }

   public static class DummySaveHandler implements ISaveHandler {
      public DummySaveHandler() {
      }

      public WorldInfo loadWorldInfo() {
         return null;
      }

      public void checkSessionLock() throws MinecraftException {
      }

      public IChunkLoader getChunkLoader(WorldProvider provider) {
         return null;
      }

      public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
      }

      public void saveWorldInfo(WorldInfo worldInformation) {
      }

      public IPlayerFileData getPlayerNBTManager() {
         return null;
      }

      public void flush() {
      }

      public File getWorldDirectory() {
         return null;
      }

      public File getMapFileFromName(String mapName) {
         return null;
      }

      public TemplateManager getStructureTemplateManager() {
         return null;
      }
   }

   public static class DummyWorld extends World {
      public static final GuiEntityRender.DummyWorld INSTANCE = new GuiEntityRender.DummyWorld();
      public final GuiEntityRender.DummyPlayerSP player;

      public DummyWorld() {
         this(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT));
      }

      public DummyWorld(WorldSettings settings) {
         super(new GuiEntityRender.DummySaveHandler(), new WorldInfo(settings, "DummyWorld"), new WorldProviderSurface(), (Profiler)null, true);
         this.provider.setWorld(this);
         this.player = new GuiEntityRender.DummyPlayerSP(this);
      }

      protected IChunkProvider createChunkProvider() {
         return null;
      }

      protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
         return false;
      }

      public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
         return null;
      }

      public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
         return false;
      }

      public Biome getBiomeForCoordsBody(BlockPos pos) {
         return Biomes.DEFAULT;
      }

      public boolean canSeeSky(BlockPos pos) {
         return true;
      }

      public int getLight(BlockPos pos) {
         return 15;
      }

      public int getLight(BlockPos pos, boolean checkNeighbors) {
         return 15;
      }

      public int getLightFor(EnumSkyBlock type, BlockPos pos) {
         return 15;
      }

      public IBlockState getBlockState(BlockPos pos) {
         return Blocks.AIR.getDefaultState();
      }
   }
}
