package melonslise.locks.client.event;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import melonslise.locks.client.util.LocksClientUtil;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ISelection;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksItems;
import melonslise.locks.common.util.AttachFace;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lockable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(
   modid = "locks",
   value = {Side.CLIENT}
)
public final class LocksClientEvents {
   private LocksClientEvents() {
   }

   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      for(Item item : LocksItems.ITEMS) {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      }

   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (event.phase != Phase.END && mc.world != null && !mc.isGamePaused()) {
         ((ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null)).getLoaded().values().forEach((lockable) -> {
            if (lockable.box.loaded(mc.world)) {
               lockable.tick();
            }

         });
      }
   }

   @SubscribeEvent
   public static void onRenderWorld(RenderWorldLastEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      Vec3d origin = new Vec3d(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ);
      MutableBlockPos mutPos = new MutableBlockPos();
      ILockableHandler lockables = (ILockableHandler)mc.world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);
      GlStateManager.enableRescaleNormal();
      ObjectIterator select = lockables.getLoaded().values().iterator();

      while(select.hasNext()) {
         Lockable lockable = (Lockable)select.next();
         Lockable.State state = lockable.getLockState(mc.world);
         if (state != null && state.inRange(origin) && state.inView(ClippingHelperImpl.getInstance(), origin)) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(state.pos.x - origin.x, state.pos.y - origin.y, state.pos.z - origin.z);
            GlStateManager.rotate(-state.orient.dir.getHorizontalAngle() - 180.0F, 0.0F, 1.0F, 0.0F);
            if (state.orient.face != AttachFace.WALL) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.translate(0.0D, 0.1D, 0.0D);
            GlStateManager.rotate(MathHelper.sin(LocksClientUtil.cubicBezier1d(1.0F, 1.0F, LocksClientUtil.lerp((float)(lockable.maxShakeTicks - lockable.prevShakeTicks), (float)(lockable.maxShakeTicks - lockable.shakeTicks), event.getPartialTicks()) / (float)lockable.maxShakeTicks) * (float)lockable.maxShakeTicks / 5.0F * 3.14F) * 10.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0D, -0.1D, 0.0D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            int light = mc.world.getCombinedLight(mutPos.setPos(state.pos.x, state.pos.y, state.pos.z), 0);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(light % 65536), (float)(light / 65536));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.entityRenderer.enableLightmap();
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItem(lockable.stack, TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            mc.entityRenderer.disableLightmap();
            GlStateManager.popMatrix();
         }
      }

      GlStateManager.disableRescaleNormal();
      ISelection select = (ISelection)mc.player.getCapability(LocksCapabilities.SELECTION, (EnumFacing)null);
      BlockPos pos1 = select.get();
      if (pos1 != null) {
         BlockPos pos2 = mc.objectMouseOver.getBlockPos() != null ? mc.objectMouseOver.getBlockPos() : pos1;
         Cuboid6i box = new Cuboid6i(pos1, pos2);
         float r = 0.0F;
         float g = 0.0F;
         LocksConfig.Server cfg = LocksConfig.getServer(mc.world);
         if (box.volume() <= cfg.maxLockableVolume && cfg.canLock(mc.world, pos2)) {
            g = 1.0F;
         } else {
            r = 1.0F;
         }

         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         mc.entityRenderer.disableLightmap();
         RenderGlobal.drawBoundingBox((double)box.x1 - origin.x, (double)box.y1 - origin.y, (double)box.z1 - origin.z, (double)box.x2 - origin.x, (double)box.y2 - origin.y, (double)box.z2 - origin.z, r, g, 0.0F, 0.5F);
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
      }
   }
}
