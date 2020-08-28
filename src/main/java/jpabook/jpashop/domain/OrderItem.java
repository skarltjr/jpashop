package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)// 단방향
    @JoinColumn(name="item_id")
    private Item item;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")//얘가 주인이니까 그럼 order에 가서 원투매니해주고 맵ed해줘야함
    private Order order;

    private int orderPrice;//주문가격
    private int count;//주문수량

    //==생성매서드 ==//
    public static OrderItem createOrderItem(Item item, int orderPrice , int count)
    {
        OrderItem orderItem=new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직 ==//
    public void cancel() {
        getItem().addStock(count);  //재고 수량 원복
    }
    //==조회로직==//
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
