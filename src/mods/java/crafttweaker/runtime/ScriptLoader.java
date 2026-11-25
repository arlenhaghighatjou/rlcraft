package crafttweaker.runtime;

import crafttweaker.CraftTweakerAPI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class ScriptLoader {
   private String mainName;
   private final Set<String> names = new HashSet();
   private final Set<String> delayUntil = new HashSet();
   private ScriptLoader.LoaderStage loaderStage = ScriptLoader.LoaderStage.NOT_LOADED;

   public ScriptLoader(String... nameAndAliases) {
      this.addAliases(nameAndAliases);
   }

   public void delayUntil(String... name) {
      this.delayUntil.addAll(Arrays.asList(name));
   }

   public boolean isDelayed() {
      return !this.delayUntil.isEmpty();
   }

   public ScriptLoader removeDelay(String... name) {
      this.delayUntil.removeAll(Arrays.asList(name));
      return this;
   }

   public Set<String> getNames() {
      return this.names;
   }

   public String getMainName() {
      if (this.mainName == null) {
         this.mainName = (String)this.names.iterator().next();
      }

      return this.mainName;
   }

   public void setMainName(String mainName) {
      if (!this.canExecute(mainName)) {
         this.addAliases(mainName);
      }

      this.mainName = mainName;
   }

   public boolean isLoaded() {
      return this.loaderStage != ScriptLoader.LoaderStage.NOT_LOADED;
   }

   public ScriptLoader.LoaderStage getLoaderStage() {
      return this.loaderStage;
   }

   public void setLoaderStage(ScriptLoader.LoaderStage loaderStage) {
      this.loaderStage = loaderStage;
   }

   public void addAliases(String... names) {
      if (this.isLoaded()) {
         CraftTweakerAPI.logInfo("Trying to add loader aliases [" + String.join(" | ", names) + "] to already loaded ScriptLoader " + this.toString());
      }

      for(String name : names) {
         this.names.add(name.trim().toLowerCase());
      }

      if (this.names.isEmpty()) {
         throw new IllegalArgumentException("Loader is empty after all aliases have been added");
      }
   }

   public boolean canExecute(String... loaderNames) {
      for(String loaderName : loaderNames) {
         if (this.names.contains(loaderName.trim().toLowerCase())) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      StringJoiner joiner = new StringJoiner(" | ", "[", "]");
      String mainName = this.getMainName();
      joiner.add(mainName);

      for(String name : this.names) {
         if (!mainName.equals(name)) {
            joiner.add(name);
         }
      }

      return joiner.toString();
   }

   public static enum LoaderStage {
      NOT_LOADED,
      LOADING,
      LOADED_SUCCESSFUL,
      ERROR,
      INVALIDATED,
      UNKNOWN;

      private LoaderStage() {
      }
   }
}
