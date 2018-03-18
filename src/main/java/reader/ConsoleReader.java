package reader;

import pojo.BookVO;
import pojo.ChapterVO;
import site.BookSiteEnum;

import java.time.LocalTime;
import java.util.*;

/**
 * 命令行阅读器
 */
public class ConsoleReader {


    private BookVO bookVO = null;

    private Scanner scanner;

    public ConsoleReader() {
    }

    public ConsoleReader(String[] args) {
        try {
            this.bookVO = BookSiteEnum.values()[Integer.parseInt(args[0])].getBookSite().search(args[1]).get(0);
            if (args.length == 3) {
                jump(Integer.parseInt(args[2]));
            }
        } catch (Exception e) {
        }
    }

    private void showContent() {
        for (int i = 0; i < bookVO.getContents().getChapters().size() - bookVO.getChapterIndex() && i < 20; i++) {
            ChapterVO chapterVO = bookVO.getContents().getChapters().get(bookVO.getChapterIndex() + i);
            System.out.println(bookVO.getChapterIndex() + i + ":" + chapterVO.getChapterName() + (i == 0 ? "(*)" : ""));
        }
        System.out.print("想看哪一个章节(n:显示下面20章):");
        String value = getInput();
        if (value.equals("n")) {
            showContent();
        } else {
            while (true) {
                try {
                    int page = Integer.parseInt(value);
                    jump(page);
                    return;
                } catch (Exception e) {
                    System.out.print("请输入正确的章节序号:");
                    value = getInput();
                }
            }
        }
    }

    private void jump(int page) {
        bookVO.setChapterIndex(page);
    }

    private BookSiteEnum selectBookSite() {
        BookSiteEnum bookSiteEnum = null;

        while (bookSiteEnum == null) {
            try {
                for (int i = 0; i < BookSiteEnum.values().length; i++) {
                    System.out.println((i + 1) + ":" + BookSiteEnum.values()[i].getDesc());
                }
                System.out.print("选择小说源:");
                String siteNum = getInput();
                bookSiteEnum = BookSiteEnum.values()[Integer.parseInt(siteNum) - 1];
            } catch (Exception e) {
                System.err.println("小说源序号有无");
            }
        }
        return bookSiteEnum;
    }

    private BookVO selectBook(List<BookVO> list) {
        BookVO bookVO = null;
        while (bookVO == null) {
            try {
                for (int i = 1; i < list.size() + 1; i++) {
                    System.out.println(i + ":" + list.get(i - 1).getBookName());
                }
                System.out.print("想看哪本书:");
                String value = getInput();
                bookVO = list.get(Integer.parseInt(value) - 1);
            } catch (Exception e) {
                System.err.println("书本序号有误");
            }


        }
        return bookVO;
    }

    private String getInput() {
        String value = scanner.nextLine();
        if ("quit".equalsIgnoreCase(value)) {
            System.exit(0);
        }
        return value;
    }

    private void searchBook() {
        while (this.bookVO == null) {
            try {
                BookSiteEnum bookSiteEnum = selectBookSite();
                System.out.print(bookSiteEnum.getDesc() + "   ");
                System.out.print("搜索:");
                String value = getInput();
                List<BookVO> list = bookSiteEnum.getBookSite().search(value);
                this.bookVO = selectBook(list);
                bookVO.cache();
                showContent();
            } catch (Exception e) {
                System.err.println("系统错误!");
            }

        }
    }

    public void start() {
        scanner = new Scanner(System.in);
        String value = null;
        do {
            searchBook();
            print();
            value = getInput();
            if (value == null || value.trim().isEmpty()) {
                value = "n";
            }
            if (value.equalsIgnoreCase("n")) {
                jump(this.bookVO.getChapterIndex() + 1);
            } else if (value.equalsIgnoreCase("p")) {
                jump(this.bookVO.getChapterIndex() - 1);
            } else if (value.equalsIgnoreCase("m")) {
                showContent();
            } else if (value.equalsIgnoreCase("r")) {
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
            } else {
                jump(this.bookVO.getChapterIndex() + 1);
            }
        } while (!"quit".equalsIgnoreCase(value));

    }

    private void print() {
        ChapterVO chapterVO = bookVO.getCurrentChapter();
        System.out.println(chapterVO.getChapterName());
        System.out.println(chapterVO.getContent());
        System.out.print(LocalTime.now().toString());
        System.out.print(" n - 下一章 p - 前一章 m - 显示目录  M:[数字] - 跳转到相应章节 r - 重新看书 quit - 退出");
    }


    public static void main(String[] args) {
        ConsoleReader consoleReader = new ConsoleReader(args);
        consoleReader.start();

    }


}
