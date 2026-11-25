package svenhjol.meson.iface;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import svenhjol.meson.Meson;
import svenhjol.meson.MesonItemBlock;
import svenhjol.meson.registry.ProxyRegistry;

public interface IMesonBlock {
   default void register() {
      this.register(this.getName());
   }

   default void register(String name) {
      Block self = (Block)this;
      ResourceLocation res = new ResourceLocation(this.getModId() + ":" + name);
      self.setUnlocalizedName(res.toString());
      self.setRegistryName(res);
      ProxyRegistry.register(self);
      ProxyRegistry.blocks.add(self);
      this.registerItemBlock(self, name);
   }

   default void registerItemBlock(Block block, String name) {
      MesonItemBlock itemBlock = null;

      try {
         itemBlock = (MesonItemBlock)this.getItemBlockClass().getConstructor(Block.class, String.class).newInstance(block, name);
      } catch (Exception var5) {
         Meson.runtimeException("Error creating item block");
      }

      itemBlock.setMaxStackSize(this.getMaxStackSize());
      ProxyRegistry.register(itemBlock);
      ProxyRegistry.items.add(itemBlock);
   }

   default Class<? extends MesonItemBlock> getItemBlockClass() {
      return MesonItemBlock.class;
   }

   default String[] getVariants() {
      return new String[]{this.getName()};
   }

   default int getMaxStackSize() {
      return 64;
   }

   String getModId();

   String getName();

   public interface IHasCustomItemBlockModel {
      void setInventoryItemModel();
   }

   public interface IHasCustomStateMapper {
      void setStateMapper();
   }
}
