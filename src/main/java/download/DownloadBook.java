package download;

import pojo.BookVO;
import pojo.ChapterVO;
import pojo.SearchResultVO;
import site.AiShangSite;
import site.BqgSite;
import site.IBookSite;
import site.XbqgSite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author j.chen@91kge.com  create on 2018/1/9
 */
public class DownloadBook {
    private IBookSite bookSite;

    private BookVO bookVO = null;

    public void download(String value) throws IOException {
        bookSite = new AiShangSite();
        List<SearchResultVO> list = bookSite.search(value);
        for (SearchResultVO r : list) {
            System.out.println(r.getBookName());
            File file = new File("D:/tmp/book1/" + r.getBookName() + ".txt");
            this.bookVO = new BookVO();
            try {
                bookVO.setContents(bookSite.getContents(r.getBookUrl()));
            }catch (Exception e){
                continue;
            }
            FileWriter fw = new FileWriter(file);
            List<ChapterVO> li = bookVO.getContents().getChapters();
            Map<Integer, List<ChapterVO>> m = new HashMap<>();
            for (int i = 0; i < li.size(); i++) {
                int id = i % 100;
                if (!m.containsKey(id)) {
                    m.put(id, new ArrayList<>());
                }
                m.get(id).add(li.get(i));

            }
            m.values().forEach(b -> {
                new Thread(() -> {
                    b.forEach(s -> {
                        while (!s.isFull()) {
                            try {

//                                    System.out.println("正在下载 ------ " + s.getChapterName() + "...");
                                bookSite.setChapterContent(s);
                            } catch (IOException e) {
                                System.err.println("下载错误---" + s.getChapterName());
                            }
                        }

                    });

                }).start();

            });


            bookVO.getContents().getChapters().forEach(s -> {
                try {
                    System.out.println("正在打印 ------ " + s.getChapterName() + "...");
                    while (!s.isFull()) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    fw.append(s.getChapterName()).append("\n\n");
                    fw.append(s.getContent()).append("\n\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }


            });
            fw.flush();
            fw.close();

//            return;
        }


    }

    public static void main(String[] args) throws IOException {
        DownloadBook d = new DownloadBook();
        d.download("盘龙");
        d.download("变性");

    }


}
