package ivorius.reccomplex.gui.worldscripts.mazegenerator;

import com.google.common.collect.Iterables;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.client.rendering.SelectionQuadCache;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.TableDirections;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMazePath extends TableDataSourceSegmented {
   public static final String[] COORD_NAMES = new String[]{"x", "y", "z"};
   private SavedMazePath mazePath;
   private Selection bounds;
   private TableDelegate tableDelegate;
   private TableCellButton invertableButton;
   private final TableDataSourceMazeRoom source;
   protected MazeVisualizationContext visualizationContext;

   public TableDataSourceMazePath(SavedMazePath mazePath, Selection bounds, TableDelegate tableDelegate, TableNavigator navigator) {
      this.mazePath = mazePath;
      this.bounds = bounds;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, this.source = new TableDataSourceMazeRoom(mazePath.sourceRoom, (mazeRoom) -> {
         this.mazePath.sourceRoom = mazeRoom;
         if (this.invertableButton != null) {
            this.invertableButton.setEnabled(this.isInvertable());
         }

      }, bounds.bounds(), (List)Arrays.stream(COORD_NAMES).map((s) -> {
         return IvTranslations.get("reccomplex.generationInfo.mazeComponent.position." + s);
      }).collect(Collectors.toList()), (List)Arrays.stream(COORD_NAMES).map((s) -> {
         return IvTranslations.getLines("reccomplex.generationInfo.mazeComponent.position." + s + ".tooltip");
      }).collect(Collectors.toList())));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellEnum.Option<EnumFacing>[] optionList = TableDirections.getDirectionOptions(EnumFacing.VALUES);
         TableCellEnum<EnumFacing> cell = new TableCellEnum<EnumFacing>("side", directionFromPath(mazePath), optionList);
         cell.addListener((val) -> {
            SavedMazePathConnection path = pathFromDirection(val, mazePath.sourceRoom.getCoordinates());
            mazePath.pathDimension = path.path.pathDimension;
            mazePath.pathGoesUp = path.path.pathGoesUp;
            tableDelegate.reloadData();
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.mazeComponent.path.side"), cell);
      }});
      this.addSegment(2, new Supplier[]{() -> {
         this.invertableButton = new TableCellButton("actions", "inverse", IvTranslations.get("reccomplex.generationInfo.mazeComponent.path.invert"), this.isInvertable());
         this.invertableButton.addAction(() -> {
            mazePath.set(mazePath.inverse());
            this.source.room = mazePath.sourceRoom;
            tableDelegate.reloadData();
         });
         return new TitledCell(this.invertableButton);
      }});
   }

   public static boolean contains(int[] array, Selection bounds) {
      return bounds.compile(true).containsKey(new MazeRoom(array));
   }

   public static EnumFacing directionFromPath(SavedMazePath path) {
      switch(path.pathDimension) {
      case 0:
         return path.pathGoesUp ? EnumFacing.EAST : EnumFacing.WEST;
      case 1:
         return path.pathGoesUp ? EnumFacing.UP : EnumFacing.DOWN;
      case 2:
         return path.pathGoesUp ? EnumFacing.SOUTH : EnumFacing.NORTH;
      default:
         return null;
      }
   }

   public static SavedMazePathConnection pathFromDirection(EnumFacing side, int[] room) {
      int pathDim = side.getFrontOffsetX() != 0 ? 0 : (side.getFrontOffsetY() != 0 ? 1 : (side.getFrontOffsetZ() != 0 ? 2 : -1));
      int offset = side.getFrontOffsetX() + side.getFrontOffsetY() + side.getFrontOffsetZ();
      return new SavedMazePathConnection(pathDim, new MazeRoom(new int[]{room[0], room[1], room[2]}), offset > 0, "Path", Collections.emptyList());
   }

   public static void addToSelection(Selection selection, SavedMazePath path) {
      selection.add(Selection.Area.from(true, path.sourceRoom.getCoordinates(), path.sourceRoom.getCoordinates(), "s"));
      selection.add(Selection.Area.from(true, path.getDestRoom().getCoordinates(), path.getDestRoom().getCoordinates(), "d"));
   }

   @Nonnull
   public static GuiHider.Visualizer visualizePaths(MazeVisualizationContext visualizationContext, Collection<SavedMazePath> mazePaths) {
      if (mazePaths.size() <= 0) {
         return new SelectionQuadCache.Visualizer(new Selection(0), visualizationContext);
      } else {
         Selection selection = new Selection(((SavedMazePath)Iterables.getFirst(mazePaths, (Object)null)).sourceRoom.getDimensions());
         mazePaths.forEach((p) -> {
            addToSelection(selection, p);
         });
         return new SelectionQuadCache.Visualizer(selection, visualizationContext);
      }
   }

   public TableDataSourceMazePath visualizing(MazeVisualizationContext context) {
      this.visualizationContext = context;
      return this;
   }

   @Nonnull
   public String title() {
      return "Path";
   }

   protected boolean isInvertable() {
      return contains(this.mazePath.inverse().getSourceRoom().getCoordinates(), this.bounds);
   }

   public boolean canVisualize() {
      return this.visualizationContext != null;
   }

   public GuiHider.Visualizer visualizer() {
      return visualizePaths(this.visualizationContext, Collections.singletonList(this.mazePath));
   }
}
