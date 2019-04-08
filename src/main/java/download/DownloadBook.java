package download;

import pojo.BookVO;
import site.BookSiteEnum;
import site.IBookSite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 下载小说的工具类
 * <p>
 * 选择小说源网站，输入小说名，要保存的文件夹下载小说
 * <p>
 * 启动多个线程下载小说用来缓存章节，用来加速下载的速度
 * 缓存线程最大值为100  {@link BookVO#startCache()} threadSize
 *
 * @author j.chen@91kge.com  create on 2018/1/9
 * @see BookVO#startCache()
 */
public class DownloadBook {

    /**
     * 下载的方法
     *
     * @param index    小说源的序号
     * @param bookName 要下载的小说名，全名
     * @param dir      保存的文件夹
     * @throws IOException
     */
    void download(int index, String bookName, String dir) throws IOException {
        IBookSite bookSite = BookSiteEnum.values()[index].getBookSite();
        List<BookVO> list = bookSite.search(bookName);
        if (list.size() == 0) {
            System.out.println("搜索不到该书籍：" + bookName);
            return;
        }
        for (BookVO r : list) {
            if (!r.getBookName().equals(bookName)) continue;
            System.out.println(r.getBookName());
            File file = new File(dir, r.getBookName() + ".txt");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }


            FileWriter fw = new FileWriter(file);
            r.cacheAll();

            r.getContents().getChapters().forEach(s -> {
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
            System.out.println("下载完成，小说地址" + file.getAbsolutePath());
            return;
        }


    }

    /**
     * 打印帮助信息
     */
    private static void help() {
        System.out.println("使用帮助:");
        System.out.println("java -jar download.jar bookSiteIndex bookName dirPath");
        System.out.println("可用的bookSite");
        for (int i = 0; i < BookSiteEnum.values().length; i++) {
            System.out.println("\t" + i + " : " + BookSiteEnum.values()[i].getDesc());
        }
    }

    /**
     * e.g.
     * args =  new String[] {"3","虚空凝剑行","D:/Downloads/tmp/book2"};
     *
     * @param args 3位
     */
    public static void main(String[] args) {
        args = new String[]{"4", "走进修仙", "D:/Downloads/tmp/book2"};

        if (args != null) {
            if (args.length == 3) {
                try {
                    DownloadBook downloadBook = new DownloadBook();
                    downloadBook.download(Integer.parseInt(args[0]), args[1], args[2]);
                    return;
                } catch (Exception e) {
                }
            }
        }
        help();

    }


}
