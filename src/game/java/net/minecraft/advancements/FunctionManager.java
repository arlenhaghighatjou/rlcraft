package net.minecraft.advancements;

import com.arlenh7.eaglercraft.forge.WorldTextFile;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class FunctionManager implements ITickable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final WorldTextFile functionDir;
    private final MinecraftServer server;
    private final Map<ResourceLocation, FunctionObject> functions = Maps.<ResourceLocation, FunctionObject>newHashMap();
    private String currentGameLoopFunctionId = "-";
    private FunctionObject gameLoopFunction;
    private final ArrayDeque<FunctionManager.QueuedCommand> commandQueue = new ArrayDeque<FunctionManager.QueuedCommand>();
    private boolean isExecuting = false;
    private final ICommandSender gameLoopFunctionSender = new ICommandSender()
    {
        public String getName()
        {
            return FunctionManager.this.currentGameLoopFunctionId;
        }
        public boolean canUseCommand(int permLevel, String commandName)
        {
            return permLevel <= 2;
        }
        public World getEntityWorld()
        {
            return FunctionManager.this.server.worlds[0];
        }
        public MinecraftServer getServer()
        {
            return FunctionManager.this.server;
        }
    };

    public FunctionManager(@Nullable File functionDirIn, MinecraftServer serverIn)
    {
        this(functionDirIn == null ? null : new WorldTextFile(functionDirIn), serverIn);
    }

    public FunctionManager(WorldTextFile functionDirIn, MinecraftServer serverIn)
    {
        this.functionDir = functionDirIn;
        this.server = serverIn;
        this.reload();
    }

    @Nullable
    public FunctionObject getFunction(ResourceLocation id)
    {
        return this.functions.get(id);
    }

    public ICommandManager getCommandManager()
    {
        return this.server.getCommandManager();
    }

    public int getMaxCommandChainLength()
    {
        return this.server.worlds[0].getGameRules().getInt("maxCommandChainLength");
    }

    public Map<ResourceLocation, FunctionObject> getFunctions()
    {
        return this.functions;
    }

    public void update()
    {
        String s = this.server.worlds[0].getGameRules().getString("gameLoopFunction");

        if (!s.equals(this.currentGameLoopFunctionId))
        {
            this.currentGameLoopFunctionId = s;
            this.gameLoopFunction = this.getFunction(new ResourceLocation(s));
        }

        if (this.gameLoopFunction != null)
        {
            this.execute(this.gameLoopFunction, this.gameLoopFunctionSender);
        }
    }

    public int execute(FunctionObject function, ICommandSender sender)
    {
        int i = this.getMaxCommandChainLength();

        if (this.isExecuting)
        {
            if (this.commandQueue.size() < i)
            {
                this.commandQueue.addFirst(new FunctionManager.QueuedCommand(this, sender, new FunctionObject.FunctionEntry(function)));
            }

            return 0;
        }
        else
        {
            int l;

            try
            {
                this.isExecuting = true;
                int j = 0;
                FunctionObject.Entry[] afunctionobject$entry = function.getEntries();

                for (int k = afunctionobject$entry.length - 1; k >= 0; --k)
                {
                    this.commandQueue.push(new FunctionManager.QueuedCommand(this, sender, afunctionobject$entry[k]));
                }

                while (true)
                {
                    if (this.commandQueue.isEmpty())
                    {
                        l = j;
                        return l;
                    }

                    (this.commandQueue.removeFirst()).execute(this.commandQueue, i);
                    ++j;

                    if (j >= i)
                    {
                        break;
                    }
                }

                l = j;
            }
            finally
            {
                this.commandQueue.clear();
                this.isExecuting = false;
            }

            return l;
        }
    }

    public void reload()
    {
        this.functions.clear();
        this.gameLoopFunction = null;
        this.currentGameLoopFunctionId = "-";
        this.loadFunctions();
    }

	private void loadFunctions() {
		if (this.functionDir == null) {
			return;
		}
		try {
			for (String path : this.functionDir.list(".mcfunction")) {
				int namespace = path.indexOf('/');
				if (namespace <= 0) {
					continue;
				}
				try {
					ResourceLocation id = new ResourceLocation(path.substring(0, namespace),
							path.substring(namespace + 1, path.length() - ".mcfunction".length()));
					List<String> commands = java.util.Arrays.asList(this.functionDir.resolve(path).read().split("\r\n|\n|\r"));
					this.functions.put(id, FunctionObject.create(this, commands));
				} catch (IllegalArgumentException | IOException ex) {
					LOGGER.error("Couldn't read custom function " + path, ex);
				}
			}
		} catch (IOException ex) {
			LOGGER.error("Couldn't list custom functions", ex);
		}
		if (!this.functions.isEmpty()) {
			LOGGER.info("Loaded " + this.functions.size() + " custom command functions");
		}
	}

    public static class QueuedCommand
        {
            private final FunctionManager functionManager;
            private final ICommandSender sender;
            private final FunctionObject.Entry entry;

            public QueuedCommand(FunctionManager functionManagerIn, ICommandSender senderIn, FunctionObject.Entry entryIn)
            {
                this.functionManager = functionManagerIn;
                this.sender = senderIn;
                this.entry = entryIn;
            }

            public void execute(ArrayDeque<FunctionManager.QueuedCommand> commandQueue, int maxCommandChainLength)
            {
                this.entry.execute(this.functionManager, this.sender, commandQueue, maxCommandChainLength);
            }

            public String toString()
            {
                return this.entry.toString();
            }
        }
}
