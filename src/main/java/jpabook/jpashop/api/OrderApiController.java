package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


//OrderSimpleApiController는 간단한예시  - 여기서는 주문조회 때 뭘 주문했는지 오더아이템까지
//그러기 위해서 컬렉션 . 일대다 최적화

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            //지금 하이버네이트 모듈5로 프록시가 초기화 된(즉 데이터가 로딩된) 애들만 출력되도록 설정
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            /*for (OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();
            }*/
            //이건 그냥
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
        //\근데 이경우는 당연히 양방향 jsonignore 해줘야한다
        //어쨌든 엔티티를 직접노출해서 결국은 하면 안되는 방법
    }

    //dto 변환
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2()
    {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        //dto변환
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }
    //쿼리 뒤지게 많이 나간다 그럼 줄여야지 join fetch


    /////=========================================


    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3()//사실 코드자체는 v2랑 똑같다 다만 레퍼지토리에서 fetch join의 유무
    {
        List<Order> orders=orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
        return collect;
    }

    //컬렉션 fetch join의 한계 = 페이징불가 -> batch size 를 이용
    //글로벌로 yml property에 항상 적용해두는게 좋다


    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset",defaultValue = "0") int offset,
            @RequestParam(value = "limit",defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        //그럼 여기서는 fetch join을 멤버랑 딜리버리만 했는데 컬렉션인 오더아이템은 어떻게 되는가?
        //batch_fetch 글로벌로 해두면 이 경우에 쿼리가 3방  오더쿼리 -> 오더 아이템쿼리 -> 아이템 쿼리
        //fetch join을 안했는데도 collect로 루프를 돌 때 오더아이템 한 방에 다 가져오고 -> 아이템도 한 방에
        //중복 없다 이거 쿼리 보면 오더아이템에 ?가 두개인데 2개 가져왔다는것 그 말은 기본설정으로 오더 2개 넣어놨고
        //오더 아이템(대표 아이템)도 2개 그리고 아이템은 ? 4개인데 당연히 아이템 기본설정으로 4개 넣어놨다
        //즉 한 방에 싹 가져오는걸 해준다

        return collect;

        //x to one 관계는 fetch join 다 사용가능 페이징도 가능 그래서 findAllWithMemberDelivery사용
        //이제 orderItems를 해결해야한다 이 경우 컬렉션은 그냥 lazy로딩으로 가져오되 batch size사용
    }

    /**
     * 결과적으로 성능이 중요한 조회에선 lazy로딩의 쿼리 수를 정리할 수 있는 join fetch가 중요
     *  그러나 컬렉션이 포함된 경우 join fetch가 페이징에서 한계를 갖는다
     *  x to one 의 관계는 모두 그대로 fetch join을 사용하면서 페이징도 다 가능하지만 컬렉션의 한계돌파 = = = =
     *   yml에 그냥 글로벌로 batch_fetch적용하여 이를 사용한다  maximum은 1000개 정도가 맥스    
     * */
    
    @Data
    static class OrderDto
    {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;  // 이런 값타입 value obj는 그냥 노출시켜도 상관없다  엔티티는 무조건 dto로
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order)
        {
            orderId=order.getId();
            name=order.getMember().getName();
            orderDate=order.getOrderDate();
            orderStatus=order.getStatus();
            address=order.getDelivery().getAddress();
            orderItems=order.getOrderItems().stream()
                    .map(orderItem->new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }
    //★★ 컬렉션일때 이 경우 orderItem도 개별 dto를 만들어서 dto로 변환시켜야 한다
    @Data
    static class OrderItemDto
    {
        //orderItem에서 내가 노춣하고 싶은게 뭘까
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem)
        {
            itemName=orderItem.getItem().getName();
            orderPrice=orderItem.getOrderPrice();
            count=orderItem.getCount();
        }
    }

    /// dto 직접
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4()
    {
        return orderQueryRepository.findOrderQueryDtos();
        /**
         * dto 직접조회 순서를 살펴보면 쿼리용 레퍼지토리 따로 만들어서 orderQueryRepository
         * orderQueryRepository에서 dto를 직접 조회하는데 findOrders 로 x to one 관계 애들은 그냥 조인으로 가져온다
         * 이렇게 가져온 결과물에 일대다 컬렉션인 오더아이템은 다시 개별 dto를 만들어서 findOrderItems으로 
         * dto에 맞게 돌린다음 foreach로 앞서 만들어둔 result에다가 set으로  orderItem을 얹혀준다
         * */

    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5()
    {
        return orderQueryRepository.findAllByDto_optimization();
    }
}
