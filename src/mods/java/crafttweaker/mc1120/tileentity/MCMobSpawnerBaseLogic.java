package crafttweaker.mc1120.tileentity;

import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.tileentity.IMobSpawnerBaseLogic;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.entity.MCEntityDefinition;
import java.util.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MCMobSpawnerBaseLogic implements IMobSpawnerBaseLogic {
   private final MobSpawnerBaseLogic mobSpawnerBaseLogic;

   public MCMobSpawnerBaseLogic(MobSpawnerBaseLogic mobSpawnerBaseLogic) {
      this.mobSpawnerBaseLogic = mobSpawnerBaseLogic;
   }

   public IEntityDefinition getEntityDefinition() {
      ResourceLocation entityId = this.mobSpawnerBaseLogic.getEntityId();
      return entityId != null && ForgeRegistries.ENTITIES.containsKey(entityId) ? new MCEntityDefinition((EntityEntry)ForgeRegistries.ENTITIES.getValue(entityId)) : null;
   }

   public void setEntityDefinition(IEntityDefinition entityDefinition) {
      Object internal = entityDefinition.getInternal();
      if (internal instanceof EntityEntry) {
         this.mobSpawnerBaseLogic.setEntityId(((EntityEntry)internal).delegate.name());
      }

   }

   public void updateSpawner() {
      this.mobSpawnerBaseLogic.updateSpawner();
   }

   public IData getNbtData() {
      return NBTConverter.from(this.mobSpawnerBaseLogic.writeToNBT(new NBTTagCompound()), true);
   }

   public void setNbtData(IData nbtData) {
      this.mobSpawnerBaseLogic.readFromNBT((NBTTagCompound)NBTConverter.from(nbtData));
   }

   public void setDelayToMin() {
      this.mobSpawnerBaseLogic.setDelayToMin(1);
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.mobSpawnerBaseLogic.getSpawnerWorld());
   }

   public IBlockPos getBlockPos() {
      return CraftTweakerMC.getIBlockPos(this.mobSpawnerBaseLogic.getSpawnerPosition());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCMobSpawnerBaseLogic that = (MCMobSpawnerBaseLogic)o;
         return Objects.equals(this.mobSpawnerBaseLogic, that.mobSpawnerBaseLogic);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.mobSpawnerBaseLogic});
   }
}
