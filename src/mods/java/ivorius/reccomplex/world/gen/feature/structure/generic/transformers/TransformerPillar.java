package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTPillar;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.nbt.NBTNone;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerPillar extends TransformerSingleBlock<NBTNone> {
   public BlockExpression sourceMatcher;
   public IBlockState destState;
   public boolean generateUpwards;

   public TransformerPillar() {
      this((String)null, BlockExpression.of(RecurrentComplex.specialRegistry, Blocks.STONE, Integer.valueOf(0)), Blocks.STONE.getDefaultState(), false);
   }

   public TransformerPillar(@Nullable String id, String sourceExpression, IBlockState destState, boolean generateUpwards) {
      super(id != null ? id : randomID(TransformerPillar.class));
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceExpression);
      this.destState = destState;
      this.generateUpwards = generateUpwards;
   }

   public boolean matches(Environment environment, NBTNone instanceData, BlockPos sourcePos, IBlockState state) {
      return this.sourceMatcher.test(state);
   }

   public void transformBlock(NBTNone instanceData, Transformer.Phase phase, StructureSpawnContext context, RunTransformer transformer, int[] areaSize, BlockPos coord, IBlockState sourceState) {
      if (RecurrentComplex.specialRegistry.isSafe(this.destState.getBlock())) {
         int direction = this.generateUpwards ? 1 : -1;
         World world = context.environment.world;
         context.setBlock(coord, this.destState, 2);

         for(int y = coord.getY() + direction; y > 0 && y < world.getHeight(); y += direction) {
            BlockPos pos = new BlockPos(coord.getX(), y, coord.getZ());
            IBlockState blockState = world.getBlockState(pos);
            if (!blockState.getBlock().isReplaceable(world, pos) && blockState.getMaterial() != Material.LEAVES && !blockState.getBlock().isFoliage(world, pos)) {
               break;
            }

            context.setBlock(pos, this.destState, 2);
         }

      }
   }

   public NBTNone prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      return new NBTNone();
   }

   public NBTNone loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new NBTNone();
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return "Pillar: " + this.sourceMatcher.getDisplayString((Object)null) + "->" + this.destState.getBlock().getLocalizedName();
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTPillar(this, navigator, delegate);
   }

   public boolean generatesInPhase(NBTNone instanceData, Transformer.Phase phase) {
      return phase == Transformer.Phase.BEFORE;
   }

   public static class Serializer implements JsonDeserializer<TransformerPillar>, JsonSerializer<TransformerPillar> {
      private MCRegistry registry;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
      }

      public TransformerPillar deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerPillar");
         String id = Transformer.readID(jsonObject);
         String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata");
         if (expression == null) {
            expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         }

         String destBlock = JsonUtils.getString(jsonObject, "dest");
         Block dest = this.registry.blockFromID(new ResourceLocation(destBlock));
         IBlockState destState = dest != null ? BlockStates.fromMetadata(dest, JsonUtils.getInt(jsonObject, "destMetadata")) : null;
         boolean generateUpwards = JsonUtils.getBoolean(jsonObject, "generateUpwards", false);
         return new TransformerPillar(id, expression, destState, generateUpwards);
      }

      public JsonElement serialize(TransformerPillar transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
         jsonObject.addProperty("dest", this.registry.idFromBlock(transformer.destState.getBlock()).toString());
         jsonObject.addProperty("destMetadata", Integer.valueOf(BlockStates.toMetadata(transformer.destState)));
         jsonObject.addProperty("generateUpwards", Boolean.valueOf(transformer.generateUpwards));
         return jsonObject;
      }
   }
}
