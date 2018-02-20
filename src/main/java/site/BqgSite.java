package site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.ChapterVO;
import pojo.SearchResultVO;
import utils.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class BqgSite implements IBookSite {


    protected String getSearchUrl() {
        return "http://zhannei.baidu.com/cse/search?q=%s&s=3654077655350271938&entry=1";
    }

    protected String getRootUrl() {
        return "http://www.biqudao.com%s";
    }

    @Override
    public List<SearchResultVO> search(String name) throws IOException {
        return BDZNSearch(getSearchUrl(), name);
    }

    @Override
    public Elements getContentElements(Document document) {
        Element element = document.getElementById("list");
        return element.getElementsByTag("a");

    }

    @Override
    public ChapterVO getChapter(String bookUrl, Element ele) {
        String name = ele.text();
        String ul = ele.attr("href");
        ChapterVO chapterVO = new ChapterVO();
        chapterVO.setChapterName(name);
        chapterVO.setChapterUrl(String.format(getRootUrl(), ul));
        return chapterVO;
    }

    @Override
    public Element getChapterContent(Document document) {
        return document.getElementById("content");
    }

    @Override
    public Set<String> AD() {
        return Collections.singleton("chaptererror\\(\\);");
    }
}
