package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")//싱글테이블이라서 무보클래스에서 dtype했으니 뭘로 구분할지
@Getter @Setter
public class Book extends Item{
    private String author;
    private String isbn;

}
