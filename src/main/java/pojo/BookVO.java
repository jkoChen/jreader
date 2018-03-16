package pojo;

import site.IBookSite;

import java.io.IOException;
import java.util.*;

/**
 * 小说实例
 * <p>
 * 由{@link site.IBookSite#search(String)} 查询出来得出，并且只在此处使用
 *
 * @author j.chen@91kge.com
 * create on 2017/8/23
 */
public class BookVO {

    //站点
    private IBookSite bookSite;
    //当前章节
    private int chapterIndex = 0;
    //书名
    private String bookName;
    //书的目录
    private ContentsVO contents;

    /**
     * 构造方法，只在{@link IBookSite#search(String)} 中使用
     *
     * @param bookName
     * @param bookSite
     * @param contents
     */
    public BookVO(String bookName, IBookSite bookSite, ContentsVO contents) {
        this.bookName = bookName;
        this.bookSite = bookSite;
        this.contents = contents;
    }

    public String getBookName() {
        return bookName;
    }

    /**
     * 获取目录
     *
     * @return
     */
    public ContentsVO getContents() {
        return contents;
    }

    /**
     * 获取当前章节，自动给下载章节内容赋值
     *
     * @return
     */
    public ChapterVO getCurrentChapter() {
        return getChapterByIndex(chapterIndex);
    }

    /**
     * 获取下一章节，自动给下载章节内容赋值
     *
     * @return
     */
    public ChapterVO getNextChapter() {
        return getChapterByIndex(chapterIndex + 1);

    }

    /**
     * 获取上一章节，自动给下载章节内容赋值
     *
     * @return
     */
    public ChapterVO getPreChapter() {
        return getChapterByIndex(chapterIndex - 1);
    }

    /**
     * 根据index获取章节，不考虑章节内容是否下载完成
     *
     * @param index
     * @return
     */
    private ChapterVO getChapterByIndexWithoutContent(int index) {
        if (index >= 0 && index < contents.getChapters().size()) {
            return contents.getChapters().get(index);
        }
        return null;
    }

    /**
     * 根据index获取章节，自动给下载章节内容赋值
     *
     * @param index
     * @return
     */
    private ChapterVO getChapterByIndex(int index) {
        ChapterVO chapterVO = getChapterByIndexWithoutContent(index);
        if (chapterVO == null) return null;
        //如果启动了缓存
        if (isCache) {
            //判断下一个 缓存好了没
            ChapterVO next = getChapterByIndexWithoutContent(index + 1);
            if (next != null && !next.isFull()) {
                startCache();
            }
        }
        int count = 0;
        while (count <= 5 && !chapterVO.isFull()) {
            try {
                count++;
                bookSite.setChapterContent(chapterVO);
            } catch (IOException ignored) {
            }
        }

        return chapterVO;
    }

    /**
     * 当前小说的阅读章节
     *
     * @return
     */
    public int getChapterIndex() {
        return chapterIndex;
    }

    /**
     * 设置小说阅读的章节
     *
     * @param chapterIndex
     */
    public void setChapterIndex(int chapterIndex) {
        if (chapterIndex < 0) {
            chapterIndex = 0;
        }
        if (chapterIndex >= contents.getChapters().size()) {
            chapterIndex = contents.getChapters().size() - 1;
        }
        this.chapterIndex = chapterIndex;
    }


    /*    ******** 以下是章节缓存相关代码 缓存用线程实现 ********     */


    /**
     * 记录当前是否有缓存线程 启动一个线程时，插入一个数字，线程执行完成，移除该数字
     */
    private Set<Integer> cacheSet = new HashSet<>();
    /**
     * 是否开启缓存
     */
    private boolean isCache = false;
    /**
     * 缓存章节数 默认为5 可以通过 {@link this#cache(int)} 来设置
     */
    private int lazyNum = 5;

    /**
     * 判断 缓存线程是否启动了 如果启动了 就不要启动了
     *
     * @return
     */
    private boolean isStartCache() {
        return !cacheSet.isEmpty();
    }

    /**
     * 启动缓存
     */
    public void cache() {
        isCache = true;
        startCache();


    }

    public void cacheAll() {
        cache(Integer.MAX_VALUE);
    }

    public void cache(int lazyNum) {
        this.lazyNum = lazyNum;
        isCache = true;
        startCache();

    }

    /**
     * 启动缓存线程
     * <p>
     * 当缓存章节数量 <= 10 时，仅启动一个线程
     * 当缓存章节数量 >10 时，启动 (lazyNum / 5) 个线程,但是最多只启动100个线程来下载章节
     * <p>
     * 对要缓存的章节，进行取模分组，使得缓存顺序更合理
     */
    private void startCache() {
        if (!isCache) {
            System.err.println("没有开启缓存");
            return;
        }
        if (isStartCache()) {
            System.err.println("正在缓存中，请稍等。。。。");
            return;
        }
        //要缓存的章节
        List<ChapterVO> waitCacheList = new ArrayList<>();
        int max = getContents().getChapters().size();
        for (int i = chapterIndex; i < chapterIndex + Math.min(max, lazyNum); i++) {
            ChapterVO chapterVO = getChapterByIndexWithoutContent(i);
            if (chapterVO != null) {
                waitCacheList.add(chapterVO);
            } else {
                break;
            }
        }
        //对缓存章节取模分组
        Map<Integer, List<ChapterVO>> waitCacheGroup = new HashMap<>();
        if (waitCacheList.size() <= 10) {
            waitCacheGroup.put(1, waitCacheList);
        } else {
            int threadSize = Math.min(waitCacheList.size() / 5, 100);
            for (int i = 0; i < waitCacheList.size(); i++) {
                int id = i % threadSize;
                if (!waitCacheGroup.containsKey(id)) {
                    waitCacheGroup.put(id, new ArrayList<>());
                }
                waitCacheGroup.get(id).add(waitCacheList.get(i));

            }
        }

        //启动缓存线程
        waitCacheGroup.forEach((k, v) -> {
            new Thread(() -> {
                cacheSet.add(k);
                v.forEach(s -> {
                    while (!s.isFull()) {
                        try {
                            bookSite.setChapterContent(s);
                        } catch (IOException e) {
                            System.err.println("下载错误---" + s.getChapterName());
                        }
                    }
                });
                cacheSet.remove(k);
            }).start();


        });
    }

    @Override
    public String toString() {
        return this.bookName;
    }
}
