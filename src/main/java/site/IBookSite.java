package site;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.ChapterVO;
import pojo.ContentsVO;
import pojo.SearchResultVO;
import utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public interface IBookSite {
    List<SearchResultVO> search(String name) throws IOException;


    Elements getContentElements(Document document);


    ChapterVO getChapter(String bookUrl, Element ele);

    Element getChapterContent(Document document);

    default void setChapterContent(ChapterVO chapterVO) throws IOException {
        Document document = Jsoup.connect(chapterVO.getChapterUrl()).get();
        Element element = getChapterContent(document);
        String value = element.text();
        String[] ss = value.replaceAll("</?[^>]+>", "").split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String s : ss) {
            s = s.replace((char) 12288, ' ');
            s = StringUtils.zipStr(s).trim();
            for (char c : s.toCharArray()) {
                sb.append(c);
                if (sb.length() % 40 == 0) {
                    sb.append("\n");
                }
            }
        }
        chapterVO.setContent(sb.toString());
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

    default List<SearchResultVO> BDZNSearch(String searchUrl, String name) throws IOException {
        Document document = Jsoup.connect(String.format(searchUrl, name)).get();
        Elements els = document.getElementsByAttributeValue("cpos", "title");
        List<SearchResultVO> list = new ArrayList<>();
        for (int i = 0; i < els.size(); i++) {
            Element a = els.get(i);
            SearchResultVO searchResultVO = new SearchResultVO();
            String url = a.attr("href");
            String bookName = a.attr("title");
            searchResultVO.setBookName(bookName);
            searchResultVO.setBookUrl(getContentUrl(url));
            list.add(searchResultVO);
        }
        return list;
    }

}
