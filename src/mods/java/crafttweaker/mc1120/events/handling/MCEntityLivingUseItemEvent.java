package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingUseItemEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

public class MCEntityLivingUseItemEvent implements EntityLivingUseItemEvent {
   private final LivingEntityUseItemEvent event;

   public MCEntityLivingUseItemEvent(LivingEntityUseItemEvent event) {
      this.event = event;
   }

   public IItemStack getItem() {
      return CraftTweakerMC.getIItemStack(this.event.getItem());
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }

   public boolean isPlayer() {
      return this.event.getEntityLiving() instanceof EntityPlayer;
   }

   public IPlayer getPlayer() {
      return this.event.getEntityLiving() instanceof EntityPlayer ? CraftTweakerMC.getIPlayer((EntityPlayer)this.event.getEntityLiving()) : null;
   }

   public int getDuration() {
      return this.event.getDuration();
   }

   public void setDuration(int duration) {
      this.event.setDuration(duration);
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public static class Finish extends MCEntityLivingUseItemEvent implements EntityLivingUseItemEvent.Finish {
      public Finish(net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish event) {
         super(event);
      }
   }

   public static class Start extends MCEntityLivingUseItemEvent implements EntityLivingUseItemEvent.Start {
      public Start(net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start event) {
         super(event);
      }
   }

   public static class Stop extends MCEntityLivingUseItemEvent implements EntityLivingUseItemEvent.Stop {
      public Stop(net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Stop event) {
         super(event);
      }
   }

   public static class Tick extends MCEntityLivingUseItemEvent implements EntityLivingUseItemEvent.Tick {
      public Tick(net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick event) {
         super(event);
      }
   }
}
