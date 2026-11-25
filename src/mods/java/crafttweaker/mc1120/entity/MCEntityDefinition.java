package crafttweaker.mc1120.entity;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.entity.IEntityDrop;
import crafttweaker.api.entity.IEntityDropFunction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.util.IntegerRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class MCEntityDefinition implements IEntityDefinition {
   private final EntityEntry entityEntry;
   private final String entityName;
   private final List<IEntityDrop> drops = new ArrayList();
   private final List<IItemStack> dropsToRemove = new ArrayList();
   private List<IEntityDropFunction> dropFunctions = new ArrayList();
   private boolean clearDrops = false;

   public MCEntityDefinition(EntityEntry entityEntry) {
      this.entityEntry = entityEntry;
      this.entityName = entityEntry.getName();
   }

   public String getId() {
      return this.entityEntry.getRegistryName() != null ? this.entityEntry.getRegistryName().toString() : "no_registry_name";
   }

   public String getName() {
      return this.entityName;
   }

   public void addDrop(IItemStack stack, int min, int max, float chance) {
      if (min >= 0 && max >= 0 && chance >= 0.0F && chance <= 1.0F) {
         this.drops.add(new EntityDrop(stack, min, max, chance));
      } else {
         CraftTweakerAPI.logError(String.format("Invalid value provided: <entity:%s>.addDrop(%s, %d, %d, %s).", this.entityName, stack, min, max, chance));
      }
   }

   public void addDrop(WeightedItemStack stack, int min, int max) {
      this.addDrop(stack.getStack(), min, max, stack.getChance());
   }

   public void addPlayerOnlyDrop(IItemStack stack, int min, int max, float chance) {
      if (min >= 0 && max >= 0 && chance >= 0.0F && chance <= 1.0F) {
         this.drops.add(new EntityDrop(stack, min, max, chance, true));
      } else {
         CraftTweakerAPI.logError(String.format("Invalid value provided: <entity:%s>.addPlayerOnlyDrop(%s, %d, %d, %s).", this.entityName, stack, min, max, chance));
      }
   }

   public void addPlayerOnlyDrop(WeightedItemStack stack, int min, int max) {
      this.addPlayerOnlyDrop(stack.getStack(), min, max, stack.getChance());
   }

   public void removeDrop(IItemStack stack) {
      this.dropsToRemove.add(stack);
   }

   public void clearDrops() {
      this.clearDrops = true;
   }

   public List<IEntityDrop> getDrops() {
      return this.drops;
   }

   public IEntity createEntity(IWorld world) {
      return CraftTweakerMC.getIEntity(this.entityEntry.newInstance(CraftTweakerMC.getWorld(world)));
   }

   public IEntity spawnEntity(IWorld world, IBlockPos pos) {
      IEntity out = this.createEntity(world);
      out.setPosition(pos);
      world.spawnEntity(out);
      return out;
   }

   public Map<IItemStack, IntegerRange> getDropsToAdd() {
      return (Map)this.drops.stream().filter((drop) -> {
         return !drop.isPlayerOnly();
      }).collect(Collectors.toMap(IEntityDrop::getItemStack, IEntityDrop::getRange));
   }

   public Map<IItemStack, IntegerRange> getDropsToAddPlayerOnly() {
      return (Map)this.drops.stream().filter(IEntityDrop::isPlayerOnly).collect(Collectors.toMap(IEntityDrop::getItemStack, IEntityDrop::getRange));
   }

   public List<IItemStack> getDropsToRemove() {
      return this.dropsToRemove;
   }

   public boolean shouldClearDrops() {
      return this.clearDrops;
   }

   public Object getInternal() {
      return this.entityEntry;
   }

   public String toString() {
      return "<entity:" + this.entityName + ">";
   }

   public void addDropFunction(IEntityDropFunction function) {
      this.dropFunctions.add(function);
   }

   public List<IEntityDropFunction> getDropFunctions() {
      return this.dropFunctions;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCEntityDefinition that = (MCEntityDefinition)o;
         return Objects.equals(this.entityEntry, that.entityEntry);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.entityEntry});
   }
}
