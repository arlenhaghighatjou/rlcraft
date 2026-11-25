package snownee.kiwi.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMod extends Block implements IModBlock {
   private final String name;

   public BlockMod(String name, Material materialIn) {
      this(name, materialIn, deduceSoundType(materialIn));
   }

   public BlockMod(String name, Material materialIn, SoundType soundType) {
      super(materialIn);
      this.name = name;
      this.setSoundType(soundType);
      this.setHardness(deduceHardness(materialIn));
   }

   public String getName() {
      return this.name;
   }

   @SideOnly(Side.CLIENT)
   public void mapModel() {
      for(int i = 0; i < this.getItemSubtypeAmount(); ++i) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(this.getRegistryName(), "inventory"));
      }

   }

   public void register(String modid) {
      this.setRegistryName(modid, this.getName());
      this.setUnlocalizedName(modid + "." + this.getName());
   }

   public Block cast() {
      return this;
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(int i = 0; i < this.getItemSubtypeAmount(); ++i) {
         items.add(new ItemStack(this, 1, i));
      }

   }

   public static SoundType deduceSoundType(Material material) {
      if (material != Material.WOOD && material != Material.GOURD) {
         if (material != Material.GROUND && material != Material.CLAY) {
            if (material != Material.PLANTS && material != Material.GRASS && material != Material.LEAVES && material != Material.VINE && material != Material.SPONGE && material != Material.TNT) {
               if (material == Material.IRON) {
                  return SoundType.METAL;
               } else if (material != Material.GLASS && material != Material.PORTAL && material != Material.ICE && material != Material.PACKED_ICE && material != Material.REDSTONE_LIGHT) {
                  if (material != Material.CLOTH && material != Material.CARPET && material != Material.CACTUS && material != Material.CAKE && material != Material.FIRE) {
                     if (material == Material.SAND) {
                        return SoundType.SAND;
                     } else if (material != Material.SNOW && material != Material.CRAFTED_SNOW) {
                        return material == Material.ANVIL ? SoundType.ANVIL : SoundType.STONE;
                     } else {
                        return SoundType.SNOW;
                     }
                  } else {
                     return SoundType.CLOTH;
                  }
               } else {
                  return SoundType.GLASS;
               }
            } else {
               return SoundType.PLANT;
            }
         } else {
            return SoundType.GROUND;
         }
      } else {
         return SoundType.WOOD;
      }
   }

   public static float deduceHardness(Material material) {
      if (material != Material.PLANTS && material != Material.CIRCUITS && material != Material.AIR) {
         if (material == Material.ROCK) {
            return 2.5F;
         } else if (material == Material.WOOD) {
            return 2.0F;
         } else if (material == Material.GRASS) {
            return 0.6F;
         } else if (material != Material.SAND && material != Material.GROUND && material != Material.CLAY) {
            if (material == Material.GLASS) {
               return 0.3F;
            } else if (material != Material.IRON && material != Material.ANVIL) {
               if (material == Material.WEB) {
                  return 4.0F;
               } else if (material == Material.CLOTH) {
                  return 0.8F;
               } else {
                  return material != Material.WATER && material != Material.LAVA ? 1.0F : 100.0F;
               }
            } else {
               return 5.0F;
            }
         } else {
            return 0.5F;
         }
      } else {
         return 0.0F;
      }
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      if (this.blockMaterial == Material.WOOD) {
         return 20;
      } else if (this.blockMaterial == Material.PLANTS) {
         return 100;
      } else if (this.blockMaterial == Material.CARPET) {
         return 20;
      } else if (this.blockMaterial == Material.VINE) {
         return 100;
      } else if (this.blockMaterial == Material.LEAVES) {
         return 60;
      } else {
         return this.blockMaterial == Material.CLOTH ? 60 : 0;
      }
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      if (this.blockMaterial == Material.WOOD) {
         return 5;
      } else if (this.blockMaterial == Material.PLANTS) {
         return 60;
      } else if (this.blockMaterial == Material.CARPET) {
         return 60;
      } else if (this.blockMaterial == Material.VINE) {
         return 15;
      } else if (this.blockMaterial == Material.LEAVES) {
         return 30;
      } else {
         return this.blockMaterial == Material.CLOTH ? 30 : 0;
      }
   }
}
