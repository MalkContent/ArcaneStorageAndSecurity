package arcaneStorageAndSecurity.proxy;

import arcaneStorageAndSecurity.config.ConfigItems;
import arcaneStorageAndSecurity.eventHandlers.KeyHandler;
import arcaneStorageAndSecurity.eventHandlers.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	public RenderEventHandler renderEventHandler;
	public KeyHandler keyHandler;

	public ClientProxy() {
		keyHandler = new KeyHandler();
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		registerRenders();
	}

	@Override
	public void registerHandlers() {
		renderEventHandler = new RenderEventHandler();

		MinecraftForge.EVENT_BUS.register(renderEventHandler);

		MinecraftForge.EVENT_BUS.register(keyHandler);

		super.registerHandlers();
	}

	public void registerRenders() {
		registerRender(ConfigItems.toolRing, 0);
		registerRender(ConfigItems.toolRing, 1);
	}

	public void registerRender(Item item) {
		registerRender(item, 0, false);
	}

	public void registerRender(Item item, int meta) {
		registerRender(item, meta, true);
	}

	private void registerRender(Item item, int meta, boolean metadep) {
		ModelLoader.setCustomModelResourceLocation(item, meta,
				new ModelResourceLocation(item.getRegistryName() + (metadep ? ("_" + meta) : "")));
	}

}
