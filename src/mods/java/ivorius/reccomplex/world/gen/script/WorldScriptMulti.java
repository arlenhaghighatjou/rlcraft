package ivorius.reccomplex.world.gen.script;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.worldscripts.multi.TableDataSourceWorldScriptMulti;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.expression.EnvironmentExpression;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class WorldScriptMulti implements WorldScript<WorldScriptMulti.InstanceData> {
   public final List<WorldScript> scripts = new ArrayList();
   public final EnvironmentExpression environmentExpression = new EnvironmentExpression();

   public WorldScriptMulti() {
   }

   public WorldScriptMulti.InstanceData prepareInstanceData(StructurePrepareContext context, BlockPos pos) {
      WorldScriptMulti.InstanceData instanceData = new WorldScriptMulti.InstanceData();

      for(WorldScript script : this.scripts) {
         instanceData.addInstanceData(script, script.prepareInstanceData(context, pos));
      }

      instanceData.deactivated = !this.environmentExpression.test(context.environment);
      return instanceData;
   }

   public WorldScriptMulti.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new WorldScriptMulti.InstanceData(nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound(), this.scripts, context);
   }

   public void generate(StructureSpawnContext context, RunTransformer transformer, WorldScriptMulti.InstanceData instanceData, BlockPos pos) {
      if (!instanceData.deactivated) {
         for(Pair<WorldScript, NBTStorable> paired : instanceData.pairedScripts) {
            ((WorldScript)paired.getLeft()).generate(context, transformer, (NBTStorable)paired.getRight(), pos);
         }
      }

   }

   public String getDisplayString() {
      int amount = this.scripts.size();
      return amount == 0 ? IvTranslations.get("reccomplex.worldscript.multi.none") : IvTranslations.format("reccomplex.worldscript.multi.multiple", new Object[]{amount});
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(BlockPos realWorldPos, TableNavigator navigator, TableDelegate tableDelegate) {
      return new TableDataSourceWorldScriptMulti(this, realWorldPos, tableDelegate, navigator);
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.scripts.clear();
      List var10000 = this.scripts;
      Stream var10001 = NBTTagLists.compoundsFrom(compound, "scripts").stream();
      WorldScriptRegistry var10002 = WorldScriptRegistry.INSTANCE;
      WorldScriptRegistry.INSTANCE.getClass();
      var10000.addAll((Collection)var10001.map(var10002::read).filter(Objects::nonNull).collect(Collectors.toList()));
      this.environmentExpression.setExpression(compound.getString("environmentMatcher"));
   }

   public void writeToNBT(NBTTagCompound compound) {
      Stream var10002 = this.scripts.stream();
      WorldScriptRegistry var10003 = WorldScriptRegistry.INSTANCE;
      WorldScriptRegistry.INSTANCE.getClass();
      NBTTagLists.writeTo(compound, "scripts", (List)var10002.map(var10003::write).collect(Collectors.toList()));
      compound.setString("environmentMatcher", this.environmentExpression.getExpression());
   }

   public static class InstanceData implements NBTStorable {
      public final List<Pair<WorldScript, NBTStorable>> pairedScripts = new ArrayList();
      public boolean deactivated;

      public InstanceData() {
      }

      public InstanceData(NBTTagCompound compound, List<WorldScript> expectedScripts, StructureLoadContext context) {
         List<NBTTagCompound> compoundsFrom = NBTTagLists.compoundsFrom(compound, "instanceDates");

         for(int i = 0; i < compoundsFrom.size(); ++i) {
            NBTTagCompound scriptTag = (NBTTagCompound)compoundsFrom.get(i);
            WorldScript script = (WorldScript)expectedScripts.get(i);
            if (WorldScriptRegistry.INSTANCE.type(script.getClass()).equals(scriptTag.getString("id"))) {
               this.addInstanceData(script, script.loadInstanceData(context, scriptTag.getTag("data")));
            }
         }

         this.deactivated = compound.getBoolean("deactivated");
      }

      public void addInstanceData(WorldScript script, NBTStorable instanceData) {
         this.pairedScripts.add(Pair.of(script, instanceData));
      }

      public NBTBase writeToNBT() {
         NBTTagCompound nbt = new NBTTagCompound();
         NBTTagList dataList = new NBTTagList();

         for(int i = 0; i < this.pairedScripts.size(); ++i) {
            NBTTagCompound scriptTag = new NBTTagCompound();
            scriptTag.setTag("data", ((NBTStorable)((Pair)this.pairedScripts.get(i)).getRight()).writeToNBT());
            scriptTag.setString("id", WorldScriptRegistry.INSTANCE.type(((WorldScript)((Pair)this.pairedScripts.get(i)).getLeft()).getClass()));
            dataList.appendTag(scriptTag);
         }

         nbt.setTag("instanceDates", dataList);
         nbt.setBoolean("deactivated", this.deactivated);
         return nbt;
      }
   }
}
