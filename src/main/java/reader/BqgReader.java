package reader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.ChapterVO;
import pojo.ContentsVO;
import pojo.SearchResultVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class BqgReader {

    public int index() {
        return 1;
    }

    private final String searchUrl = "http://zhannei.baidu.com/cse/search?q=%s&s=3654077655350271938&entry=1";
    private final String rootUrl = "http://www.biqudao.com%s";

    public List<SearchResultVO> search(String name) throws IOException {
        Document document = Jsoup.connect(String.format(searchUrl, name)).get();
        Elements els = document.getElementsByAttributeValue("cpos", "title");
        List<SearchResultVO> list = new ArrayList<>();
        for (int i = 0; i < els.size(); i++) {
            Element a = els.get(i);
            SearchResultVO searchResultVO = new SearchResultVO();
            String url = a.attr("href");
            String bookName = a.attr("title");
            searchResultVO.setBookName(bookName);
            searchResultVO.setBookUrl(url);
            list.add(searchResultVO);

        }
        return list;


    }

    public ContentsVO getContents(String url) throws IOException {
        ContentsVO contentsVO = new ContentsVO();
        List<ChapterVO> list = new ArrayList<>();
        contentsVO.setChapters(list);
        Document document = Jsoup.connect(url).get();
        Element element = document.getElementById("list");
        Elements elements = element.getElementsByTag("a");
        for (int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            String name = ele.text();
            String ul = ele.attr("href");
            ChapterVO chapterVO = new ChapterVO();
            chapterVO.setChapterName(name);
            chapterVO.setChapterUrl(String.format(rootUrl, ul));
            list.add(chapterVO);
        }
        return contentsVO;
    }

    public void setChapterContent(ChapterVO chapterVO) throws IOException {
        Document document = Jsoup.connect(chapterVO.getChapterUrl()).get();
        Element element = document.getElementById("content");
        String[] ss = element.text().replaceAll("</?[^>]+>", "").split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String s : ss) {
            s = s.replace((char) 12288, ' ');
            s = zipStr(s).trim();
            for(char c : s.toCharArray()){
                sb.append(c);
                if(sb.length() % 40 ==0){
                    sb.append("\n");
                }
            }
        }
        chapterVO.setContent(sb.toString());
        chapterVO.setFull(true);
    }


    public String zipStr(String jsonString) {
        return (jsonString + "").replaceAll("</?[^>]+>", "").replaceAll("\\s+|\\t|\\n|\\r", "");
    }


    public static void main(String[] args) throws IOException {
        BqgReader bqgReader = new BqgReader();
        bqgReader.getContents("http://www.biqudao.com/bqge12414/").getChapters().forEach(s -> System.out.println(s.getChapterName() + ":" + s.getChapterUrl()));
    }

}
