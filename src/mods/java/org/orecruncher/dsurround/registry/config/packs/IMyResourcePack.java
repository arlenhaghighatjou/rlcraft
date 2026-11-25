package org.orecruncher.dsurround.registry.config.packs;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;

public interface IMyResourcePack {
   String getModName();

   boolean hasManifest();

   boolean resourceExists(@Nonnull ResourceLocation var1);

   InputStream getInputStream(@Nonnull ResourceLocation var1) throws IOException;
}
