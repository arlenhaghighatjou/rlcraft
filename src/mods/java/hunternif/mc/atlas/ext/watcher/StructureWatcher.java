package hunternif.mc.atlas.ext.watcher;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.Sets;
import hunternif.mc.atlas.util.Log;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import org.apache.commons.lang3.tuple.Pair;

public class StructureWatcher {
   public static final StructureWatcher INSTANCE = new StructureWatcher();
   private final Set<IStructureWatcher> structureWatchers = Sets.newHashSet();

   public StructureWatcher() {
      this.register(MinecraftForge.EVENT_BUS);
   }

   public void register(EventBus bus) {
      bus.register(Load.class, EventPriority.LOWEST, false, this::onWorldLoad);
      bus.register(Post.class, this::onPopulateChunk);
   }

   public void onWorldLoad(Load event) {
      this.handlePotential(event.getWorld());
   }

   public void onPopulateChunk(Post event) {
      this.handlePotential(event.getWorld());
   }

   private void handlePotential(World world) {
      if (!world.isRemote) {
         for(IStructureWatcher watcher : this.structureWatchers) {
            if (watcher.isDimensionValid(world.provider.getDimensionType())) {
               NBTTagCompound structureData = watcher.getStructureData(world);
               if (structureData != null) {
                  for(Pair<WatcherPos, String> visit : watcher.visitStructure(world, structureData)) {
                     Log.debug("Visited %s in dimension %d at %s", visit.getRight(), world.provider.getDimension(), ((WatcherPos)visit.getLeft()).toString());
                  }
               }
            }
         }

      }
   }

   public void handleShutdown() {
      for(IStructureWatcher watcher : this.structureWatchers) {
         watcher.getVisited().clear();
      }

   }

   public void addWatcher(IStructureWatcher watcher) {
      if (!this.structureWatchers.contains(watcher)) {
         this.structureWatchers.add(watcher);
      }

   }
}
