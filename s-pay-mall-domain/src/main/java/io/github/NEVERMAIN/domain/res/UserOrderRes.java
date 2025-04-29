package io.github.NEVERMAIN.domain.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderRes {

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

}
