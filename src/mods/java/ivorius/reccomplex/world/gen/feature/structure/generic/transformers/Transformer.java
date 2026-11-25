package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.JsonObject;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLiveContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Stack;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Transformer<S extends NBTStorable> {
   public static Stack<EaglercraftRandom> idRandomizers = new Stack();
   @Nonnull
   protected String id;

   public Transformer(@Nonnull String id) {
      this.id = id;
   }

   public static String randomID(Class<? extends Transformer> type) {
      EaglercraftRandom random = new EaglercraftRandom();
      return String.format("%s_%s", StructureRegistry.TRANSFORMERS.iDForType(type), Integer.toHexString(random.nextInt()));
   }

   public static String randomID(String type) {
      EaglercraftRandom random = new EaglercraftRandom();
      return String.format("%s_%s", type, Integer.toHexString(random.nextInt()));
   }

   public static String readID(JsonObject object) {
      String id = JsonUtils.getString(object, "id", (String)null);
      if (id == null || id.length() == 0) {
         id = Integer.toHexString(((EaglercraftRandom)idRandomizers.peek()).nextInt());
      }

      return id;
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   @SideOnly(Side.CLIENT)
   public abstract String getDisplayString();

   @SideOnly(Side.CLIENT)
   public abstract TableDataSource tableDataSource(TableNavigator var1, TableDelegate var2);

   public abstract S prepareInstanceData(StructurePrepareContext var1, IvWorldData var2);

   public void configureInstanceData(S s, StructurePrepareContext context, IvWorldData worldData, RunTransformer transformer) {
   }

   public abstract S loadInstanceData(StructureLoadContext var1, NBTBase var2);

   public boolean mayGenerate(S instanceData, StructurePrepareContext context, IvWorldData worldData) {
      return true;
   }

   public boolean skipGeneration(S instanceData, StructureLiveContext context, BlockPos pos, IBlockState state, IvWorldData worldData, BlockPos sourcePos) {
      return false;
   }

   public void transform(S instanceData, Transformer.Phase phase, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer) {
   }

   static {
      idRandomizers.push(new EaglercraftRandom(-559038737L));
   }

   public static enum Phase {
      BEFORE,
      AFTER;

      private Phase() {
      }
   }
}
