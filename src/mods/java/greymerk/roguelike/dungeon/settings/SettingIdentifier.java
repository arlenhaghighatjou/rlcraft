package greymerk.roguelike.dungeon.settings;

import net.minecraft.util.ResourceLocation;

public class SettingIdentifier {
   private ResourceLocation identifier;

   public SettingIdentifier(String namespace, String name) {
      this.identifier = new ResourceLocation(namespace, name);
   }

   public SettingIdentifier(String name) {
      String[] parts = name.split(":");
      String namespace = parts.length > 1 ? parts[0] : "default";
      String uniqueName = parts.length > 1 ? parts[1] : name;
      this.identifier = new ResourceLocation(namespace, uniqueName);
   }

   public String getNamespace() {
      return this.identifier.getResourceDomain();
   }

   public String getName() {
      return this.identifier.getResourcePath();
   }

   public boolean equals(Object o) {
      return !(o instanceof SettingIdentifier) ? false : this.identifier.equals(((SettingIdentifier)o).identifier);
   }

   public String toString() {
      return this.getNamespace() + ":" + this.getName();
   }
}
