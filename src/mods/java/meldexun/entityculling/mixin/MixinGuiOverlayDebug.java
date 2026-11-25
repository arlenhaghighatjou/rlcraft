package meldexun.entityculling.mixin;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import meldexun.renderlib.renderer.EntityRenderManager;
import meldexun.renderlib.renderer.TileEntityRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {GuiOverlayDebug.class},
   priority = 1100
)
public class MixinGuiOverlayDebug {
   @Shadow
   private Minecraft mc;

   public MixinGuiOverlayDebug() {
   }

   @Inject(
      method = {"call"},
      at = {@At("RETURN")}
   )
   public void call(CallbackInfoReturnable<List<String>> info) {
      List<String> list = (List)info.getReturnValue();
      search(list, (s) -> {
         return (void)s.startsWith("Entities:");
      }, (i) -> {
         list.set(i, (String)list.get(i) + ", Culled: " + EntityRenderManager.occludedEntities());
      });
      search(list, (s) -> {
         return (void)s.startsWith("Tile Entities:");
      }, (i) -> {
         list.set(i, (String)list.get(i) + ", Culled: " + TileEntityRenderManager.occludedTileEntities());
      });
   }

   private static void search(List<String> list, Predicate<String> predicate, IntConsumer action) {
      for(int i = 0; i < list.size(); ++i) {
         if (predicate.test(list.get(i))) {
            action.accept(i);
            return;
         }
      }

   }
}
