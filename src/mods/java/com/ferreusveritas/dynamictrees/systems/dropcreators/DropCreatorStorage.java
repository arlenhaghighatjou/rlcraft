package com.ferreusveritas.dynamictrees.systems.dropcreators;

import com.ferreusveritas.dynamictrees.api.treedata.IDropCreator;
import com.ferreusveritas.dynamictrees.api.treedata.IDropCreatorStorage;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DropCreatorStorage implements IDropCreatorStorage {
   private final HashMap<ResourceLocation, IDropCreator> dropCreators = new HashMap();

   public DropCreatorStorage() {
   }

   public ResourceLocation getName() {
      return new ResourceLocation("dynamictrees", "storage");
   }

   public boolean addDropCreator(IDropCreator dropCreator) {
      this.dropCreators.put(dropCreator.getName(), dropCreator);
      return true;
   }

   public IDropCreator findDropCreator(ResourceLocation name) {
      return (IDropCreator)this.dropCreators.get(name);
   }

   public boolean remDropCreator(ResourceLocation name) {
      return this.dropCreators.remove(name) != null;
   }

   public Map<ResourceLocation, IDropCreator> getDropCreators() {
      return new HashMap(this.dropCreators);
   }

   private List<ItemStack> makeDropListIfNull(List<ItemStack> dropList) {
      if (dropList == null) {
         dropList = new ArrayList();
      }

      return dropList;
   }

   public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, Random random, List<ItemStack> dropList, int soilLife, int fortune) {
      dropList = this.makeDropListIfNull(dropList);

      for(IDropCreator dropCreator : this.dropCreators.values()) {
         dropList = dropCreator.getHarvestDrop(world, species, leafPos, random, dropList, soilLife, fortune);
      }

      return dropList;
   }

   public List<ItemStack> getVoluntaryDrop(World world, Species species, BlockPos rootPos, Random random, List<ItemStack> dropList, int soilLife) {
      dropList = this.makeDropListIfNull(dropList);

      for(IDropCreator dropCreator : this.dropCreators.values()) {
         dropList = dropCreator.getVoluntaryDrop(world, species, rootPos, random, dropList, soilLife);
      }

      return dropList;
   }

   public List<ItemStack> getLeavesDrop(IBlockAccess access, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, int fortune) {
      dropList = this.makeDropListIfNull(dropList);

      for(IDropCreator dropCreator : this.dropCreators.values()) {
         dropList = dropCreator.getLeavesDrop(access, species, breakPos, random, dropList, fortune);
      }

      return dropList;
   }

   public List<ItemStack> getLogsDrop(World world, Species species, BlockPos rootPos, Random random, List<ItemStack> dropList, float volume) {
      dropList = this.makeDropListIfNull(dropList);

      for(IDropCreator dropCreator : this.dropCreators.values()) {
         dropList = dropCreator.getLogsDrop(world, species, rootPos, random, dropList, volume);
      }

      return dropList;
   }
}
