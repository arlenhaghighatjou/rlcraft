package ivorius.reccomplex.gui;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.utils.RCStrings;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCaches;
import ivorius.reccomplex.utils.expression.BiomeExpression;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.utils.expression.DependencyExpression;
import ivorius.reccomplex.utils.expression.DimensionExpression;
import ivorius.reccomplex.utils.expression.EnvironmentExpression;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceExpression<T, U, E extends FunctionExpressionCache<T, ?, U>> implements TableDataSource {
   public String title;
   private List<String> tooltip;
   private List<String> expressionTooltip;
   public E e;
   public U u;
   protected TableCellTitle parsed;
   protected TableCellString expressionCell;
   @Nullable
   protected BooleanSupplier enabledSupplier;

   public TableDataSourceExpression(String title, List<String> tooltip, List<String> expressionTooltip, E e, U u) {
      this.title = title;
      this.tooltip = tooltip;
      this.expressionTooltip = expressionTooltip;
      this.e = e;
      this.u = u;
   }

   public static <T, U, E extends FunctionExpressionCache<T, ?, U>> TableDataSourceExpression<T, U, E> constructDefault(String title, E e, U u) {
      return constructDefault(title, (List)null, e, u);
   }

   public static <T, U, E extends FunctionExpressionCache<T, ?, U>> TableDataSourceExpression<T, U, E> constructDefault(String title, List<String> tooltip, E e, U u) {
      if (e instanceof BiomeExpression) {
         return new TableDataSourceExpression<T, U, E>(title, tooltip, IvTranslations.formatLines("reccomplex.expression.biome.tooltip", new Object[0]), e, u);
      } else if (e instanceof BlockExpression) {
         return new TableDataSourceExpression<T, U, E>(title, tooltip, IvTranslations.formatLines("reccomplex.expression.block.tooltip", new Object[0]), e, u);
      } else if (e instanceof PositionedBlockExpression) {
         return new TableDataSourceExpression<T, U, E>(title, tooltip, IvTranslations.formatLines("reccomplex.expression.positioned_block.tooltip", new Object[0]), e, u);
      } else if (e instanceof DependencyExpression) {
         return new TableDataSourceExpression<T, U, E>(title, tooltip, IvTranslations.formatLines("reccomplex.expression.dependency.tooltip", new Object[0]), e, u);
      } else if (e instanceof DimensionExpression) {
         return new TableDataSourceExpression<T, U, E>(title, tooltip, IvTranslations.formatLines("reccomplex.expression.dimension.tooltip", new Object[0]), e, u);
      } else if (e instanceof EnvironmentExpression) {
         return new TableDataSourceExpression<T, U, E>(title, tooltip, IvTranslations.formatLines("reccomplex.expression.environment.tooltip", new Object[0]), e, u);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static <U> String parsedString(FunctionExpressionCache<?, ?, U> expressionCache, U u) {
      return expressionCache.isExpressionValid() ? expressionCache.getDisplayString(u) : FunctionExpressionCaches.readableException(expressionCache);
   }

   public static <U> GuiValidityStateIndicator.State getValidityState(FunctionExpressionCache<?, ?, U> expressionCache, U u) {
      switch(expressionCache.validity(u)) {
      case KNOWN:
         return GuiValidityStateIndicator.State.VALID;
      case UNKNOWN:
         return GuiValidityStateIndicator.State.SEMI_VALID;
      default:
         return GuiValidityStateIndicator.State.INVALID;
      }
   }

   public TableDataSourceExpression<T, U, E> enabled(BooleanSupplier enabledSupplier) {
      this.enabledSupplier = enabledSupplier;
      return this;
   }

   public int numberOfCells() {
      return 2;
   }

   public TableCell cellForIndex(GuiTable table, int index) {
      if (index == 0) {
         this.expressionCell = new TableCellString("expression", this.e.getExpression());
         this.expressionCell.setTooltip(this.expressionTooltip);
         this.expressionCell.setEnabled(this.canEdit());
         this.expressionCell.setShowsValidityState(true);
         this.expressionCell.setValidityState(getValidityState(this.e, this.u));
         this.expressionCell.addListener((val) -> {
            this.e.setExpression(val);
            this.expressionCell.setValidityState(getValidityState(this.e, this.u));
         });
         this.expressionCell.setChangeListener(() -> {
            if (this.parsed != null) {
               this.parsed.setDisplayString(this.parsedString());
            }

         });
         return (new TitledCell(this.title, this.expressionCell)).withTitleTooltip(this.tooltip);
      } else if (index == 1) {
         this.parsed = new TableCellTitle("parsedExpression", this.parsedString());
         this.parsed.setPositioning(TableCellTitle.Positioning.TOP);
         return new TitledCell(this.parsed);
      } else {
         return null;
      }
   }

   @Nullable
   protected String parsedString() {
      return RCStrings.abbreviateFormatted(parsedString(this.e, this.u), this.getCursorOffset(), 71);
   }

   protected int getCursorOffset() {
      int offset = 0;
      if (this.expressionCell != null && this.expressionCell.getTextField() != null) {
         offset = this.expressionCell.getTextField().getCursorPosition();
      }

      return offset;
   }

   public boolean canEdit() {
      return this.enabledSupplier == null || this.enabledSupplier.getAsBoolean();
   }
}
