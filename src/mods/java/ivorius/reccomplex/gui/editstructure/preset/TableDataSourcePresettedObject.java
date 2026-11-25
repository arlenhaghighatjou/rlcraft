package ivorius.reccomplex.gui.editstructure.preset;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TableCellPresetAction;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.utils.presets.PresettedObject;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourcePresettedObject<T> extends TableDataSourceSegmented {
   public TableDelegate delegate;
   public TableNavigator navigator;
   public PresettedObject<T> object;
   public String saverID;
   public Runnable applyPresetAction;

   public TableDataSourcePresettedObject(PresettedObject<T> object, String saverID, TableDelegate delegate, TableNavigator navigator) {
      this.object = object;
      this.saverID = saverID;
      this.delegate = delegate;
      this.navigator = navigator;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellMulti multi = new TableCellMulti(new TableCell[]{getSetElement(object, delegate, this.getActions(), this.applyPresetAction), getCustomizeElement(object, saverID, delegate, navigator, this.applyPresetAction)});
         multi.setSize(0, 7.0F);
         return (void)(new TitledCell(IvTranslations.get("reccomplex.presets"), multi));
      }});
   }

   @Nonnull
   public static <T> TableCell getCustomizeElement(PresettedObject<T> object, String saverID, TableDelegate delegate, TableNavigator navigator, Runnable applyPresetAction) {
      if (!object.isCustom()) {
         TableCellButton cell = new TableCellButton("customize", "customize", TextFormatting.AQUA + "O", true);
         cell.setTooltip(IvTranslations.formatLines("reccomplex.preset.customize", new Object[]{object.presetTitle().get()}));
         cell.addAction(() -> {
            object.setToCustom();
            if (applyPresetAction != null) {
               applyPresetAction.run();
            }

            delegate.reloadData();
         });
         return cell;
      } else {
         return TableCellMultiBuilder.create(navigator, delegate).addSimpleNavigation(() -> {
            return new TableDataSourceSavePreset(object, saverID, delegate, navigator);
         }, () -> {
            return String.format("%s+", TextFormatting.GREEN);
         }, () -> {
            return IvTranslations.getLines("reccomplex.preset.save");
         }).enabled(() -> {
            return saverID != null;
         }).build();
      }
   }

   @Nonnull
   public static <T> TableCell getSetElement(PresettedObject<T> object, TableDelegate delegate, List<TableCellButton> actions, Runnable applyPresetAction) {
      if (actions.isEmpty()) {
         return new TableCellButton((String)null, (String)null, "-", false);
      } else {
         TableCellPresetAction cell = new TableCellPresetAction("preset", actions);
         cell.addAction((actionID) -> {
            object.setPreset(actionID);
            if (applyPresetAction != null) {
               applyPresetAction.run();
            }

            delegate.reloadData();
         });
         if (object.getPreset() != null) {
            cell.setPropertyValue(object.getPreset());
            TableCellButton action = cell.findAction(object.getPreset());
            if (action != null) {
               action.setEnabled(false);
            }
         }

         return cell;
      }
   }

   @Nonnull
   public static <T> List<TableCellButton> getActions(PresettedObject<T> object) {
      PresetRegistry<T> registry = object.getPresetRegistry();
      return (List)TableCellPresetAction.sorted(registry.allIDs().stream().map((type) -> {
         return new TableCellButton(type, type, IvTranslations.format(type.equals(registry.defaultID()) ? "reccomplex.preset.use.default" : "reccomplex.preset.use", new Object[]{registry.title(type).orElse(type)}), (List)registry.description(type).orElse((Object)null));
      })).collect(Collectors.toList());
   }

   public TableDataSourcePresettedObject<T> withApplyPresetAction(Runnable applyPresetAction) {
      this.applyPresetAction = applyPresetAction;
      return this;
   }

   public TableDataSourcePresettedObject<T> withCurrentOnTop(boolean currentOnTop) {
      return this;
   }

   public List<TableCellButton> getActions() {
      return getActions(this.object);
   }
}
