package ivorius.reccomplex.gui;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.utils.SaveDirectoryData;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableElementSaveDirectory {
   public TableElementSaveDirectory() {
   }

   public static TitledCell create(SaveDirectoryData data, Supplier<String> idSupplier, TableDelegate delegate) {
      TableCellEnum<ResourceDirectory> cellFolder;
      label14: {
         String id = (String)idSupplier.get();
         cellFolder = new TableCellEnum<ResourceDirectory>("activeFolder", data.getDirectory(), TableCellEnum.options(ResourceDirectory.values(), (d) -> {
            return TextFormatting.GREEN + d.readableName();
         }, (Function)null));
         cellFolder.addListener((cell) -> {
            data.setDirectory((ResourceDirectory)cellFolder.getPropertyValue());
            delegate.reloadData();
         });
         if (data.getDirectory().isActive()) {
            if (data.getFilesInInactive().contains(id)) {
               break label14;
            }
         } else if (data.getFilesInActive().contains(id)) {
            break label14;
         }

         return new TitledCell(IvTranslations.get("reccomplex.structure.directory"), new TableCellMulti(new TableCell[]{cellFolder, new TableCellButton("", "", "-", false)}));
      }

      String path = data.getDirectory().opposite().subDirectoryName();
      TableCellBoolean cellDelete = new TableCellBoolean("deleteOther", data.isDeleteOther(), IvTranslations.format("reccomplex.structure.deleteOther.true", new Object[]{TextFormatting.RED, TextFormatting.RESET, String.format("%s/%s%s", TextFormatting.AQUA, path, TextFormatting.RESET)}), IvTranslations.format("reccomplex.structure.deleteOther.false", new Object[]{TextFormatting.YELLOW, TextFormatting.RESET}));
      cellDelete.addListener((cell) -> {
         data.setDeleteOther(((Boolean)cellDelete.getPropertyValue()).booleanValue());
      });
      cellDelete.setTooltip(IvTranslations.formatLines("reccomplex.structure.deleteOther.tooltip", new Object[]{TextFormatting.AQUA + ResourceDirectory.INACTIVE.subDirectoryName() + TextFormatting.RESET, TextFormatting.AQUA + ResourceDirectory.ACTIVE.subDirectoryName() + TextFormatting.RESET}));
      return new TitledCell(IvTranslations.get("reccomplex.structure.directory"), new TableCellMulti(new TableCell[]{cellFolder, cellDelete}));
   }
}
