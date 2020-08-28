package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery
{
    @Id @GeneratedValue
    @Column(name="delivery_id")
    private Long id;


    @OneToOne(mappedBy = "delivery",fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)//ordinal은 1234숫자로 들어가는데 만약에[ enum에 뭔가가 추가가 되면 순서가꼬임 절대사용x
    private DeliveryStatus status;

}
