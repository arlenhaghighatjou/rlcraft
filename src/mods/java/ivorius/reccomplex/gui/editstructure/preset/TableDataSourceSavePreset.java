package ivorius.reccomplex.gui.editstructure.preset;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.utils.presets.PresettedObject;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceSavePreset<T> extends TableDataSourceSegmented {
   public PresettedObject<T> object;
   public String saverID;
   public String id = "";
   public String title = "";
   public String description = "";
   public TableDelegate delegate;
   public TableNavigator navigator;

   public TableDataSourceSavePreset(PresettedObject<T> object, String saverID, TableDelegate delegate, TableNavigator navigator) {
      this.object = object;
      this.saverID = saverID;
      this.delegate = delegate;
      this.navigator = navigator;
      this.addSegment(0, new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellString cell = new TableCellString((String)null, this.id);
         cell.setShowsValidityState(true);
         cell.setValidityState(this.currentIDState());
         cell.addListener((s) -> {
            this.id = s;
            cell.setValidityState(this.currentIDState());
            if (object.getPresetRegistry().has(s)) {
               this.title = (String)object.getPresetRegistry().title(s).orElse("");
               this.description = (String)object.getPresetRegistry().description(s).flatMap((d) -> {
                  return (void)d.stream().reduce((s1, s2) -> {
                     return (void)(s1 + "<br>" + s2);
                  });
               }).orElse("");
               TableCells.reloadExcept(delegate, "presetID");
            }

         });
         return (void)(new TitledCell("presetID", IvTranslations.get("reccomplex.preset.id"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.preset.id.tooltip"));
      }, () -> {
         TableCellString cell = new TableCellString((String)null, this.title);
         cell.addListener((s) -> {
            this.title = s;
         });
         return (void)(new TitledCell(IvTranslations.get("reccomplex.preset.title"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.preset.title.tooltip"));
      }, () -> {
         TableCellString cell = new TableCellString((String)null, this.description);
         cell.addListener((s) -> {
            this.description = s;
         });
         return (void)(new TitledCell(IvTranslations.get("reccomplex.preset.description"), cell)).withTitleTooltip((List)IvTranslations.getLines("reccomplex.preset.description.tooltip").stream().map((s) -> {
            return (void)s.replaceAll("<BR>", "<br>");
         }).collect(Collectors.toList()));
      }}));
      this.addSegment(1, TableCellMultiBuilder.create(navigator, delegate).addAction(this::save, () -> {
         return (void)IvTranslations.get("reccomplex.gui.save");
      }, (Supplier)null).buildDataSource());
   }

   @Nonnull
   public String title() {
      return String.format("Save: %s", this.saverID);
   }

   public boolean save() {
      if (!Structures.isSimpleID(this.id)) {
         return false;
      } else {
         this.object.getPresetRegistry().getRegistry().register(this.id, "", PresetRegistry.fullPreset(this.id, this.object.getContents(), new PresetRegistry.Metadata(this.title, this.description.split("<br>"))), true, LeveledRegistry.Level.CUSTOM);
         RecurrentComplex.saver.trySave(ResourceDirectory.ACTIVE.toPath(), this.saverID, this.id);
         this.navigator.popTable();
         this.object.setPreset(this.id);
         return true;
      }
   }

   private GuiValidityStateIndicator.State currentIDState() {
      return Structures.isSimpleID(this.id) ? (this.object.getPresetRegistry().has(this.id) ? GuiValidityStateIndicator.State.SEMI_VALID : GuiValidityStateIndicator.State.VALID) : GuiValidityStateIndicator.State.INVALID;
   }
}
