package org.orecruncher.dsurround.event;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DiagnosticEvent extends Event {
   public DiagnosticEvent() {
   }

   public static class Gather extends DiagnosticEvent {
      public final World world;
      public final EntityPlayer player;
      public final List<String> output = new ArrayList();

      public Gather(World world, EntityPlayer player) {
         this.world = world;
         this.player = player;
      }
   }
}
