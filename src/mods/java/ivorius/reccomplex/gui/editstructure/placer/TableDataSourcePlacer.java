package ivorius.reccomplex.gui.editstructure.placer;

import ivorius.reccomplex.gui.editstructure.preset.TableDataSourcePresettedObject;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.utils.presets.PresettedObject;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.GenericPlacer;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourcePlacer extends TableDataSourceSegmented {
   public TableDataSourcePlacer(PresettedObject<GenericPlacer> object, TableDelegate delegate, TableNavigator navigator) {
      this.addSegment(0, (new TableDataSourcePresettedObject(object, "placer_preset", delegate, navigator)).withApplyPresetAction(() -> {
         this.addPresetSegments(object, delegate, navigator);
      }));
      this.addPresetSegments(object, delegate, navigator);
   }

   public void addPresetSegments(final PresettedObject<GenericPlacer> object, TableDelegate delegate, TableNavigator navigator) {
      this.addSegment(1, new TableDataSourcePlacerFactorList((object.getContents()).factors, delegate, navigator) {
         public boolean canEditList() {
            return object.isCustom();
         }
      });
   }

   @Nonnull
   public String title() {
      return "Placer";
   }
}
