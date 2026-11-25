package vazkii.quark.base.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.interf.IModBlock;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.util.ProxyRegistry;

public interface IQuarkBlock extends IModBlock {
   default String getModNamespace() {
      return "quark";
   }

   default void register(String name) {
      Block self = (Block)this;
      ResourceLocation regName = new ResourceLocation("quark:" + name);
      self.setRegistryName(regName);
      ProxyRegistry.register(self);
      ProxyRegistry.register(this.createItemInstance(regName));
   }

   default ItemBlock createItemInstance(ResourceLocation regName) {
      return new ItemModBlock((Block)this, regName);
   }
}
