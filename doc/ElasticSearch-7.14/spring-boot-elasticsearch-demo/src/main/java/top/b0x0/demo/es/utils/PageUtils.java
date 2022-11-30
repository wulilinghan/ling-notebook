package top.b0x0.demo.es.utils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class PageUtils<T> implements Serializable {
    private static final long serialVersionUID = -15606694466640900L;

    private long total;

    private int pageNum;

    private int pageSize;

    private int pages;

    private List<T> list;

    public PageUtils(List<T> list, int pageNum, int pageSize) {
        List<T> pageList = list.stream().skip((long) pageSize * (pageNum - 1)).limit(pageSize).collect(Collectors.toList());
        this.setTotal(list.size());
        this.setPages(list.size() % pageSize == 0 ? (list.size() / pageSize) : (list.size() / pageSize + 1));
        this.setPageNum(pageNum);
        this.setPageSize(pageSize);
        this.setList(pageList);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "PageUtils{" +
                "total=" + total +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", pages=" + pages +
                ", list=" + list +
                '}';
    }
}

