package org.orecruncher.dsurround.capabilities.entityfx;

import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.effects.EntityEffectHandler;

@SideOnly(Side.CLIENT)
public class EntityFXData implements IEntityFX {
   protected EntityEffectHandler handler;

   public EntityFXData() {
   }

   public void set(@Nullable EntityEffectHandler handler) {
      this.handler = handler;
   }

   @Nullable
   public EntityEffectHandler get() {
      return this.handler;
   }
}
