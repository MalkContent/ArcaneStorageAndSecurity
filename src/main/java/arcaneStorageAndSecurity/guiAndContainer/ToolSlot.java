package arcaneStorageAndSecurity.guiAndContainer;

import java.util.Iterator;
import java.util.stream.Stream;

import arcaneStorageAndSecurity.config.Config;
import arcaneStorageAndSecurity.config.ConfigItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.oredict.OreDictionary;

public abstract class ToolSlot extends SingleItemSlot {

	public ToolSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	public static class Pure extends ToolSlot {

		public Pure(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

	}

	// public static class ToolClassBased extends Pure {
	//
	// public ToolClassBased(IInventory inventoryIn, int index, int xPosition,
	// int yPosition) {
	// super(inventoryIn, index, xPosition, yPosition);
	// }
	//
	// @Override
	// public boolean isItemValid(ItemStack stack) {
	// Set<String> toolClasses =
	// ((ItemTool)stack.getItem()).getToolClasses(stack);
	// return super.isItemValid(stack) && (toolClasses.contains("pickaxe") ||
	// toolClasses.contains("axe") || toolClasses.contains("shovel"));
	// }
	// }

	public static class ConfigBased extends Pure {

		private static final Class[] allowedItems = new Class[] { ItemHoe.class, ItemShears.class,
				ItemFlintAndSteel.class, ItemFishingRod.class };

		public ConfigBased(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		protected boolean isItemAllowed(ItemStack stack) {
			return super.isItemAllowed(stack) || validByItem(stack.getItem()) || validByItemStack(stack);
		}

		private boolean validByItemStack(ItemStack stack) {
			for (Iterator<ItemStack> iter = Config.customToolStorageItemStacks.iterator(); iter.hasNext();) {
				if (OreDictionary.itemMatches(iter.next(), stack, false))
					return true;
			}
			return false;
		}

		/**
		 * Used to match the Item Class Only use for important things likely to
		 * get extended
		 * 
		 * @param item
		 * @return
		 */
		private boolean validByItem(Item item) {
			return Stream.of(allowedItems).anyMatch(cl -> cl.isInstance(item));
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return isItemAllowed(stack) && !ConfigItems.toolRing.isToolRing(stack);
	}
	
	protected boolean isItemAllowed(ItemStack stack) {
		return stack.getItem() instanceof ItemTool;
	}

}
