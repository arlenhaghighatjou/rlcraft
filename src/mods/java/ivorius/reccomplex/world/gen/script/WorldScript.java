package ivorius.reccomplex.world.gen.script;

import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface WorldScript<S extends NBTStorable> extends NBTCompoundObject {
   S prepareInstanceData(StructurePrepareContext var1, BlockPos var2);

   S loadInstanceData(StructureLoadContext var1, NBTBase var2);

   void generate(StructureSpawnContext var1, RunTransformer var2, S var3, BlockPos var4);

   String getDisplayString();

   @SideOnly(Side.CLIENT)
   TableDataSource tableDataSource(BlockPos var1, TableNavigator var2, TableDelegate var3);
}
