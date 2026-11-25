package org.orecruncher.dsurround.registry.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.lib.JsonUtils;

public class Profiles {
   private static final String PROFILE_PATH = "/assets/dsurround/profiles/";
   private static Profiles profiles;
   private final List<Profiles.ProfileEntry> entries = ImmutableList.of();

   public Profiles() {
   }

   public static void tickle() {
      if (profiles != null) {
         profiles.entries.forEach(Profiles.ProfileEntry::isEnabled);
      }

   }

   public static List<Profiles.ProfileScript> getProfileStreams() {
      return (List<Profiles.ProfileScript>)(profiles != null ? (List)profiles.entries.stream().filter(Profiles.ProfileEntry::isEnabled).map(Profiles.ProfileScript::new).collect(Collectors.toList()) : ImmutableList.of());
   }

   static {
      String index = "/assets/dsurround/profiles/_index.json";

      try {
         profiles = (Profiles)JsonUtils.loadFromJar(Profiles.class, "/assets/dsurround/profiles/_index.json");
      } catch (Throwable var2) {
         ;
      }

   }

   private static class ProfileEntry {
      @SerializedName("profileName")
      public String profileName;
      @SerializedName("optionName")
      public String optionName;
      @SerializedName("optionDescription")
      public String optionDescription;

      private ProfileEntry() {
      }

      public boolean isValid() {
         return !StringUtils.isEmpty(this.profileName) && !StringUtils.isEmpty(this.optionName) && !StringUtils.isEmpty(this.optionDescription);
      }

      public boolean isEnabled() {
         if (!this.isValid()) {
            return false;
         } else {
            ConfigCategory cat = ModBase.config().getCategory("profiles");
            Property prop = cat.get(this.optionName);
            if (prop == null) {
               prop = new Property(this.optionName, "false", Type.BOOLEAN);
               cat.put(this.optionName, prop);
               prop.setValue(false);
            }

            prop.setComment(this.optionDescription);
            prop.setLanguageKey(this.optionName);
            prop.setRequiresMcRestart(false);
            prop.setRequiresWorldRestart(false);
            prop.setDefaultValue(false);
            return prop.getBoolean();
         }
      }

      public InputStream getStream() {
         String path = "/assets/dsurround/profiles/" + this.profileName + ".json";
         return Profiles.class.getResourceAsStream(path);
      }
   }

   public static class ProfileScript {
      public final String packName;
      public final InputStream stream;

      public ProfileScript(@Nonnull Profiles.ProfileEntry entry) {
         this.packName = entry.profileName;
         this.stream = entry.getStream();
      }
   }
}
