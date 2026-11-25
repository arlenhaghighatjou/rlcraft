package com.charles445.simpledifficulty.proxy;

import com.charles445.simpledifficulty.client.command.ClientCommandCopy;
import com.charles445.simpledifficulty.client.command.ClientCommandIdentityCopy;
import com.charles445.simpledifficulty.client.gui.TemperatureGui;
import com.charles445.simpledifficulty.client.gui.TemperatureInfoGui;
import com.charles445.simpledifficulty.client.gui.ThirstGui;
import com.charles445.simpledifficulty.client.particle.ParticleChiller;
import com.charles445.simpledifficulty.client.particle.ParticleHeater;
import com.charles445.simpledifficulty.client.render.RenderSpit;
import com.charles445.simpledifficulty.compat.CompatController;
import com.charles445.simpledifficulty.handler.TooltipHandler;
import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit() {
      super.preInit();
      MinecraftForge.EVENT_BUS.register(new TemperatureGui());
      MinecraftForge.EVENT_BUS.register(new TemperatureInfoGui());
      MinecraftForge.EVENT_BUS.register(new ThirstGui());
      MinecraftForge.EVENT_BUS.register(new TooltipHandler());
   }

   public void init() {
      super.init();
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpit.class, new RenderSpit());
   }

   public void postInit() {
      super.postInit();
      CompatController.setupClient();
      ClientCommandHandler.instance.registerCommand(new ClientCommandCopy());
      ClientCommandHandler.instance.registerCommand(new ClientCommandIdentityCopy());
   }

   public Side getSide() {
      return Side.CLIENT;
   }

   public EntityPlayer getClientMinecraftPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public Boolean isClientConnectedToServer() {
      return Minecraft.getMinecraft().getConnection().getNetworkManager().isChannelOpen();
   }

   public void spawnClientParticle(World world, String type, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ) {
      if (world.isRemote) {
         Particle particle = null;
         byte var17 = -1;
         switch(type.hashCode()) {
         case 1464259761:
            if (type.equals("CHILLER")) {
               var17 = 1;
            }
            break;
         case 2127041181:
            if (type.equals("HEATER")) {
               var17 = 0;
            }
         }

         switch(var17) {
         case 0:
            particle = new ParticleHeater(world, xPos, yPos, zPos, motionX, motionY, motionZ);
            break;
         case 1:
            particle = new ParticleChiller(world, xPos, yPos, zPos, motionX, motionY, motionZ);
         }

         if (particle != null) {
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
         }

      }
   }
}
