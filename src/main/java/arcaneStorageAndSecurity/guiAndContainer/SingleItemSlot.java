package arcaneStorageAndSecurity.guiAndContainer;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SingleItemSlot extends Slot {

	public SingleItemSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
