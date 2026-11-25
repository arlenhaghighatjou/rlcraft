package reborncore.modcl;

import net.minecraft.item.Item;

public abstract class ItemCL extends Item {
   public ModCL mod;
   public String name;

   public ItemCL(ModCL mod, String name, boolean registerModel) {
      this.setInfo(mod, name);
      if (registerModel) {
         mod.itemModelsToRegister.add(this);
      }

   }

   public ItemCL(ModCL mod, String name) {
      this(mod, name, true);
   }

   private void setInfo(ModCL mod, String name) {
      this.mod = mod;
      this.name = name;
      this.setUnlocalizedName(mod.getPrefix() + name);
      this.setRegistryName(mod.getModID(), name);
      this.setCreativeTab(mod.getTab());
   }
}
