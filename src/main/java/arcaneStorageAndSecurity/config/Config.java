package arcaneStorageAndSecurity.config;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import net.minecraft.command.CommandBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.items.ItemsTC;

public class Config {

	private static final String kCatToolstorage = "Toolstorage Settings";
	private static final String kCatGeneral = "General";
	private static final String kPropToolstorageItems = "AllowedItems";
	private static final String kPropInvasive = "Invasive";
	private static final String toolstorageItemsExample = "examplemod:exampleitem 4";
	public static Configuration config;
	
	public static HashSet<ItemStack> customToolStorageItemStacks;
	public static boolean invasive;

	public static void initConfig(File configfile) {
		config = new Configuration(configfile);
		config.addCustomCategoryComment(kCatGeneral, "General Settings");
		config.addCustomCategoryComment(kCatToolstorage,
				"Configuring of the a items applicable for the eldritch tool storage");
		config.load();

		Property vPropInvasive = config.get(kCatGeneral, kPropInvasive, false, "Decides whether this Addons Research should integrate into existing Thaumonomicon tabs.\nIf you set this to true, do not complain to ANYONE that it looks shit collides with other research.");
		invasive = vPropInvasive.getBoolean();
		
		Property vPropToolstorageItems = config.get(kCatToolstorage, kPropToolstorageItems, new String[] {toolstorageItemsExample}, "Add item names and metadata of items that should be allowed in the eldritch tool storage\nOne entry per line. If no metadata is provided, any metadata will be allowed.\nTake care about what you add, you might end up with an easily destroyed toolstorage.\nExamples: thaumcraft:golem_bell, thaumcraft:resonator 0 (these are already added internally though ;P)");
		String[] itemStacksString = vPropToolstorageItems.getStringList();
		customToolStorageItemStacks = getItemStacks(itemStacksString);
		customToolStorageItemStacks.addAll(Arrays.asList(new ItemStack(ItemsTC.golemBell), new ItemStack(ItemsTC.resonator), new ItemStack(ItemsTC.handMirror), new ItemStack(Items.compass), new ItemStack(Items.clock)));

		config.save();
	}

	private static HashSet<ItemStack> getItemStacks(String[] itemStacksString) {
		HashSet<ItemStack> resultSet = new HashSet<ItemStack>();
		for (int i = 0; i < itemStacksString.length; i++) {
			String curStr = itemStacksString[i];
			if (curStr != null && !curStr.isEmpty() && !curStr.equals(toolstorageItemsExample)) {
				ItemStack itemStack = null;
				try {
					String[] args = curStr.split(" ");
					Item item = CommandBase.getItemByText(null, args[0]);
					if (args.length >= 2)
						itemStack = new ItemStack(item, 1, CommandBase.parseInt(args[2]));
					else
						itemStack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
				} catch (Exception e) {
					System.out.println("Invalid ItemStack from " + Reference.MOD_ID + " config: " + curStr);
				}
				if (itemStack != null)
					resultSet.add(itemStack);
			}
		}
		return resultSet;
	}

}
