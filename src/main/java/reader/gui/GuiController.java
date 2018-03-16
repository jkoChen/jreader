package reader.gui;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import pojo.BookVO;
import pojo.ChapterVO;
import site.BookSiteEnum;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


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

    private void chooseBook(MouseEvent event) {
        if (event.getClickCount() == 2) {
            bookVO = searchResult.getSelectionModel().getSelectedItem();
            bookLabel.setText(bookVO.getBookName());
            catalog.setItems(FXCollections.observableArrayList(bookVO.getContents().getChapters()));
            catalog.setVisible(true);
            searchResult.setVisible(false);
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
        bookVO.setChapterIndex(index);
        showContent();
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
                case LEFT:
                    preButton.getOnMouseClicked().handle(null);
                    break;
                case RIGHT:
                    nextButton.getOnMouseClicked().handle(null);
                    break;
                case M:
                    if (catalogButton.isVisible()) {
                        catalogButton.getOnMouseClicked().handle(null);
                    }
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
            bookVO.setChapterIndex(bookVO.getChapterIndex() - 1);
            showContent();
        });
        nextButton.setOnMouseClicked(e -> {
            bookVO.setChapterIndex(bookVO.getChapterIndex() + 1);
            showContent();
        });
    }

    private void clearBook() {
        bookVO = null;

        catalog.setVisible(false);
        catalogButton.setVisible(false);
        preButton.setVisible(false);
        nextButton.setVisible(false);
    }


}
