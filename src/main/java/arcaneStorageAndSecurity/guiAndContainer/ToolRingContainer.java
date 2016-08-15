package arcaneStorageAndSecurity.guiAndContainer;

import arcaneStorageAndSecurity.WarpHelper;
import arcaneStorageAndSecurity.config.ConfigItems;
import arcaneStorageAndSecurity.items.ToolRing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public class ToolRingContainer extends Container {

	private World worldObj;
	private ItemStack toolRing;
	private InventoryBasic tempInv;
	private int blockedSlot;
	private int internalSlots;
	private static final int playerSlots = 36;

	public ToolRingContainer(InventoryPlayer inventoryPlayer, ItemStack toolRingStack, World world) {
		// the Slot constructor takes the IInventory and the slot number in that
		// it binds to
		// and the x-y coordinates it resides on-screen
		if (toolRingStack.getItem() instanceof ToolRing) {

			worldObj = world;
			toolRing = toolRingStack;
			ItemStack[] itemStacks = ((ToolRing) (toolRing.getItem())).getContainedItems(toolRing);
			internalSlots = ConfigItems.toolRing.getInternalSlots(toolRingStack);
			blockedSlot = (inventoryPlayer.currentItem + 27 + internalSlots);
			tempInv = new InventoryBasic("", false, internalSlots);
			for (int i = 0; i < itemStacks.length; i++) {
				tempInv.setInventorySlotContents(i, itemStacks[i]);
			}
			((ToolRing) (toolRingStack.getItem())).getContainedItems(toolRingStack);
			// for (int i = 0; i < 3; i++) { //TODO: edit so it accepts 3 slots
			// only
			// for (int j = 0; j < 3; j++) {
			// addSlotToContainer(new Slot(tempInv, j + i * 3, 62 + j * 18, 17 +
			// i * 18));
			// }
			// }
			if (toolRingStack.getMetadata() == 1) {
				for(int y = 0; y<4; y++) {
					for(int x = 0; x<4; x++) {
						addSlotToContainer(new ToolSlot.ConfigBased(tempInv, x+ y*4, x*18 + 53, y*18 - 1));
					}
				}
			}
			else {
				for(int x = 0; x<2; x++) {
					for(int y = 0; y<4; y++) {
						addSlotToContainer(new ToolSlot.Pure(tempInv, y+ x*4, x*88 + 36, y*18 - 1));
					}
				}
//				addSlotToContainer(new ToolSlot.Pure(tempInv, 0, 62, 17));
//				addSlotToContainer(new ToolSlot.Pure(tempInv, 1, 80, 17));
//				addSlotToContainer(new ToolSlot.Pure(tempInv, 2, 98, 17));
			}
			bindPlayerInventory(inventoryPlayer);
		}
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 86 + i * 18)); //84
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 144));
		}
	}
	
	public int getBlockedSlot() {
		return blockedSlot;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
		if (slotId == blockedSlot)
			return null;
		return super.slotClick(slotId, clickedButton, mode, playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
		ItemStack previous = null;
		Slot slot = (Slot) this.inventorySlots.get(fromSlot);

		if (slot != null && slot.getHasStack()) {
			ItemStack current = slot.getStack();
			previous = current.copy();
			if (fromSlot < internalSlots) {
				// From ToolRing Inventory to Player Inventory
				if (!this.mergeItemStack(current, internalSlots, internalSlots + playerSlots, true))
					return null;
			} else {
				// From Player Inventory to ToolRing Inventory
				if (!this.mergeItemStack(current, 0, internalSlots, false))
					return null;
			}

			if (current.stackSize == 0)
				slot.putStack((ItemStack) null);
			else
				slot.onSlotChanged();

			if (current.stackSize == previous.stackSize)
				return null;
			slot.onPickupFromSlot(playerIn, current);
		}
		return previous;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (!this.worldObj.isRemote) {
			ItemStack[] itemStacks = new ItemStack[tempInv.getSizeInventory()];
			int warping = 0;
			boolean emptyInventory = true;
			for (int i = 0; i < itemStacks.length; i++) {
				itemStacks[i] = tempInv.getStackInSlot(i);
				if (itemStacks[i] != null) {
					warping += WarpHelper.getItemWarpingTotal(itemStacks[i]);
					emptyInventory = false;
				}
			}
			ConfigItems.toolRing.setContainedItems(toolRing, itemStacks);
			WarpHelper.setItemWarpingNBT(toolRing, warping);
			ItemStack hand = player.getCurrentEquippedItem();
			if (hand != null && !hand.equals(toolRing))
				player.setCurrentItemOrArmor(0, toolRing);
			player.inventory.markDirty();
		}
	}
}
