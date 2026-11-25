package bettercombat.mod.client;

import bettercombat.mod.client.handler.EventHandlersClient;
import bettercombat.mod.client.particle.EntitySweepAttack2FX;
import bettercombat.mod.network.PacketHandler;
import bettercombat.mod.util.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      MinecraftForge.EVENT_BUS.register(EventHandlersClient.INSTANCE);
      PacketHandler.registerClientMessages();
   }

   public void spawnSweep(EntityPlayer player) {
      double x = (double)(-MathHelper.sin(player.rotationYaw * 0.017453292F));
      double z = (double)MathHelper.cos(player.rotationYaw * 0.017453292F);
      Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySweepAttack2FX(Minecraft.getMinecraft().getTextureManager(), player.world, player.posX + x, player.posY + (double)player.height * 0.5D, player.posZ + z, 0.0D));
   }
}
