package translateTweets;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFSheet; 
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.ss.usermodel.Sheet; 
import org.apache.poi.ss.usermodel.Workbook;

public class translator {
	static WebDriver driver;;
	public static void connectWeb() throws InterruptedException
	{
		String driverPath = "driver/chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", driverPath);
		driver = new ChromeDriver();
		driver.get("https://translate.google.com/");
		Thread.sleep(6000);
	}
	public static class retObj
	{
		String tweet;
		String language;
	};
	public static retObj translate(String tweet) throws InterruptedException
	{
		retObj ret = new retObj();
//		String translatedString = "NA";
		WebElement enter = driver.findElement(By.xpath("//*[@id=\"source\"]"));
		enter.clear();
		enter.sendKeys(tweet);
		Thread.sleep(5000);
		WebElement lang = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]"));
		WebElement trans = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[3]/div[1]/div[2]/div/span[1]"));
		
//		translatedString = trans.getText();
//		String language = lang.getText();
		ret.tweet = trans.getText();
		ret.language = lang.getText();
		return ret;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, InterruptedException
	{
		
		String filePath = "Files/tweets.xls";
		String translatedFile = "Files/translated.xls";
		
		InputStream fIn = new FileInputStream(filePath);
		HSSFWorkbook myExcelBook = new HSSFWorkbook(fIn); 
		HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0); 
		
		Workbook tweetsTranslated = new HSSFWorkbook(); 
		Sheet sheet = tweetsTranslated.createSheet("Tweets");
		
		Iterator<Row> rowIterator = myExcelSheet.iterator();
		ArrayList<String> listOfTweets = new ArrayList<String>();
		
		while(rowIterator.hasNext())
		{
			Row currRow = rowIterator.next();
			String tweet = currRow.getCell(0).getStringCellValue();
			listOfTweets.add(tweet);
		}
		ArrayList<String> translatedText = new ArrayList<String>();
		Iterator<String> listIter = listOfTweets.iterator();
		int i = 0;
		connectWeb();
		while(listIter.hasNext())
		{
			retObj ret = new retObj();
			ret.tweet = "NA";
			ret.language = "NA";
			String text = listIter.next();
			String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
		    String result = text.replaceAll(regex, "");
		    result = result.replaceAll("&amp;", "");
//		    System.out.println(result);
			try 
			{
				ret = translate(result.toString());
			}
			catch(Exception E) {}
			
			translatedText.add(ret.tweet);
			
			Row tranRow = sheet.createRow(i);
			Cell tranCell = tranRow.createCell(0);
			tranCell.setCellValue(ret.tweet);
			Cell langCell = tranRow.createCell(1);
			langCell.setCellValue(ret.language);
			i++;
		}
		tweetsTranslated.write(new FileOutputStream(translatedFile));
		tweetsTranslated.close();
	}
}
