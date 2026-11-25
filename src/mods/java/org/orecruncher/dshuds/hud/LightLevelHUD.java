package org.orecruncher.dshuds.hud;

import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dshuds.Environment;
import org.orecruncher.dshuds.ModBase;
import org.orecruncher.dshuds.ModOptions;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.gfx.OpenGlState;
import org.orecruncher.lib.gfx.OpenGlUtil;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public final class LightLevelHUD extends GuiOverlay {
   private static FontRenderer font;
   private static LightLevelTextureSheet sheet;
   public static boolean showHUD = false;
   private static final int ALLOCATION_SIZE = 2048;
   private static final ObjectArray<LightLevelHUD.LightCoord> lightLevels = new ObjectArray(2048);
   private static final MutableBlockPos mutable = new MutableBlockPos();
   private static int nextCoord = 0;
   private static final Frustum frustum;

   public LightLevelHUD() {
   }

   private static LightLevelHUD.LightCoord nextCoord() {
      if (nextCoord == lightLevels.size()) {
         lightLevels.add(new LightLevelHUD.LightCoord());
      }

      return (LightLevelHUD.LightCoord)lightLevels.get(nextCoord++);
   }

   protected static boolean inFrustum(double x, double y, double z) {
      return frustum.isBoxInFrustum(x, y, z, x, y, z);
   }

   protected static boolean renderLightLevel(@Nonnull IBlockState state, @Nonnull IBlockState below) {
      Material stateMaterial = state.getMaterial();
      Material belowMaterial = below.getMaterial();
      return !stateMaterial.isSolid() && !stateMaterial.isLiquid() && belowMaterial.isSolid();
   }

   protected static boolean canMobSpawn(@Nonnull BlockPos pos) {
      return WorldEntitySpawner.canCreatureTypeSpawnAtLocation(SpawnPlacementType.ON_GROUND, Environment.getWorld(), pos);
   }

   protected static float heightAdjustment(@Nonnull IBlockState state, @Nonnull IBlockState below, @Nonnull BlockPos pos) {
      if (state.getBlock() == Blocks.AIR) {
         AxisAlignedBB box = below.getCollisionBoundingBox(Environment.getWorld(), pos.down());
         return box == null ? 0.0F : (float)box.maxY - 1.0F;
      } else {
         AxisAlignedBB box = state.getCollisionBoundingBox(Environment.getWorld(), pos);
         if (box == null) {
            return 0.0F;
         } else {
            float adjust = (float)box.maxY;
            return state.getBlock() == Blocks.SNOW_LAYER ? adjust + 0.125F : adjust;
         }
      }
   }

   protected static void updateLightInfo(@Nonnull RenderManager manager, double x, double y, double z) {
      font = Minecraft.getMinecraft().fontRenderer;
      boolean isThirdPerson = manager.options.thirdPersonView == 2;
      EntityPlayer player = Environment.getPlayer();
      Vec3d lookVec = player.getLookVec();
      double fX;
      double fY;
      double fZ;
      if (isThirdPerson) {
         fX = x + lookVec.x * 2.0D;
         fY = y + lookVec.y * 2.0D;
         fZ = z + lookVec.z * 2.0D;
      } else {
         fX = x - lookVec.x * 2.0D;
         fY = y - lookVec.y * 2.0D;
         fZ = z - lookVec.z * 2.0D;
      }

      frustum.setPosition(fX, fY, fZ);
      nextCoord = 0;
      LightLevelHUD.ColorSet colors = LightLevelHUD.ColorSet.getStyle(ModOptions.lightLevelHUD.colors);
      LightLevelHUD.Mode displayMode = LightLevelHUD.Mode.getMode(ModOptions.lightLevelHUD.displayMode);
      int skyLightSub = Environment.getWorld().calculateSkylightSubtracted(1.0F);
      int rangeXZ = ModOptions.lightLevelHUD.range * 2 + 1;
      int rangeY = ModOptions.lightLevelHUD.range + 1;
      int originX = MathStuff.floor(x) - rangeXZ / 2;
      int originZ = MathStuff.floor(z) - rangeXZ / 2;
      int originY = MathStuff.floor(y) - (rangeY - 3);
      IBlockAccessEx blocks = ClientChunkCache.instance();

      for(int dX = 0; dX < rangeXZ; ++dX) {
         for(int dZ = 0; dZ < rangeXZ; ++dZ) {
            int trueX = originX + dX;
            int trueZ = originZ + dZ;
            if (!blocks.isAvailable(trueX, trueZ)) {
               return;
            }

            IBlockState lastState = null;

            for(int dY = 0; dY < rangeY; ++dY) {
               int trueY = originY + dY;
               if (trueY >= 1 && inFrustum((double)trueX, (double)trueY, (double)trueZ)) {
                  IBlockState state = blocks.getBlockState(trueX, trueY, trueZ);
                  if (lastState == null) {
                     lastState = blocks.getBlockState(trueX, trueY - 1, trueZ);
                  }

                  if (renderLightLevel(state, lastState)) {
                     mutable.setPos(trueX, trueY, trueZ);
                     boolean mobSpawn = canMobSpawn(mutable);
                     if (mobSpawn || !ModOptions.lightLevelHUD.hideSafe) {
                        int blockLight = blocks.getLightFor(EnumSkyBlock.BLOCK, mutable);
                        int skyLight = blocks.getLightFor(EnumSkyBlock.SKY, mutable) - skyLightSub;
                        int effective = Math.max(blockLight, skyLight);
                        int result = displayMode == LightLevelHUD.Mode.BLOCK_SKY ? effective : blockLight;
                        Color color = colors.safe;
                        if (!mobSpawn) {
                           color = colors.noSpawn;
                        } else if (blockLight <= ModOptions.lightLevelHUD.spawnThreshold) {
                           if (effective > ModOptions.lightLevelHUD.spawnThreshold) {
                              color = colors.caution;
                           } else {
                              color = colors.hazard;
                           }
                        }

                        if (color != colors.safe || !ModOptions.lightLevelHUD.hideSafe) {
                           LightLevelHUD.LightCoord coord = nextCoord();
                           coord.x = trueX;
                           coord.y = (double)((float)trueY + heightAdjustment(state, lastState, mutable));
                           coord.z = trueZ;
                           coord.lightLevel = result;
                           coord.color = color;
                        }
                     }
                  }

                  lastState = state;
               }
            }
         }
      }

   }

   public void doTick(int tickRef) {
      if (showHUD && tickRef != 0 && tickRef % 3 == 0) {
         RenderManager manager = Minecraft.getMinecraft().getRenderManager();
         updateLightInfo(manager, manager.viewerPosX, manager.viewerPosY, manager.viewerPosZ);
      }
   }

   @SubscribeEvent
   public static void doRender(@Nonnull RenderWorldLastEvent event) {
      if (showHUD && nextCoord != 0) {
         EntityPlayer player = Environment.getPlayer();
         if (player != null) {
            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            OpenGlState glState = OpenGlState.push();
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            if (useOldRenderMethod()) {
               drawStringRender(player, manager);
            } else {
               textureRender(player, manager);
            }

            OpenGlState.pop(glState);
         }
      }
   }

   private static void drawStringRender(EntityPlayer player, RenderManager manager) {
      boolean thirdPerson = manager.options.thirdPersonView == 2;
      EnumFacing playerFacing = player.getHorizontalFacing();
      if (thirdPerson) {
         playerFacing = playerFacing.getOpposite();
      }

      if (playerFacing == EnumFacing.SOUTH || playerFacing == EnumFacing.NORTH) {
         playerFacing = playerFacing.getOpposite();
      }

      float rotationAngle = playerFacing.getOpposite().getHorizontalAngle();

      for(int i = 0; i < nextCoord; ++i) {
         LightLevelHUD.LightCoord coord = (LightLevelHUD.LightCoord)lightLevels.get(i);
         double x = (double)coord.x - manager.viewerPosX;
         double y = coord.y - manager.viewerPosY;
         double z = (double)coord.z - manager.viewerPosZ;
         String text = String.valueOf(coord.lightLevel);
         int margin = -(font.getStringWidth(text) + 1) / 2;
         double scale = 0.08D;
         GlStateManager.pushMatrix();
         GlStateManager.translate(x + 0.5D, y, z + 0.5D);
         GlStateManager.rotate(rotationAngle, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.05D, 5.0E-4D, 0.3D);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-0.08D, -0.08D, 0.08D);
         GlStateManager.translate(0.3F, 0.3F, 0.0F);
         font.drawString(text, (float)margin, 0.0F, Color.BLACK.rgbWithAlpha(0.99F), false);
         GlStateManager.translate(-0.3F, -0.3F, -0.001F);
         font.drawString(text, (float)margin, 0.0F, coord.color.rgbWithAlpha(0.99F), false);
         GlStateManager.popMatrix();
      }

   }

   private static void textureRender(EntityPlayer player, RenderManager manager) {
      boolean isThirdPerson = manager.options.thirdPersonView == 2;
      EnumFacing playerFacing = player.getHorizontalFacing();
      if (isThirdPerson) {
         playerFacing = playerFacing.getOpposite();
      }

      float rotationAngle = playerFacing.getOpposite().getHorizontalAngle();
      sheet.bindTexture();
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();

      for(int i = 0; i < nextCoord; ++i) {
         LightLevelHUD.LightCoord coord = (LightLevelHUD.LightCoord)lightLevels.get(i);
         double x = (double)coord.x - manager.viewerPosX;
         double y = coord.y - manager.viewerPosY;
         double z = (double)coord.z - manager.viewerPosZ;
         Vec2f U = sheet.getMinMaxU(coord.lightLevel);
         Vec2f V = sheet.getMinMaxV(coord.lightLevel);
         GlStateManager.pushMatrix();
         GlStateManager.translate(x + 0.5D, y, z + 0.5D);
         GlStateManager.rotate(-rotationAngle, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.5D, 5.0E-4D, -0.5D);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         float red = coord.color.red;
         float green = coord.color.green;
         float blue = coord.color.blue;
         float alpha = 1.0F;
         renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         renderer.pos(0.0D, 1.0D, 0.0D).tex((double)U.x, (double)V.x).color(red, green, blue, 1.0F).endVertex();
         renderer.pos(1.0D, 1.0D, 0.0D).tex((double)U.y, (double)V.x).color(red, green, blue, 1.0F).endVertex();
         renderer.pos(1.0D, 0.0D, 0.0D).tex((double)U.y, (double)V.y).color(red, green, blue, 1.0F).endVertex();
         renderer.pos(0.0D, 0.0D, 0.0D).tex((double)U.x, (double)V.y).color(red, green, blue, 1.0F).endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.popMatrix();
      }

   }

   private static boolean useOldRenderMethod() {
      return !OpenGlUtil.areFrameBuffersSafe();
   }

   public static void initialize() {
      if (useOldRenderMethod()) {
         ModBase.log().info("Either OptiFine is installed or Framebuffers are disabled", new Object[0]);
         ModBase.log().info("Using drawString method for light level HUD render", new Object[0]);
         if (sheet != null) {
            sheet.release();
            sheet = null;
         }
      } else {
         ModBase.log().info("Using cached texture method for light level HUD render", new Object[0]);
         if (sheet == null) {
            sheet = new LightLevelTextureSheet();
         }

         sheet.updateTexture();
      }

      MinecraftForge.EVENT_BUS.register(LightLevelHUD.class);
   }

   static {
      for(int i = 0; i < 2048; ++i) {
         lightLevels.add(new LightLevelHUD.LightCoord());
      }

      frustum = new Frustum();
   }

   private static enum ColorSet {
      BRIGHT(Color.MC_GREEN, Color.MC_YELLOW, Color.MC_RED, Color.MC_DARKAQUA),
      DARK(Color.MC_DARKGREEN, Color.MC_GOLD, Color.MC_DARKRED, Color.MC_DARKBLUE);

      public static final float ALPHA = 1.0F;
      public final Color safe;
      public final Color caution;
      public final Color hazard;
      public final Color noSpawn;

      private ColorSet(@Nonnull Color safe, @Nonnull Color caution, @Nonnull Color hazard, @Nonnull Color noSpawn) {
         this.safe = safe;
         this.caution = caution;
         this.hazard = hazard;
         this.noSpawn = noSpawn;
      }

      public static LightLevelHUD.ColorSet getStyle(int v) {
         return v >= values().length ? BRIGHT : values()[v];
      }
   }

   private static final class LightCoord {
      public int x;
      public double y;
      public int z;
      public int lightLevel;
      public Color color;

      private LightCoord() {
      }
   }

   public static enum Mode {
      BLOCK,
      BLOCK_SKY;

      private Mode() {
      }

      public static LightLevelHUD.Mode cycle(LightLevelHUD.Mode mode) {
         int next = mode.ordinal() + 1;
         if (next >= values().length) {
            next = 0;
         }

         return values()[next];
      }

      @Nonnull
      public static LightLevelHUD.Mode getMode(int v) {
         return v >= values().length ? BLOCK : values()[v];
      }
   }
}
