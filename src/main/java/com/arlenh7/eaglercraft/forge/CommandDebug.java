package com.arlenh7.eaglercraft.forge;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public final class CommandDebug extends CommandBase
{
	private long startedAt;
	private int firstTick;

	@Override
	public String getName()
	{
		return "debug";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 3;
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.debug.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length != 1) throw new WrongUsageException(getUsage(sender));
		if ("start".equals(args[0]))
		{
			this.startedAt = MinecraftServer.getCurrentTimeMillis();
			this.firstTick = server.getTickCounter();
			server.enableProfiling();
			notifyCommandListener(sender, this, "commands.debug.start");
			return;
		}

		if (!"stop".equals(args[0])) throw new WrongUsageException(getUsage(sender));
		if (!server.profiler.profilingEnabled) throw new CommandException("commands.debug.notStarted");
		long duration = MinecraftServer.getCurrentTimeMillis() - this.startedAt;
		int ticks = server.getTickCounter() - this.firstTick;

		try
		{
			save(server, duration, ticks);
		}
		catch (IOException ex)
		{
			throw new CommandException("commands.generic.exception");
		}
		finally
		{
			server.profiler.profilingEnabled = false;
		}
		notifyCommandListener(sender, this, "commands.debug.stop", format(duration * 0.001), ticks);
	}

	private static void save(MinecraftServer server, long duration, int ticks) throws IOException
	{
		StringBuilder report = new StringBuilder("Minecraft server profiler\n");
		report.append("Elapsed: ").append(duration).append(" ms\n");
		report.append("Ticks: ").append(ticks).append('\n');
		report.append("TPS: ").append(format(duration > 0 ? ticks * 1000.0 / duration : 0)).append("\n\n");
		writeSection(server.profiler, "root", 0, report);
		File folder = server.getFile("debug");
		Files.createDirectories(folder.toPath());
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS", Locale.ROOT).format(new Date());
		Files.write(new File(folder, "profile-results-" + timestamp + ".txt").toPath(),
				report.toString().getBytes(StandardCharsets.UTF_8));
	}

	private static void writeSection(Profiler profiler, String section, int depth, StringBuilder report)
	{
		List<Profiler.Result> results = profiler.getProfilingData(section);
		if (results == null) return;
		for (int i = 1; i < results.size(); ++i)
		{
			Profiler.Result row = results.get(i);
			for (int j = 0; j < depth; ++j) report.append("  ");
			report.append(row.profilerName).append("  ").append(format(row.usePercentage)).append("% local / ")
					.append(format(row.totalUsePercentage)).append("% overall\n");
			if (!"unspecified".equals(row.profilerName))
			{
				writeSection(profiler, section + '.' + row.profilerName, depth + 1, report);
			}
		}
	}

	private static String format(double value)
	{
		return String.format(Locale.ROOT, "%.2f", value);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos target)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, "start", "stop") : Collections.emptyList();
	}
}
