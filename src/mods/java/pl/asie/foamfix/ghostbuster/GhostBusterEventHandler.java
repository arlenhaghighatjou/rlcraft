package pl.asie.foamfix.ghostbuster;

import java.lang.reflect.Field;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import pl.asie.foamfix.FoamFix;

public class GhostBusterEventHandler {
   public GhostBusterEventHandler() {
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      if (event.getWorld() instanceof WorldServer) {
         FoamFix.getLogger().info("Overriding ChunkProviderServer in dimension " + event.getWorld().provider.getDimension() + "!");
         ChunkProviderServerWrapped wrapped = new ChunkProviderServerWrapped((WorldServer)event.getWorld());

         try {
            Field f = ReflectionHelper.findField(World.class, "chunkProvider", "chunkProvider");
            f.setAccessible(true);
            f.set(event.getWorld(), wrapped);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }
}
