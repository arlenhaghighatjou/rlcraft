package org.orecruncher.dsurround.registry.item;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.sound.SoundEffect;

@SideOnly(Side.CLIENT)
public interface IItemData {
   ItemClass getItemClass();

   SoundEffect getEquipSound(@Nonnull ItemStack var1);

   SoundEffect getSwingSound(@Nonnull ItemStack var1);

   SoundEffect getUseSound(@Nonnull ItemStack var1);
}
