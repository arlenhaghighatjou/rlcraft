package ivorius.reccomplex.gui.editstructure.transformers;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerProperty;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBTProperty extends TableDataSourceSegmented {
   private TransformerProperty transformer;
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private TableCellString propertyIDCell;
   private TableCellString propertyValueCell;

   public TableDataSourceBTProperty(TransformerProperty transformer, TableNavigator navigator, TableDelegate delegate) {
      this.transformer = transformer;
      this.navigator = navigator;
      this.tableDelegate = delegate;
      this.addSegment(0, new TableDataSourceTransformer(transformer, delegate, navigator));
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.sources"), IvTranslations.getLines("reccomplex.transformer.block.source.tooltip"), transformer.sourceMatcher, (Object)null));
      this.addSegment(2, new TableDataSourceSupplied(new Supplier[]{() -> {
         this.propertyIDCell = new TableCellString((String)null, transformer.propertyName);
         this.propertyIDCell.setShowsValidityState(true);
         this.propertyIDCell.setValidityState(this.currentIDState());
         this.propertyIDCell.addListener((v) -> {
            transformer.propertyName = v;
            this.propertyIDCell.setValidityState(this.currentIDState());
            if (this.propertyValueCell != null) {
               this.propertyValueCell.setValidityState(this.currentValueState());
            }

         });
         return new TitledCell(IvTranslations.get("reccomplex.transformer.propertyReplace.id"), this.propertyIDCell);
      }}));
      this.addSegment(3, new TableDataSourceSupplied(new Supplier[]{() -> {
         this.propertyValueCell = new TableCellString((String)null, transformer.propertyValue);
         this.propertyValueCell.setShowsValidityState(true);
         this.propertyValueCell.setValidityState(this.currentValueState());
         this.propertyValueCell.addListener((v) -> {
            transformer.propertyValue = v;
            this.propertyValueCell.setValidityState(this.currentValueState());
         });
         return new TitledCell(IvTranslations.get("reccomplex.transformer.propertyReplace.value"), this.propertyValueCell);
      }}));
   }

   @Nonnull
   public GuiValidityStateIndicator.State currentIDState() {
      return TransformerProperty.propertyNameStream().anyMatch((s) -> {
         return s.equals(this.propertyIDCell.getPropertyValue());
      }) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.INVALID;
   }

   @Nonnull
   public GuiValidityStateIndicator.State currentValueState() {
      return TransformerProperty.propertyValueStream((String)this.propertyIDCell.getPropertyValue()).anyMatch((s) -> {
         return s.equals(this.propertyValueCell.getPropertyValue());
      }) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.INVALID;
   }

   public TransformerProperty getTransformer() {
      return this.transformer;
   }

   public void setTransformer(TransformerProperty transformer) {
      this.transformer = transformer;
   }
}
