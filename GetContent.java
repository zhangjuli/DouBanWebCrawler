package webCrawler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetContent {
	
    private static Content content = null;
    private static List<String> urlList = null;
    private static List<Book> books = new ArrayList<Book>();

    public GetContent(List<String> urlList){
        this.urlList = urlList;
        content = new Content();
    }
    public List<Book> getBooks(){

        ExecutorService executor = Executors.newCachedThreadPool();
        for (String url : urlList){
            executor.execute(new AddContent(url));
        }
        executor.shutdown();
        while(!executor.isTerminated()){}
        return content.getBooks();

    }

    public static class AddContent implements Runnable{
        String url;
        public AddContent(String url){
            this.url = url;
        }
        public void run(){
        	content.addContent(url);
        }
    }

    public static class Content {

        private static Lock lock = new ReentrantLock();
        
        public void addContent(String url){
        	String content = "";
            BufferedReader in = null;
            try{
                URL realUrl = new URL(url);
                URLConnection connection = realUrl.openConnection();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line;
                while( (line = in.readLine()) != null){
                    content += line + "/n";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            finally{
                try{
                    if (in != null){
                        in.close();
                    }
                }catch(Exception e2){
                    e2.printStackTrace();
                }
            }
            content = content.replaceAll("/n", "");
            content = content.replaceAll(" ", "");
            Pattern p = Pattern.compile("<liclass=\"subject-item\">.+?</li>");
            Matcher match = p.matcher(content);
            String tmp;
            lock.lock();
            while(match.find()){
                tmp = match.group();
                Book cur = convertBook(tmp);
                if(cur.pl >= 1000 && books.size() < 40){
                	books.add(convertBook(tmp));
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
           lock.unlock();

        }
        private Book convertBook(String input){
        	//System.out.println(input);
        	Book current = new Book(null, 0, 0, null, null, null, null);
        	Pattern name = Pattern.compile("title=\"(.+?)\"");
            Matcher match = name.matcher(input);
            if(match.find()){
            	current.name = match.group(1);
            }
            
            Pattern rank = Pattern.compile("<spanclass=\"rating_nums\">(.+?)</span>");
            match = rank.matcher(input);
            if(match.find()){
            	current.rank = Double.parseDouble(match.group(1));
            }
            		
            Pattern pl = Pattern.compile("<spanclass=\"pl\">\\((\\d+)");
            match = pl.matcher(input);
            if(match.find()){
            	current.pl = Integer.parseInt(match.group(1));
            }
            
            String pubInfo = "";
            Pattern pub = Pattern.compile("<divclass=\"pub\">(.+?)</div>");
            match = pub.matcher(input);
            if(match.find()){
            	pubInfo = match.group(1);
            }
            String[] pubInfoList = pubInfo.split("/");
            int length = pubInfoList.length;
            current.author = pubInfoList[0];
            current.publish = pubInfoList[length - 3];
            current.date = pubInfoList[length - 2];
            current.price = pubInfoList[length - 1];
            
            return current;
        }
        public List<Book> getBooks(){
            return books;
        }
    }
    public static void main(String[] args){
        long start  = System.currentTimeMillis();
        String urlFront = "https://book.douban.com/";
        String tag = "%E7%BC%96%E7%A8%8B";
        GetUrls myThreading = new GetUrls(urlFront, tag);
        List <String> urlList = myThreading.getUriList();
        GetContent threadingCrawel = new GetContent(urlList);
        List <Book> books = threadingCrawel.getBooks();
        //System.out.println(books.size());
        Collections.sort(books, new Comparator<Book>(){
        	@Override
        	public int compare(Book a, Book b){
        		if(a.rank == b.rank){
        			return 0;
        		}
        		return a.rank < b.rank ? 1 : -1;
        	}
        });
//        for(Book book : books){
//        	System.out.println(book.name);
//        	System.out.println(book.rank);
//        }
        System.out.println(books.size());
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}

