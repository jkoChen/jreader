# JAVA 小说网站爬虫

## 使用 `jsoup` 解析各个免费小说网站的小说页面

### 项目背景
- 用手机看小说，得低着头，颈椎受不了
- 用网页看小说，各种弹窗、各种广告
- 于是写了个小工具，可以把小说内容输出到控制台
- 然后就有了javafx写的界面
- 然后就有了Download 下载txt文件放入手机和Kindle阅读

### 项目相关
- `jsoup` 解析网页内容 

### 支持的小说库    
```
site.BookSiteEnum 小说站点常量
site    具体的实现
```
- 笔趣阁
- 新笔趣阁
- 顶点小说
- 爱尚小说

### 打包方法 

打包成功后，直接运行或者使用 `java -jar abc.jar` 运行


- 下载小说
```
mvn clean package -P download 
```

- console在线阅读器
```
mvn clean package
或者
mvn clean package -P console
```

- javafx 实现的gui 在线阅读器
```
mvn clean package -P gui
```
