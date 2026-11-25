package ivorius.ivtoolkit.tools;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.transform.Mover;
import ivorius.ivtoolkit.world.MockWorld;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IvWorldData {
   public IvBlockCollection blockCollection;
   public List<NBTTagCompound> tileEntities;
   public List<NBTTagCompound> entities;

   public IvWorldData(IvBlockCollection blockCollection, List<NBTTagCompound> tileEntities, List<NBTTagCompound> entities) {
      this.blockCollection = blockCollection;
      this.tileEntities = tileEntities;
      this.entities = entities;
   }

   public IvWorldData(NBTTagCompound compound, MCRegistry registry) {
      compound = compound.copy();
      DataFixer fixer = DataFixesManager.createFixer();
      this.blockCollection = new IvBlockCollection(compound.getCompoundTag("blockCollection"), registry);
      this.tileEntities = new ArrayList();
      this.tileEntities.addAll(NBTTagLists.compoundsFrom(compound, "tileEntities"));
      this.tileEntities.forEach((teCompound) -> {
         NBTStateInjector.recursivelyApply(teCompound, registry, false);
      });
      this.tileEntities.replaceAll((teCompound) -> {
         return fixer.process(FixTypes.BLOCK_ENTITY, teCompound);
      });
      this.entities = new ArrayList();
      this.entities.addAll(NBTTagLists.compoundsFrom(compound, "entities"));
      this.entities.forEach((entityCompound) -> {
         NBTStateInjector.recursivelyApply(entityCompound, registry, false);
      });
      this.tileEntities.replaceAll((teCompound) -> {
         return fixer.process(FixTypes.ENTITY, teCompound);
      });
   }

   public static IvWorldData capture(World world, BlockArea blockArea, boolean captureEntities) {
      return capture(MockWorld.of(world), blockArea, captureEntities);
   }

   public static IvWorldData capture(MockWorld world, BlockArea blockArea, boolean captureEntities) {
      BlockPos referenceCoord = blockArea.getLowerCorner();
      BlockPos invertedReference = BlockPositions.invert(referenceCoord);
      int[] size = blockArea.areaSize();
      IvBlockCollection blockCollection = new IvBlockCollection(size[0], size[1], size[2]);
      List<NBTTagCompound> tileEntities = new ArrayList();

      for(BlockPos worldCoord : blockArea) {
         BlockPos dataCoord = worldCoord.subtract(blockArea.getLowerCorner());
         blockCollection.setBlockState(dataCoord, world.getBlockState(worldCoord));
         TileEntity tileEntity = world.getTileEntity(worldCoord);
         if (tileEntity != null) {
            Mover.moveTileEntity(tileEntity, invertedReference);
            NBTTagCompound teCompound = NBTCompoundObjectsMC.write(tileEntity, true);
            NBTStateInjector.recursivelyInject(teCompound);
            tileEntities.add(teCompound);
            Mover.moveTileEntity(tileEntity, referenceCoord);
         }
      }

      List<NBTTagCompound> entities = captureEntities ? (List)world.getEntities(blockArea.asAxisAlignedBB(), saveableEntityPredicate()).stream().map((entity) -> {
         Mover.moveEntity(entity, invertedReference);
         NBTTagCompound entityCompound = NBTCompoundObjectsMC.write(entity, true);
         NBTStateInjector.recursivelyInject(entityCompound);
         Mover.moveEntity(entity, referenceCoord);
         return entityCompound;
      }).collect(Collectors.toList()) : Collections.emptyList();
      return new IvWorldData(blockCollection, tileEntities, entities);
   }

   public static Predicate<Entity> saveableEntityPredicate() {
      return (entity) -> {
         return !(entity instanceof EntityPlayer);
      };
   }

   /** @deprecated */
   @Deprecated
   public NBTTagCompound createTagCompound(BlockPos referenceCoord) {
      return this.createTagCompound();
   }

   public NBTTagCompound createTagCompound() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setTag("blockCollection", this.blockCollection.createTagCompound());
      compound.setTag("tileEntities", NBTTagLists.write(this.tileEntities));
      compound.setTag("entities", NBTTagLists.write(this.entities));
      return compound;
   }
}
