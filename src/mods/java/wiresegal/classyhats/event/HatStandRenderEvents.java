package wiresegal.classyhats.event;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.ClassyHatsContent;
import wiresegal.classyhats.block.BlockHatStand;

@EventBusSubscriber(
   modid = "classyhats"
)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J(\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002¨\u0006\u0010"},
   d2 = {"Lwiresegal/classyhats/event/HatStandRenderEvents;", "", "()V", "onDrawBlockHighlight", "", "event", "Lnet/minecraftforge/client/event/DrawBlockHighlightEvent;", "renderSelectionBox", "selectionBox", "Lnet/minecraft/util/math/AxisAlignedBB;", "pos", "Lnet/minecraft/util/math/BlockPos;", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "partialTicks", "", "classyhats"}
)
public final class HatStandRenderEvents {
   public static final HatStandRenderEvents INSTANCE;

   @SubscribeEvent
   @JvmStatic
   @SideOnly(Side.CLIENT)
   public static final void onDrawBlockHighlight(@NotNull DrawBlockHighlightEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (event.getTarget() != null && event.getTarget().typeOfHit == Type.BLOCK) {
         RayTraceResult var10000 = event.getTarget();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "event.target");
         BlockPos pos = var10000.getBlockPos();
         EntityPlayer player = event.getPlayer();
         Intrinsics.checkExpressionValueIsNotNull(player, "player");
         IBlockState state = player.getEntityWorld().getBlockState(pos);
         Intrinsics.checkExpressionValueIsNotNull(state, "state");
         if (state.getBlock() instanceof BlockHatStand) {
            for(AxisAlignedBB aabb : ClassyHatsContent.INSTANCE.getHAT_STAND().getCollisionBoxes(state)) {
               HatStandRenderEvents var6 = INSTANCE;
               Intrinsics.checkExpressionValueIsNotNull(pos, "pos");
               var6.renderSelectionBox(aabb, pos, player, event.getPartialTicks());
            }

            event.setCanceled(true);
         }
      }

   }

   private final void renderSelectionBox(AxisAlignedBB selectionBox, BlockPos pos, EntityPlayer player, float partialTicks) {
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.glLineWidth(2.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      double offsetX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
      double offsetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
      double offsetZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
      AxisAlignedBB target = selectionBox.grow(0.002D).offset(pos).offset(-offsetX, -offsetY, -offsetZ);
      RenderGlobal.drawSelectionBoundingBox(target, 0.0F, 0.0F, 0.0F, 0.4F);
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   private HatStandRenderEvents() {
   }

   static {
      HatStandRenderEvents var0 = new HatStandRenderEvents();
      INSTANCE = var0;
   }
}
