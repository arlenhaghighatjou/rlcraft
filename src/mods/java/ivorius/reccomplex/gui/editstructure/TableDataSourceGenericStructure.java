package ivorius.reccomplex.gui.editstructure;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.TableElementSaveDirectory;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceGenericStructure extends TableDataSourceSegmented {
   protected GenericStructure structureInfo;
   protected String structureKey;
   protected SaveDirectoryData saveDirectoryData;
   protected TableDelegate tableDelegate;
   protected TableNavigator navigator;

   public TableDataSourceGenericStructure(GenericStructure structureInfo, String structureKey, SaveDirectoryData saveDirectoryData, TableDelegate delegate, TableNavigator navigator, MazeVisualizationContext visualizationContext) {
      this.structureInfo = structureInfo;
      this.structureKey = structureKey;
      this.saveDirectoryData = saveDirectoryData;
      this.tableDelegate = delegate;
      this.navigator = navigator;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString cell = new TableCellString((String)null, this.structureKey);
         cell.addListener((cell1) -> {
            this.structureKey = (String)cell.getPropertyValue();
            cell.setValidityState(this.currentNameState());
            TableCells.reloadExcept(this.tableDelegate, "structureID");
         });
         cell.setShowsValidityState(true);
         cell.setValidityState(this.currentNameState());
         return (new TitledCell("structureID", IvTranslations.get("reccomplex.structure.id"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.structure.id.tooltip", new Object[0]));
      }});
      this.addSegment(1, new TableDataSourceSupplied(new Supplier[]{() -> {
         return TableElementSaveDirectory.create(saveDirectoryData, () -> {
            return structureKey;
         }, delegate);
      }}));
      this.addSegment(2, new Supplier[]{() -> {
         TableCellBoolean cellRotatable = new TableCellBoolean("rotatable", structureInfo.rotatable, IvTranslations.get("reccomplex.structure.rotatable.true"), IvTranslations.get("reccomplex.structure.rotatable.false"));
         cellRotatable.addListener((cell) -> {
            structureInfo.rotatable = ((Boolean)cellRotatable.getPropertyValue()).booleanValue();
         });
         TableCellBoolean cellMirrorable = new TableCellBoolean("mirrorable", structureInfo.mirrorable, IvTranslations.format("reccomplex.structure.mirrorable.true", new Object[0]), IvTranslations.format("reccomplex.structure.mirrorable.false", new Object[0]));
         cellMirrorable.addListener((cell) -> {
            structureInfo.mirrorable = ((Boolean)cellMirrorable.getPropertyValue()).booleanValue();
         });
         return new TitledCell((new TableCellMulti(new TableCell[]{cellRotatable, cellMirrorable})).withTooltip(IvTranslations.formatLines("reccomplex.structure.orientation.tooltip", new Object[0])));
      }, () -> {
         TableCellBoolean cellBlocking = new TableCellBoolean("blocking", structureInfo.blocking, IvTranslations.format("reccomplex.structure.blocking.true", new Object[0]), IvTranslations.format("reccomplex.structure.blocking.false", new Object[0]));
         cellBlocking.addListener((cell) -> {
            structureInfo.blocking = ((Boolean)cellBlocking.getPropertyValue()).booleanValue();
         });
         return new TitledCell(cellBlocking.withTooltip(IvTranslations.formatLines("reccomplex.structure.blocking.tooltip", new Object[0])));
      }});
      this.addSegment(3, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceGenerationType(structureInfo.generationTypes, visualizationContext, delegate, navigator);
      }, () -> {
         return IvTranslations.get("reccomplex.structure.generation");
      }, () -> {
         return IvTranslations.getLines("reccomplex.structure.generation.tooltip");
      }).addNavigation(() -> {
         return structureInfo.transformer.tableDataSource(navigator, delegate);
      }, () -> {
         return IvTranslations.get("reccomplex.structure.transformers");
      }, () -> {
         return IvTranslations.getLines("reccomplex.structure.transformers.tooltip");
      }).withTitle("").buildDataSource());
      this.addSegment(4, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceMetadata(structureInfo.metadata);
      }, () -> {
         return IvTranslations.get("reccomplex.structure.metadata");
      }, () -> {
         return IvTranslations.getLines("reccomplex.structure.metadata.tooltip");
      }).addNavigation(() -> {
         return new TableDataSourceGenericVariableDomain(delegate, navigator, structureInfo.variableDomain);
      }, () -> {
         return IvTranslations.get("reccomplex.structure.variables");
      }, () -> {
         return IvTranslations.getLines("reccomplex.structure.variables.tooltip");
      }).withTitle("").buildDataSource());
      this.addSegment(5, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.structure.dependencies"), IvTranslations.getLines("reccomplex.structure.dependencies.tooltip"), structureInfo.dependencies, RecurrentComplex.saver));
   }

   public GenericStructure getStructureInfo() {
      return this.structureInfo;
   }

   public void setStructureInfo(GenericStructure structureInfo) {
      this.structureInfo = structureInfo;
   }

   public String getStructureKey() {
      return this.structureKey;
   }

   public void setStructureKey(String structureKey) {
      this.structureKey = structureKey;
   }

   public SaveDirectoryData getSaveDirectoryData() {
      return this.saveDirectoryData;
   }

   public void setSaveDirectoryData(SaveDirectoryData saveDirectoryData) {
      this.saveDirectoryData = saveDirectoryData;
   }

   public TableDelegate getTableDelegate() {
      return this.tableDelegate;
   }

   public void setTableDelegate(TableDelegate tableDelegate) {
      this.tableDelegate = tableDelegate;
   }

   public TableNavigator getNavigator() {
      return this.navigator;
   }

   public void setNavigator(TableNavigator navigator) {
      this.navigator = navigator;
   }

   @Nonnull
   public String title() {
      return "Generic Structure";
   }

   private GuiValidityStateIndicator.State currentNameState() {
      return Structures.isSimpleID(this.structureKey) ? (StructureRegistry.INSTANCE.ids().contains(this.structureKey) ? GuiValidityStateIndicator.State.SEMI_VALID : GuiValidityStateIndicator.State.VALID) : GuiValidityStateIndicator.State.INVALID;
   }
}
