package ivorius.reccomplex.world.gen.script;

import com.google.common.collect.ImmutableListMultimap;
import gnu.trove.list.array.TIntArrayList;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.maze.components.MazeComponentConnector;
import ivorius.ivtoolkit.maze.components.MazePredicate;
import ivorius.ivtoolkit.maze.components.MazePredicateMany;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.maze.components.MorphingMazeComponent;
import ivorius.ivtoolkit.maze.components.PlacedMazeComponent;
import ivorius.ivtoolkit.maze.components.SetMazeComponent;
import ivorius.ivtoolkit.tools.IvNBTHelper;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.ivtoolkit.tools.Visitor;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.worldscripts.mazegenerator.TableDataSourceWorldScriptMazeGenerator;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.IntAreas;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.Connector;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.ConnectorFactory;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.ConnectorStrategy;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.MazeComponentStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.PlacedStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeComponent;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeReachability;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.WorldGenMaze;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.BlockedConnectorStrategy;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.LimitAABBStrategy;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.MazeRule;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.MazeRuleRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldScriptMazeGenerator implements WorldScript<WorldScriptMazeGenerator.InstanceData> {
   public final List<MazeRule> rules = new ArrayList();
   public String mazeID = "";
   public BlockPos structureShift = BlockPos.ORIGIN;
   public int[] roomSize = new int[]{3, 5, 3};
   public SavedMazeComponent mazeComponent = new SavedMazeComponent();

   public WorldScriptMazeGenerator() {
      this.mazeComponent.reachability.groupByDefault = false;
   }

   public static <C> void blockRooms(MorphingMazeComponent<C> component, Set<MazeRoom> rooms, C wallConnector) {
      component.add(WorldGenMaze.createCompleteComponent(rooms, Collections.emptyMap(), wallConnector));
   }

   public static <C> void enclose(MorphingMazeComponent<C> component, MazeRoom lower, MazeRoom higher, C wallConnector) {
      if (lower.getDimensions() != higher.getDimensions()) {
         throw new IllegalArgumentException();
      } else {
         Set<MazeRoom> rooms = new HashSet();
         int[] coords = lower.getCoordinates();

         for(int i = 0; i < coords.length; ++i) {
            int lowest = lower.getCoordinate(i);
            int highest = higher.getCoordinate(i);
            IntAreas.visitCoordsExcept(lower.getCoordinates(), higher.getCoordinates(), TIntArrayList.wrap(new int[]{i}), (ints) -> {
               ints[finalI] = lowest;
               rooms.add(new MazeRoom(ints));
               ints[finalI] = highest;
               rooms.add(new MazeRoom(ints));
               return (void)true;
            });
         }

         blockRooms(component, rooms, wallConnector);
      }
   }

   public void generate(StructureSpawnContext context, RunTransformer transformer, WorldScriptMazeGenerator.InstanceData instanceData, BlockPos pos) {
      for(PlacedStructure placedComponent : instanceData.placedStructures) {
         WorldGenMaze.generate(context, placedComponent, pos);
      }

   }

   public String getDisplayString() {
      return IvTranslations.get("reccomplex.worldscript.mazeGen");
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(BlockPos realWorldPos, TableNavigator navigator, TableDelegate tableDelegate) {
      return new TableDataSourceWorldScriptMazeGenerator(this, realWorldPos, tableDelegate, navigator);
   }

   public String getMazeID() {
      return this.mazeID;
   }

   public void setMazeID(String mazeID) {
      this.mazeID = mazeID;
   }

   public BlockPos getStructureShift() {
      return this.structureShift;
   }

   public void setStructureShift(BlockPos structureShift) {
      this.structureShift = structureShift;
   }

   public int[] getRoomSize() {
      return (int[])this.roomSize.clone();
   }

   public void setRoomSize(int[] roomSize) {
      this.roomSize = roomSize;
   }

   public SavedMazeComponent getMazeComponent() {
      return this.mazeComponent;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.mazeID = compound.getString("mazeID");
      if (compound.hasKey("mazeComponent", 10)) {
         this.mazeComponent.readFromNBT(compound.getCompoundTag("mazeComponent"));
      } else {
         NBTTagCompound rooms = compound.getCompoundTag("rooms");
         this.mazeComponent.rooms.readFromNBT(rooms);
         if (compound.hasKey("roomNumbers", 11)) {
            this.mazeComponent.rooms.add(new Selection.Area(true, new int[]{0, 0, 0}, IvVecMathHelper.sub(IvNBTHelper.readIntArrayFixedSize("roomNumbers", 3, compound), new int[][]{{1, 1, 1}})));
         }

         if (compound.hasKey("blockedRoomAreas", 9)) {
            NBTTagList blockedRoomsList = compound.getTagList("blockedRoomAreas", 10);

            for(int i = 0; i < blockedRoomsList.tagCount(); ++i) {
               NBTTagCompound blockedRoomTag = blockedRoomsList.getCompoundTagAt(i);
               this.mazeComponent.rooms.add(new Selection.Area(false, IvNBTHelper.readIntArrayFixedSize("min", 3, blockedRoomTag), IvNBTHelper.readIntArrayFixedSize("max", 3, blockedRoomTag)));
            }
         }

         this.mazeComponent.exitPaths.clear();
         this.mazeComponent.exitPaths.addAll(NBTCompoundObjects.readListFrom(compound, "mazeExits", SavedMazePathConnection::new));
         this.mazeComponent.defaultConnector.id = "Wall";
         this.mazeComponent.reachability.set(Collections.emptyList(), Collections.emptyList(), false);
      }

      this.structureShift = BlockPositions.readFromNBT("structureShift", compound);
      this.roomSize = IvNBTHelper.readIntArrayFixedSize("roomSize", 3, compound);
      this.rules.clear();
      List var10000 = this.rules;
      Stream var10001 = NBTTagLists.compoundsFrom(compound, "rules").stream();
      MazeRuleRegistry var10002 = MazeRuleRegistry.INSTANCE;
      MazeRuleRegistry.INSTANCE.getClass();
      var10000.addAll((Collection)var10001.map(var10002::read).collect(Collectors.toList()));
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setString("mazeID", this.mazeID);
      compound.setTag("mazeComponent", NBTCompoundObjects.write(this.mazeComponent));
      BlockPositions.writeToNBT("structureShift", this.structureShift, compound);
      compound.setIntArray("roomSize", this.roomSize);
      Stream var10002 = this.rules.stream();
      MazeRuleRegistry var10003 = MazeRuleRegistry.INSTANCE;
      MazeRuleRegistry.INSTANCE.getClass();
      NBTTagLists.writeTo(compound, "rules", (List)var10002.map(var10003::write).collect(Collectors.toList()));
   }

   public WorldScriptMazeGenerator.InstanceData prepareInstanceData(StructurePrepareContext context, BlockPos pos) {
      WorldScriptMazeGenerator.InstanceData instanceData = new WorldScriptMazeGenerator.InstanceData();
      instanceData.placedStructures.addAll((Collection)this.getPlacedRooms(context.random, context.transform, context.environment).stream().map((placedComponent) -> {
         return WorldGenMaze.place(context.random, context.environment.child(), this.structureShift, this.roomSize, placedComponent, pos, context.transform);
      }).filter(Objects::nonNull).collect(Collectors.toList()));
      return instanceData;
   }

   public WorldScriptMazeGenerator.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new WorldScriptMazeGenerator.InstanceData(nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound());
   }

   public List<PlacedMazeComponent<MazeComponentStructure<Connector>, Connector>> getPlacedRooms(EaglercraftRandom random, AxisAlignedTransform2D transform, Environment environment) {
      if (this.mazeComponent.rooms.isEmpty()) {
         return null;
      } else {
         ConnectorFactory factory = new ConnectorFactory();
         Connector wallConnector = factory.get("Wall");
         Connector defaultConnector = this.mazeComponent.defaultConnector.toConnector(factory);
         Set<Connector> blockedConnections = Collections.singleton(wallConnector);
         int[] boundsHigher = this.mazeComponent.rooms.boundsHigher();
         int[] boundsLower = this.mazeComponent.rooms.boundsLower();
         int[] oneArray = new int[boundsHigher.length];
         Arrays.fill(oneArray, 1);
         int[] outsideBoundsHigher = IvVecMathHelper.add(new int[][]{boundsHigher, oneArray});
         int[] outsideBoundsLower = IvVecMathHelper.sub(boundsLower, new int[][]{oneArray});
         List<MazeComponentStructure<Connector>> transformedComponents = (List)MazeGeneration.structures(StructureRegistry.INSTANCE, this.mazeID).flatMap((pair) -> {
            return ((Structure)pair.getLeft()).declaredVariables().omega(environment, true).flatMap((domain) -> {
               return WorldGenMaze.transforms((Structure)pair.getLeft(), (MazeGeneration)pair.getRight(), transform, factory, environment.copy(domain), blockedConnections);
            });
         }).collect(Collectors.toList());
         MorphingMazeComponent<Connector> maze = new SetMazeComponent();
         enclose(maze, new MazeRoom(outsideBoundsLower), new MazeRoom(outsideBoundsHigher), defaultConnector);
         blockRooms(maze, this.mazeComponent.rooms.compile(false).keySet(), defaultConnector);
         WorldGenMaze.buildExitPaths(environment, factory, this.mazeComponent.exitPaths, maze.rooms()).forEach((path) -> {
            Connector var10000 = (Connector)maze.exits().put(path.getKey(), path.getValue());
         });
         maze.reachability().putAll(WorldGenMaze.addExternalReachability(ImmutableListMultimap.builder(), maze.exits(), blockedConnections).build());
         maze.reachability().putAll(this.mazeComponent.reachability.build(ImmutableListMultimap.builder(), AxisAlignedTransform2D.ORIGINAL, this.mazeComponent.boundsSize(), SavedMazeReachability.notBlocked(blockedConnections, maze.exits()), maze.exits().keySet()).build());
         ConnectorStrategy connectorStrategy = new ConnectorStrategy();
         List<MazePredicate<Connector>> predicates = (List)this.rules.stream().map((r) -> {
            return r.build(this, blockedConnections, factory, transformedComponents, connectorStrategy);
         }).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
         predicates.add(new LimitAABBStrategy(outsideBoundsHigher));
         predicates.add(new BlockedConnectorStrategy(blockedConnections));
         int totalRooms = this.mazeComponent.rooms.compile(true).size();
         ExecutorService executor = Executors.newSingleThreadExecutor();
         Future<List<PlacedMazeComponent<MazeComponentStructure<Connector>, Connector>>> future = executor.submit(() -> {
            return MazeComponentConnector.connect(maze, transformedComponents, connectorStrategy, new MazePredicateMany(predicates), random, RCConfig.mazePlacementReversesPerRoom >= 0.0F ? MathHelper.floor((float)totalRooms * RCConfig.mazePlacementReversesPerRoom + 0.5F) : MazeComponentConnector.INFINITE_REVERSES);
         });
         executor.shutdown();

         try {
            return (List)future.get(RCConfig.mazeTimeout, TimeUnit.MILLISECONDS);
         } catch (TimeoutException var22) {
            future.cancel(true);
            throw new WorldScriptMazeGenerator.GenerationException("Maze generation timed out: " + this.mazeID);
         } catch (InterruptedException | ExecutionException var23) {
            Throwable t = var23.getCause();
            if (t instanceof Error) {
               throw (Error)t;
            } else {
               throw new WorldScriptMazeGenerator.GenerationException("Error generating maze; " + this.mazeID, t);
            }
         }
      }
   }

   public static class GenerationException extends RuntimeException implements StructureGenerator.ExpectedException {
      public GenerationException(String message) {
         super(message);
      }

      public GenerationException(String message, Throwable cause) {
         super(message, cause);
      }

      public boolean isExpected() {
         return this.getCause() == null;
      }
   }

   public static class InstanceData implements NBTStorable {
      public final List<PlacedStructure> placedStructures = new ArrayList();

      public InstanceData() {
      }

      public InstanceData(NBTTagCompound compound) {
         this.placedStructures.addAll(NBTCompoundObjects.readListFrom(compound, "placedStructures", PlacedStructure::new));
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         NBTCompoundObjects.writeListTo(compound, "placedStructures", this.placedStructures);
         return compound;
      }
   }
}
