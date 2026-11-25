package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.MinecartUpdateEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;

public class MCMinecartUpdateEvent implements MinecartUpdateEvent {
   private net.minecraftforge.event.entity.minecart.MinecartUpdateEvent event;

   public MCMinecartUpdateEvent(net.minecraftforge.event.entity.minecart.MinecartUpdateEvent event) {
      this.event = event;
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }

   public IEntity getEntity() {
      return CraftTweakerMC.getIEntity(this.event.getEntity());
   }
}
