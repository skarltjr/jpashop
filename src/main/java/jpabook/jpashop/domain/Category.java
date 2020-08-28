package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    @ManyToMany  //category - item  연습을 위한 매니투매니 실무사용 x
    @JoinTable(name="category_item", //중간테이블(일대다 다대일로 풀어낼 )
        joinColumns = @JoinColumn(name="category_id"),//이 중간테이블을 기준으로 한쪽에선 카테고리id - 한쪽에선 아이템id를 끌고온다
            inverseJoinColumns = @JoinColumn(name="item_id"))  // item_id -> 중간테이블 <-category_id
    private List<Item> items= new ArrayList<>();




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    /**
     * 상위 카테고리를 나타내기 위해 parent - child 당연히 부모 하나에 여러 자식  셀프 맵핑이 가능
     * ★ 여러자식 --> 부모하나 manytoone
     * */



    @OneToMany(mappedBy = "parent")
    private List<Category> child=new ArrayList<>();

    //연관관계 편의 매서드
    public void addChildCategory(Category child)
    {
        this.child.add(child);
        child.setParent(this);
    }


}
