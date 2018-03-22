package webCrawler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class GetUrls {
    private static String urlFront = null;
    private static String tag = null;
    private static Url urls = null;

    public GetUrls(String urlFront, String tag){
        this.urlFront = urlFront ;
        this.tag = tag;
        urls = new Url(urlFront, tag); 
    }

    public List<String> getUriList(){
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0 ; i < 100 ; i += 20){
            executor.execute(new AddUrl(i));        
        }
        executor.shutdown();
        while (!executor.isTerminated()){}
        return urls.getList();
    }

    public static class AddUrl implements Runnable{
        int page;
        public AddUrl(int page){
            this.page = page;
        }
        public void run(){
            urls.addList(page);     
        }
    }

    public static class Url {

        private static Lock lock = new ReentrantLock();         
        private static List<String> urlList = new ArrayList<String>();          
        private String urlFront;
        private static String tag;

        public Url(String urlFront, String tag ){
            this.urlFront = urlFront;
            this.tag = tag;
        }
        public List<String> getList(){
            return urlList;
        }
        public void addList(int page){
            lock.lock();
            try{
                String url = urlFront +"tag/" + tag + "?start="+ String.valueOf(page) + "&type=S";
//              Thread.sleep(5);
                urlList.add(url);       
            }catch(Exception ex ){
            }
            finally {
                lock.unlock();        
            }

        }
    }
    public static void main(String[] args) {
        String urlFront = "https://book.douban.com/";
        String tag = "%E7%BC%96%E7%A8%8B";
        GetUrls myThreading = new GetUrls(urlFront, tag);
        List <String> urlList = myThreading.getUriList();
        for(String url : urlList){
            System.out.println(url);
        }
        System.out.println(urlList.size());
    }
}

