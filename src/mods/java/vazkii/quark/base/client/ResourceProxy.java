package vazkii.quark.base.client;

import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.AbstractResourcePack;
import vazkii.quark.base.Quark;

public class ResourceProxy extends AbstractResourcePack {
   private static final String MINECRAFT = "minecraft";
   private static final Set<String> RESOURCE_DOMAINS = ImmutableSet.of("minecraft");
   private static final String BARE_FORMAT = "assets/minecraft/%s/%s";
   private static final String OVERRIDE_FORMAT = "/assets/quark/%s/overrides/%s";
   private static final Map<String, String> overrides = new HashMap();

   public ResourceProxy(File file) {
      super(file);
      overrides.put("pack.mcmeta", "/proxypack.mcmeta");
   }

   public void addResource(String path, String file) {
      String bare = String.format("assets/minecraft/%s/%s", path, file);
      String override = String.format("/assets/quark/%s/overrides/%s", path, file);
      overrides.put(bare, override);
   }

   @Nonnull
   public Set<String> getResourceDomains() {
      return RESOURCE_DOMAINS;
   }

   @Nonnull
   protected InputStream getInputStreamByName(@Nonnull String name) {
      return Quark.class.getResourceAsStream((String)overrides.get(name));
   }

   protected boolean hasResourceName(@Nonnull String name) {
      return overrides.containsKey(name);
   }

   protected void logNameNotLowercase(@Nonnull String name) {
   }

   @Nonnull
   public String getPackName() {
      return "quark-texture-proxy";
   }
}
