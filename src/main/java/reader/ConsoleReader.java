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
        for (int i = 0; i < bookVO.getContents().getChapters().size(); i++) {
            System.out.println(i + ":" + bookVO.getContents().getChapters().get(i).getChapterName());
        }
        System.out.print("想看哪一个章节:");
        boolean flag = false;
        while (!flag) {
            try {
                String value = scanner.nextLine();
                int page = Integer.parseInt(value);
                bookVO.setChapter(page);
                flag = true;
            } catch (Exception e) {
                System.out.println("请输入正确的章节序号!");
            }

        }
    }

    private void jump(int page) throws IOException {
        contentQueue.clear();
        bookVO.setChapter(page);
        addContent();
    }

    private void switchBookSite() {
        while (this.bookSite == null) {

            for (int i = 0; i < BookSiteEnum.values().length; i++) {
                System.out.println(i + ":" + BookSiteEnum.values()[i].getDesc());
            }
            System.out.print("选择书库:");
            String value = scanner.nextLine();
            int index = Integer.parseInt(value);
            this.bookSite = BookSiteEnum.values()[index].getBookSite();
        }
    }

    private void searchBook() throws IOException {
        while (this.bookVO == null) {
            try {
                System.out.print("搜索:");
                String value = scanner.nextLine();
                List<SearchResultVO> list = bookSite.search(value);
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(i + ":" + list.get(i).getBookName());
                }
                System.out.print("想看哪本书:");
                value = scanner.nextLine();
                int index = Integer.parseInt(value);
                this.bookVO = new BookVO();
                contentQueue.clear();
                bookVO.setContents(bookSite.getContents(list.get(index).getBookUrl()));
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
            switchBookSite();
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
