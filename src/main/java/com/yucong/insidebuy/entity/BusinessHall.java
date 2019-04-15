package com.yucong.insidebuy.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "vwt_ins_business_hall")
public class BusinessHall implements Serializable {

    private static final long serialVersionUID = 3895694918949066352L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "vwt_ins_hall_gen")
    @SequenceGenerator(name = "vwt_ins_hall_gen", sequenceName = "vwt_ins_hall_seq", allocationSize = 1)
    private Long id;

    /** 营业厅名称 */
    @Column(name = "business_hall")
    private String businessHall;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessHall() {
        return businessHall;
    }

    public void setBusinessHall(String businessHall) {
        this.businessHall = businessHall;
    }

}
