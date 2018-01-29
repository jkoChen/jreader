package site;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.ChapterVO;
import pojo.SearchResultVO;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public class AiShangSite implements IBookSite {

    private final String searchUrl = "http://zhannei.baidu.com/cse/search?searchtype=articlename&q=%s&s=11957988939957980963&entry=1";

    @Override
    public List<SearchResultVO> search(String name) throws IOException {
        return BDZNSearch(searchUrl, name);
    }

    @Override
    public String getContentUrl(String url) {
        if (url.endsWith("index.html")) {
            return url;
        }
        return url + "index.html";
    }

    @Override
    public Elements getContentElements(Document document) {
        return document.getElementById("at").getElementsByTag("a");
    }

    @Override
    public ChapterVO getChapter(String bookUrl, Element ele) {
        String name = ele.text();
        String ul = ele.attr("href");
        ChapterVO chapterVO = new ChapterVO();
        chapterVO.setChapterName(name);
        chapterVO.setChapterUrl(bookUrl.replace("index.html", ul));
        return chapterVO;
    }

    @Override
    public Element getChapterContent(Document document) {
        return document.getElementById("contents");
    }

    @Override
    public Set<String> AD() {
        return Collections.singleton("一秒记住【爱♂尚★小△说§网 WwW.23XS.Cc】，精彩小说无弹窗免费阅读！");
    }
}
