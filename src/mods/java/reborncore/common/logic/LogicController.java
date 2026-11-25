package reborncore.common.logic;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import reborncore.RebornCore;
import reborncore.RebornRegistry;
import reborncore.client.guibuilder.GuiBuilder;
import reborncore.common.network.VanillaPacketDispatcher;

public abstract class LogicController extends TileEntity {
   String name;
   public ItemStackHandler inv = new ItemStackHandler(this.getInvSize());
   public GuiBuilder builder = new GuiBuilder(GuiBuilder.defaultTextureSheet);
   public static final AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

   public LogicController() {
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public abstract int getInvSize();

   public boolean hasInv() {
      return this.getInvSize() != 0;
   }

   public ItemStackHandler getInv() {
      return this.inv;
   }

   @SideOnly(Side.CLIENT)
   public GuiBuilder getBuilder() {
      return this.builder;
   }

   public int getXSize() {
      return 176;
   }

   public int getYsize() {
      return 176;
   }

   @SideOnly(Side.CLIENT)
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY, int guiLeft, int guiTop, int xSize, int ySize, LogicGui gui) {
      this.getBuilder().drawDefaultBackground(gui, guiLeft, guiTop, xSize, ySize);
      this.getBuilder().drawPlayerSlots(gui, guiLeft + xSize / 2, guiTop + 84, true);
      if (this.getSlots() != null) {
         for(Slot s : this.getSlots()) {
            this.getBuilder().drawSlot(gui, guiLeft + s.xPos - 1, guiTop + s.yPos - 1);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY, GuiContainer gui, int guiLeft, int guiTop) {
   }

   @SideOnly(Side.CLIENT)
   public List<GuiButton> getButtons() {
      return null;
   }

   public abstract List<Slot> getSlots();

   public int inventoryOffsetX() {
      return 8;
   }

   public int inventoryOffsetY() {
      return 85;
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      try {
         return (TileEntity)this.getClass().newInstance();
      } catch (IllegalAccessException | InstantiationException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      openGui(playerIn, (LogicController)worldIn.getTileEntity(pos));
      return true;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return FULL_BLOCK_AABB;
   }

   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, boolean advanced) {
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
   }

   public void actionPerformed(int buttonID) {
   }

   public boolean dropInv() {
      return false;
   }

   public float getHardness() {
      return 2.0F;
   }

   public boolean hasRotation() {
      return false;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound = super.writeToNBT(compound);
      if (this.hasInv()) {
         compound.merge(this.getInv().serializeNBT());
      }

      return compound;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (this.hasInv()) {
         this.inv.deserializeNBT(compound);
      }

   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return this.hasInv() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
   }

   @Nullable
   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(this.hasInv() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.getInv()) : super.getCapability(capability, facing));
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
      super.onDataPacket(net, packet);
      this.readFromNBT(packet.getNbtCompound());
   }

   public void sync() {
      VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
   }

   public void initBlock(LogicBlock block) {
   }

   public static void openGui(EntityPlayer player, LogicController machine) {
      if (!player.isSneaking()) {
         player.openGui(RebornCore.INSTANCE, 0, machine.world, machine.pos.getX(), machine.pos.getY(), machine.pos.getZ());
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerItemModel(Block block) {
      RebornRegistry.registerItemModel(block, 0);
   }
}
