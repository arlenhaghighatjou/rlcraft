package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMultimap.Builder;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.math.Transforms;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazePassages;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.maze.components.MazeRooms;
import ivorius.ivtoolkit.maze.components.PlacedMazeComponent;
import ivorius.ivtoolkit.maze.components.SetMazeComponent;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.apache.commons.lang3.tuple.Pair;

public class WorldGenMaze {
   public WorldGenMaze() {
   }

   public static Boolean generate(StructureSpawnContext context, PlacedStructure placedComponent, BlockPos pos) {
      Structure<?> structure = (Structure)StructureRegistry.INSTANCE.get(placedComponent.structureID);
      if (structure == null) {
         RecurrentComplex.logger.error(String.format("Could not find structure '%s' for maze", placedComponent.structureID));
         return false;
      } else {
         StructureGenerator<?> generator = (new StructureGenerator(structure)).asChild(context).generationInfo(placedComponent.generationInfoID).transform(Transforms.apply(placedComponent.transform, context.transform)).lowerCoord(lowerCoord(structure, placedComponent.lowerCoord, placedComponent.transform, pos, context.transform)).structureID(placedComponent.structureID).instanceData(placedComponent.instanceData);
         return !context.generateMaturity.isFirstTime() && context.generationBB != null && !context.generationBB.intersectsWith((StructureBoundingBox)generator.boundingBox().get()) ? null : generator.generate().succeeded();
      }
   }

   protected static BlockPos lowerCoord(Structure structure, BlockPos lowerCoord, AxisAlignedTransform2D placedTransform, BlockPos pos, AxisAlignedTransform2D transform) {
      int[] placedSize = RCAxisAlignedTransform.applySize(placedTransform, structure.size());
      return transform.apply(lowerCoord, IvVecMathHelper.sub(new int[]{2, 2, 2}, new int[][]{placedSize})).add(pos);
   }

   @Nullable
   public static PlacedStructure place(EaglercraftRandom random, Environment environment, BlockPos shift, int[] roomSize, PlacedMazeComponent<MazeComponentStructure<Connector>, Connector> placedComponent, BlockPos pos, AxisAlignedTransform2D transform) {
      MazeComponentStructure<Connector> componentInfo = (MazeComponentStructure)placedComponent.component();
      Structure<?> structure = (Structure)StructureRegistry.INSTANCE.get(componentInfo.structureID);
      if (structure == null) {
         RecurrentComplex.logger.error(String.format("Could not find structure '%s' for maze", componentInfo.structureID));
         return null;
      } else {
         Environment childEnvironment = environment.copy(componentInfo.variableDomain);
         environment.variables.fill(childEnvironment.variables);
         BlockPos compLowerPos = getBoundingBox(roomSize, placedComponent, structure, componentInfo.transform).add(shift);
         NBTStorable instanceData = (NBTStorable)(new StructureGenerator(structure)).seed(Long.valueOf(random.nextLong())).environment(childEnvironment).transform(Transforms.apply(componentInfo.transform, transform)).lowerCoord(lowerCoord(structure, compLowerPos, componentInfo.transform, pos, transform)).structureID(componentInfo.structureID).instanceData().orElse((Object)null);
         return new PlacedStructure(componentInfo.structureID, componentInfo.structureID, componentInfo.transform, compLowerPos, instanceData.writeToNBT());
      }
   }

   protected static BlockPos getBoundingBox(int[] roomSize, PlacedMazeComponent<MazeComponentStructure<Connector>, Connector> placedComponent, Structure structure, AxisAlignedTransform2D transform) {
      int[] scaledMazePosition = IvVecMathHelper.mul(new int[][]{placedComponent.shift().getCoordinates(), roomSize});
      int[] size = RCAxisAlignedTransform.applySize(transform, structure.size());
      int[] expectedSize = IvVecMathHelper.mul(new int[][]{((MazeComponentStructure)placedComponent.component()).getSize(), roomSize});
      int[] sizeDependentShift = new int[expectedSize.length];

      for(int i = 0; i < size.length; ++i) {
         sizeDependentShift[i] = (expectedSize[i] - size[i]) / 2;
      }

      return (new BlockPos(scaledMazePosition[0], scaledMazePosition[1], scaledMazePosition[2])).add(sizeDependentShift[0], sizeDependentShift[1], sizeDependentShift[2]);
   }

   public static Stream<MazeComponentStructure<Connector>> transforms(Structure info, MazeGeneration mazeInfo, AxisAlignedTransform2D transform, ConnectorFactory factory, Environment environment, Collection<Connector> blockedConnections) {
      int[] compSize = mazeInfo.mazeComponent.boundsSize();
      List<AxisAlignedTransform2D> transforms = (List)Transforms.transformStream((r) -> {
         return info.isRotatable() || transform.apply(r) == 0;
      }, (m) -> {
         return info.isMirrorable() || m == 0;
      }).collect(Collectors.toList());
      double compWeight = mazeInfo.getWeight() * (double)info.declaredVariables().chance(environment) * (double)RCConfig.tweakedSpawnRate(StructureRegistry.INSTANCE.id(info));
      double splitCompWeight = compWeight / (double)transforms.size();
      return transforms.stream().map((t) -> {
         return transform(info, mazeInfo.mazeComponent, t, compSize, splitCompWeight, factory, environment, blockedConnections);
      });
   }

   public static MazeComponentStructure<Connector> transform(Structure info, SavedMazeComponent comp, AxisAlignedTransform2D transform, int[] size, double weight, ConnectorFactory factory, Environment environment, Collection<Connector> blockedConnections) {
      Collection<MazeRoom> rooms = comp.getRooms();
      Set<MazeRoom> transformedRooms = (Set)rooms.stream().map((input) -> {
         return MazeRooms.rotated(input, transform, size);
      }).collect(Collectors.toSet());
      Map<MazePassage, Connector> transformedExits = new HashMap();
      buildExitPaths(environment, factory, comp.getExitPaths(), rooms).forEach((path) -> {
         Connector var10000 = (Connector)transformedExits.put(MazePassages.rotated((MazePassage)path.getKey(), transform, size), path.getValue());
      });
      addMissingExits(transformedRooms, transformedExits, comp.defaultConnector.toConnector(factory));
      Builder<MazePassage, MazePassage> reachability = ImmutableMultimap.builder();
      comp.reachability.build(reachability, transform, size, SavedMazeReachability.notBlocked(blockedConnections, transformedExits), transformedExits.keySet());
      addExternalReachability(reachability, transformedExits, blockedConnections);
      return new MazeComponentStructure<Connector>(weight, StructureRegistry.INSTANCE.id(info), environment.variables, transform, ImmutableSet.copyOf(transformedRooms), ImmutableMap.copyOf(transformedExits), reachability.build());
   }

   public static Builder<MazePassage, MazePassage> addExternalReachability(Builder<MazePassage, MazePassage> reachability, Map<MazePassage, Connector> transformedExits, Collection<Connector> blockedConnections) {
      transformedExits.keySet().stream().filter((passage) -> {
         return !blockedConnections.contains(transformedExits.get(passage));
      }).forEach((passage) -> {
         reachability.put(passage, passage.inverse());
      });
      return reachability;
   }

   public static <C> SetMazeComponent<C> createCompleteComponent(Set<MazeRoom> rooms, Map<MazePassage, C> exits, C wallConnector) {
      SetMazeComponent<C> component = new SetMazeComponent(rooms, exits, HashMultimap.create());
      addMissingExits(component.rooms, component.exits, wallConnector);
      return component;
   }

   public static <C> void addMissingExits(Set<MazeRoom> rooms, Map<MazePassage, C> exits, C connector) {
      for(MazeRoom room : rooms) {
         MazeRooms.neighborPassages(room).filter((passage) -> {
            return (void)(!exits.containsKey(passage) && (!rooms.contains(passage.getLeft()) || !rooms.contains(passage.getRight())));
         }).forEach((passage) -> {
            exits.put(passage, connector);
         });
      }

   }

   public static Stream<Entry<MazePassage, Connector>> buildExitPaths(Environment environment, ConnectorFactory factory, List<SavedMazePathConnection> mazeExits, Collection<MazeRoom> rooms) {
      return mazeExits.stream().map(SavedMazePaths.buildFunction(environment, factory)).map((e) -> {
         return (Stream<Entry<MazePassage, Connector>>)(rooms.contains(((MazePassage)e.getKey()).getSource()) ? e : Pair.of(((MazePassage)e.getKey()).inverse(), e.getValue()));
      });
   }
}
