package hunternif.mc.atlas.ext.watcher;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public interface IStructureWatcher {
   @Nonnull
   Set<WatcherPos> getVisited();

   boolean isDimensionValid(DimensionType var1);

   @Nullable
   NBTTagCompound getStructureData(@Nonnull World var1);

   @Nonnull
   Set<Pair<WatcherPos, String>> visitStructure(@Nonnull World var1, @Nonnull NBTTagCompound var2);
}
