package vazkii.quark.base.block;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockQuarkPressurePlate extends BlockPressurePlate implements IQuarkBlock {
   private final String[] variants;
   private final String bareName;

   public BlockQuarkPressurePlate(String name, Material material, Sensitivity sensitivity) {
      super(material, sensitivity);
      this.bareName = name;
      this.variants = new String[]{this.bareName};
      this.setUnlocalizedName(this.bareName);
   }

   protected int computeRedstoneStrength(@Nonnull World worldIn, @Nonnull BlockPos pos) {
      AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
      List<? extends Entity> list = this.getValidEntities(worldIn, axisalignedbb);
      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected abstract List<Entity> getValidEntities(World var1, AxisAlignedBB var2);

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.register(name);
      return this;
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[0];
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }
}
