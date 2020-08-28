package jpabook.jpashop.repository;

//repository에서 컨트롤러 의존관계 생기면 망한다
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;

import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository
{
    private final EntityManager em;
    // final 없이 persistcontext로 하거나 final에 required쓰거나

    public void save(Order order)
    {
        em.persist(order);
    }
    public Order findOne(Long id)
    {
        return em.find(Order.class,id);
    }

    public List<Order> findAll(OrderSearch orderSearch)
    {
        return em.createQuery("select o From Order o", Order.class).getResultList();

      /* return em.createQuery("select o from Order o join o.member"+
               "where o.status  = : status" +
               "and m.name like :name",Order.class)
               .setParameter("status",orderSearch.getOrderStatus())
               .setParameter("name",orderSearch.getMemberName())
               .getResultList();*/
       //이 경우는 orderSearch에 null이 없이 다 정보가 있는경우만 가능해서 사실상 아래처럼 동적쿼리짜야한다
    }


    // 현재 모든 order를 끌어오는 findAllByString대신 이렇게 간편하게 사용할 수 있다
    public List<Order> findAll2(OrderSearch orderSearch) {
        QOrder  order = QOrder.order;
        QMember member =QMember.member;

        JPAQueryFactory qeury = new JPAQueryFactory(em);
        return qeury
                .select(order)
                .from(order)
                .join(order.member,member)
                .where(statusEq(orderSearch.getOrderStatus())) //동적쿼리일 때 아래처럼 조건 만들어서 매서드만들고 파라미터로 받아서
                .limit(1000)
                .fetch();

        //qeury dsl
    }
    //dsl은 아래처럼 조건 생성해서 그냥 where넣으면 된다
    private BooleanExpression statusEq(OrderStatus statusCond)
    {
        if (statusCond == null) {
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }






    //만약 orderSearch의 상태같은게 null인 경우도 있으니까 
    //즉 검색조건 없이 다 가져오는 것
    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .getResultList();

        //fetch join으로 오더부를 때 멤버랑 딜리버리까지 한방쿼리
        //이 경우는 프록시도아니고 오더부를 때 진짜 객체 다 채워서 보낸다
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +  //jpa - 컬렉션 fetch join 필살기
                        " join fetch o.member m"+
                        " join fetch o.delivery d"+
                        " join fetch o.orderItems oi"+
                        " join fetch oi.item i",Order.class//join앞에 띄워야한다 실수 많이 함
        ).getResultList();
    }
    //fetch join의 치명적 단점 -- ★★★ 일 대 다 인 경우 페이징 불가능  x 대 일은 상관없다
    //batch size 이용

    public List<Order> findAllWithMemberDelivery(int offset,int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        //fetch join으로 오더부를 때 멤버랑 딜리버리까지 한방쿼리
        //이 경우는 프록시도아니고 오더부를 때 진짜 객체 다 채워서 보낸다
    }
    
}
