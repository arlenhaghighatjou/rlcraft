package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlock;
import crafttweaker.api.event.BlockHarvestDropsEvent;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import java.util.List;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

public class MCBlockHarvestDropsEvent extends MCBlockEvent implements BlockHarvestDropsEvent {
   private final HarvestDropsEvent event;

   public MCBlockHarvestDropsEvent(HarvestDropsEvent event) {
      super(event);
      this.event = event;
   }

   public float getDropChance() {
      return this.event.getDropChance();
   }

   public void setDropChance(float dropChance) {
      this.event.setDropChance(dropChance);
   }

   public int getFortuneLevel() {
      return this.event.getFortuneLevel();
   }

   public IBlock getBlock() {
      return this.getBlockState().getBlock();
   }

   public List<WeightedItemStack> getDrops() {
      return CraftTweakerMC.getWeightedItemStackList(this.event.getDrops());
   }

   public void setDrops(List<WeightedItemStack> drops) {
      this.event.getDrops().clear();

      for(WeightedItemStack drop : drops) {
         this.addItem(drop);
      }

   }

   public void addItem(WeightedItemStack itemStack) {
      if (this.event.getWorld().rand.nextFloat() <= itemStack.getChance()) {
         this.event.getDrops().add(CraftTweakerMC.getItemStack(itemStack.getStack()));
      }

   }

   public boolean isSilkTouch() {
      return this.event.isSilkTouching();
   }

   public boolean isPlayer() {
      return this.event.getHarvester() instanceof EntityPlayerMP || this.event.getHarvester() instanceof EntityPlayerSP;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getHarvester());
   }
}
