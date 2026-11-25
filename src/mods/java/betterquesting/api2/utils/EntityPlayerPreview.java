package betterquesting.api2.utils;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPlayerPreview extends EntityOtherPlayerMP {
   private final ResourceLocation resource;

   public EntityPlayerPreview(World worldIn) {
      this(worldIn, new GameProfile((UUID)null, "Notch"));
   }

   public EntityPlayerPreview(World worldIn, GameProfile gameProfileIn) {
      super(worldIn, gameProfileIn);
      this.resource = new ResourceLocation("betterquesting", "textures/skin_cache/" + gameProfileIn.getName());
      this.getDataManager().set(PLAYER_MODEL_FLAG, Byte.valueOf((byte)1));
   }

   public ResourceLocation getLocationSkin() {
      return this.resource;
   }

   public ResourceLocation getLocationCape() {
      return null;
   }

   public boolean hasSkin() {
      return true;
   }

   public ITextComponent getDisplayName() {
      return new TextComponentString("");
   }

   @SideOnly(Side.CLIENT)
   public boolean isWearing(EnumPlayerModelParts part) {
      return true;
   }
}
