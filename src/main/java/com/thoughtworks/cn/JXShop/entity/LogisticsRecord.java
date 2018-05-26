package com.thoughtworks.cn.JXShop.entity;

import cn.tws.utils.LogisticsStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "logisticsRecord")
public class LogisticsRecord {
    @Id
    @GeneratedValue
    private Long id;
    private String deliveryMan;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp outboundTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Timestamp signedTime;

    @Enumerated(EnumType.STRING)
    private LogisticsStatus logisticsStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Enum getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(LogisticsStatus logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(String deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Timestamp getOutboundTime() {
        return outboundTime;
    }

    public void setOutboundTime(Timestamp outboundTime) {
        this.outboundTime = outboundTime;
    }

    public Timestamp getSignedTime() {
        return signedTime;
    }

    public void setSignedTime(Timestamp signedTime) {
        this.signedTime = signedTime;
    }
}
