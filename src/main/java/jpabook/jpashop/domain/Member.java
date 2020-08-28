package jpabook.jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * 중요한 점은 엔티티는 여러 곳에서 손댄다 그런데 예를들어 name을 username으로만 바꿔도  api스펙 자체가 변해버려서
 * 누군가가 엔티티를 살짝 바꾸기만해도 오류가 난다. 그래서 api스펙을위한 별도의 data transfer object DTO를 만들어야한다 ->v2로수정
 * */

@Entity//(name="Member")//기본적으로 클래스이름과 동일해서 굳이 안적어도된다
@Getter @Setter
public class Member
{
    @Id
    @GeneratedValue
    @Column(name="member_id") //테이블아래 지금 여긴 id라고되어있는데 member_id컬럼과 맵핑하도록 설정하는것 db가서보면 member_id로나옴
    private Long id;  //즉 객체에서는 id라 사용하고싶고 db컬럼명은 member_id로사용하고싶다

    @NotEmpty//이름은 있어야지
    private String name;


    @Embedded//내장타입 address맵핑
    private Address address;

    @JsonIgnore //★ 양방향일때 무한루프를 피하기위해
    //회원정보  api로 보낼 때 오더는 빼고 싶다.하면 - 그러나 이 엔티티가 여러곳에서 사용되고 어디선 이름은 필요없다하면
    @OneToMany(mappedBy = "member")//그래서 order에 있는 멤버에 의해 맵핑되었다 주인이아니다 나는 읽기전용
    //member입장에서 이 리스트는 하나의 회원이 여러개 상품을 주문하니까 - order에선 매니투원
    private List<Order> orders = new ArrayList<>();
    //이럼 결국 양방향연결  -> ★ 이 연관관계의 주인을 정해줘야한다 양쪽에서 다 수정을 하면 안되니까 주인은 order의 멤버

}
