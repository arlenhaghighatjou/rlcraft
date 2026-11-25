package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.gui.FloatRange;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.TableDataSourceBlockPos;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.TableDirections;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellFloatRange;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.scale.Scales;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaGeneration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(Side.CLIENT)
public class TableDataSourceVanillaGeneration extends TableDataSourceSegmented {
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private VanillaGeneration generationInfo;

   public TableDataSourceVanillaGeneration(TableNavigator navigator, TableDelegate tableDelegate, VanillaGeneration generationInfo) {
      this.navigator = navigator;
      this.tableDelegate = tableDelegate;
      this.generationInfo = generationInfo;
      this.addSegment(0, new TableDataSourceGenerationType(generationInfo, navigator, tableDelegate));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellEnum<String> cell = new TableCellEnum<String>("type", "village", new TableCellEnum.Option[]{new TableCellEnum.Option("village", IvTranslations.get("reccomplex.generationInfo.vanilla.type.village"))});
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.vanilla.type"), cell);
      }});
      this.addSegment(2, new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            generationInfo.generationWeight = TableCells.toDouble(val);
         }, generationInfo.generationWeight);
      }, () -> {
         TableCellEnum<EnumFacing> cell = new TableCellEnum<EnumFacing>("front", generationInfo.front, TableDirections.getDirectionOptions((EnumFacing[])ArrayUtils.add(Directions.HORIZONTAL, (Object)null), "all"));
         cell.addListener((val) -> {
            generationInfo.front = val;
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.vanilla.front"), cell);
      }});
      this.addSegment(3, new Supplier[]{() -> {
         TableCellFloatRange cell = new TableCellFloatRange("baseLimit", new FloatRange((float)generationInfo.minBaseLimit, (float)generationInfo.maxBaseLimit), 0.0F, 1000.0F, "%.2f");
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((val) -> {
            generationInfo.minBaseLimit = (double)val.getMin();
            generationInfo.maxBaseLimit = (double)val.getMax();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.generationInfo.vanilla.amount.pervillage"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.generationInfo.vanilla.amount.pervillage.tooltip"));
      }, () -> {
         TableCellFloatRange cell = new TableCellFloatRange("scaledLimit", new FloatRange((float)generationInfo.minScaledLimit, (float)generationInfo.maxScaledLimit), 0.0F, 1000.0F, "%.2f");
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((val) -> {
            generationInfo.minScaledLimit = (double)val.getMin();
            generationInfo.maxScaledLimit = (double)val.getMax();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.generationInfo.vanilla.amount.scaled"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.generationInfo.vanilla.amount.scaled.tooltip"));
      }});
      this.addSegment(4, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.gui.biomes"), generationInfo.biomeExpression, (Object)null));
      this.addSegment(5, new TableDataSourceBlockPos(generationInfo.spawnShift, generationInfo::setSpawnShift, IvTranslations.get("reccomplex.gui.blockpos.shift"), IvTranslations.getLines("reccomplex.gui.blockpos.shift.tooltip")));
   }
}
