package com.datbear;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GuardiansOfTheRiftPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GuardiansOfTheRiftHelperPlugin.class);
		RuneLite.main(args);
	}
}