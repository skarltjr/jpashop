package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//앨범 북 무비가 아이템을 상속받는다 상속관계 전략을 잡아야한다 부모 클래스에

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)//싱글테이블이니까 북,무비 앨범 테이블에있는 artist,author같은거
//전부다 한 테이블(item)에 때려박는다 일단 -> joined를 사용하면 앨범테이블엔 artist랑 atc만
// - joined , singletable , 등등은 상황에 맞게 사용하기 ->annotation만 바꿔도 singletable->joined로 바꿀 수 이;ㅆ는 장점
@DiscriminatorColumn(name="dtype")//book이면 어떻게 할거야? -> book가서 value설정  -singletable에선 필수
@Getter @Setter
public abstract class Item
{
    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;


    //북 무비 앨범등의 공통속성이기때문에 이걸 추상클래스로
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories=new ArrayList<>();

    //==비즈니스 로직==상품 재고 취소하면 늘어나고 등등//
    //책 영화 앨범 객체의 부모에서  전체적인 관리를하는 객체지향을 효율적으로

    //재고수량 증가
    //지금은 연습을 위해 setter를 넣어놔서 재고조절을 직접할 수 있지만 실제론 아래처럼 매서드 생성해놓고 이를 통해
    public void addStock(int quatity)
    {
        this.stockQuantity+=quatity;
    }
    //감소
    public void removeStock(int quantity)
    {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0)
        {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity=restStock;
    }

}
