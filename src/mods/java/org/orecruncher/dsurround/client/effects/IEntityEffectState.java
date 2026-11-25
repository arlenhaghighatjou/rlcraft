package org.orecruncher.dsurround.client.effects;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IEntityEffectState extends IEffectState {
   @Nonnull
   Optional<Entity> subject();

   boolean isSubjectAlive();

   long getWorldTime();
}
