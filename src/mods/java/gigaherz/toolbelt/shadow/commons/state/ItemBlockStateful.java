package gigaherz.toolbelt.shadow.commons.state;

import com.google.common.collect.UnmodifiableIterator;
import gigaherz.toolbelt.shadow.commons.state.implementation.ItemStateManager;
import java.util.function.IntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockStateful extends ItemBlock implements StatefulItem {
   private IItemStateManager stateManager;

   public ItemBlockStateful(Block block) {
      super(block);
      this.setRegistryName(block.getRegistryName());
      this.stateManager = this.createStateManager();
   }

   public IItemStateManager getStateManager() {
      return this.stateManager;
   }

   public IItemState getDefaultState() {
      return this.stateManager.getDefaultState();
   }

   public void setDefaultState(IItemState defaultState) {
      this.stateManager.setDefaultState(defaultState);
   }

   public IItemStateManager createStateManager() {
      BlockStateContainer blockStateManager = this.block.getBlockState();
      return new ItemStateManager(this, (IProperty[])blockStateManager.getProperties().stream().toArray((x$0) -> {
         return new IProperty[x$0];
      }));
   }

   public ItemStack getStack(IBlockState bState) {
      IItemState iState = this.getDefaultState();

      IProperty prop;
      for(UnmodifiableIterator var3 = this.getStateManager().getProperties().iterator(); var3.hasNext(); iState = iState.withProperty(prop, bState.getValue(prop))) {
         prop = (IProperty)var3.next();
      }

      return iState.getStack();
   }

   public IBlockState getBlockState(ItemStack stack) {
      IItemState iState = this.getStateManager().get(stack.getMetadata());
      IBlockState bState = this.block.getDefaultState();
      IProperty prop;
      if (iState != null) {
         for(UnmodifiableIterator var4 = this.getStateManager().getProperties().iterator(); var4.hasNext(); bState = bState.withProperty(prop, iState.getValue(prop))) {
            prop = (IProperty)var4.next();
         }
      }

      return bState;
   }
}
