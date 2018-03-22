# DouBanWebCrawler

GetUrls
-----------------------
通过book.douban.com获取所有网址存入 UrlList 以便爬取
使用多线程池获取所有url

GetContents
-----------------------
将url从UrlList中取出进行爬取数据，解析HTML， 多线程爬取数据，使用RE正则获取图书相关信息并存入 Books 以便写入excel

Book
-----------------------
创建book class 便于储存book相关信息

DataExcel
-----------------------
将Books 中已爬取好的数据写入Excel表单，使用jxl.jar库

RunTime
-----------------------
3049ms
