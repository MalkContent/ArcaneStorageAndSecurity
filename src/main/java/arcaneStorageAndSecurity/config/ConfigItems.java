package arcaneStorageAndSecurity.config;

import arcaneStorageAndSecurity.items.ToolRing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ConfigItems {
	
	public static ToolRing toolRing;
	
	public static void initializeItems() {
		toolRing = (ToolRing)registerItem(new ToolRing(), ToolRing.unlocalizedNameBase);
	}
	
	
	private static Item registerItem(Item item, String name) {
		item.setUnlocalizedName(name);
	    GameRegistry.registerItem(item, name);
		return item;
	}

}