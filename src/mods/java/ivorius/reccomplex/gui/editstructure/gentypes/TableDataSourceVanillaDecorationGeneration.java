package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.editstructure.TableDataSourceBiomeGenList;
import ivorius.reccomplex.gui.editstructure.TableDataSourceDimensionGenList;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.decoration.RCBiomeDecorator;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaDecorationGeneration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceVanillaDecorationGeneration extends TableDataSourceSegmented {
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private VanillaDecorationGeneration generationInfo;

   public TableDataSourceVanillaDecorationGeneration(TableNavigator navigator, TableDelegate delegate, VanillaDecorationGeneration generationInfo) {
      this.navigator = navigator;
      this.tableDelegate = delegate;
      this.generationInfo = generationInfo;
      this.addSegment(0, new TableDataSourceGenerationType(generationInfo, navigator, delegate));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellEnum<RCBiomeDecorator.DecorationType> cell = new TableCellEnum<RCBiomeDecorator.DecorationType>("type", generationInfo.type, TableCellEnum.options(RCBiomeDecorator.DecorationType.values(), "reccomplex.generationInfo.decoration.types.", true));
         cell.addListener((v) -> {
            generationInfo.type = v;
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.vanilla.type"), cell);
      }});
      this.addSegment(2, new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            generationInfo.generationWeight = TableCells.toDouble(val);
         }, generationInfo.generationWeight);
      }});
      this.addSegment(3, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceBiomeGenList(generationInfo.biomeWeights, delegate, navigator);
      }, () -> {
         return IvTranslations.get("reccomplex.gui.biomes");
      }).addNavigation(() -> {
         return new TableDataSourceDimensionGenList(generationInfo.dimensionWeights, delegate, navigator);
      }, () -> {
         return IvTranslations.get("reccomplex.gui.dimensions");
      }).withTitle("").buildDataSource());
      this.addSegment(4, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceSelectivePlacer(generationInfo.placer, navigator, delegate);
      }, () -> {
         return IvTranslations.get("reccomplex.placer");
      }, () -> {
         return IvTranslations.getLines("reccomplex.placer.tooltip");
      }).withTitle("").buildDataSource());
   }
}
