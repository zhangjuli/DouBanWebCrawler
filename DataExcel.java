package webCrawler;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jxl.Workbook;  
import jxl.write.Label;  
import jxl.write.WritableSheet;  
import jxl.write.WritableWorkbook;  
import jxl.write.Number;  
public class DataExcel {
	
	// get all results of top 40 books
	public List<Book> getBooks(){
		String urlFront = "https://book.douban.com/";
        String tag = "%E7%BC%96%E7%A8%8B";
        GetUrls myThreading = new GetUrls(urlFront, tag);
        List <String> urlList = myThreading.getUriList();
        GetContent threadingCrawel = new GetContent(urlList);
        List <Book> books = threadingCrawel.getBooks();
        
        Collections.sort(books, new Comparator<Book>(){
        	@Override
        	public int compare(Book a, Book b){
        		if(a.rank == b.rank){
        			return 0;
        		}
        		return a.rank < b.rank ? 1 : -1;
        	}
        });
        return books;
	}
    public static void main(String args[]) {
    	DataExcel excel = new DataExcel();
    	List<Book> doubanBooks = excel.getBooks();
    	
    	// write the data into excel using JXL
        try {  
             
            WritableWorkbook book = Workbook.createWorkbook(new File(  
                    "Top40.xls"));  
            
            WritableSheet sheet = book.createSheet("sheet1", 0);  
            Label A = new Label(0, 0, "序号");  
            sheet.addCell(A);
        	Label B = new Label(1, 0, "书名"); 
        	sheet.addCell(B);
        	Label C = new Label(2, 0, "评分"); 
        	sheet.addCell(C);
        	Label D = new Label(3, 0, "评价人数"); 
        	sheet.addCell(D);
            Label E = new Label(4, 0, "作者"); 
        	sheet.addCell(E);
        	Label F = new Label(5, 0, "出版社"); 
        	sheet.addCell(F);
        	Label G = new Label(6, 0, "出版日期"); 
        	sheet.addCell(G);
        	Label H = new Label(7, 0, "价格"); 
        	sheet.addCell(H);
            int lane = 1;
            for(Book item : doubanBooks){
            	Number order = new Number(0, lane, lane);  
                sheet.addCell(order);
            	Label name = new Label(1, lane, item.name); 
            	sheet.addCell(name);
            	Number rank = new Number(2, lane, item.rank);  
                sheet.addCell(rank);
                Number pl = new Number(3, lane, item.pl);  
                sheet.addCell(pl);
                Label author = new Label(4, lane, item.author); 
            	sheet.addCell(author);
            	Label publish = new Label(5, lane, item.publish); 
            	sheet.addCell(publish);
            	Label date = new Label(6, lane, item.date); 
            	sheet.addCell(date);
            	Label price = new Label(7, lane, item.price); 
            	sheet.addCell(price);
            	lane++;
            }
            book.write();  
            book.close();  
        } catch (Exception e) {  
            System.out.println(e);  
        }  
    }  
}  


