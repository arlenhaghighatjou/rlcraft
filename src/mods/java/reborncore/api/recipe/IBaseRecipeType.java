package reborncore.api.recipe;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IBaseRecipeType {
   List<Object> getInputs();

   ItemStack getOutput(int var1);

   int getOutputsSize();

   List<ItemStack> getOutputs();

   String getRecipeName();

   String getUserFreindlyName();

   int tickTime();

   int euPerTick();

   boolean canCraft(TileEntity var1);

   boolean onCraft(TileEntity var1);

   Object clone() throws CloneNotSupportedException;

   boolean useOreDic();
}
