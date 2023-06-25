package cateye.chris.bean.bo;

import lombok.Data;

//影院的查询条件Bo
@Data
public class CinemaSearchBo extends BaseBo {

    //筛选条件
    private Integer brandId;        //品牌编号
    private Integer specialHallId;  //放映厅编号
    private Integer serviceId;      //服务编号（2：可改签不可退票；12：可退票可改签；空字符串或null：不可退票不可改签）
    private String chinaName;       //用户当前所属的行政地区
}
