package com.ferreusveritas.dynamictrees.items;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.ModTabs;
import com.ferreusveritas.dynamictrees.blocks.BlockBonsaiPot;
import com.ferreusveritas.dynamictrees.event.SeedVoluntaryPlantEvent;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.TreeGenerator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class Seed extends Item {
   public static final Seed NULLSEED = new Seed("null") {
      {
         this.setCreativeTab((CreativeTabs)null);
      }

      public void setSpecies(Species species, ItemStack seedStack) {
      }

      public Species getSpecies(ItemStack seedStack) {
         return Species.NULLSPECIES;
      }

      public boolean onEntityItemUpdate(EntityItem entityItem) {
         entityItem.setDead();
         return false;
      }

      public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
         return EnumActionResult.FAIL;
      }
   };
   private Species species;

   public Seed(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(ModTabs.dynamicTreesTab);
   }

   public void setSpecies(Species species, ItemStack seedStack) {
      this.species = species;
   }

   public Species getSpecies(ItemStack seedStack) {
      return this.species;
   }

   public boolean isValid() {
      return this != NULLSEED;
   }

   public void ifValid(Consumer<Seed> c) {
      if (this.isValid()) {
         c.accept(this);
      }

   }

   public boolean onEntityItemUpdate(EntityItem entityItem) {
      if (entityItem.lifespan == 6000) {
         entityItem.lifespan = this.getTimeToLive(entityItem.getItem()) + 20;
         if (entityItem.lifespan == 6000) {
            entityItem.lifespan = 6001;
         }
      }

      if (entityItem.ticksExisted >= entityItem.lifespan - 20) {
         World world = entityItem.world;
         if (!world.isRemote) {
            ItemStack seedStack = entityItem.getItem();
            BlockPos pos = new BlockPos(entityItem);
            SeedVoluntaryPlantEvent seedVolEvent = new SeedVoluntaryPlantEvent(entityItem, this.getSpecies(seedStack), pos, this.shouldPlant(world, pos, seedStack));
            MinecraftForge.EVENT_BUS.post(seedVolEvent);
            if (!seedVolEvent.isCanceled() && seedVolEvent.getWillPlant()) {
               this.doPlanting(world, pos, (EntityPlayer)null, seedStack);
            }

            seedStack.setCount(0);
         }

         entityItem.setDead();
      }

      return false;
   }

   public boolean doPlanting(World world, BlockPos pos, EntityPlayer planter, ItemStack seedStack) {
      Species species = this.getSpecies(seedStack);
      Species speciesForLocation = species.getFamily().getSpeciesForLocation(world, pos);
      if (species.equals(species.getFamily().getCommonSpecies()) && !speciesForLocation.equals(species.getFamily().getCommonSpecies())) {
         species = speciesForLocation;
      }

      if (species.plantSapling(world, pos)) {
         String joCode = this.getCode(seedStack);
         if (!joCode.isEmpty()) {
            world.setBlockToAir(pos);
            species.getJoCode(joCode).setCareful(true).generate(world, species, pos.down(), world.getBiome(pos), planter != null ? planter.getHorizontalFacing() : EnumFacing.NORTH, 8, SafeChunkBounds.ANY);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean shouldPlant(World world, BlockPos pos, ItemStack seedStack) {
      if (this.hasForcePlant(seedStack)) {
         return true;
      } else if (!world.canBlockSeeSky(pos)) {
         return false;
      } else {
         float plantChance = this.getSpecies(seedStack).biomeSuitability(world, pos) * ModConfigs.seedPlantRate;
         TreeGenerator treeGen = TreeGenerator.getTreeGenerator();
         if (ModConfigs.seedOnlyForest && treeGen != null) {
            plantChance *= treeGen.getBiomeDataBase(world).getForestness(world.getBiome(pos));
         }

         float accum = 1.0F;

         for(int count = seedStack.getCount(); count-- > 0; accum *= 1.0F - plantChance) {
            ;
         }

         plantChance = 1.0F - accum;
         return plantChance > world.rand.nextFloat();
      }
   }

   public boolean hasForcePlant(ItemStack seedStack) {
      boolean forcePlant = false;
      if (seedStack.hasTagCompound()) {
         NBTTagCompound nbtData = seedStack.getTagCompound();
         forcePlant = nbtData.getBoolean("forceplant");
      }

      return forcePlant;
   }

   public int getTimeToLive(ItemStack seedStack) {
      int lifespan = ModConfigs.seedTimeToLive;
      if (seedStack.hasTagCompound()) {
         NBTTagCompound nbtData = seedStack.getTagCompound();
         if (nbtData.hasKey("lifespan")) {
            lifespan = nbtData.getInteger("lifespan");
         }
      }

      return lifespan;
   }

   public String getCode(ItemStack seedStack) {
      String joCode = "";
      if (seedStack.hasTagCompound()) {
         NBTTagCompound nbtData = seedStack.getTagCompound();
         joCode = nbtData.getString("code");
      }

      return joCode;
   }

   public EnumActionResult onItemUseFlowerPot(EntityPlayer player, World world, BlockPos pos, EnumHand hand, ItemStack seedStack, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState emptyPotState = world.getBlockState(pos);
      if (emptyPotState.getBlock() instanceof BlockFlowerPot && emptyPotState == emptyPotState.getBlock().getDefaultState()) {
         Species species = this.getSpecies(seedStack);
         BlockBonsaiPot bonzaiPot = species.getBonzaiPot();
         world.setBlockState(pos, bonzaiPot.getDefaultState());
         if (bonzaiPot.setSpecies(world, species, pos) && bonzaiPot.setPotState(world, emptyPotState, pos)) {
            seedStack.shrink(1);
            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.PASS;
   }

   public EnumActionResult onItemUsePlantSeed(EntityPlayer player, World world, BlockPos pos, EnumHand hand, ItemStack seedStack, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState iblockstate = world.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (block.isReplaceable(world, pos)) {
         pos = pos.down();
         facing = EnumFacing.UP;
      }

      if (facing == EnumFacing.UP && player.canPlayerEdit(pos, facing, seedStack) && player.canPlayerEdit(pos.up(), facing, seedStack) && this.doPlanting(world, pos.up(), player, seedStack)) {
         seedStack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack seedStack = player.getHeldItem(hand);
      if (this.onItemUseFlowerPot(player, world, pos, hand, seedStack, facing, hitX, hitY, hitZ) == EnumActionResult.SUCCESS) {
         return EnumActionResult.SUCCESS;
      } else {
         return this.onItemUsePlantSeed(player, world, pos, hand, seedStack, facing, hitX, hitY, hitZ) == EnumActionResult.SUCCESS ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      }
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, world, tooltip, flagIn);
      if (stack.hasTagCompound()) {
         String joCode = this.getCode(stack);
         if (!joCode.isEmpty()) {
            tooltip.add("Code: Â§6" + joCode);
         }

         if (this.hasForcePlant(stack)) {
            tooltip.add("Force Planting: Â§3Enabled");
         }

         NBTTagCompound nbtData = stack.getTagCompound();
         if (nbtData.hasKey("lifespan")) {
            tooltip.add("Seed Life Span: Â§3" + nbtData.getInteger("lifespan"));
         }
      }

   }
}
