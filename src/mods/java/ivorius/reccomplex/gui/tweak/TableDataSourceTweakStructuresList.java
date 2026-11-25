package ivorius.reccomplex.gui.tweak;

import gnu.trove.map.TObjectFloatMap;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellFloatNullable;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.scale.Scales;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceTweakStructuresList extends TableDataSourceSegmented {
   protected TObjectFloatMap<String> tweaks;
   protected List<String> editingIDs = new ArrayList();
   protected TableDelegate delegate;

   public TableDataSourceTweakStructuresList(TableDelegate delegate, TObjectFloatMap<String> tweaks) {
      this.tweaks = tweaks;
      this.delegate = delegate;
      this._search((String)null, false);
   }

   public void search(@Nullable String search) {
      this._search(search, true);
   }

   protected void _search(@Nullable String search, boolean reload) {
      this.editingIDs.clear();
      this.editingIDs.addAll(StructureRegistry.INSTANCE.ids());
      if (search != null) {
         this.editingIDs.removeIf((id) -> {
            return !id.toLowerCase().contains(search.toLowerCase());
         });
      }

      this.editingIDs.sort(String::compareTo);
      if (reload) {
         this.delegate.reloadData();
      }

   }

   public static void suggest(GuiChat chat, String command) {
      GuiTextField inputField = (GuiTextField)ReflectionHelper.getPrivateValue(GuiChat.class, chat, "inputField", "inputField");
      inputField.setText(command);
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      String id = (String)this.editingIDs.get(index);
      return TableCellMultiBuilder.create((TableNavigator)null, this.delegate).addCell(() -> {
         TableCellFloatNullable cell = new TableCellFloatNullable((String)null, this.getTweak(id), 1.0F, 0.0F, 10.0F, "D", "T");
         cell.setScale(Scales.pow(5.0F));
         cell.addListener((value) -> {
            if (value == null) {
               this.tweaks.remove(id);
            } else {
               this.tweaks.put(id, value.floatValue());
            }

         });
         return cell;
      }).addAction(() -> {
         GuiHider.hideGUI();
         GuiChat chat = new GuiChat();
         Minecraft.getMinecraft().displayGuiScreen(chat);
         String genCommand = String.format("/%s %s", RCCommands.generate.getName(), id);
         suggest(chat, genCommand);
      }, () -> {
         return IvTranslations.get("gui.tweakStructures.generate");
      }, () -> {
         return IvTranslations.getLines("gui.tweakStructures.generate.tooltip");
      }).sized(() -> {
         return 0.3F;
      }).withTitle(id, Collections.singletonList(id)).build();
   }

   public Float getTweak(String id) {
      return this.tweaks.containsKey(id) ? this.tweaks.get(id) : null;
   }

   public int sizeOfSegment(int segment) {
      return this.editingIDs.size();
   }

   public int numberOfSegments() {
      return 1;
   }
}
