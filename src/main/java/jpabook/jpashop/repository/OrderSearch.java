package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

//동적 쿼리를 사용해보기
@Getter @Setter
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;//주문 상태

}
