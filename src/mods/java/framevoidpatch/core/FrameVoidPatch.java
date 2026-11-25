package framevoidpatch.core;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(
   modid = "framevoidpatch",
   version = "1.0.0",
   name = "Frame Void Patch",
   acceptableRemoteVersions = "*"
)
public class FrameVoidPatch {
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
      }

   }

   public static final String MODID = "framevoidpatch";
   public static final String VERSION = "1.0.0";
   public static final String NAME = "Frame Void Patch";

   public FrameVoidPatch() {
   }

   public static boolean shouldCancelInteract(EntityItemFrame frame) {
      return frame.isDead;
   }
}
