package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService
{
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item)
    {
        itemRepository.save(item);
    }
    @Transactional//!!
    public void updateItem(Long itemId, String name, int price ,int stockQuatity)//우선 북만 사용하기위해
    {
        //변경감지 사용방법 편
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuatity);
        //이것도 매서드만들어서 set없애야 좋다


        //이것도 사실 set을 까는게 아니라 매서드로 엔티티에서 변경되는 걸 알 수 있도록 깔끔하게
        //..등등 다 set을 해줬다 하고 이 후 ★★ itemrepository에 save를 해줘야할까>??/
        // ★ 안해줘도된다. findItem은 레퍼지토리에서 영속성상태인 아이템을 그대로 가져오는 것
        //이미 영속성 상태인 아이템을 set으로 내용 좀 바꾸고 transactional인 상태로 db에 다시 넣을때
        //변경된 부분 알아서 jpa가 처리해서 변경시킨다.


        //transactional의 중요성 ★★ 트랜잭션이 있는 서비스 계층에서 update
    }
    public List<Item> findItems()
    {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId)
    {
        return itemRepository.findOne(itemId);
    }


}
