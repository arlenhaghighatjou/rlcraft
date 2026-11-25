package ivorius.reccomplex.gui.editstructure;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.scale.Scales;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericVariableDomain;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceGenericVariable extends TableDataSourceSegmented {
   private GenericVariableDomain.Variable variable;
   private TableDelegate tableDelegate;

   public TableDataSourceGenericVariable(GenericVariableDomain.Variable variable) {
      this.variable = variable;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString cell = new TableCellString((String)null, variable.id);
         cell.setShowsValidityState(true);
         cell.addListener((s) -> {
            if (Structures.isSimpleID(s)) {
               cell.setValidityState(GuiValidityStateIndicator.State.VALID);
               variable.id = s;
            } else {
               cell.setValidityState(GuiValidityStateIndicator.State.INVALID);
            }

         });
         return (new TitledCell(IvTranslations.get("reccomplex.structure.variables.id"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.structure.variables.id.tooltip"));
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider((String)null, variable.chance, 0.0F, 1.0F);
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((f) -> {
            variable.chance = f.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.structure.variables.chance"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.structure.variables.chance.tooltip"));
      }, () -> {
         TableCellBoolean cell = new TableCellBoolean((String)null, variable.affectsLogic);
         cell.addListener((b) -> {
            variable.affectsLogic = b.booleanValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.structure.variables.logical"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.structure.variables.logical.tooltip"));
      }});
      this.addSegment(1, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.structure.variables.condition"), variable.condition, (Object)null));
   }

   @Nonnull
   public String title() {
      return "Local Toggle";
   }
}
