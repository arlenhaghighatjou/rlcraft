package lumien.chunkanimator;

import lumien.chunkanimator.config.ChunkAnimatorConfig;
import lumien.chunkanimator.handler.AnimationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(
   modid = "chunkanimator",
   name = "Chunk Animator",
   version = "1.12.2-1.2",
   guiFactory = "lumien.chunkanimator.config.ChunkAnimatorGuiFactory",
   clientSideOnly = true
)
public class ChunkAnimator {
   public static ChunkAnimator INSTANCE;
   public AnimationHandler animationHandler;
   public ChunkAnimatorConfig config;

   public ChunkAnimator() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLPreInitializationEvent) {
         preInit((FMLPreInitializationEvent)event);
      }
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.animationHandler = new AnimationHandler();
      this.config = new ChunkAnimatorConfig();
      this.config.preInit(event);
      INSTANCE = this;
      MinecraftForge.EVENT_BUS.register(OnConfigChangedEvent.class, this::onConfigChanged);
      MinecraftForge.EVENT_BUS.register(ClientTickEvent.class, tick -> {
         if (tick.phase == Phase.END) {
            this.animationHandler.tick();
         }
      });
   }

   public static void startCooldown() {
      if (INSTANCE != null) {
         INSTANCE.animationHandler.startCooldown();
      }
   }

   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("chunkanimator")) {
         this.config.syncConfig();
      }

   }
}
