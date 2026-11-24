package net.minecraft.server.management;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class DemoPlayerInteractionManager extends PlayerInteractionManager
{
	private boolean displayedIntro;
	private boolean demoTimeExpired;
	private int demoEndedReminder;
	private int gameModeTicks;

	public DemoPlayerInteractionManager(World world) { super(world); }

	public void updateBlockRemoving()
	{
		super.updateBlockRemoving();
		long time = world.getTotalWorldTime();
		long day = time / 24000L + 1L;
		if (!displayedIntro && ++gameModeTicks > 20)
		{
			displayedIntro = true;
			player.connection.sendPacket(new SPacketChangeGameState(5, 0.0F));
		}
		demoTimeExpired = time > 120500L;
		if (demoTimeExpired) ++demoEndedReminder;
		if (time % 24000L == 500L && day <= 6L) player.sendMessage(new TextComponentTranslation("demo.day." + day));
		else if (day == 1L && time == 100L) player.connection.sendPacket(new SPacketChangeGameState(5, 101.0F));
		else if (day == 1L && time == 175L) player.connection.sendPacket(new SPacketChangeGameState(5, 102.0F));
		else if (day == 1L && time == 250L) player.connection.sendPacket(new SPacketChangeGameState(5, 103.0F));
		else if (day == 5L && time % 24000L == 22000L) player.sendMessage(new TextComponentTranslation("demo.day.warning"));
	}

	private void sendDemoReminder()
	{
		if (demoEndedReminder > 100) { player.sendMessage(new TextComponentTranslation("demo.reminder")); demoEndedReminder = 0; }
	}

	public void onBlockClicked(BlockPos pos, EnumFacing side) { if (demoTimeExpired) sendDemoReminder(); else super.onBlockClicked(pos, side); }
	public void blockRemoving(BlockPos pos) { if (!demoTimeExpired) super.blockRemoving(pos); }
	public boolean tryHarvestBlock(BlockPos pos) { return !demoTimeExpired && super.tryHarvestBlock(pos); }
	public EnumActionResult processRightClick(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		if (demoTimeExpired) { sendDemoReminder(); return EnumActionResult.PASS; }
		return super.processRightClick(player, world, stack, hand);
	}
	public EnumActionResult processRightClickBlock(EntityPlayer player, World world, ItemStack stack, EnumHand hand, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (demoTimeExpired) { sendDemoReminder(); return EnumActionResult.PASS; }
		return super.processRightClickBlock(player, world, stack, hand, pos, facing, hitX, hitY, hitZ);
	}
}
