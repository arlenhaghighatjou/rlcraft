package org.orecruncher.dsurround.client.effects;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface IEntityEffectFactory {
   @Nonnull
   List<EntityEffect> create(@Nonnull Entity var1);
}
