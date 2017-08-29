package start;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.BookVO;
import pojo.ChapterVO;
import pojo.SearchResultVO;
import reader.BqgReader;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

public class JReader {


    BqgReader reader = new BqgReader();

    BookVO bookVO = null;

    Map<Integer, Queue<String>> cache = new HashMap<>();
    Queue<String> contentQueue = new LinkedList<>();

    public BqgReader getReader() {
        return reader;
    }

    public BookVO getBookVO() {
        return bookVO;
    }

    public void setBookVO(BookVO bookVO) {
        this.bookVO = bookVO;
    }

    public void addContent() throws IOException {
        int page = bookVO.getPage();
        if (!cache.containsKey(page)) {
            ChapterVO chapterVO = bookVO.getCurrentChapter();
            addQueue(chapterVO, contentQueue);
            cache.put(page, new LinkedList<>(contentQueue));
        } else {
            cache.get(page).forEach(contentQueue::offer);
        }


    }

    public void addQueue(ChapterVO chapterVO, Queue<String> queue) throws IOException {
        if (chapterVO == null) {
            return;
        }
        if (!chapterVO.isFull()) {
            reader.setChapterContent(chapterVO);
        }
        queue.offer(bookVO.getContents().getChapters().indexOf(chapterVO) + ":" + chapterVO.getChapterName());
        String[] cos = chapterVO.getContent().split("\n");
        for (String s : cos) {
            queue.offer(s);
        }
    }

    int len = 10;

    int lazy = 5;
    int MAX_LAZY = 0;

    public void lazy() throws IOException {
        while (true) {
            if (MAX_LAZY < bookVO.getPage() + lazy) {
                int page = bookVO.getPage();
                while (MAX_LAZY < bookVO.getPage() + lazy) {
                    page = page + 1;
                    ChapterVO chapterVO = bookVO.getChapterByPage(page);
                    Queue<String> c = new LinkedList<>();
                    addQueue(chapterVO, c);
                    cache.put(page, c);
                    MAX_LAZY = page;

                }
            } else {
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }


    }

    public List<String> pollContent() throws IOException {
        if (contentQueue.size() < len) {
            bookVO.setPage(bookVO.getPage() + 1);
            addContent();
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            list.add(contentQueue.poll());
        }


        return list;
    }


    public void showContent(Scanner scanner) {
        for (int i = 0; i < bookVO.getContents().getChapters().size(); i++) {
            System.out.println(i + ":" + bookVO.getContents().getChapters().get(i).getChapterName());
        }
        System.out.print("想看哪一个章节:");
        boolean flag = false;
        while (!flag) {
            try {
                String value = scanner.nextLine();
                int page = Integer.parseInt(value);
                bookVO.setPage(page);
                flag = true;
            } catch (Exception e) {
                System.out.println("请输入正确的章节序号!");
            }

        }
    }

    public void jump(int page) throws IOException {
        contentQueue.clear();
        bookVO.setPage(page);
        addContent();
    }


    public static void main(String[] args) throws IOException {
        JReader jReader = new JReader();
        String value = null;
        Scanner scanner = new Scanner(System.in);
        boolean isStart = false;
        Thread t = new Thread(() -> {
            try {
                jReader.lazy();
            } catch (IOException e) {
            }
        });
        do {

            if (jReader.getBookVO() == null) {
                System.out.print("搜索:");
                value = scanner.nextLine();
                List<SearchResultVO> list = jReader.getReader().search(value);
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(i + ":" + list.get(i).getBookName());
                }
                System.out.print("想看哪本书:");
                value = scanner.nextLine();
                int index = Integer.parseInt(value);
                BookVO bookVO = new BookVO();
                bookVO.setContents(jReader.getReader().getContents(list.get(index).getBookUrl()));
                jReader.setBookVO(bookVO);
                jReader.showContent(scanner);

            }
            if (!isStart) {
                t.start();
                isStart = true;
            }


            List<String> list = jReader.pollContent();
            list.forEach(System.out::println);

            System.out.println(LocalTime.now().toString());
            value = scanner.nextLine();
            if (value != null) {
                if (value.equalsIgnoreCase("n")) {
                    jReader.jump(jReader.getBookVO().getPage() + 1);
                } else if (value.equalsIgnoreCase("p")) {
                    jReader.jump(jReader.getBookVO().getPage() - 1);

                } else if (value.equalsIgnoreCase("m")) {
                    jReader.contentQueue.clear();
                    jReader.showContent(scanner);
                } else if (value.equalsIgnoreCase("r")) {
                    jReader.setBookVO(null);
                } else if (value.startsWith("M:")) {
                    String[] page = value.split(":", 2);
                    if (page.length == 2) {
                        try {
                            int v = Integer.parseInt(page[1]);
                            jReader.jump(v);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        } while (!"quit".equalsIgnoreCase(value));


    }


}
