package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.effect.EntityEffectInfo;

@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface IEntityEffectFactoryFilter {
   boolean applies(@Nonnull Entity var1, @Nonnull EntityEffectInfo var2);
}
