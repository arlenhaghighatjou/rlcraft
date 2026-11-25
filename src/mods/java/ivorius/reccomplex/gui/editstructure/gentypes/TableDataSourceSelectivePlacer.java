package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.editstructure.placer.TableDataSourcePlacer;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.SelectivePlacer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TableDataSourceSelectivePlacer extends TableDataSourceSegmented {
   protected TableNavigator navigator;
   protected TableDelegate tableDelegate;
   protected SelectivePlacer placer;

   public TableDataSourceSelectivePlacer(SelectivePlacer placer, TableNavigator navigator, TableDelegate delegate) {
      this.navigator = navigator;
      this.tableDelegate = delegate;
      this.placer = placer;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellIntTextField cell = new TableCellIntTextField((String)null, placer.baseline);
         cell.addListener((i) -> {
            placer.baseline = i.intValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.placer.baseline"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.placer.baseline.tooltip"));
      }});
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), IvTranslations.getLines("reccomplex.transformer.block.source.tooltip"), placer.sourceMatcher, (Object)null));
      this.addSegment(2, new TableDataSourcePlacer(placer.placer, delegate, navigator));
   }
}
