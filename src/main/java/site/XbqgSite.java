package site;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.ChapterVO;
import pojo.SearchResultVO;

import java.io.IOException;
import java.util.List;

/**
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public class XbqgSite extends BqgSite {

    @Override
    protected String getSearchUrl() {
        return "http://zhannei.baidu.com/cse/search?s=5199337987683747968&ie=utf-8&q=%s";
    }

    @Override
    protected String getRootUrl() {
        return "http://www.xxbiquge.com%s";
    }
}
