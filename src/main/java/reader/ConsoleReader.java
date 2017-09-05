package reader;

import pojo.BookVO;
import pojo.ChapterVO;
import pojo.SearchResultVO;
import site.BookSiteEnum;
import site.IBookSite;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class ConsoleReader {


    private IBookSite bookSite;

    private BookVO bookVO = null;

    private Queue<String> contentQueue = new LinkedList<>();
    private Scanner scanner;

    private final int len = 10;
    private final int lazy = 5;
    private int MAX_LAZY = 0;


    public void addContent() throws IOException {
        ChapterVO chapterVO = bookVO.getCurrentChapter();
        addQueue(chapterVO, contentQueue);


    }

    private void addQueue(ChapterVO chapterVO, Queue<String> queue) throws IOException {
        if (chapterVO == null) {
            return;
        }
        if (!chapterVO.isFull()) {
            bookSite.setChapterContent(chapterVO);
        }
        queue.offer(bookVO.getContents().getChapters().indexOf(chapterVO) + ":" + chapterVO.getChapterName());
        String[] cos = chapterVO.getContent().split("\n");
        for (String s : cos) {
            queue.offer(s);
        }
    }


    private void lazy() throws IOException {
        while (true) {
            try {
                if (MAX_LAZY < bookVO.getChapter() + lazy) {
                    int page = bookVO.getChapter();
                    while (MAX_LAZY < bookVO.getChapter() + lazy) {
                        page = page + 1;
                        ChapterVO chapterVO = bookVO.getChapterByPage(page);
                        if (!chapterVO.isFull()) {
                            bookSite.setChapterContent(chapterVO);
                        }
                        MAX_LAZY = page;

                    }
                } else {
                    try {
                        Thread.sleep(10000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                MAX_LAZY = 0;
            }

        }


    }

    private List<String> pollContent() throws IOException {
        if (contentQueue.size() < len) {
            bookVO.setChapter(bookVO.getChapter() + 1);
            addContent();
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            list.add(contentQueue.poll());
        }


        return list;
    }


    private void showContent(Scanner scanner) {
        int j = 0;
        for (ChapterVO chapterVO : bookVO.getContents().getChapters()) {
            System.out.println(j + ":" + chapterVO.getChapterName());
            j++;
            if (j % 20 == 0) {
                System.out.print("想看哪一个章节(n:显示下面20章):");

                String value = scanner.nextLine();
                if (value.equals("n")) {
                    continue;
                } else {
                    while (true) {
                        try {
                            int page = Integer.parseInt(value);
                            bookVO.setChapter(page);
                            return;
                        } catch (Exception e) {
                            System.out.print("请输入正确的章节序号:");
                            value = scanner.nextLine();
                        }
                    }
                }
            }
        }


    }

    private void jump(int page) throws IOException {
        contentQueue.clear();
        bookVO.setChapter(page);
        addContent();
    }


    private void searchBook() throws IOException {
        while (this.bookVO == null) {
            try {
                System.out.print("搜索:");
                String value = scanner.nextLine();
                Map<BookSiteEnum, List<SearchResultVO>> tmp = new HashMap<>();
                for (int i = 0; i < BookSiteEnum.values().length; i++) {
                    BookSiteEnum bookSiteEnum = BookSiteEnum.values()[i];
                    List<SearchResultVO> list = bookSiteEnum.getBookSite().search(value);
                    tmp.put(bookSiteEnum, list);
                    System.out.println(bookSiteEnum.getDesc());
                    for (int j = 0; j < list.size(); j++) {
                        System.out.println(i + ":" + j + ":" + list.get(j).getBookName());
                    }
                }


                System.out.print("想看哪本书:");
                value = scanner.nextLine();
                String[] vals = value.split(":", 2);
                int index1 = Integer.parseInt(vals[0]);
                int index2 = Integer.parseInt(vals[1]);
                BookSiteEnum bookSiteEnum = BookSiteEnum.values()[index1];
                this.bookSite = bookSiteEnum.getBookSite();
                this.bookVO = new BookVO();
                contentQueue.clear();
                bookVO.setContents(bookSite.getContents(tmp.get(bookSiteEnum).get(index2).getBookUrl()));
                showContent(scanner);
            } catch (Exception e) {
                System.out.println("系统错误!");
            }

        }
    }

    public void start() throws IOException {
        scanner = new Scanner(System.in);

        boolean isStart = false;
        Thread t = new Thread(() -> {
            try {
                lazy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        String value = null;


        do {
            searchBook();

            if (!isStart) {
                t.start();
                isStart = true;
            }

            List<String> list = pollContent();
            list.forEach(System.out::println);

            System.out.println(LocalTime.now().toString());
            value = scanner.nextLine();
            if (value != null) {
                if (value.equalsIgnoreCase("n")) {
                    jump(this.bookVO.getChapter() + 1);
                } else if (value.equalsIgnoreCase("p")) {
                    jump(this.bookVO.getChapter() - 1);

                } else if (value.equalsIgnoreCase("m")) {
                    contentQueue.clear();
                    showContent(scanner);
                } else if (value.equalsIgnoreCase("r")) {
                    this.bookVO = null;
                } else if (value.equalsIgnoreCase("rs")) {
                    this.bookSite = null;
                    this.bookVO = null;
                } else if (value.startsWith("M:")) {
                    String[] page = value.split(":", 2);
                    if (page.length == 2) {
                        try {
                            int v = Integer.parseInt(page[1]);
                            jump(v);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        } while (!"quit".equalsIgnoreCase(value));

    }


    public static void main(String[] args) throws IOException {
        ConsoleReader consoleReader = new ConsoleReader();
        consoleReader.start();

    }


}
