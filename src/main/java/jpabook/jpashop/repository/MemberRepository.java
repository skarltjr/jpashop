package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //그냥 이대로 사용하면 구현할 필요없이 알아서
    //select m from Member m where m.name =? 알아서 날린다
    //규칙처럼 findBy 뒤에 Name인 경우
    List<Member> findByName(String name);
}

//기본기 다지고 사용  -> 엄청 많은것들 거의 모든것들이 구현되어있다 