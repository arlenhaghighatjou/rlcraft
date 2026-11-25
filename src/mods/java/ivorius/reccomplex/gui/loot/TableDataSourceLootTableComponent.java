package ivorius.reccomplex.gui.loot;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.RCGuiHandler;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.TableElementSaveDirectory;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.storage.loot.GenericItemCollectionRegistry;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import ivorius.reccomplex.world.storage.loot.WeightedItemCollectionRegistry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceLootTableComponent extends TableDataSourceSegmented {
   public String key;
   public GenericLootTable.Component component;
   private SaveDirectoryData saveDirectoryData;
   public EntityPlayer player;
   public TableNavigator navigator;
   public TableDelegate delegate;

   public TableDataSourceLootTableComponent(String key, GenericLootTable.Component component, SaveDirectoryData saveDirectoryData, EntityPlayer player, TableNavigator navigator, TableDelegate delegate) {
      this.key = key;
      this.component = component;
      this.saveDirectoryData = saveDirectoryData;
      this.player = player;
      this.navigator = navigator;
      this.delegate = delegate;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString cell = new TableCellString((String)null, this.key);
         cell.setShowsValidityState(true);
         cell.setValidityState(this.currentKeyState());
         cell.addListener((val) -> {
            this.key = val;
            cell.setValidityState(this.currentKeyState());
         });
         return (new TitledCell(IvTranslations.get("reccomplex.gui.inventorygen.componentid"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.gui.inventorygen.componentid.tooltip", new Object[0]));
      }});
      this.addSegment(1, new TableDataSourceSupplied(new Supplier[]{() -> {
         return TableElementSaveDirectory.create(saveDirectoryData, () -> {
            return key;
         }, delegate);
      }}));
      this.addSegment(2, new Supplier[]{() -> {
         TableCellString cell = new TableCellString((String)null, component.tableID);
         cell.setShowsValidityState(true);
         cell.setValidityState(this.currentGroupIDState());
         cell.addListener((val) -> {
            component.tableID = val;
            cell.setValidityState(this.currentGroupIDState());
         });
         return (new TitledCell(IvTranslations.get("reccomplex.gui.inventorygen.groupid"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.gui.inventorygen.groupid.tooltip", new Object[0]));
      }});
      this.addSegment(3, TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.inventorygen.dependencies"), IvTranslations.getLines("reccomplex.inventorygen.dependencies.tooltip"), this.component.dependencies, RecurrentComplex.saver));
      this.addSegment(4, TableCellMultiBuilder.create(navigator, delegate).addAction(() -> {
         RCGuiHandler.editLootTableComponentItems(this.player, this.key, this.component, this.saveDirectoryData);
      }, () -> {
         return IvTranslations.format("reccomplex.gui.inventorygen.items.summary", new Object[]{String.valueOf(this.component.items.size())});
      }, (Supplier)null).withTitle("").buildDataSource());
   }

   public SaveDirectoryData getSaveDirectoryData() {
      return this.saveDirectoryData;
   }

   public void setSaveDirectoryData(SaveDirectoryData saveDirectoryData) {
      this.saveDirectoryData = saveDirectoryData;
   }

   @Nonnull
   public String title() {
      return "Item Generation Component";
   }

   private GuiValidityStateIndicator.State currentKeyState() {
      return this.key.trim().isEmpty() ? GuiValidityStateIndicator.State.INVALID : (GenericItemCollectionRegistry.INSTANCE.ids().contains(this.key) ? GuiValidityStateIndicator.State.SEMI_VALID : GuiValidityStateIndicator.State.VALID);
   }

   private GuiValidityStateIndicator.State currentGroupIDState() {
      return this.component.tableID.trim().isEmpty() ? GuiValidityStateIndicator.State.INVALID : (WeightedItemCollectionRegistry.INSTANCE.get(this.component.tableID) instanceof GenericLootTable ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.SEMI_VALID);
   }
}
