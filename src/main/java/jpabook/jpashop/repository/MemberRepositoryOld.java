package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepositoryOld
{
    @PersistenceContext //jpa표준  엔티티매니저는 무조건 이걸로 autowired사용할 수 없다 원 래 는
    private EntityManager em;

    public void save(Member member)
    {
        em.persist(member);
    }
    /**
     * 원래 id는 트랜잭션 커밋한 후에 생기는데 그렇게되면 아직 커밋안된 상태에서 jpa가 1차캐쉬에서
     * 무언가를 찾으려할 때 id가 아직 없으니 찾을 수 없다 그래서 특수한 케이스로 persist경우에만
     * persist하고 바로 커밋한다 그럼 영속화-> 바로 commit(db에 insert커리 날린다)으로 데이터가 db에 가고 id생성된다.
     * ★다만 sequence전략을 사용하면 위에 내용과 다르게  그 경우에는 jpa가 db한테 시퀀스 다음 숫자를 먼저 받아와서
     * id값을 주기 때문에 바로 커밋할 필요가 없어진다 -> 그래서 시퀀스 전략을 사용하는 경우는
     * 버퍼링 처럼 모았다가  한 번에 트랜잭션 커밋날릴 수 있다
     * ★근데 이건 네트워크 왔다갔다해서 성능 문제가 있을 수 있는데 결국 성능최적화는
     * ★allocationsize방식으로 최적화 ->db에는 먼저 50개 쌓아두고 나는 1부터 사용하다 50개 채우면 다시 db에 50~100싸ㅣㅎ아두고 쓰고
     *
     * 전략사용은 당연히 도메인 -엔티티에서 사용하는것
     *  */
    public Member findOne(Long id)
    {
        return em.find(Member.class,id);
    }
    public List<Member> findAll()
    {
        List<Member> result = em.createQuery("select m from Member m", Member.class)//jpql , 반환타입
                .getResultList();
        return result;
    }
    public List<Member> findByName(String name)
    {
        return em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name",name)
                .getResultList();
    }

//만약 회원정보에 회원이 주문한 주문까지  나타낼려면 주문 오더 조인해서

}
