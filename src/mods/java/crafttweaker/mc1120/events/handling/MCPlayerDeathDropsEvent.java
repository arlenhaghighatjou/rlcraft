package crafttweaker.mc1120.events.handling;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.event.PlayerDeathDropsEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

public class MCPlayerDeathDropsEvent implements PlayerDeathDropsEvent {
   private final PlayerDropsEvent event;

   public MCPlayerDeathDropsEvent(PlayerDropsEvent event) {
      this.event = event;
   }

   public List<IEntityItem> getItems() {
      return (List)this.event.getDrops().stream().map(CraftTweakerMC::getIEntityItem).collect(Collectors.toList());
   }

   public void setItems(List<IEntityItem> items) {
      List<EntityItem> drops = this.event.getDrops();
      drops.clear();
      items.stream().map(CraftTweakerMC::getEntityItem).filter(Objects::nonNull).forEach(drops::add);
   }

   public void addItem(IItemStack item) {
      BlockPos pos = this.event.getEntityPlayer().getPosition();
      this.event.getDrops().add(new EntityItem(this.event.getEntityPlayer().getEntityWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), CraftTweakerMC.getItemStack(item)));
   }

   public void addItem(IEntityItem entityItem) {
      EntityItem entity = CraftTweakerMC.getEntityItem(entityItem);
      if (entity != null) {
         this.event.getDrops().add(entity);
      }

   }

   public IDamageSource getDamageSource() {
      return CraftTweakerMC.getIDamageSource(this.event.getSource());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
