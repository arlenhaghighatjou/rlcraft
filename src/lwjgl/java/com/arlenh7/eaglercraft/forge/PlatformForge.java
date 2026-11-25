package com.arlenh7.eaglercraft.forge;

import net.lax1dude.eaglercraft.v1_8.sp.server.internal.lwjgl.DesktopIntegratedServer;

public class PlatformForge {

	public static boolean isServerThread(boolean serverContext) {
		return Thread.currentThread() == DesktopIntegratedServer.serverThread;
	}

}
