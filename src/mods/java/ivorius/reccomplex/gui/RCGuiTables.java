package ivorius.reccomplex.gui;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.cell.TableCellFloatNullable;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.utils.scale.Scales;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RCGuiTables {
   public RCGuiTables() {
   }

   public static TitledCell defaultWeightElement(Consumer<Float> consumer, Float value, String title, @Nullable List<String> tooltip) {
      TableCellFloatNullable cell = new TableCellFloatNullable("value", value, 1.0F, 0.0F, 1000.0F, IvTranslations.get("reccomplex.gui.random.weight.default.short"), IvTranslations.get("reccomplex.gui.random.weight.custom.short"));
      cell.setScale(Scales.pow(5.0F));
      cell.addListener(consumer);
      return (new TitledCell(title, cell)).withTitleTooltip(tooltip);
   }

   public static TitledCell defaultWeightElement(Consumer<Float> consumer, Double value, String title, @Nullable List<String> tooltip) {
      return defaultWeightElement(consumer, TableCells.toFloat(value), title, tooltip);
   }

   public static TitledCell defaultWeightElement(Consumer<Float> consumer, Float value) {
      return defaultWeightElement(consumer, value, IvTranslations.get("reccomplex.gui.random.weight"), IvTranslations.formatLines("reccomplex.gui.random.weight.tooltip", new Object[0]));
   }

   public static TitledCell defaultWeightElement(Consumer<Float> listener, Double value) {
      return defaultWeightElement(listener, TableCells.toFloat(value));
   }
}
