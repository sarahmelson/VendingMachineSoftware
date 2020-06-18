package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {

	private final int STARTING_INVENTORY_AMOUNT = 5;
	private final int STARTING_SALES_COUNT = 0;
	
	public VendingMachine() {
		
	}
	
	//Create map for key value pair to hold our slot name and the inventory count for that item.
	 private Map< String,Integer> inventoryCount =  new HashMap< String,Integer>(); 
	//Create list to hold all item specific data (slot name, item name, price, and type)
	 private List<VendingMachineItem> inventoryList = new ArrayList<VendingMachineItem>();
	 //Create map to link number of sales to each item name
	 private Map< String,Integer> salesMap =  new HashMap< String,Integer>(); //not having a getter/setter because we don't want users to know this exists in the background
	 
	 
	 private BigDecimal userBalance = BigDecimal.ZERO;
	 
		public Map<String, Integer> getInventoryCount() {
			return inventoryCount;
		}
		
		public List<VendingMachineItem> getInventoryList() {
			//Create a new list and save inventory list to that list and return that list
			return inventoryList;
		}
		
		public BigDecimal getUserBalance() {
			return userBalance;
		}
		

		public void depositAmount(BigDecimal addedAmount) throws IOException {
			userBalance = userBalance.add(addedAmount);
			//write txt doc entry with amount deposited and current balance
			FileOutputStream fos = appendFile();
			String feedMoneyEntry = String.format(getCurrentTimeAndDate() + "%-22s|$%-5s$" + userBalance + "\n", " FEED MONEY:", addedAmount);
			fos.write(feedMoneyEntry.getBytes());
			fos.close();
			
		}
		
		//create txt doc to be written to for audit file
		public void createAuditFile(){
			File myFile = new File("AuditDoc.txt");
		}
		
		//create sales log (would have been used if we figured it out)
		public void createSalesFile() throws FileNotFoundException, IOException{
			File myFile = new File("SalesReport.txt");
		}
		
		//used to update audit file
		public FileOutputStream appendFile() throws FileNotFoundException {
			FileOutputStream fos = new FileOutputStream("AuditFile.txt", true);
			return fos;
		}
		
		//would have been used if we figured out sales report
		public FileOutputStream updateSalesFileFOS() throws IOException {
			FileOutputStream fosSales = new FileOutputStream("SalesReport.txt", false);
			return fosSales;
		}
		
		public String getCurrentTimeAndDate() {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			Date date = new Date();
			return formatter.format(date);
		}
		
		public void updateInventory() throws IOException {
			String path = "VendingMachine.txt";
			File inputFile = new File(path);
			
			
			
			//create an object for each item in the text file
			try (Scanner fileScanner = new Scanner(inputFile)) {
				while (fileScanner.hasNextLine()) {
					String line = fileScanner.nextLine();
					String[] items = line.split("\\|");
					if (items[3].equals("Chip")) {
						VendingMachineItem newItem  = new Chips(items[1], new BigDecimal(items[2]), items[3], items[0]);
						inventoryList.add(newItem);	
						//inside while, add slot number and inventory count to key-value pair to keep track of inventory
						inventoryCount.put(items[0], STARTING_INVENTORY_AMOUNT);
						//Also add each item and sales count to sales map
						salesMap.put(items[1], STARTING_SALES_COUNT);
					} else if (items[3].equals("Candy")) {
						VendingMachineItem newItem  = new Candy(items[1], new BigDecimal(items[2]), items[3], items[0]);
						inventoryList.add(newItem);	
						//inside while, add slot number and inventory count to key-value pair to keep track of inventory
						inventoryCount.put(items[0], STARTING_INVENTORY_AMOUNT);
						//Also add each item and sales count to sales map
						salesMap.put(items[1], STARTING_SALES_COUNT);
					} else if (items[3].equals("Gum")) {
						VendingMachineItem newItem  = new Gum(items[1], new BigDecimal(items[2]), items[3], items[0]);
						inventoryList.add(newItem);	
						//inside while, add slot number and inventory count to key-value pair to keep track of inventory
						inventoryCount.put(items[0], STARTING_INVENTORY_AMOUNT);
						//Also add each item and sales count to sales map
						salesMap.put(items[1], STARTING_SALES_COUNT);
					} else if (items[3].equals("Drink")) {
						VendingMachineItem newItem  = new Drinks(items[1], new BigDecimal(items[2]), items[3], items[0]);
						inventoryList.add(newItem);	
						//inside while, add slot number and inventory count to key-value pair to keep track of inventory
						inventoryCount.put(items[0], STARTING_INVENTORY_AMOUNT);
						//Also add each item and sales count to sales map
						salesMap.put(items[1], STARTING_SALES_COUNT);
					}
				}
			}
			catch(IOException e) {
				System.out.println("Something broke.");
			}
		}
		
		public String makePurchase(String slotNumber) throws IOException {
			
			//pulling the inventory count from map, updating it, and using the same key name to update the value. Used .toUpperCase 
			//to ensure that the correct key was being replaced, rather than creating a new key/value pair.
			int currentInventory = inventoryCount.get(slotNumber.toUpperCase());
			currentInventory -= 1;
			inventoryCount.put(slotNumber.toUpperCase(), currentInventory);
			
			//this is where we are updating the user's balance
			BigDecimal subtractThisFromBalance = BigDecimal.ZERO;
			for (VendingMachineItem item : inventoryList) {
				if (item.getSlotName().equalsIgnoreCase(slotNumber)) {
					subtractThisFromBalance = item.getPrice();
					if (userBalance.compareTo(subtractThisFromBalance) >= 0) {
						userBalance = userBalance.subtract(subtractThisFromBalance);
						
						//Add this purchase to the audit file
						FileOutputStream fos = appendFile();
						String purchaseEntry = String.format(getCurrentTimeAndDate() + " %-18s %-2s|$" + item.getPrice() + " $" + userBalance + "\n", item.getName(), item.getSlotName());
						fos.write(purchaseEntry.getBytes()); //have to use use .getBytes because FOS's can only enter bytes
						fos.close();

					}
					else {
						return "You do not have enough money for this item. Please select another item";
					}
				}
			}
			
			//write to txt doc the date, time, item purchased, item name, item slot name, item price, and balance after purchase
			
			
			
			//This returns our item specific message after purchase.
			for (VendingMachineItem item : inventoryList) {
				if (item.getSlotName().equalsIgnoreCase(slotNumber)) {
					return item.getSound();
				}
			}
			return "Item not found.";
		}
		
		public String getChange() throws IOException {
			
			BigDecimal dollar = new BigDecimal("1.00");
			BigDecimal quarter = new BigDecimal("0.25");
			BigDecimal dime = new BigDecimal("0.10");
			BigDecimal nickel = new BigDecimal("0.05");
			BigDecimal totalChange = new BigDecimal("0.00");
			BigDecimal zero = BigDecimal.ZERO;
			
			int dollarCount = 0;
			int quarterCount = 0;
			int dimeCount = 0;
			int nickelCount = 0;
			
			while (userBalance.compareTo(zero) > 0) {
				while (userBalance.compareTo(dollar) >= 0) {
					userBalance = userBalance.subtract(dollar);
					totalChange = totalChange.add(dollar);
					dollarCount++;
				}
				while (userBalance.compareTo(quarter) >= 0) {
					userBalance = userBalance.subtract(quarter);
					totalChange = totalChange.add(quarter);
					quarterCount++;
				}
				while (userBalance.compareTo(dime) >= 0) {
					userBalance = userBalance.subtract(dime);
					totalChange = totalChange.add(dime);
					dimeCount++;
				}
				while (userBalance.compareTo(nickel) >= 0) {
					userBalance = userBalance.subtract(nickel);
					totalChange = totalChange.add(nickel);
					nickelCount++;
				}
			}
		
			String changeDisplay = ("Your change is $" + totalChange + " in ");
			if (dollarCount > 0) {
				changeDisplay = changeDisplay + dollarCount + " dollars";
			}
			if (quarterCount > 0) {
				changeDisplay = changeDisplay + ", " + quarterCount + " quarters";
			}
			if (dimeCount > 0) {
				changeDisplay = changeDisplay + ", " + dimeCount + " dimes";
			}
			if (nickelCount > 0) {
				changeDisplay = changeDisplay + ", " + nickelCount + " nickels";
			}
			
			//write to txt doc amount of change and remaining balance which should be $0
			FileOutputStream fos = appendFile();
			String getChangeEntry = String.format(getCurrentTimeAndDate() + "%-22s|$%-5s$" + userBalance + "\n"," GIVE CHANGE:", totalChange);
			//String feedMoneyEntry = String.format(getCurrentTimeAndDate() + "%-23s$%-5s$" + userBalance + "\n", " FEED MONEY:", addedAmount);
			fos.write(getChangeEntry.getBytes());
			fos.close();
			
			return changeDisplay;
		}
}
