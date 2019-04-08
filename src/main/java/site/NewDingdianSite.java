package site;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yaml.snakeyaml.util.UriEncoder;
import pojo.BookVO;
import pojo.ChapterVO;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author j.chen@91kge.com  create on 2019/4/8
 */
public class NewDingdianSite implements IBookSite {
    @Override
    public List<BookVO> search(String name) throws IOException {

        String _url = String.format("https://www.booktxt.net/search?searchkey=%s", URLEncoder.encode(name, "gb2312"));

        Connection connect = Jsoup.connect(_url);
        connect.followRedirects(false);
        Connection.Response execute = connect.execute();

        if (execute.statusCode() == 302) {
            //重定向 说明只有一本书
            String url = execute.header("location");
            BookVO book = new BookVO(name, this, getContents(getContentUrl(url)));
            return Collections.singletonList(book);

        }


        Document document = execute.parse();

        Elements els = document.getElementsByAttributeValue("id", "content");
        Elements grid = els.first().getElementsByClass("grid");
        Elements elementsByTag = grid.first().getElementsByTag("tbody").first().getElementsByTag("tr");
        return elementsByTag.stream().filter(s -> s.hasAttr("id") && s.attr("id").equals("nr")).map(s -> {
            Elements td = s.getElementsByTag("td");
            Element first = td.first();
            String url = first.getElementsByTag("a").attr("href");
            String bookName = first.text();
            BookVO book = null;
            try {
                book = new BookVO(bookName, this, getContents(getContentUrl(url)));
            } catch (IOException ignored) {
            }
            return book;

        }).collect(Collectors.toList());


    }

    @Override
    public Elements getContentElements(Document document) {
        Elements els = document.getElementsByTag("dl");
        return els.first().getElementsByTag("a");
    }

    @Override
    public ChapterVO getChapter(String bookUrl, Element ele) {
        String name = ele.text();
        String ul = ele.attr("href");
        ChapterVO chapterVO = new ChapterVO();
        chapterVO.setChapterName(name);
        chapterVO.setChapterUrl(bookUrl + ul);
        return chapterVO;
    }

    @Override
    public Element getChapterContent(Document document) {
        return document.getElementById("content");
    }

}
