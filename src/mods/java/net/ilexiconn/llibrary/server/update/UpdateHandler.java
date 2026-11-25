package net.ilexiconn.llibrary.server.update;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.util.WebUtils;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

/** @deprecated */
@Deprecated
public enum UpdateHandler {
   INSTANCE;

   private List<UpdateContainer> updateContainerList = new ArrayList();
   private List<UpdateContainer> outdatedModList = new ArrayList();

   private UpdateHandler() {
   }

   /** @deprecated */
   @Deprecated
   public void registerUpdateChecker(Object mod, String url) {
      Thread thread = new Thread(() -> {
         if (!mod.getClass().isAnnotationPresent(Mod.class)) {
            LLibrary.LOGGER.warn("Please register the update checker using the main mod class. Skipping registration of object {}.", mod);
         } else {
            Mod annotation = (Mod)mod.getClass().getAnnotation(Mod.class);

            try {
               UpdateContainer updateContainer = (UpdateContainer)(new Gson()).fromJson(WebUtils.readURL(url), UpdateContainer.class);
               if (updateContainer != null) {
                  ModContainer[] modContainer = new ModContainer[]{null};
                  Loader.instance().getModList().stream().filter((container) -> {
                     return container.getModId().equals(annotation.modid());
                  }).forEach((container) -> {
                     modContainer[0] = container;
                  });
                  if (modContainer[0] == null) {
                     LLibrary.LOGGER.warn("Couldn't find mod container with id {}. Skipping registration of object {}.", annotation.modid(), mod);
                     return;
                  }

                  updateContainer.setModContainer(modContainer[0]);
                  if (!updateContainer.getIconURL().isEmpty()) {
                     updateContainer.setIcon(WebUtils.downloadImage(updateContainer.getIconURL()));
                  }

                  this.updateContainerList.add(updateContainer);
               } else {
                  LLibrary.LOGGER.warn("Failed to load update container for mod {} ({})!", annotation.name(), annotation.modid());
               }
            } catch (JsonSyntaxException var6) {
               LLibrary.LOGGER.warn("Failed to load update container for mod {} ({})!", annotation.name(), annotation.modid());
            }

         }
      });
      thread.setName("Update Checker: " + mod);
      thread.start();
   }

   /** @deprecated */
   @Deprecated
   public void searchForUpdates() {
      this.updateContainerList.stream().filter((updateContainer) -> {
         return updateContainer.getLatestVersion().compareTo(updateContainer.getModContainer().getProcessedVersion()) > 0;
      }).forEach((updateContainer) -> {
         this.outdatedModList.add(updateContainer);
      });
   }

   /** @deprecated */
   @Deprecated
   public List<UpdateContainer> getOutdatedModList() {
      return this.outdatedModList;
   }

   /** @deprecated */
   @Deprecated
   public String[] getChangelog(UpdateContainer updateContainer, ArtifactVersion version) {
      return this.hasChangelog(updateContainer, version) ? (String[])updateContainer.getVersions().get(version.getVersionString()) : new String[0];
   }

   /** @deprecated */
   @Deprecated
   public boolean hasChangelog(UpdateContainer updateContainer, ArtifactVersion version) {
      return updateContainer.getVersions().containsKey(version.getVersionString());
   }
}
