package site;


/**
 * 小说源 枚举值
 *
 * @author j.chen@91kge.com  create on 2017/8/29
 */
public enum BookSiteEnum {

    BQG(new BqgSite(), "笔趣阁"),
    DING_DIAN(new DingdianSite(), "顶点小说"),
    AI_SHANG(new AiShangSite(), "爱尚小说"),
    XBQG(new XbqgSite(), "新笔趣阁"),;

    public IBookSite getBookSite() {
        return bookSite;
    }

    public String getDesc() {
        return desc;
    }

    IBookSite bookSite;

    String desc;

    BookSiteEnum(IBookSite bookSite, String desc) {
        this.bookSite = bookSite;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
