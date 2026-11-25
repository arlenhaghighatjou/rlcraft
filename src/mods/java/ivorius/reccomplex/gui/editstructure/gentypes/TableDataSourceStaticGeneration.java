package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceBlockSurfacePos;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.editstructure.gentypes.staticgen.TableDataSourceStaticPattern;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.StaticGeneration;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceStaticGeneration extends TableDataSourceSegmented {
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private StaticGeneration generationInfo;

   public TableDataSourceStaticGeneration(TableNavigator navigator, TableDelegate delegate, StaticGeneration generationInfo) {
      this.navigator = navigator;
      this.tableDelegate = delegate;
      this.generationInfo = generationInfo;
      this.addSegment(0, new TableDataSourceGenerationType(generationInfo, navigator, delegate));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellBoolean cell = new TableCellBoolean("relativeToSpawn", generationInfo.relativeToSpawn);
         cell.addListener((val) -> {
            generationInfo.relativeToSpawn = val.booleanValue();
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.static.spawn"), cell);
      }});
      BlockSurfacePos var10004 = generationInfo.position;
      generationInfo.getClass();
      this.addSegment(2, new TableDataSourceBlockSurfacePos(var10004, generationInfo::setPosition));
      this.addSegment(3, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceSelectivePlacer(generationInfo.placer, navigator, delegate);
      }, () -> {
         return IvTranslations.get("reccomplex.placer");
      }, () -> {
         return IvTranslations.getLines("reccomplex.placer.tooltip");
      }).withTitle("").buildDataSource());
      this.addSegment(4, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.dimensions"), generationInfo.dimensionExpression, (Object)null));
      TableCellMultiBuilder var10002 = TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceStaticPattern(generationInfo.pattern, delegate);
      });
      generationInfo.getClass();
      this.addSegment(5, var10002.enabled(generationInfo::hasPattern).addAction(() -> {
         generationInfo.pattern = generationInfo.hasPattern() ? null : new StaticGeneration.Pattern();
      }, () -> {
         return generationInfo.hasPattern() ? IvTranslations.get("reccomplex.gui.remove") : IvTranslations.get("reccomplex.gui.add");
      }, (Supplier)null).withTitle(IvTranslations.get("reccomplex.generationInfo.static.pattern")).buildDataSource());
   }
}
