package cateye.chris.utils;

import java.util.List;
public class ESResponse<T> {

    private List<T> data;       // 实体模型对象集合
    private int resultCount;   // 总命中数

    // getters and setters
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
