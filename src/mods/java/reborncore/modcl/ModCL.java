package reborncore.modcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ModCL {
   public ModCL.CreativeTabCL tab = new ModCL.CreativeTabCL(this);
   public List<Item> itemModelsToRegister = new ArrayList();
   public List<Block> blockModelsToRegister = new ArrayList();
   public HashMap<ItemMetadataCL, String> customBlockStates = new HashMap();

   public ModCL() {
   }

   public CreativeTabs getTab() {
      return this.tab;
   }

   public abstract String getServerProxy();

   public abstract String getClientProxy();

   public abstract RegistryCL getRegistry();

   public ItemStack getTabStack() {
      return ItemStack.EMPTY;
   }

   public String getPrefix() {
      return this.getModID() + ":";
   }

   public abstract String getModName();

   public abstract String getModID();

   public abstract String getModVersion();

   public abstract String getModDependencies();

   private class CreativeTabCL extends CreativeTabs {
      ModCL mod;

      public CreativeTabCL(ModCL mod) {
         super(mod.getModID());
         this.mod = mod;
      }

      public ItemStack getTabIconItem() {
         return this.mod.getTabStack();
      }
   }
}
