package top.woodwhale.blog.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 自己写的分页类型
 * @param <T>
 */
public class PageList<T> implements Serializable {
    /**
     * 当前页码
     */
    private int currentPage;

    /**
     * 总数量
     */
    private long totalCount;

    /**
     * 每一页的数量
     */
    private int pageSize;

    /**
     * 总页数 = 总的数量 / 每页数量
     */
    private long totalPage;

    /**
     * 是否是第一页
     */
    private boolean isFirst;

    /**
     * 是否是最后一页
     */
    private boolean isLast;

    /**
     * 数据
     */
    private List<T> contents;


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }
}
