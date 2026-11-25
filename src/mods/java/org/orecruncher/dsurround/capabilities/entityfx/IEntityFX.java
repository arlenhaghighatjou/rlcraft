package org.orecruncher.dsurround.capabilities.entityfx;

import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.effects.EntityEffectHandler;

@SideOnly(Side.CLIENT)
public interface IEntityFX {
   void set(@Nullable EntityEffectHandler var1);

   EntityEffectHandler get();

   default void clear() {
      this.set((EntityEffectHandler)null);
   }
}
