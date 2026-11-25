package com.teamacronymcoders.contenttweaker.modules.vanilla.tileentity;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.tileentities.TileEntityBase;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityContent extends TileEntityBase implements ITickable {
   public static final Map<String, TileEntityRepresentation> REPRESENTATION_MAP = Maps.newHashMap();
   private TileEntityRepresentation representation;
   private IData tileEntityData;
   private MCWorld zenWorld;
   private MCBlockPos zenPos;

   public TileEntityContent() {
      this(new TileEntityRepresentation("none"));
   }

   public TileEntityContent(TileEntityRepresentation representation) {
      this.representation = representation;
      this.tileEntityData = new DataMap(Maps.newHashMap(), false);
   }

   protected void readFromDisk(NBTTagCompound data) {
      String representationName = data.getString("representation");
      this.representation = (TileEntityRepresentation)Optional.ofNullable(REPRESENTATION_MAP.get(representationName)).orElseGet(() -> {
         return new TileEntityRepresentation(representationName);
      });
      this.tileEntityData = NBTConverter.from(data.getCompoundTag("data"), false);
   }

   protected NBTTagCompound writeToDisk(NBTTagCompound data) {
      data.setString("representation", this.representation.name);
      data.setTag("data", NBTConverter.from(this.tileEntityData));
      return data;
   }

   public void update() {
      if (this.world.isRemote) {
         this.representation.clientUpdate.onUpdate(this.zenWorld, this.zenPos, this.tileEntityData);
      } else {
         this.representation.serverUpdate.onUpdate(this.zenWorld, this.zenPos, this.tileEntityData);
      }

   }

   public void setWorld(@Nonnull World world) {
      super.setWorld(world);
      this.zenWorld = new MCWorld(world);
   }

   public void setPos(BlockPos blockPos) {
      super.setPos(blockPos);
      this.zenPos = new MCBlockPos(blockPos);
   }
}
