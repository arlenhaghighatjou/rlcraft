package pl.asie.foamfix.coremod;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import pl.asie.foamfix.coremod.injections.VertexLighterSmoothAoCheap;
import pl.asie.foamfix.shared.FoamFixShared;
import pl.asie.foamfix.util.MethodHandleHelper;

public class VertexLighterOverrideHandler {
   private static final MethodHandle SMOOTH_LIGHT_GETTER = MethodHandleHelper.findFieldGetter(ForgeBlockModelRenderer.class, "lighterSmooth");
   private static final MethodHandle SMOOTH_LIGHT_SETTER = MethodHandleHelper.findFieldSetter(ForgeBlockModelRenderer.class, "lighterSmooth");
   private boolean isUsingMinimumLighter;

   public VertexLighterOverrideHandler() {
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (event.phase == Phase.END && FoamFixShared.isCoremod) {
         this.updateSmoothLighting();
      }

   }

   public void updateSmoothLighting() {
      BlockModelRenderer bmr = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
      if (bmr instanceof ForgeBlockModelRenderer && Minecraft.getMinecraft().world == null) {
         boolean newUse = Minecraft.getMinecraft().gameSettings.ambientOcclusion == 1;
         newUse = newUse & FoamFixShared.config.clCheapMinimumLighter;
         if (this.isUsingMinimumLighter != newUse) {
            this.isUsingMinimumLighter = newUse;

            try {
               SMOOTH_LIGHT_SETTER.invoke(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer(), this.isUsingMinimumLighter ? ThreadLocal.withInitial(() -> {
                  return new VertexLighterSmoothAoCheap(Minecraft.getMinecraft().getBlockColors());
               }) : ThreadLocal.withInitial(() -> {
                  return new VertexLighterSmoothAo(Minecraft.getMinecraft().getBlockColors());
               }));
            } catch (Throwable var4) {
               var4.printStackTrace();
            }
         }
      }

   }
}
