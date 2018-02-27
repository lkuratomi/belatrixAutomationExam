package com.belatrix.selenium.SeleniumTest;

import org.testng.annotations.Test;
import org.testng.ITestContext;
import org.testng.TestRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EbayTest {

	public static int USD_TO_COP = 2850;
	
	/**
	 * Driver of the browser
	 */
	private WebDriver driver;
	
	/**
	 * First five results for verifications
	 */
	private EbayItem[] itemsMap;

	@Test
	public void numberOfResults()
	{
		// Get the number of results and print it
		String results = driver.findElement(By.xpath("//div[@id='cbelm']/div[3]/h1/span")).getText();
		System.out.println("*** NUMBER OF RESULTS ***");
		System.out.println( "The number of results is: " + results );
	}

	@Test
	public void assertAscendantOrder()
	{
		// Find the desired order by criteria and click it
		WebElement orderByContainer = driver.findElement(By.id("DashSortByContainer"));

		Actions actions = new Actions(driver);
		actions.moveToElement(orderByContainer).perform();

		WebElement orderBy = driver.findElement(By.xpath("//ul[@id='SortMenu']/li[3]/a"));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", orderBy);

		//Wait for the page to load
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.urlContains("sop="));

		// Save the results for verifications
		itemsMap = new EbayItem[5];
		for(int i = 1; i < 6; i++)
		{
			int searchDiv = 2;
			if(driver.findElement(By.xpath("//ul[@id='GalleryViewInner']/li[" + i + "]/div/div[3]/div")).getAttribute("class").equals("bid"))
			{
				searchDiv = 1;
			}

			String searchSpan = "/span";
			if(driver.findElements(By.xpath("//ul[@id='GalleryViewInner']/li[" + i + "]/div/div[3]/div[" + searchDiv + "]/span")).size() == 0)
			{
				searchSpan = "/div/span/span";
			} 

			WebElement name = driver.findElement(By.xpath("//ul[@id='GalleryViewInner']/li[" + i + "]/div/div[2]/h3/a"));
			String nam = name.getText();
			WebElement currency = driver.findElement(By.xpath("//ul[@id='GalleryViewInner']/li[" + i + "]/div/div[3]/div[" + searchDiv + "]" + searchSpan + "/b"));
			String cur = currency.getText().substring(0, 3);
			WebElement price = driver.findElement(By.xpath("//ul[@id='GalleryViewInner']/li[" + i + "]/div/div[3]/div[" + searchDiv + "]" + searchSpan));
			String justPrice = price.getText().split("\\$")[1];
			double pri = Double.parseDouble(justPrice.substring(0, justPrice.length()-7) + justPrice.substring(justPrice.length()-6, justPrice.length()));
			double ship = 0.0;
			if(driver.findElements(By.xpath("//ul[@id='GalleryViewInner']/li[" + i + "]/div/div[4]/div[1]/div")).size() != 0)
			{
				WebElement shipping = driver.findElement(By.xpath("//ul[@id='GalleryViewInner']/li[" + i + "]/div/div[4]/div[1]/div"));
				ship = Double.parseDouble(shipping.getText().replace("+", "").replaceAll("USD", "").replaceAll(" envío", ""));
			}
			EbayItem item = new EbayItem(i, nam, cur, pri, ship);
			itemsMap[i-1] = item;
		}

		// Assert the results of the Order By
		double pricePlusShipping = 0.0;
		boolean isAscendant = true;
		for(int i = 0; isAscendant && i < itemsMap.length; i++)
		{
			EbayItem item = itemsMap[i];
			double price = item.getPrice();
			double shipping = item.getShipping();
			if(item.getCurrency().equals("COP"))
			{
				shipping = shipping*USD_TO_COP;
			}
			double itemsPricePlusShipping = price + shipping;
			if(itemsPricePlusShipping < pricePlusShipping)
			{
				isAscendant = false;
			}
			pricePlusShipping = itemsPricePlusShipping;
		}
		assertTrue(isAscendant);
	}
	
	@Test
	public void firstFiveProducts()
	{
		System.out.println("*** FIRST FIVE PRODUCTS WITH PRICES ***");
		for(int i = 0; i < itemsMap.length; i++)
		{
			EbayItem item = itemsMap[i];
			double price = item.getPrice();
			double shipping = item.getShipping();
			if(item.getCurrency().equals("COP"))
			{
				shipping = shipping*USD_TO_COP;
			}
			double itemPricePlusShipping = price + shipping;
			System.out.println("Product #" + (i+1) + ": " + item.getName() + "\n" + item.getCurrency() + " " + itemPricePlusShipping);
		}
	}
	
	@Test
	public void orderByName()
	{
		System.out.println("*** FIRST FIVE PRODUCTS ORDERED BY NAME ***");
		EbayItem[] orderedArray = itemsMap;
		for(int i = 1; i < orderedArray.length; i++)
		{
			EbayItem item = orderedArray[i];
			String name = item.getName();
			int j = i-1;
			
			EbayItem itemToMove = orderedArray[j];
			String nameToMove = itemToMove.getName();
			while(j >= 0 && nameToMove.compareTo(name) > 0)
			{
				orderedArray[j+1] = orderedArray[j];
				j=j-1;
			}
			orderedArray[j+1] = item;
		}
		for(int i = 0; i < orderedArray.length; i++)
		{
			EbayItem item = orderedArray[i];
			System.out.println("Product #" + (i+1) + ": " + item.getName());
		}
	}
	
	@Test
	public void orderByPriceDesc()
	{
		System.out.println("*** FIRST FIVE PRODUCTS ORDERED BY PRICE ***");
		EbayItem[] orderedArray = itemsMap;
		for(int i = 1; i < orderedArray.length; i++)
		{
			EbayItem item = orderedArray[i];
			double price = item.getPrice();
			double shipping = item.getShipping();
			if(item.getCurrency().equals("COP"))
			{
				shipping = shipping*USD_TO_COP;
			}
			double itemFullPrice = price + shipping;
			int j = i-1;
			
			while(j >= 0 && (orderedArray[j].getPrice() + ((orderedArray[j].getCurrency().equals("COP")) ? orderedArray[j].getShipping()*USD_TO_COP : orderedArray[j].getShipping())) < itemFullPrice)
			{
				orderedArray[j+1] = orderedArray[j];
				j=j-1;
			}
			orderedArray[j+1] = item;
		}
		for(int i = 0; i < orderedArray.length; i++)
		{
			EbayItem item = orderedArray[i];
			double price = item.getPrice();
			double shipping = item.getShipping();
			if(item.getCurrency().equals("COP"))
			{
				shipping = shipping*USD_TO_COP;
			}
			double itemPricePlusShipping = price + shipping;
			System.out.println("Product #" + (i+1) + ": " + item.getName() + "\n" + item.getCurrency() + " " + itemPricePlusShipping);
		}
	}

	@BeforeClass
	public void beforeMethod(ITestContext ctx)
	{
		// Set the output directory
		TestRunner runner = (TestRunner) ctx;
		runner.setOutputDirectory("/test-output");

		// New instance of the Firefox driver and it's implicit wait setting
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Go to ebay
		driver.get("http://www.ebay.com");

		// Find the elements text input search text field and button
		WebElement searchTextField = driver.findElement(By.id("gh-ac-box"));
		WebElement searchButton = driver.findElement(By.id("gh-btn"));

		// Enter the search criteria
		searchTextField.sendKeys("Shoes");

		// Click the search button
		searchButton.click();

		// Narrow the brands to a minimum
		WebElement searchBrandTextField = driver.findElement(By.xpath("//div[@id='LeftNavCategoryContainer']/following-sibling::div/div/div/input[@placeholder='Search in all available brands']"));
		searchBrandTextField.sendKeys("Puma");

		// Find the desired brand's link and click it
		WebElement brandLink = driver.findElement(By.linkText("PUMA"));
		brandLink.click();	

		//Find the desired size's link and click it
		WebElement sizeLink = driver.findElement(By.xpath("//div[@id='LeftNavCategoryContainer']/following-sibling::div/div/div/div/a/span[text()='10']"));
		sizeLink.click();
	}
	
	@AfterClass
	public void afterClass()
	{
		driver.quit();		
	}
}
