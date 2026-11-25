package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.TableDataSourceBlockPos;
import ivorius.reccomplex.gui.TableDirections;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.ListGeneration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceStructureListGeneration extends TableDataSourceSegmented {
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private ListGeneration generationInfo;

   public TableDataSourceStructureListGeneration(TableNavigator navigator, TableDelegate tableDelegate, ListGeneration generationInfo) {
      this.navigator = navigator;
      this.tableDelegate = tableDelegate;
      this.generationInfo = generationInfo;
      this.addSegment(0, new TableDataSourceGenerationType(generationInfo, navigator, tableDelegate));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellString cell = new TableCellString("listID", generationInfo.listID);
         cell.setShowsValidityState(true);
         cell.setValidityState(this.currentStructureListIDState());
         cell.addListener((cell1) -> {
            generationInfo.listID = (String)cell.getPropertyValue();
            cell.setValidityState(this.currentStructureListIDState());
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.structureList.id"), cell);
      }});
      this.addSegment(2, new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            generationInfo.weight = TableCells.toDouble(val);
         }, generationInfo.weight);
      }});
      this.addSegment(3, new TableDataSourceBlockPos(generationInfo.shift, generationInfo::setShift, IvTranslations.get("reccomplex.gui.blockpos.shift"), IvTranslations.getLines("reccomplex.gui.blockpos.shift.tooltip")));
      this.addSegment(4, new Supplier[]{() -> {
         TableCellEnum<EnumFacing> cell = new TableCellEnum<EnumFacing>("front", generationInfo.front, TableDirections.getDirectionOptions(Directions.HORIZONTAL));
         cell.addListener((cell1) -> {
            generationInfo.front = (EnumFacing)cell.getPropertyValue();
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.structureList.front"), cell);
      }});
   }

   @Nonnull
   protected GuiValidityStateIndicator.State currentStructureListIDState() {
      return Structures.isSimpleID(this.generationInfo.listID) ? (ListGeneration.structures(StructureRegistry.INSTANCE, this.generationInfo.listID, (EnumFacing)null).count() > 0L ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.SEMI_VALID) : GuiValidityStateIndicator.State.INVALID;
   }
}
