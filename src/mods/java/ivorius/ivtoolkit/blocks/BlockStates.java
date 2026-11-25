package ivorius.ivtoolkit.blocks;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class BlockStates {
   public BlockStates() {
   }

   public static int toMetadata(IBlockState state) {
      return state.getBlock().getMetaFromState(state);
   }

   public static IBlockState fromMetadata(Block block, int metadata) {
      return block.getStateFromMeta(metadata);
   }

   public static IBlockState readBlockState(ByteBuf buf) {
      Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
      return block.getStateFromMeta(buf.readInt());
   }

   public static void writeBlockState(ByteBuf buf, IBlockState state) {
      ByteBufUtils.writeUTF8String(buf, ((ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock())).toString());
      buf.writeInt(toMetadata(state));
   }
}
