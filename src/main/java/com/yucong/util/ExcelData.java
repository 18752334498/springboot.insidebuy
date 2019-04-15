package com.yucong.util;

import java.io.Serializable;
import java.util.List;

public class ExcelData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // 文件名称
    private String fileName;
    // 表头数据
    private String[] head;
    // 数据
    private List<String[]> data;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String[] getHead() {
        return head;
    }

    public void setHead(String[] head) {
        this.head = head;
    }

    public List<String[]> getData() {
        return data;
    }

    public void setData(List<String[]> data) {
        this.data = data;
    }
}
