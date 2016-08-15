package arcaneStorageAndSecurity.proxy;

import arcaneStorageAndSecurity.ArcaneStorageAndSecurity;
import arcaneStorageAndSecurity.config.Config;
import arcaneStorageAndSecurity.config.ConfigItems;
import arcaneStorageAndSecurity.config.ConfigTCRegister;
import arcaneStorageAndSecurity.eventHandlers.PlayerEventHandler;
import arcaneStorageAndSecurity.guiAndContainer.ASASGuiHandler;
import arcaneStorageAndSecurity.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
	
	public void init(FMLInitializationEvent event) {
		registerHandlers();
	}
	
	public void registerHandlers() {
		NetworkRegistry.INSTANCE.registerGuiHandler(ArcaneStorageAndSecurity.instance, new ASASGuiHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
	}

	public void preInit(FMLPreInitializationEvent event) {
		Config.initConfig(event.getSuggestedConfigurationFile());
		PacketHandler.init();
		ConfigItems.initializeItems();
	}

	public void postInit(FMLPostInitializationEvent event) {
		ConfigTCRegister cr = new ConfigTCRegister();
		cr.initTC();
	}
}