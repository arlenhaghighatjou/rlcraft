package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class EntityEffect {
   private IEntityEffectHandlerState state;

   public EntityEffect() {
   }

   @Nonnull
   public abstract String name();

   public void initialize(@Nonnull IEntityEffectHandlerState state) {
      this.state = state;
   }

   protected IEntityEffectHandlerState getState() {
      return this.state;
   }

   public abstract void update(@Nonnull Entity var1);

   public boolean receiveLastCall() {
      return false;
   }

   public boolean isFirstPersonView() {
      GameSettings settings = Minecraft.getMinecraft().getRenderManager().options;
      return settings == null || settings.thirdPersonView == 0;
   }

   public String toString() {
      return this.name();
   }
}
