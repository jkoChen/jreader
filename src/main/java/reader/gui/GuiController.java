package reader.gui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import pojo.BookVO;
import pojo.ChapterVO;
import pojo.ContentsVO;
import site.BookSiteEnum;
import site.IBookSite;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * javafx 实现的小说GUI阅读器
 *
 * @author j.chen@91kge.com  create on 2017/9/25
 */
public class GuiController implements Initializable {

    public Button searchButton;
    public Button catalogButton;
    public Button preButton;
    public Button nextButton;
    public TextArea content;
    public ListView<ChapterVO> catalog;
    public ListView<BookVO> searchResult;
    public ChoiceBox<BookSiteEnum> siteChoice;
    public TextField searchText;
    public Label chapterLabel;
    public Label bookLabel;
    public Button small;
    public Button big;
    public Button readCache;
    public Button saveCache;

    private BookVO bookVO;
    private BookSiteEnum bookSite;

    private void searchBtnClick(MouseEvent event) {
        search();
    }

    private void search() {
        clearBook();
        BookSiteEnum bookSiteEnum = siteChoice.getValue();
        bookSite = bookSiteEnum;
        try {
            List<BookVO> searchResultCache = bookSiteEnum.getBookSite().search(searchText.getText());
            searchResult.setVisible(true);
            searchResult.setItems(FXCollections.observableArrayList(searchResultCache));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path cachePath = Paths.get(".", "cache");

    private void saveBookInfo( final BookVO cacheBookVO) throws IOException {
        File bookDir = new File(cachePath.toFile(), cacheBookVO.getBookName());
        if (!bookDir.exists()) {
            bookDir.mkdirs();
        }

        //保存相关信息
        Writer infoWriter = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(new File(bookDir, "book.info")), "UTF-8"));
        infoWriter.append(cacheBookVO.toInfo());
        infoWriter.flush();
        infoWriter.close();
    }


    private void saveCache() {
        if (bookVO == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请先选择书籍");
            alert.show();
            return;
        }
        final BookVO cacheBookVO = this.bookVO;
        new Thread(() -> {

            try {

                File bookDir = new File(cachePath.toFile(), cacheBookVO.getBookName());
                if (!bookDir.exists()) {
                    bookDir.mkdirs();
                }
                saveBookInfo(cacheBookVO);

                cacheBookVO.cacheAll();
                AtomicInteger integer = new AtomicInteger();
                for (ChapterVO chapter : cacheBookVO.getContents().getChapters()) {
                    System.out.println("缓存---" + chapter.getChapterName());
                    int index = integer.getAndIncrement();
                    Writer writer = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(new File(bookDir, index + ".chapter")), "UTF-8"));

                    while (!chapter.isFull()) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    writer.append(JSONObject.toJSONString(chapter));
                    writer.flush();
                    writer.close();

                    System.out.println("缓存成功");
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "缓存成功");
                        alert.show();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }


        }).start();

    }

    private void readCache() throws IOException {
        boolean hasCache = false;
        File[] caches = null;
        if (cachePath != null) {
            File cacheDir = cachePath.toFile();
            if (cacheDir.exists() && cacheDir.isDirectory()) {
                caches = cacheDir.listFiles();
                if (caches != null && caches.length > 0) {
                    hasCache = true;
                }

            }

        }


        if (!hasCache) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "没有缓存");
            alert.show();
            return;
        } else {
            List<BookVO> searchResultCache = new ArrayList<>();
            for (File cach : caches) {
                File bookDir = cach;
                //解析缓存
                File bookInfo = new File(bookDir, "book.info");
                byte[]bytes = Files.readAllBytes(bookInfo.toPath());
                JSONObject jsonObject = JSON.parseObject((new String(bytes, "utf-8")));
                String name = jsonObject.getString("bookName");
                IBookSite bookSite = BookSiteEnum.of(jsonObject.getString("site")).getBookSite();
                Integer chapterIndex = jsonObject.getInteger("chapter");

                ContentsVO contentsVO = new ContentsVO();
                List<ChapterVO> list = new ArrayList<>();
                List<File> chapter = new ArrayList<File>(Arrays.asList(bookDir.listFiles()));
                chapter.remove(bookInfo);
                chapter.sort(Comparator.comparingInt(s -> Integer.parseInt(s.getName().replace(".chapter", ""))));
                for (File file : chapter) {
                    ChapterVO chapterVO = JSONObject.parseObject(new String(Files.readAllBytes(file.toPath()), "utf-8"), ChapterVO.class);
                    list.add(chapterVO);
                }
                contentsVO.setChapters(list);
                BookVO b = new BookVO(name, bookSite, contentsVO);
                if(chapterIndex!= null){
                    b.setChapterIndex(chapterIndex);
                }
                searchResultCache.add(b);
            }

            clearBook();
            searchResult.setVisible(true);
            searchResult.setItems(FXCollections.observableArrayList(searchResultCache));


        }

    }

    private void chooseBook(MouseEvent event) {
        if (event.getClickCount() == 2) {
            bookVO = searchResult.getSelectionModel().getSelectedItem();
            bookLabel.setText(bookVO.getBookName());
            catalog.setItems(FXCollections.observableArrayList(bookVO.getContents().getChapters()));
            searchResult.setVisible(false);
            if(bookVO.getChapterIndex() > 0){
                showContent();
            }else{
                catalog.setVisible(true);
            }
            catalogButton.setVisible(true);

        }
    }


    private void clickCatalog(MouseEvent event) {
        if (catalog.isVisible()) {
            catalog.setVisible(false);
        } else {
            catalog.setVisible(true);

        }
    }

    private void chooseChapter(MouseEvent event) {
        int index = catalog.getSelectionModel().getSelectedIndex();
        setChapterIndex(index);
        showContent();
    }
    private void setChapterIndex(int index){
        bookVO.setChapterIndex(index);
        //保存
        try {
            saveBookInfo(bookVO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showContent() {
        bookVO.cache(5);
        catalog.getSelectionModel().select(bookVO.getChapterIndex());
        if (!catalog.isVisible()) {
            catalog.scrollTo(bookVO.getChapterIndex() - 4);
        }
        ChapterVO current = bookVO.getCurrentChapter();
        chapterLabel.setText(current.getChapterName());
        content.setText(current.getContent());
        preButton.setVisible(true);
        nextButton.setVisible(true);
//        catalog.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchResult.setVisible(false);
        searchResult.setOnMouseClicked(this::chooseBook);

        catalog.setVisible(false);
        catalog.setOnMouseClicked(this::chooseChapter);
        searchButton.setOnMouseClicked(this::searchBtnClick);

        catalogButton.setOnMouseClicked(this::clickCatalog);
        catalogButton.setVisible(false);
        siteChoice.setItems(FXCollections.observableArrayList(BookSiteEnum.values()));
        siteChoice.setValue(BookSiteEnum.values()[0]);

        searchText.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                search();
            }
            if (e.getCode() == KeyCode.DOWN) {
                SingleSelectionModel singleSelectionModel = siteChoice.getSelectionModel();
                singleSelectionModel.select(singleSelectionModel.getSelectedIndex() + 1);
            }
            if (e.getCode() == KeyCode.UP) {
                SingleSelectionModel singleSelectionModel = siteChoice.getSelectionModel();
                singleSelectionModel.select(singleSelectionModel.getSelectedIndex() - 1);
            }
        });

        content.setEditable(false);
        content.setWrapText(true);
        content.setFont(Font.font(16));
        content.setOnMouseClicked(e -> {
            if (catalog.isVisible()) {
                catalog.setVisible(false);
            }
        });
        content.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case J:
                case LEFT:
                    preButton.getOnMouseClicked().handle(null);
                    break;
                case L:
                case RIGHT:
                    nextButton.getOnMouseClicked().handle(null);
                    break;
                case M:
                    if (catalogButton.isVisible()) {
                        catalogButton.getOnMouseClicked().handle(null);
                    }
                    break;
                case U:
                    content.setScrollTop(content.getScrollTop() - content.getWidth() / 2);
                    break;
                case SPACE:
                    content.setScrollTop(content.getScrollTop() + content.getWidth() / 2);
                    break;
            }


        });
        small.setOnMouseClicked(e -> {
            content.setFont(Font.font(content.getFont().getSize() - 1));

        });
        big.setOnMouseClicked(e -> {
            content.setFont(Font.font(content.getFont().getSize() + 1));

        });
        preButton.setOnMouseClicked(e -> {
            setChapterIndex(bookVO.getChapterIndex() - 1);
            showContent();
        });
        nextButton.setOnMouseClicked(e -> {
            setChapterIndex(bookVO.getChapterIndex() + 1);
            showContent();
        });
        saveCache.setOnMouseClicked(e -> {
            saveCache();
        });
        readCache.setOnMouseClicked(e -> {

            try {
                readCache();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        try {
            readCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearBook() {
        bookVO = null;

        catalog.setVisible(false);
        catalogButton.setVisible(false);
        preButton.setVisible(false);
        nextButton.setVisible(false);
    }


}
