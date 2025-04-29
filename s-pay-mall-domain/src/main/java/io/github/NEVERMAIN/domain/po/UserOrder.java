package io.github.NEVERMAIN.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrder {

    private Long id;
    private String orderId;
    private String paymentTransactionId;
    private String userId;
    private Date orderTime;
    private Double totalAmount;
    private Double paidAmount;
    private String status;
    private String payUrl;
    private Date payTime;
    private Date cancelTime;
    private Date finishTime;
    private Date createTime;
    private Date updateTime;

}
