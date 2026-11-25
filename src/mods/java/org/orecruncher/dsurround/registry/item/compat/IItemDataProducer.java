package org.orecruncher.dsurround.registry.item.compat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.orecruncher.dsurround.registry.item.ItemClass;

@SideOnly(Side.CLIENT)
public interface IItemDataProducer {
   @Nullable
   IItemData create(@Nonnull Item var1, @Nonnull ItemClass var2);
}
