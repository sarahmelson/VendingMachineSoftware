package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import com.techelevator.view.Menu;

public class VendingMachineCLI {

	VendingMachine vendThis = new VendingMachine();

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_HIDDEN_SALES_MENU = "You should not see me";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE,
			MAIN_MENU_OPTION_EXIT, MAIN_MENU_OPTION_HIDDEN_SALES_MENU };
	private final String PURCHASE_MENU_FEED_MONEY = "Add money to your account";
	private final String PURCHASE_MENU_SELECT_ITEM = "Purchase an item";
	private final String PURCHASE_MENU_FINISH_TRANSACTION = "Get change";
	private final String[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_FEED_MONEY, PURCHASE_MENU_SELECT_ITEM,
			PURCHASE_MENU_FINISH_TRANSACTION };
	private Menu menu;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() throws IOException {
		vendThis.updateInventory();
		vendThis.createAuditFile();
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
				displayMenuOptions();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
				displayPurchaseMenu();
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				// break out of loop
				System.out.println("Exiting vending machine. Thank you for your business!");
				break;
			} else if (choice.equals(MAIN_MENU_OPTION_HIDDEN_SALES_MENU)) {
				// create the sales log
				//vendThis.createSalesFile();
				System.out.println("Easter egg found. Sales log would have been created if we could have figured it out.");
			} 
			
		}
	}

	public static void main(String[] args) throws IOException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();

	}

	public void displayMenuOptions() {
		for (VendingMachineItem item : vendThis.getInventoryList()) {
			if (vendThis.getInventoryCount().get(item.getSlotName()) > 0) {// i.e. if it isn't sold out
				String formattedMoney = String.format("%.2f", item.getPrice());
				System.out.format(item.getSlotName() + ": %-20s $" + formattedMoney + "\n", item.getName()); //%-20s makes item name take up 20 spaces in the print
			} else {
				System.out.format(item.getSlotName() + ": %-20s" + " SOLD OUT" + "\n", item.getName());
			}
		}
	}

	public void displayPurchaseMenu() throws IOException {
		System.out.println("What would you like to do?");
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);
			if (choice.equals(PURCHASE_MENU_FEED_MONEY)) {
				// Show user current balance and ask for amount to add to balance
				System.out.println("Your current balance is $" + vendThis.getUserBalance()
						+ ". Please enter a whole dollar amount to deposit to your balance.");
				try {
					BigDecimal userAddedAmount = menu.getAmountFromUser();
					vendThis.depositAmount(userAddedAmount);
					if (userAddedAmount.compareTo(BigDecimal.ZERO) <= 0) {
						System.out.println("Please enter a valid amount.");
					} else {
						System.out.println("You added $" + userAddedAmount + " to your balance. Your new balance is $"
								+ vendThis.getUserBalance());
					}
				} catch (NumberFormatException e) {
					System.out.println("You entered an invalid value. Please enter a whole dollar amount to deposit");
				}

			} else if (choice.equals(PURCHASE_MENU_SELECT_ITEM)) {
				// Prompt the user to enter the slotName of the item they wish to add to cart
				if (vendThis.getUserBalance().compareTo(BigDecimal.ZERO) == 0) {
					System.out.println(
							"You have a zero balance. Please add money to your account before you purchase anything");
				} else {
					displayMenuOptions();
					System.out.println("\n\nPlease enter the slot name for the item you would like to purchase.");
					String userChoice = menu.getFoodChoiceFromUser();
					boolean isValidChoice = false;
					for (VendingMachineItem item : vendThis.getInventoryList()) { // iterate through the inventory
						if (item.getSlotName().equalsIgnoreCase(userChoice)) { // if the user choice equals something in
																				// inventory
							if (vendThis.getInventoryCount().get(userChoice.toUpperCase()) > 0) { // checks if inventory
																									// is greater than 0
								isValidChoice = true;
							} else {
								System.out.println("That item is sold out. Please select an item in stock");
							}
						}
					}
					if (isValidChoice) {
						System.out.println(vendThis.makePurchase(userChoice));
						System.out.println("Your balance is now $" + vendThis.getUserBalance());
					}
				}

			} else if (choice.equals(PURCHASE_MENU_FINISH_TRANSACTION)) {
				// Give user their change in the smallest amount of coins possible
				System.out.println(vendThis.getChange());
				break;
			}
		}
	}
}
