package ivorius.reccomplex.block;

import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.reccomplex.gui.worldscripts.GuiEditScriptBlock;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import ivorius.reccomplex.world.gen.script.WorldScriptMulti;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBlockScript extends TileEntity implements GeneratingTileEntity<WorldScriptMulti.InstanceData>, TileEntityWithGUI {
   public final WorldScriptMulti script = new WorldScriptMulti();
   public boolean spawnTriggerable = true;
   public boolean redstoneTriggerable = false;
   public boolean redstoneTriggered = false;

   public TileEntityBlockScript() {
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      super.readFromNBT(nbtTagCompound);
      this.readSyncedNBT(nbtTagCompound);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
      super.writeToNBT(nbtTagCompound);
      this.writeSyncedNBT(nbtTagCompound);
      return nbtTagCompound;
   }

   public WorldScriptMulti.InstanceData prepareInstanceData(StructurePrepareContext context) {
      return this.spawnTriggerable ? this.doPrepareInstanceData(context) : null;
   }

   public WorldScriptMulti.InstanceData doPrepareInstanceData(StructurePrepareContext context) {
      return this.script.prepareInstanceData(context, this.getPos());
   }

   public WorldScriptMulti.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return this.script.loadInstanceData(context, nbt);
   }

   public void generate(StructureSpawnContext context, RunTransformer transformer, WorldScriptMulti.InstanceData instanceData) {
      this.script.generate(context, transformer, instanceData, this.pos);
   }

   public boolean shouldPlaceInWorld(StructurePrepareContext context, WorldScriptMulti.InstanceData instanceData) {
      return this.redstoneTriggerable;
   }

   public void writeSyncedNBT(NBTTagCompound compound) {
      compound.setTag("script", NBTCompoundObjects.write(this.script));
      compound.setBoolean("spawnTriggerable", this.spawnTriggerable);
      compound.setBoolean("redstoneTriggerable", this.redstoneTriggerable);
      compound.setBoolean("redstoneTriggered", this.redstoneTriggered);
   }

   public void readSyncedNBT(NBTTagCompound compound) {
      this.script.readFromNBT(compound.getCompoundTag("script"));
      this.spawnTriggerable = !compound.hasKey("spawnTriggerable", 1) || compound.getBoolean("spawnTriggerable");
      this.redstoneTriggerable = compound.getBoolean("redstoneTriggerable");
      this.redstoneTriggered = compound.getBoolean("redstoneTriggered");
   }

   @SideOnly(Side.CLIENT)
   public void openEditGUI() {
      Minecraft.getMinecraft().displayGuiScreen(new GuiEditScriptBlock(this));
   }
}
