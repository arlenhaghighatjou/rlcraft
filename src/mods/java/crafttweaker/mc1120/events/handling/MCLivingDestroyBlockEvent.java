package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.LivingDestroyBlockEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;

public class MCLivingDestroyBlockEvent implements LivingDestroyBlockEvent {
   private net.minecraftforge.event.entity.living.LivingDestroyBlockEvent event;

   public MCLivingDestroyBlockEvent(net.minecraftforge.event.entity.living.LivingDestroyBlockEvent event) {
      this.event = event;
   }

   public IBlockState getState() {
      return CraftTweakerMC.getBlockState(this.event.getState());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }
}
