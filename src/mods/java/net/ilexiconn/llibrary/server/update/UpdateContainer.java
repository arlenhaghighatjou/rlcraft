package net.ilexiconn.llibrary.server.update;

import java.awt.image.BufferedImage;
import java.util.Map;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class UpdateContainer {
   private transient ModContainer modContainer;
   private transient BufferedImage icon;
   private transient ArtifactVersion latestVersion;
   private String version;
   private String updateURL;
   private String iconURL;
   private Map<String, String[]> versions;

   public UpdateContainer() {
   }

   public ModContainer getModContainer() {
      return this.modContainer;
   }

   public void setModContainer(ModContainer modContainer) {
      this.modContainer = modContainer;
   }

   public BufferedImage getIcon() {
      return this.icon;
   }

   public void setIcon(BufferedImage icon) {
      this.icon = icon;
   }

   public ArtifactVersion getLatestVersion() {
      if (this.latestVersion == null) {
         this.latestVersion = new DefaultArtifactVersion(this.version);
      }

      return this.latestVersion;
   }

   public String getVersion() {
      return this.version;
   }

   public String getUpdateURL() {
      return this.updateURL;
   }

   public String getIconURL() {
      return this.iconURL;
   }

   public Map<String, String[]> getVersions() {
      return this.versions;
   }
}
