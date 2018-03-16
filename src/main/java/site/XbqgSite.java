package site;

import java.util.Collections;
import java.util.Set;

/**
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public class XbqgSite extends BqgSite {

    @Override
    protected String getSearchUrl() {
        return "http://zhannei.baidu.com/cse/search?s=5199337987683747968&ie=utf-8&q=%s";
    }

    @Override
    public Set<String> AD() {
        return  Collections.singleton("最新章节请百度搜索【\\*（7）78xs（8）78xs（小）78xs（说）\\*】78xs【（W）78xs（W）78xs（W）78xs\\.（7）78xs（8）78xs（X）78xs（S）\\.（C）78xs（O）78xs（M）】");
    }

    @Override
    protected String getRootUrl() {
        return "http://www.xxbiquge.com%s";
    }

}
