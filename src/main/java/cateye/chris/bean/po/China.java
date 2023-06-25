package cateye.chris.bean.po;

import lombok.Data;

//中国行政区域的实体类
@Data
public class China {
    private Integer id;     //行政区域主键Id
    private String name;    //行政地区名字
    private Integer pid;    //行政地区父级行政地区

}
