package com.zms.openzone.search.utils;

/**
 * @author: zms
 * @create: 2022/1/19 10:07
 */
public class PageUtils {
    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;

    public int getCurrent() {
        return current;
    }

    public int getLimit() {
        return limit;
    }

    public int getRows() {
        return rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //数据总数（用于总的页数）
    private int rows;
    //查询路径（复用分页链接）
    private String path;

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }

    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }

    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }

    }

    /*
    获取当前页的起始行
    * */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotal() {
        // rows / limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    //显示起始页
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;//如果当前页是第一页，显示第一页
    }

    //显示截至页
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;//如果末页大于总页数，显示末页
    }
}
