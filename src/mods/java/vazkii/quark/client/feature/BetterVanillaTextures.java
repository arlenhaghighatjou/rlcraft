package vazkii.quark.client.feature;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

public class BetterVanillaTextures extends Feature {
   private static final String OVERRIDES_JSON_FILE = "/assets/quark/overrides.json";
   private static final Gson GSON = new Gson();
   public static BetterVanillaTextures.OverrideHolder overrides = null;

   public BetterVanillaTextures() {
   }

   public void setupConfig() {
      if (overrides == null) {
         InputStreamReader reader = new InputStreamReader(Quark.class.getResourceAsStream("/assets/quark/overrides.json"));
         overrides = (BetterVanillaTextures.OverrideHolder)GSON.fromJson(reader, BetterVanillaTextures.OverrideHolder.class);
      }

      for(BetterVanillaTextures.OverrideEntry e : overrides.overrides) {
         e.configVal = this.loadPropBool("Enable " + e.name, "", !e.disabled);
      }

   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      overrides.overrides.forEach(BetterVanillaTextures.OverrideEntry::apply);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   private static class OverrideEntry {
      public String name;
      public String[] files;
      public boolean disabled;
      public boolean configVal;

      private OverrideEntry() {
      }

      void apply() {
         if (this.configVal) {
            for(String file : this.files) {
               String[] tokens = file.split("//");
               Quark.proxy.addResourceOverride(tokens[0], tokens[1]);
            }
         }

      }
   }

   private static class OverrideHolder {
      public List<BetterVanillaTextures.OverrideEntry> overrides;

      private OverrideHolder() {
      }
   }
}
