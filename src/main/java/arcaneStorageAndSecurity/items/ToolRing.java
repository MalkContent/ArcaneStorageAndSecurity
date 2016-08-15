package arcaneStorageAndSecurity.items;

import java.util.List;

import arcaneStorageAndSecurity.ArcaneStorageAndSecurity;
import arcaneStorageAndSecurity.GeneralHelper;
import arcaneStorageAndSecurity.WarpHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.Thaumcraft;

public class ToolRing extends Item implements IWarpingGear{// implements ICapabilityProvider,
									// INBTSerializable<NBTTagCompound> {

	static final String invNBTKey = "InvToolRing";
	public static final String unlocalizedNameBase = "toolring";
	
	// @CapabilityInject(IItemHandler.class)
	// static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

	public ToolRing() {
		setHasSubtypes(true);
		setMaxStackSize(1);
		setCreativeTab(Thaumcraft.tabTC);
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || oldStack == null || oldStack.getMetadata() != newStack.getMetadata() || oldStack.getItem() != newStack.getItem();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if (!worldIn.isRemote) {
			playerIn.openGui(ArcaneStorageAndSecurity.instance, 0, worldIn, (int) playerIn.posX, (int) playerIn.posY,
					(int) playerIn.posZ);
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn);
	}

	/**
	 * Handles switching items out and into the ToolRing inventory and the
	 * players hand
	 * 
	 * @param player
	 *            The Player
	 * @param newDialedSlot
	 *            Slot number of the Item you want to pull out.
	 */
	public void dialNewItem(EntityPlayer player, int newDialedSlot) {
		dialNewItem(player, newDialedSlot, null);
	}

	/**
	 * Handles switching items out and into the ToolRing inventory and the
	 * players hand
	 * 
	 * @param player
	 *            The Player
	 * @param newDialedSlot
	 *            Slot number of the Item you want to pull out.
	 * @param original
	 *            Hands ItemStack instead of detecting currentEquippedItem.
	 *            Specifically used to handle breaking Items.
	 */
	public void dialNewItem(EntityPlayer player, int newDialedSlot, ItemStack original) {
		/*
		 * oldDialedSlot is the slotid the current item had inside the inventory
		 * before being swapped. inventory[oldDialedSlot] stores the ToolRing
		 * item. (unless ofc the ToolRing item is outside the inventory, then
		 * oldDialedSlot is -1)
		 */
		ItemStack oldDialed = original != null ? original : player.getCurrentEquippedItem();
		if (oldDialed != null && isToolRing(oldDialed)) {
			ItemStack[] toolRingInv = getContainedItems(oldDialed);
			int oldDialedSlot = -2;
			if (oldDialed.getItem() instanceof ToolRing) {
				oldDialedSlot = -1;
			} else {
				for (int i = 0; i < toolRingInv.length; i++) {
					ItemStack curStack = toolRingInv[i];
					if (curStack != null && curStack.getItem() instanceof ToolRing) {
						oldDialedSlot = i;
						break;
					}
				}
			}
			if (oldDialedSlot <= -2 || (oldDialedSlot == -1 && newDialedSlot == -1))
				return;

			if (oldDialed.hasTagCompound())
				oldDialed.getTagCompound().removeTag(invNBTKey);

			ItemStack newDialed;
			World world = player.getEntityWorld();
			float pitch = 1.0F;

			if (oldDialedSlot == -1) { // ToolRing outside, move Tool out
				newDialed = toolRingInv[newDialedSlot];
				WarpHelper.transferWarpingNBT(newDialed, oldDialed);
				toolRingInv[newDialedSlot] = oldDialed;
			} else {
				if (newDialedSlot <= -1) { // ToolRing inside, move ToolRing out
					newDialed = toolRingInv[oldDialedSlot];
					WarpHelper.transferWarpingNBT(newDialed, oldDialed);
					if (newDialedSlot == -2) {
						WarpHelper.setItemWarpingNBT(newDialed, WarpHelper.getItemWarpingNBT(newDialed)
								- WarpHelper.getItemWarpingItembased(oldDialed));
					}
					pitch = 0.9F;
				} else { // ToolRing inside, move Tool out
					newDialed = toolRingInv[newDialedSlot];
					WarpHelper.transferWarpingNBT(toolRingInv[oldDialedSlot], oldDialed);
					WarpHelper.transferWarpingNBT(newDialed, toolRingInv[oldDialedSlot]);
					toolRingInv[newDialedSlot] = toolRingInv[oldDialedSlot];
				}
				toolRingInv[oldDialedSlot] = newDialedSlot == -2 ? null : oldDialed;
			}
			setContainedItems(newDialed, toolRingInv);

			world.playSoundAtEntity(player, "thaumcraft:cameraticks", 0.3F, pitch);
			player.inventory.setInventorySlotContents(player.inventory.currentItem, newDialed);
		}
	}

	public boolean isToolRing(ItemStack itemStack) {
		return (itemStack.getItem() instanceof ToolRing)
				|| hasToolRingInventory(itemStack);
	}
	
	public boolean hasToolRingInventory(ItemStack itemStack) {
		return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(invNBTKey);
	}

	public ItemStack[] getContainedItems(ItemStack stack) {
		ItemStack[] stackList = new ItemStack[getInternalSlots(stack)];
		if (stack.hasTagCompound()) {
			NBTTagList inv = stack.getTagCompound().getTagList(invNBTKey, 10); // TODO:
																				// find
																				// out
																				// what
																				// 10
																				// stands
																				// for
			for (int i = 0; i < inv.tagCount(); i++) {
				NBTTagCompound tag = inv.getCompoundTagAt(i);
				int slot = tag.getByte("Slot") & 0xFF;
				if ((slot >= 0) && (slot < stackList.length))
					stackList[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		return stackList;
	}

	public void setContainedItems(ItemStack stack, ItemStack[] stackList) {
		NBTTagList inv = new NBTTagList();
		boolean nonEmptyInventory = false;
		if (stackList != null) {
			for (int i = 0; i < stackList.length; i++)
				if (stackList[i] != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					stackList[i].writeToNBT(tag);
					inv.appendTag(tag);
					nonEmptyInventory = true;
				}
		}
		if (nonEmptyInventory) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setTag(invNBTKey, inv);
		}
		else if (stack.hasTagCompound()){
			stack.getTagCompound().removeTag(invNBTKey);
		}
	}

	public int getInternalSlots(ItemStack stack) {
		return getInternalSlots(stack.getMetadata());
	};

	public int getInternalSlots(int metadata) {
		if (metadata == 1)
			return 16;
		return 8;
	};
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return GeneralHelper.getUnlocalizedName(stack, unlocalizedNameBase);
	}

	@Override
	public int getWarp(ItemStack itemStack, EntityPlayer player) {
		return itemStack.getMetadata() == 1 ? 2 : 0;
	}

}
