package arcaneStorageAndSecurity;

import arcaneStorageAndSecurity.config.Reference;
import net.minecraft.item.ItemStack;

public class GeneralHelper {

	public static String getUnlocalizedName(ItemStack stack, String unlocalizedNameBase) {
		return "item." + Reference.MOD_ID + "." + unlocalizedNameBase + "." + stack.getMetadata();
	}
}
