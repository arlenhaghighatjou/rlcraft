package xzeroair.trinkets.items.trinkets;

import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.blocks.BlockTeddyBear;
import xzeroair.trinkets.blocks.tileentities.TileEntityTeddyBear;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.init.ModBlocks;
import xzeroair.trinkets.items.base.AccessoryBase;
import xzeroair.trinkets.traits.abilities.AbilityWellRested;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigTeddyBear;

public class TrinketTeddyBear extends AccessoryBase {
   public static final ConfigTeddyBear serverConfig;

   public TrinketTeddyBear(String name) {
      super(name);
      this.setUUID("33b34669-715d-4caa-a31e-9c643c52ba66");
   }

   public String[] getAttributeConfig() {
      return serverConfig.attributes;
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
      if (serverConfig.sleep_bonus) {
         abilities.add(new AbilityWellRested());
      }

   }

   public String getItemStackDisplayName(ItemStack stack) {
      return (String)Capabilities.getTrinketProperties(stack, super.getItemStackDisplayName(stack), (prop, name) -> {
         String crafter = prop.getCrafter();
         return !crafter.isEmpty() && !stack.hasDisplayName() ? crafter + "'s " + name : name;
      });
   }

   public int getTeddyVariant(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String name = stack.getDisplayName().toLowerCase();
         String crafterID = (String)Capabilities.getTrinketProperties(stack, "", (prop, UUID) -> {
            String ID = prop.getCrafterUUID();
            return !ID.isEmpty() ? ID : UUID;
         });
         boolean hasDisplayName = stack.hasDisplayName();
         if ((!crafterID.equalsIgnoreCase("854adc0b-ae55-48d6-b7ba-e641a1eebf42") || hasDisplayName) && !name.contains("nyan")) {
            if (name.contains("ken")) {
               return 9;
            } else if (name.contains("ryu")) {
               return 8;
            } else if ((!crafterID.equalsIgnoreCase("b2b629a6-454e-4047-a143-4f357171d639") || hasDisplayName) && !name.contains("twilight")) {
               if ((!crafterID.equalsIgnoreCase("14bba455-affa-46d0-9cf0-806cc0f3d454") || hasDisplayName) && !name.contains("artsy")) {
                  if ((!crafterID.equalsIgnoreCase("5a215d65-e57d-47c6-a322-9ede12a4a100") || hasDisplayName) && !name.contains("potastic") && !name.contains("potasticP") && !name.contains("panda")) {
                     if ((!crafterID.equalsIgnoreCase("7d50a302-a01c-4e6a-8ea4-03f98662df28") || hasDisplayName) && !name.contains("stingin") && !name.contains("bee") && !name.contains("bzzz")) {
                        if ((!crafterID.equalsIgnoreCase("cdfccefb-1a2e-4fb8-a3b5-041da27fde61") || hasDisplayName) && !name.contains("shivaxi")) {
                           if ((!crafterID.equalsIgnoreCase("6b5d5e9b-1fe8-4c61-a043-1d84ce95765d") || hasDisplayName) && !name.contains("rembo") && !name.contains("cool") && !name.contains("badass")) {
                              return !name.contains("scary") && !name.contains("freddy") && !name.contains("snuggles") ? 0 : 2;
                           } else {
                              return 1;
                           }
                        } else {
                           return 3;
                        }
                     } else {
                        return 4;
                     }
                  } else {
                     return 5;
                  }
               } else {
                  return 6;
               }
            } else {
               return 7;
            }
         } else {
            return 10;
         }
      } else {
         return 0;
      }
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (facing == EnumFacing.DOWN) {
         return EnumActionResult.FAIL;
      } else {
         if (world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            facing = EnumFacing.UP;
            pos = pos.down();
         }

         IBlockState iblockstate = world.getBlockState(pos);
         Block block = iblockstate.getBlock();
         boolean flag = block.isReplaceable(world, pos);
         if (!flag) {
            if (!world.getBlockState(pos).getMaterial().isSolid() && !world.isSideSolid(pos, facing, true)) {
               return EnumActionResult.FAIL;
            }

            pos = pos.offset(facing);
         }

         ItemStack itemstack = player.getHeldItem(hand);
         BlockTeddyBear TeddyBlock = ModBlocks.Placeables.getTeddy(this.getTeddyVariant(itemstack));
         if (player.canPlayerEdit(pos, facing, itemstack) && TeddyBlock.canPlaceBlockAt(world, pos)) {
            if (world.isRemote) {
               return EnumActionResult.SUCCESS;
            } else {
               IBlockState iblockstate1 = TeddyBlock.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, 0, player, hand);
               if (this.placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, iblockstate1)) {
                  int i = 0;
                  if (facing == EnumFacing.UP) {
                     i = MathHelper.floor((double)(player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
                  }

                  TileEntity tileentity = world.getTileEntity(pos);
                  if (tileentity instanceof TileEntityTeddyBear) {
                     TileEntityTeddyBear teddy = (TileEntityTeddyBear)tileentity;
                     teddy.setRotation(i);
                  }

                  iblockstate1 = world.getBlockState(pos);
                  SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, world, pos, player);
                  world.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                  itemstack.shrink(1);
                  if (player instanceof EntityPlayerMP) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, itemstack);
                  }
               }

               return EnumActionResult.SUCCESS;
            }
         } else {
            return EnumActionResult.FAIL;
         }
      }
   }

   public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
      if (!world.setBlockState(pos, newState, 11)) {
         return false;
      } else {
         IBlockState state = world.getBlockState(pos);
         BlockTeddyBear myBlock = ModBlocks.Placeables.getTeddy(this.getTeddyVariant(stack));
         if (state.getBlock() == myBlock) {
            ItemBlock.setTileEntityNBT(world, player, pos, stack);
            myBlock.onBlockPlacedBy(world, pos, state, player, stack);
            if (player instanceof EntityPlayerMP) {
               CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            }
         }

         return true;
      }
   }

   public boolean ItemEnabled() {
      return serverConfig.enabled;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelResourceLocation normal = new ModelResourceLocation(this.getRegistryName().toString(), "inventory");
      ModelResourceLocation rembo = new ModelResourceLocation(this.getRegistryName().toString() + "_rembos", "inventory");
      ModelResourceLocation scary = new ModelResourceLocation(this.getRegistryName().toString() + "_scary", "inventory");
      ModelResourceLocation shivaxi = new ModelResourceLocation(this.getRegistryName().toString() + "_shivaxi", "inventory");
      ModelResourceLocation bee = new ModelResourceLocation(this.getRegistryName().toString() + "_bee", "inventory");
      ModelResourceLocation panda = new ModelResourceLocation(this.getRegistryName().toString() + "_panda", "inventory");
      ModelResourceLocation artsy = new ModelResourceLocation(this.getRegistryName().toString() + "_artsy", "inventory");
      ModelResourceLocation twilight = new ModelResourceLocation(this.getRegistryName().toString() + "_twilight", "inventory");
      ModelResourceLocation ryu = new ModelResourceLocation(this.getRegistryName().toString() + "_ryu", "inventory");
      ModelResourceLocation ken = new ModelResourceLocation(this.getRegistryName().toString() + "_ken", "inventory");
      ModelResourceLocation nyan = new ModelResourceLocation(this.getRegistryName().toString() + "_nyan", "inventory");
      ModelBakery.registerItemVariants(this, new ResourceLocation[]{normal, scary, rembo, shivaxi, bee, panda, artsy, twilight, ryu, ken, nyan});
      ModelLoader.setCustomMeshDefinition(this, (stack) -> {
         int type = this.getTeddyVariant(stack);
         if (type == 10) {
            return nyan;
         } else if (type == 9) {
            return ken;
         } else if (type == 8) {
            return ryu;
         } else if (type == 7) {
            return twilight;
         } else if (type == 6) {
            return artsy;
         } else if (type == 5) {
            return panda;
         } else if (type == 4) {
            return bee;
         } else if (type == 3) {
            return shivaxi;
         } else if (type == 2) {
            return scary;
         } else {
            return type == 1 ? rembo : normal;
         }
      });
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.TEDDY_BEAR;
   }
}
