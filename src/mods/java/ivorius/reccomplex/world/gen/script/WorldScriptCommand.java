package ivorius.reccomplex.world.gen.script;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.random.WeightedSelector.Item;
import ivorius.ivtoolkit.tools.IvCollections;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.block.SpawnCommandLogic;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.worldscripts.command.TableDataSourceWorldScriptCommand;
import ivorius.reccomplex.nbt.NBTNone;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldScriptCommand implements WorldScript<NBTNone> {
   public List<WorldScriptCommand.Entry> entries = new ArrayList();

   public WorldScriptCommand() {
   }

   public List<WorldScriptCommand.Entry> getEntries() {
      return Collections.unmodifiableList(this.entries);
   }

   public void setEntries(List<WorldScriptCommand.Entry> entries) {
      IvCollections.setContentsOfList(this.entries, entries);
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      this.entries.clear();
      NBTTagList entryNBTs = nbtTagCompound.getTagList("commands", 10);

      for(int i = 0; i < entryNBTs.tagCount(); ++i) {
         this.entries.add(new WorldScriptCommand.Entry(entryNBTs.getCompoundTagAt(i)));
      }

   }

   public void writeToNBT(NBTTagCompound nbtTagCompound) {
      NBTTagList structureNBTList = new NBTTagList();

      for(WorldScriptCommand.Entry entry : this.entries) {
         structureNBTList.appendTag(entry.writeToNBT());
      }

      nbtTagCompound.setTag("commands", structureNBTList);
   }

   public NBTNone prepareInstanceData(StructurePrepareContext context, BlockPos pos) {
      return new NBTNone();
   }

   public NBTNone loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new NBTNone();
   }

   public void generate(final StructureSpawnContext context, RunTransformer transformer, NBTNone instanceData, final BlockPos pos) {
      if (context.includes(pos) && this.entries.size() > 0) {
         WorldScriptCommand.Entry entry = (WorldScriptCommand.Entry)WeightedSelector.selectItem(context.random, this.entries);
         if (entry.command.trim().isEmpty()) {
            return;
         }

         SpawnCommandLogic logic = new SpawnCommandLogic() {
            public BlockPos getPosition() {
               return pos;
            }

            public Vec3d getPositionVector() {
               return new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
            }

            public World getEntityWorld() {
               return context.environment.world;
            }

            public void updateCommand() {
               IBlockState iblockstate = context.environment.world.getBlockState(pos);
               context.environment.world.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
            }

            @SideOnly(Side.CLIENT)
            public int getCommandBlockType() {
               return 0;
            }

            @SideOnly(Side.CLIENT)
            public void fillInInfo(ByteBuf buf) {
               buf.writeInt(pos.getX());
               buf.writeInt(pos.getY());
               buf.writeInt(pos.getZ());
            }

            public Entity getCommandSenderEntity() {
               return null;
            }

            public MinecraftServer getServer() {
               return context.environment.world.getMinecraftServer();
            }
         };
         logic.setCommand(entry.command);

         try {
            logic.trigger(context.environment.world);
         } catch (Exception var8) {
            RecurrentComplex.logger.error("Error executing command '%s'", entry.command);
            RecurrentComplex.logger.error("Command execution failed", var8);
         }
      }

   }

   public String getDisplayString() {
      return IvTranslations.get("reccomplex.worldscript.command");
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(BlockPos realWorldPos, TableNavigator navigator, TableDelegate tableDelegate) {
      return new TableDataSourceWorldScriptCommand(this, tableDelegate, navigator);
   }

   public static class Entry implements Item {
      public String command;
      public Double weight;

      public Entry(Double weight, String command) {
         this.command = command;
         this.weight = weight;
      }

      public Entry(NBTTagCompound compound) {
         this.command = compound.getString("command");
         if (compound.hasKey("weight", 6)) {
            this.weight = compound.getDouble("weight");
         } else if (compound.hasKey("weight", 3)) {
            this.weight = (double)compound.getInteger("weight") * 0.01D;
         } else {
            this.weight = null;
         }

      }

      public NBTTagCompound writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setString("command", this.command);
         if (this.weight != null) {
            compound.setDouble("weight", this.weight.doubleValue());
         }

         return compound;
      }

      public double getWeight() {
         return this.weight != null ? this.weight.doubleValue() : 1.0D;
      }

      public boolean hasDefaultWeight() {
         return this.weight == null;
      }
   }
}
