package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity//연습을위해 엔티티에 세터 열어둔것 가급적 닫기
@Table(name="orders")// 데이터베이스 특정테이블과 연결하기위해 여기선 특정 orders와 맵핑을 하기위해 표기
//보면 db에는 ORDER라는게 없고 ORDERS로 나온다 이러한 이유
//★기본적으로 ORDER라는게 db에 예약어로 걸려있어서 에러날 수 있어서 이걸 피하기 위해 다른거 사용할려고
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//오더서비스에서 주문생성과정에서 order = new order해서 생성하지 못하도록 로직 유지를 위해
public class Order {

    //noargsconstructor -> 엔티티 맵핑은 기본적으로 기본생성자가 필수이기 때문에 롬복으로 만들어줬다!

    @Id @GeneratedValue
    @Column(name="order_id") //table명 _id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)//여러개의 주문이 한 회원으로부터 나오니까
    @JoinColumn(name="member_id")//member_id랑 맵핑  ->당연히 이경우 오더가 주인 mappedby인 멤버는 주인x
    private Member member;


    /**
     * 지;금은 대부분 양방향인데 단방향이어도 똑같다 만약 멤버->오더 단방향이라면 외래키를 갖는 주인은 오더니까 오더에서만
     * 조인컬럼해주면 된다. 단 이 때는 단방향이기 때문에 당연히 멤버클래스에는 오더가 없어야한다.
     * */



    @OneToMany(mappedBy ="order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems=new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id") //일대일관계에서도 외래키 fk를 주인이 될 놈한테 줘야하는데 주로 접근이 많은 쪽
    private Delivery delivery;
    /**
     * cascade는 맵핑,연관관계랑 전혀 상관이없이 편리를 위해 영속성 전이. 단 주의해야할 점은
     * 지금처럼 order만 delivery와 연관, 관리하는 경우에만 사용 다른 클래스도 delivery를 관리하는데 cascade를
     * 쓰면 안된다.★ + lifecycle이 똑같을 때 = delivery는 order가 만들어질때 만들어지고 없어질때 같이 없어진다.
     * */

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;//주문상태 order / cancel

    //===연관관계 편의 매서드    - 양방향일때 컨트롤하는쪽 여기선 오더니까 오더가 매서드를 들고있는다
    //오더-멤버  / 오더 - 오더아이템 / 오더- 딜리버리 양방향
    public void setMember(Member member)
    {
        this.member=member;
        member.getOrders().add(this);   //멤버는 오더를 리스트로 갖고있어서 add할 수 있따
        //이걸 왜 하냐면 원래같은 양방향관계를 맺을 때 메인에서 아래처럼햐던걸 한 줄 없앤다
        /*main
        Member member =new Member();
        Order order = new Order();

                                         member.getOrders().add(order);  !!이거 없앨 수 있다
        order.setMember(member);
        * */
    }
    public void addOrderItem(OrderItem orderItem)
    {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery)
    {
        this.delivery=delivery;
        delivery.setOrder(this);
    }

    /*
    * 가장 중요한 주문 도메인
    * */
    //==생성 매서드==//  주문의 총 절차
    public static Order createOrder(Member member,Delivery delivery, OrderItem... orderItems)//여러개 오더넘기기위해..
    {
        Order order=new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems)
        {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
        //주문의 총 생성을 한 방에
    }

    //==비즈니스 로직 ==//
    //주문취소
    public void cancel()
    {
        if(delivery.getStatus()==DeliveryStatus.COMP)//이미 배송완료이면
        {
            throw new IllegalStateException("이미 배송완료된 상품은 취소 불가");
        }
        //그게 아니라면 주문 자체를 일단 취소상태 -> 주문신청된 개별 아이템 모두 취소
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems)//orderItems 는 위에 만들어놓은 list
        {
            orderItem.cancel();//재고 수량까지 원복
        }
    }

    /*
     * 전체 주문 가격 조회
     */
    public int getTotalPrice()
    {
        int totalPrice=0;
        for(OrderItem orderItem : orderItems)
        {
            totalPrice+=orderItem.getTotalPrice();
        }
        return totalPrice;
    }


    //==조회 로직==//

}
