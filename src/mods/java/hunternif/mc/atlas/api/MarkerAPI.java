package hunternif.mc.atlas.api;

import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.registry.MarkerType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.World;

public interface MarkerAPI {
   void registerMarker(MarkerType var1);

   @Nullable
   Marker putMarker(@Nonnull World var1, boolean var2, int var3, String var4, String var5, int var6, int var7);

   @Nullable
   Marker putMarker(@Nonnull World var1, boolean var2, int var3, MarkerType var4, String var5, int var6, int var7);

   @Nullable
   Marker putGlobalMarker(@Nonnull World var1, boolean var2, String var3, String var4, int var5, int var6);

   @Nullable
   Marker putGlobalMarker(@Nonnull World var1, boolean var2, MarkerType var3, String var4, int var5, int var6);

   void deleteMarker(@Nonnull World var1, int var2, int var3);

   void deleteGlobalMarker(@Nonnull World var1, int var2);
}
