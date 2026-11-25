package crafttweaker.mc1120.events.handling;

import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingDeathDropsEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class MCEntityLivingDeathDropsEvent implements EntityLivingDeathDropsEvent {
   private final LivingDropsEvent event;

   public MCEntityLivingDeathDropsEvent(LivingDropsEvent event) {
      this.event = event;
   }

   public IDamageSource getDamageSource() {
      return CraftTweakerMC.getIDamageSource(this.event.getSource());
   }

   public void addItem(IItemStack item) {
      IEntity entity = this.getEntity();
      this.addItem(item.createEntityItem(entity.getWorld(), entity.getPosition()));
   }

   public void addItem(IEntityItem entityItem) {
      EntityItem item = CraftTweakerMC.getEntityItem(entityItem);
      if (item != null) {
         this.event.getDrops().add(item);
      }

   }

   public int getLootingLevel() {
      return this.event.getLootingLevel();
   }

   public boolean getIsRecentlyHit() {
      return this.event.isRecentlyHit();
   }

   public List<IEntityItem> getDrops() {
      return (List)this.event.getDrops().stream().map(CraftTweakerMC::getIEntityItem).collect(Collectors.toList());
   }

   public void setDrops(List<IEntityItem> drops) {
      this.event.getDrops().clear();
      drops.forEach(this::addItem);
   }

   public IEntityLivingBase getEntityLivingBase() {
      return CraftTweakerMC.getIEntityLivingBase(this.event.getEntityLiving());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
