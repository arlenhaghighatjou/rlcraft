package org.orecruncher.dsurround.client.handlers;

import com.google.common.base.MoreObjects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.math.TimerEMA;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public abstract class EffectHandlerBase {
   protected final EaglercraftRandom RANDOM = XorShiftRandom.current();
   private final TimerEMA timer;
   private final String handlerName;

   EffectHandlerBase(@Nonnull String name) {
      this.handlerName = name;
      this.timer = new TimerEMA(name);
   }

   public final String getHandlerName() {
      return this.handlerName;
   }

   public boolean doTick(int tick) {
      return true;
   }

   public void process(@Nonnull EntityPlayer player) {
   }

   public void onConnect() {
   }

   public void onDisconnect() {
   }

   protected static EntityPlayer getPlayer() {
      return Minecraft.getMinecraft().player;
   }

   protected static World getWorld() {
      return Minecraft.getMinecraft().world;
   }

   final void updateTimer(long nanos) {
      this.timer.update((double)nanos);
   }

   final void connect0() {
      ((DiagnosticHandler)EffectManager.instance().lookupService(DiagnosticHandler.class)).addTimer(this.timer);
      this.onConnect();
      MinecraftForge.EVENT_BUS.register(this);
   }

   final void disconnect0() {
      MinecraftForge.EVENT_BUS.unregister(this);
      this.onDisconnect();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.getHandlerName()).toString();
   }
}
