package com.belatrix.selenium.SeleniumTest;

public class EbayItem {
	/**
	 * Position of the item in the resulting list
	 */
	private int position;
	
	/**
	 * Listed name of the item
	 */
	private String name;
	
	/**
	 * Currency in which the price is listed
	 */
	private String currency;
	
	/**
	 * Price listed
	 */
	private double price;
	
	/**
	 * Shipping price listed
	 */
	private double shipping;
	
	/**
	 * Constructor or the class
	 * @param position
	 * @param name
	 * @param currency
	 * @param price
	 * @param shipping
	 */
	public EbayItem(int position, String name, String currency, double price, double shipping)
	{
		this.position = position;
		this.name = name;
		this.currency = currency;
		this.price = price;
		this.shipping = shipping;
	}
	
	/**
	 * Gives position of the item
	 * @return Position
	 */
	public int getPosition()
	{
		return position;
	}
	
	/**
	 * Gives the listed name of the item
	 * @return Name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gives currency of the price of the item
	 * @return Currency
	 */
	public String getCurrency()
	{
		return currency;
	}
	
	/**
	 * Gives the prices of the item
	 * @return Price
	 */
	public double getPrice()
	{
		return price;
	}
	
	/**
	 * Gives the price of the shipping
	 * @return Shipping
	 */
	public double getShipping()
	{
		return shipping;
	}
}
