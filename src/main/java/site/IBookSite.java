package site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.BookVO;
import pojo.ChapterVO;
import pojo.ContentsVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 小说源接口
 *
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public interface IBookSite {

    /**
     * 根据小说名查询小说页面方法
     * <p>
     * 有一个默认的实现 很多小说网站没有自己的搜索器，都是用的百度站内搜索 可以直接使用{@link this#BDZNSearch(String, String)}
     * 如果有其他的实现，参考该方法
     *
     * @param name 小说名
     * @return
     * @throws IOException
     * @see #BDZNSearch(String, String)
     */
    List<BookVO> search(String name) throws IOException;

    /**
     * 在目录页面，获取 章节 元素列表
     *
     * @param document 目录页面
     * @return
     */
    Elements getContentElements(Document document);

    /**
     * 根据 {@link this#getContentElements(Document)} 方法获取到的章节元素 封装成章节类的方法
     *
     * @param bookUrl bookUrl,有的网站 完整的章节url是根据bookUrl拼接完成
     * @param ele     章节元素{@link this#getContentElements(Document)} 获取到的列表的每个元素
     * @return
     * @see #getContentElements(Document)
     */
    ChapterVO getChapter(String bookUrl, Element ele);

    /**
     * 从章节页面 获取章节内容的方法
     *
     * @param document 章节页面
     * @return
     */
    Element getChapterContent(Document document);

    /**
     * 小说中插入的广告词 ，在相应的类中重写这个方法，可以替换掉广告词
     *
     * @return
     */
    default Set<String> AD() {
        return new HashSet<>();
    }

    /**
     * 将 html 字符串格式化成 常规字符
     * 同时去除其中的广告词
     *
     * @param content
     * @return
     */
    default String formatContent(String content) {
        for (String ad : AD()) {
            content = content.replaceAll(ad, "");
        }
        StringBuilder sb = new StringBuilder();
        for (String s : content.split("\n")) {

            s = s.replaceAll("<(S*?)[^>]*>.*?|<.*? />", "").replaceAll("<br>", "").replaceAll("&nbsp;", "").trim();

            if (!s.isEmpty()) {
                sb.append("\t").append(s).append("\n");
            }

        }
        return sb.toString();
    }

    default void setChapterContent(ChapterVO chapterVO) throws IOException {
        Document document = Jsoup.connect(chapterVO.getChapterUrl()).get();
        Element element = getChapterContent(document);
        String value = element.toString();
        chapterVO.setContent(formatContent(value));
        chapterVO.setFull(true);
    }

    default String getContentUrl(String url) {
        return url;
    }

    default ContentsVO getContents(String url) throws IOException {
        ContentsVO contentsVO = new ContentsVO();
        List<ChapterVO> list = new ArrayList<>();
        contentsVO.setChapters(list);
        Document document = Jsoup.connect(url).get();
        Elements elements = getContentElements(document);
        for (int i = 0; i < elements.size(); i++) {
            list.add(getChapter(url, elements.get(i)));
        }
        return contentsVO;
    }

    /**
     * 百度站内搜索的一个实现
     *
     * @param searchUrl
     * @param name
     * @return
     * @throws IOException
     */
    default List<BookVO> BDZNSearch(String searchUrl, String name) throws IOException {
        Document document = Jsoup.connect(String.format(searchUrl, name)).get();
        Elements els = document.getElementsByAttributeValue("cpos", "title");
        List<BookVO> list = new ArrayList<>();
        for (int i = 0; i < els.size(); i++) {
            Element a = els.get(i);
            String url = a.attr("href");
            String bookName = a.attr("title");
            BookVO bookVO = new BookVO(bookName, this, getContents(getContentUrl(url)));
            list.add(bookVO);
        }
        return list;
    }

}
