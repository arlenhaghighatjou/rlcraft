package reborncore.modcl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemMetadataCL extends ItemCL {
   public List<String> types;

   public ItemMetadataCL(ModCL mod, String name, String blockstateLocation) {
      super(mod, name, false);
      this.types = new ArrayList();
      this.setHasSubtypes(true);
      mod.customBlockStates.put(this, blockstateLocation);
   }

   public ItemMetadataCL(ModCL mod, String name) {
      this(mod, name, "");
   }

   public ItemStack getStack(String name) {
      return this.getStack(name, 1);
   }

   public ItemStack getStack(String name, int count) {
      for(String type : this.types) {
         if (type.equalsIgnoreCase(name)) {
            ItemStack stack = new ItemStack((Item)this.mod.getRegistry().itemRegistry.get(this.name), count, this.types.indexOf(name));
            stack.setCount(count);
            return stack;
         }
      }

      throw new InvalidParameterException("Stack not found: " + name);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(int meta = 0; meta < this.types.size(); ++meta) {
         list.add(new ItemStack(this, 1, meta));
      }

   }

   public String getUnlocalizedName(ItemStack itemStack) {
      int meta = itemStack.getItemDamage();
      if (meta < 0 || meta >= this.types.size()) {
         meta = 0;
      }

      return super.getUnlocalizedName() + "." + (String)this.types.get(meta);
   }
}
