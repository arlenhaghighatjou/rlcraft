package vazkii.quark.automation.block;

import java.util.List;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.quark.base.block.BlockQuarkPressurePlate;

public class BlockObsidianPressurePlate extends BlockQuarkPressurePlate {
   public BlockObsidianPressurePlate() {
      super("obsidian_pressure_plate", Material.ROCK, Sensitivity.MOBS);
      this.setHardness(50.0F);
      this.setResistance(2000.0F);
      this.setSoundType(SoundType.STONE);
   }

   protected List<Entity> getValidEntities(World world, AxisAlignedBB aabb) {
      return world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
   }
}
