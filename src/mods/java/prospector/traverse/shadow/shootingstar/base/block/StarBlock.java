package prospector.traverse.shadow.shootingstar.base.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class StarBlock extends Block {
   protected String modId;
   protected String name;

   public StarBlock(String modId, String name, Material materialIn) {
      super(materialIn);
      this.modId = modId;
      this.name = name;
      this.setRegistryName(modId, name);
      ShootingStar.registerModel((new ModelCompound(modId, this, "block", new IProperty[0])).setInvVariant("normal"));
   }

   public StarBlock(String modId, String name) {
      this(modId, name, Material.ROCK);
   }

   public String getModId() {
      return this.modId;
   }

   public String getName() {
      return this.name;
   }
}
