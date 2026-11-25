package prospector.traverse.client;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraftforge.fml.common.Loader;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.core.TraverseMod;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Traverse2Textures extends AbstractResourcePack {
   private static final String BARE_FORMAT = "assets/%s/%s/%s/%s.%s";
   private static final String OVERRIDE_FORMAT = "/assets/%s/%s/%s/overrides/%s.%s";
   private static final Set<String> RESOURCE_DOMAINS = ImmutableSet.of("traverse");
   private static final Map<String, String> overrides = new HashMap();

   public Traverse2Textures() {
      super(Loader.instance().activeModContainer().getSource());
      overrides.put("pack.mcmeta", "/proxypack.mcmeta");
   }

   public void addTextureOverride(String dir, String file) {
      this.addResourceOverride("textures", dir, file, "png");
   }

   public void addResourceOverride(String space, String dir, String file, String ext) {
      String bare = String.format("assets/%s/%s/%s/%s.%s", "traverse", space, dir, file, ext);
      String override = String.format("/assets/%s/%s/%s/overrides/%s.%s", "traverse", space, dir, file, ext);
      overrides.put(bare, override);
   }

   protected InputStream getInputStreamByName(String name) throws IOException {
      return TraverseMod.class.getResourceAsStream((String)overrides.get(name));
   }

   protected boolean hasResourceName(String name) {
      return name.equals("pack.mcmeta") || TraverseConfig.enableNewTextures && overrides.containsKey(name);
   }

   public Set<String> getResourceDomains() {
      return RESOURCE_DOMAINS;
   }
}
