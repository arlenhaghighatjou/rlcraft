package prospector.traverse.shadow.shootingstar.base.item;

import net.minecraft.item.Item;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class StarItem extends Item {
   protected String modId;
   protected String name;

   public StarItem(String modId, String name) {
      this.modId = modId;
      this.name = name;
      this.setRegistryName(modId, name);
      this.setUnlocalizedName(modId + "." + name);
      ShootingStar.registerModel(new ModelCompound(modId, this));
   }

   public String getModId() {
      return this.modId;
   }

   public String getName() {
      return this.name;
   }
}
