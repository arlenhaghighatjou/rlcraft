package xzeroair.trinkets.util.registry;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ITrinketRegistry<K, V> extends Iterable<V> {
   @Nullable
   @SideOnly(Side.CLIENT)
   V getObject(K var1);

   void putObject(K var1, V var2);

   @SideOnly(Side.CLIENT)
   Set<K> getKeys();
}
