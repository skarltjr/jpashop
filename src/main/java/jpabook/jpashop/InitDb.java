package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * userA
 * JPA1 BOOK
 * JPA2 BOOK
 * ===
 * userB
 * SPRING1 BOOK
 * SPRING2 BOOK
 * */

//이렇게 하면 서버띄울 때 init으로 다 호출 ->api 시작할 때 기본으로 자료 들어가있게

@Component //컴포넌트스캔 -의존성 
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init()
    {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService
    {
        private final EntityManager em;
        public void dbInit1()
        {
            Member member = new Member();
            member.setName("userA");
            member.setAddress(new Address("서울", "123", "123123"));
            em.persist(member);

            Book book1 = new Book();
            book1.setName("JPA1 BOOK");
            book1.setPrice(10000);
            book1.setStockQuantity(100);
            em.persist(book1);

            Book book2 = new Book();
            book2.setName("JPA2 BOOK");
            book2.setPrice(20000);
            book2.setStockQuantity(100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery =new Delivery();
            delivery.setAddress(member.getAddress());
            em.persist(delivery);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2()
        {
            Member member = new Member();
            member.setName("userB");
            member.setAddress(new Address("청주", "345", "123123"));
            em.persist(member);

            Book book1 = new Book();
            book1.setName("SPRING BOOK1");
            book1.setPrice(20000);
            book1.setStockQuantity(200);
            em.persist(book1);

            Book book2 = new Book();
            book2.setName("SPRING BOOK2");
            book2.setPrice(40000);
            book2.setStockQuantity(300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
            em.persist(orderItem1);
            em.persist(orderItem2);
            Delivery delivery =new Delivery();
            delivery.setAddress(member.getAddress());
            em.persist(delivery);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
    }

}

