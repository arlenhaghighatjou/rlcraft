package ivorius.reccomplex.gui.worldscripts.structuregenerator;

import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.TableDataSourceBlockPos;
import ivorius.reccomplex.gui.TableDirections;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.worldscripts.TableDataSourceWorldScript;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.script.WorldScriptStructureGenerator;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(Side.CLIENT)
public class TableDataSourceWorldScriptStructureGenerator extends TableDataSourceSegmented {
   protected WorldScriptStructureGenerator script;
   protected TableNavigator tableNavigator;
   protected TableDelegate tableDelegate;

   public TableDataSourceWorldScriptStructureGenerator(WorldScriptStructureGenerator script, TableNavigator tableNavigator, TableDelegate tableDelegate) {
      this.script = script;
      this.tableNavigator = tableNavigator;
      this.tableDelegate = tableDelegate;
      this.addSegment(0, new TableDataSourceWorldScript(script));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellBoolean cell = new TableCellBoolean("simpleMode", script.isSimpleMode());
         cell.addListener((val) -> {
            script.setSimpleMode(val.booleanValue());
            tableDelegate.reloadData();
         });
         return new TitledCell(IvTranslations.get("reccomplex.worldscript.strucGen.mode.simple"), cell);
      }});
      this.addSegment(3, new TableDataSourceBlockPos(script.getStructureShift(), script::setStructureShift, IvTranslations.get("reccomplex.gui.blockpos.shift"), IvTranslations.getLines("reccomplex.gui.blockpos.shift.tooltip")));
   }

   private static boolean doAllStructuresExist(Iterable<String> structures) {
      for(String s : structures) {
         if (s.length() != 0 && StructureRegistry.INSTANCE.get(s) == null) {
            return false;
         }
      }

      return true;
   }

   public int numberOfSegments() {
      return this.script.isSimpleMode() ? 5 : 5;
   }

   public int sizeOfSegment(int segment) {
      if (segment == 2) {
         return 1;
      } else if (segment == 4) {
         return this.script.isSimpleMode() ? 2 : 1;
      } else {
         return super.sizeOfSegment(segment);
      }
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      if (segment == 2) {
         if (this.script.isSimpleMode()) {
            TableCellString cell = new TableCellString("generators", String.join(",", this.script.getStructureNames()));
            cell.setShowsValidityState(true);
            cell.setValidityState(doAllStructuresExist(this.script.getStructureNames()) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.SEMI_VALID);
            cell.addListener((val) -> {
               this.script.setStructureNames(Arrays.asList(val.split(",")));
               cell.setValidityState(doAllStructuresExist(this.script.getStructureNames()) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.SEMI_VALID);
            });
            return (new TitledCell(IvTranslations.get("reccomplex.worldscript.strucGen.simple.generators"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.worldscript.strucGen.simple.generators.tooltip"));
         } else {
            TableCellString cell = new TableCellString("listID", this.script.getStructureListID());
            WorldScriptStructureGenerator var11 = this.script;
            this.script.getClass();
            cell.addListener(var11::setStructureListID);
            return new TitledCell(IvTranslations.get("reccomplex.worldscript.strucGen.mode.list.id"), cell);
         }
      } else {
         if (segment == 4) {
            if (!this.script.isSimpleMode()) {
               TableCellEnum<EnumFacing> cell = new TableCellEnum<EnumFacing>("front", this.script.getFront(), TableDirections.getDirectionOptions((EnumFacing[])ArrayUtils.add(Directions.HORIZONTAL, (Object)null), "random"));
               WorldScriptStructureGenerator var10 = this.script;
               this.script.getClass();
               cell.addListener(var10::setFront);
               return new TitledCell(IvTranslations.get("reccomplex.worldscript.strucGen.mode.list.front"), cell);
            }

            if (index == 0) {
               TableCellEnum<Integer> cell = new TableCellEnum<Integer>("rotation", this.script.getStructureRotation(), new TableCellEnum.Option[]{new TableCellEnum.Option(Integer.valueOf(0), IvTranslations.get("reccomplex.rotation.clockwise.0")), new TableCellEnum.Option(Integer.valueOf(1), IvTranslations.get("reccomplex.rotation.clockwise.1")), new TableCellEnum.Option(Integer.valueOf(2), IvTranslations.get("reccomplex.rotation.clockwise.2")), new TableCellEnum.Option(Integer.valueOf(3), IvTranslations.get("reccomplex.rotation.clockwise.3")), new TableCellEnum.Option((Object)null, IvTranslations.get("reccomplex.worldscript.strucGen.rotation.random"))});
               WorldScriptStructureGenerator var9 = this.script;
               this.script.getClass();
               cell.addListener(var9::setStructureRotation);
               return new TitledCell(IvTranslations.get("reccomplex.rotation"), cell);
            }

            if (index == 1) {
               TableCellEnum<Boolean> cell = new TableCellEnum<Boolean>("mirror", this.script.getStructureMirror(), new TableCellEnum.Option[]{new TableCellEnum.Option(false, IvTranslations.get("reccomplex.gui.false")), new TableCellEnum.Option(true, IvTranslations.get("reccomplex.gui.true")), new TableCellEnum.Option((Object)null, IvTranslations.get("reccomplex.worldscript.strucGen.mirror.random"))});
               WorldScriptStructureGenerator var10001 = this.script;
               this.script.getClass();
               cell.addListener(var10001::setStructureMirror);
               return new TitledCell(IvTranslations.get("reccomplex.mirror"), cell);
            }
         }

         return super.cellForIndexInSegment(table, index, segment);
      }
   }
}
