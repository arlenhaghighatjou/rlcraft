package crafttweaker.mc1120.events.handling;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.entity.IEntityFishHook;
import crafttweaker.api.event.ItemFishedEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class MCItemFishedEvent implements ItemFishedEvent {
   private final net.minecraftforge.event.entity.player.ItemFishedEvent event;
   private final IItemStack[] drops;

   public MCItemFishedEvent(net.minecraftforge.event.entity.player.ItemFishedEvent event) {
      this.event = event;
      this.drops = (IItemStack[])event.getDrops().stream().map(CraftTweakerMC::getIItemStack).toArray((x$0) -> {
         return new IItemStack[x$0];
      });
   }

   public int getRodDamage() {
      return this.event.getRodDamage();
   }

   public void additionalDamage(int damage) {
      if (damage < 0) {
         CraftTweakerAPI.logError("ItemFishedEvent: additional damage applied must be non-negative");
      } else {
         this.event.damageRodBy(damage);
      }

   }

   public IItemStack[] getDrops() {
      return this.drops;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public IEntityFishHook getFishHook() {
      return CraftTweakerMC.getIEntityFishHook(this.event.getHookEntity());
   }
}
