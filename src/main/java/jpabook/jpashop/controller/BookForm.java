package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter //엔티티를 직접사용하지 않고 폼을 이용하기위해 폼을만든다
public class BookForm {
    private Long id;  //나중에 상품수정을 위해 id

    private String name;
    private int price;
    private int stockQuantity;
    //여기까진 상품의 공통

    private String author;
    private String isbn;

}
