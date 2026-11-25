package org.orecruncher.dsurround.registry.footstep;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum Substrate {
   CARPET("carpet"),
   FOLIAGE("foliage"),
   MESSY("messy"),
   FENCE("bigger");

   private static final Map<String, Substrate> lookup = new Object2ObjectOpenHashMap();
   private final String name;

   private Substrate(@Nonnull String name) {
      this.name = name;
   }

   @Nullable
   public static Substrate get(@Nonnull String name) {
      return (Substrate)lookup.get(name);
   }

   static {
      for(Substrate s : values()) {
         lookup.put(s.name, s);
      }

   }
}
