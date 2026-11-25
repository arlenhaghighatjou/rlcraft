package org.orecruncher.dsurround.mixins;

import net.minecraft.item.Item;
import org.orecruncher.dsurround.registry.IDataAccessor;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Item.class})
public abstract class MixinItem implements IDataAccessor<IItemData> {
   private IItemData dsurround_item_info = null;

   public MixinItem() {
   }

   public IItemData getData() {
      return this.dsurround_item_info;
   }

   public void setData(IItemData data) {
      this.dsurround_item_info = data;
   }
}
