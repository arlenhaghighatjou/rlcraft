package org.orecruncher.dsurround.registry.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;

@SideOnly(Side.CLIENT)
public interface IArmorItemData {
   @Nullable
   IAcoustic getArmorSound(@Nonnull ItemStack var1);

   @Nullable
   IAcoustic getFootArmorSound(@Nonnull ItemStack var1);
}
