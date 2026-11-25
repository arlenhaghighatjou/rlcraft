package crafttweaker.mc1120.events.handling;

import crafttweaker.api.command.ICommand;
import crafttweaker.api.command.ICommandSender;
import crafttweaker.api.event.CommandEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;

public class MCCommandEvent implements CommandEvent {
   private final net.minecraftforge.event.CommandEvent event;

   public MCCommandEvent(net.minecraftforge.event.CommandEvent event) {
      this.event = event;
   }

   public ICommandSender getCommandSender() {
      return CraftTweakerMC.getICommandSender(this.event.getSender());
   }

   public ICommand getCommand() {
      return CraftTweakerMC.getICommand(this.event.getCommand());
   }

   public String[] getParameters() {
      return this.event.getParameters();
   }

   public void setParameters(String[] parameters) {
      this.event.setParameters(parameters);
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
