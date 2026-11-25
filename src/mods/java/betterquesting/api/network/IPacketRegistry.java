package betterquesting.api.network;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IPacketRegistry {
   void registerServerHandler(@Nonnull ResourceLocation var1, @Nonnull Consumer<Tuple<NBTTagCompound, EntityPlayerMP>> var2);

   @SideOnly(Side.CLIENT)
   void registerClientHandler(@Nonnull ResourceLocation var1, @Nonnull Consumer<NBTTagCompound> var2);

   @Nullable
   Consumer<Tuple<NBTTagCompound, EntityPlayerMP>> getServerHandler(@Nonnull ResourceLocation var1);

   @Nullable
   @SideOnly(Side.CLIENT)
   Consumer<NBTTagCompound> getClientHandler(@Nonnull ResourceLocation var1);
}
