package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshafts;
import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import com.yungnickyoung.minecraft.bettermineshafts.util.BoxUtil;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.Arrays;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class OreDeposit extends MineshaftPiece {
   private OreDeposit.OreType oreType;
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 4;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 3;

   public OreDeposit() {
   }

   public OreDeposit(int i, int chunkPieceLen, EaglercraftRandom random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, chunkPieceLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   @ParametersAreNonnullByDefault
   protected void writeStructureToNBT(NBTTagCompound tag) {
      super.writeStructureToNBT(tag);
      tag.setInteger("OreType", this.oreType.value);
   }

   @ParametersAreNonnullByDefault
   protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager templateManager) {
      super.readStructureFromNBT(tag, templateManager);
      this.oreType = OreDeposit.OreType.valueOf(tag.getInteger("OreType"));
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, EaglercraftRandom random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = BoxUtil.boxFromCoordsWithRotation(x, y, z, 5, 5, 4, direction);
      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, EaglercraftRandom random) {
      int r = random.nextInt(100);

      for(OreDeposit.OreType oreType : OreDeposit.OreType.values()) {
         if (r < oreType.threshold) {
            this.oreType = oreType;
            break;
         }
      }

      if (OreDeposit.OreType.DIAMOND.threshold != 100) {
         BetterMineshafts.LOGGER.error("Your ore spawn chances don't add up to 100! Ores won't spawn as you intend!");
      }

      if (this.oreType == null) {
         this.oreType = OreDeposit.OreType.COBBLE;
      }

   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, EaglercraftRandom random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 4, 3)) {
         IBlockState COBBLE = Blocks.COBBLESTONE.getDefaultState();
         IBlockState ORE_BLOCK = this.oreType.getBlock();
         this.chanceReplaceNonAir(world, box, random, 0.9F, 0, 0, 0, 4, 4, 3, COBBLE);
         this.chanceReplaceNonAir(world, box, random, 0.65F, 1, 1, 1, 3, 3, 2, ORE_BLOCK);
         this.chanceReplaceNonAir(world, box, random, 0.15F, 0, 0, 0, 4, 4, 3, ORE_BLOCK);
         return true;
      } else {
         return false;
      }
   }

   public static enum OreType {
      COBBLE(0, Blocks.COBBLESTONE.getDefaultState(), Configuration.ores.cobble),
      COAL(1, Blocks.COAL_ORE.getDefaultState(), Configuration.ores.coal + COBBLE.threshold),
      IRON(2, Blocks.IRON_ORE.getDefaultState(), Configuration.ores.iron + COAL.threshold),
      REDSTONE(3, Blocks.REDSTONE_ORE.getDefaultState(), Configuration.ores.redstone + IRON.threshold),
      GOLD(4, Blocks.GOLD_ORE.getDefaultState(), Configuration.ores.gold + REDSTONE.threshold),
      LAPIS(5, Blocks.LAPIS_ORE.getDefaultState(), Configuration.ores.lapis + GOLD.threshold),
      EMERALD(6, Blocks.EMERALD_ORE.getDefaultState(), Configuration.ores.emerald + LAPIS.threshold),
      DIAMOND(7, Blocks.DIAMOND_ORE.getDefaultState(), Configuration.ores.diamond + EMERALD.threshold);

      private final int value;
      private final IBlockState block;
      private int threshold;

      private OreType(int value, IBlockState block, int threshold) {
         this.value = value;
         this.block = block;
         this.threshold = threshold;
      }

      public static OreDeposit.OreType valueOf(int value) {
         return (OreDeposit.OreType)Arrays.stream(values()).filter((oreType) -> {
            return oreType.value == value;
         }).findFirst().get();
      }

      public IBlockState getBlock() {
         return this.block;
      }
   }
}
