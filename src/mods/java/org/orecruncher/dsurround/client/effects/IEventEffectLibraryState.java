package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IEventEffectLibraryState extends IEffectState {
   boolean isActivePlayer(@Nonnull Entity var1);
}
