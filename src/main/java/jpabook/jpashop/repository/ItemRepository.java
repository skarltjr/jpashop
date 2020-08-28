package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository

public class ItemRepository
{
    @PersistenceContext
    private EntityManager em;

    public void save(Item item)
    {
        if(item.getId()==null)//jpa 저장하기전까지 item은 처음에 저장할 때 id가 없다
        {
            em.persist(item);//persist하면서 아이디 부여
        }
//        else {
//            em.merge(item);
//        }
    }

    public Item findOne(Long id)
    {
        return em.find(Item.class,id);
    }

    public List<Item> findAll()
    {
        return em.createQuery("select i from Item i",Item.class)
                .getResultList();
    }


}
