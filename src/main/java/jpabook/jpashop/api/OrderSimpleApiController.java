package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
//import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
//import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ★  x to one 관계 (One to one , Many to one)
 * Order 조회시
 * Order->Member
 * Order->Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    //service는 전체조회만 갖고있다 repository를 이용해서 조회성능 최적화
    private final OrderRepository orderRepository;
   // private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1//simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
        //이 상태로 실행하면 무한루프 -> 왜냐하면 오더가 멤버를 갖고있고 멤버도 오더를 갖고있어서
        //오더부르면 오더에있는 멤버를 부르고 멤버가 또 자신의 오더를 부르고부르고부르고~~
        //무한루프 방지를 위해선 양방향★인것들 한쪽을 jsonignore
    }


    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2()
    {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    //dto
    @Data
    static class SimpleOrderDto
    {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        public SimpleOrderDto(Order order)//dto가 엔티티를 파라미터ㅏ로 받는것은 별로 문제가 되지않는다
        {
            orderId = order.getId();
            name = order.getMember().getName();//lazy로딩으로 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();//여기서도 lazy초기화 = 영속성컨텍스트에서 딜리버리있나 찾아보고 없으면 db쿼리날린다
        }
    }


    //=======
    //그런데 dto를 사용한 v2도 문제점이 lazy로딩으로 인한 db쿼리가 너무 많이 호출된다
    //순서를 살펴보면 ★★ ordersV2에서 결과적으로 기본적으로 넣어둔 주문 2개가 나온다 2개.
    //그러면 각 주문마다 dto로 변환시킬 때 getname getAddress하는데  그 때마다 lazy 로딩이기때문에 쿼리 날린다
    //주문이 2개인 경우에 ordersV2에서 쿼리 1번 주문 각각마다 getname getAddress쿼리 1,1
    //그래서 ordersV2에서 처음 주문 가져오는데 쿼리 1방 주문마다 각각 2방 총 5방 쿼리가 나간ㄷ, 어마어마하게 많이 나가는것
    //fetch join 이럴 때 사용

    @GetMapping("/api/v3/simple-orders") //dto에는 엔티티 노출시켜도 가능
    public List<SimpleOrderDto> ordersV3()
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        //dto변환  순서를 보자면 ★ dto로 내보내기위해 dto가 필요로 하는 오더-멤버,오더-딜리버리만 가져오도록
        //이후 이 결과물을 dto로 내보낸다 애초에 조회는 등록과 다르게 뭘 받아서 한느게 아니라 파라미터로 엔티티 받을일도없다 내보내기만
        //등록과 다른점은 회원등록은 엔티티를 직접 건드리면 안되기때문에 dto형태로 필요한 내용을 받아서 update 한 후 또 다른 형태로 내보낸다

       //★★여기서의 핵심은 성능! 조회가 성능의 많은 부분을 차지하고 조회에서 성능을 최적화하기 위해선 쏘는 쿼리의 횟수가 포인트 !
        //lazy로딩과의 연관성이 핵심 그래서 애초부터 레퍼지토리에서 가져올때 join fetch로 필요한 부분 한방에
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;// Result 를 만들어서 감싸준 후 return해주는게 좋다
    }

    /*@AllArgsConstructor
    @Data
    static class Result<T> //이런식으로 한 번 감싸줘야지 안그러면 그대로 list내보내면 배열로 나가고 배열은 유연성이 낮다
    {
        //만약 count도 필요하다하면 그냥
        private T data;
    }*/

    //==========================


    //바로 dto = 최적화 그러나 유지보수 혹은 객체지향은 v3 일단 v3를 사용
  /*  @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4()
    {
        return orderSimpleQueryRepository.findOrderDtos();
    }
*/

}


