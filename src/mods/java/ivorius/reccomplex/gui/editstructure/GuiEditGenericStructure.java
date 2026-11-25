package ivorius.reccomplex.gui.editstructure;

import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.table.screen.GuiScreenEditTable;
import ivorius.reccomplex.network.PacketSaveStructureHandler;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.Consumer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEditGenericStructure extends GuiScreenEditTable<TableDataSourceGenericStructure> {
   public GuiEditGenericStructure(String key, GenericStructure structureInfo, BlockPos lowerCoord, SaveDirectoryData data) {
      this.setDataSource(new TableDataSourceGenericStructure(structureInfo, key, data, this, this, new MazeVisualizationContext(lowerCoord, (int[])null)), (ds) -> {
         PacketSaveStructureHandler.saveStructure(ds.getStructureInfo(), ds.getStructureKey(), ds.getSaveDirectoryData().getResult());
      });
   }
}
