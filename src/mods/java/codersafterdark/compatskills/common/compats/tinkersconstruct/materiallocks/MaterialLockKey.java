package codersafterdark.compatskills.common.compats.tinkersconstruct.materiallocks;

import codersafterdark.reskillable.api.data.ParentLockKey;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.Objects;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class MaterialLockKey implements ParentLockKey {
   private final Material material;

   public MaterialLockKey(Material material) {
      this.material = material;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof MaterialLockKey && Objects.equals(this.material, ((MaterialLockKey)o).material);
   }

   public int hashCode() {
      return this.material == null ? super.hashCode() : this.material.hashCode();
   }

   public RequirementHolder getSubRequirements() {
      return LevelLockHandler.getLocks(IToolMod.class, this.material.getDefaultTraits().toArray(new IToolMod[0]));
   }
}
