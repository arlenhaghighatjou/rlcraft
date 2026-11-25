package lumien.chunkanimator;

import lumien.chunkanimator.config.ChunkAnimatorConfig;
import lumien.chunkanimator.handler.AnimationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
   modid = "chunkanimator",
   name = "Chunk Animator",
   version = "1.12.2-1.2",
   guiFactory = "lumien.chunkanimator.config.ChunkAnimatorGuiFactory",
   clientSideOnly = true
)
public class ChunkAnimator {
   @Instance("chunkanimator")
   public static ChunkAnimator INSTANCE;
   public AnimationHandler animationHandler;
   public ChunkAnimatorConfig config;

   public ChunkAnimator() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.animationHandler = new AnimationHandler();
      this.config = new ChunkAnimatorConfig();
      this.config.preInit(event);
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("chunkanimator")) {
         this.config.syncConfig();
      }

   }
}
