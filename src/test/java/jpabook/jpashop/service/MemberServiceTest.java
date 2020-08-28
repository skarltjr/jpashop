package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepositoryOld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional//test는 항상 초기화시켜서 진행해야하니까 rollback


//★★ 테스트에서도 리소스폴더만들어서 yml따로 적용시킨다 -> h2 db없이 메모리db로 바꿔줘서 db안켜도 가능하게끔 사실 그냥 디폴트가 메모리로동작하게끔 부트
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepositoryOld memberRepository;

    @Test
    public void 회원가입() throws Exception
    {
        //given
        Member member=new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        //then
        assertEquals(member,memberRepository.findOne(saveId));
    }


    @Test(expected = IllegalStateException.class)
    public void 중복회원예외() throws Exception
    {
        //given
        Member member1=new Member();
        member1.setName("kim");

        Member member2=new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);//여기서 예외가 터져야한다. 여기서 터진 예외가 IllegalStateException이면 성공

        //then
        fail("예외가 발생해야 한다");//여기 오면 안되는데       알림
    }


//    @Test 위처럼 변경시킬 수 있다
//    public void 중복회원예외() throws Exception
//    {
//        //given
//        Member member1=new Member();
//        member1.setName("kim");
//
//        Member member2=new Member();
//        member2.setName("kim");
//
//        //when
//        memberService.join(member1);
//        try{
//            memberService.join(member2);//여기서 예외가 터져야한다.
//        }catch(IllegalStateException e)
//        {
//            return;
//        }
//        //then
//        fail("예외가 발생해야 한다");//여기 오면 안되는데       알림
//    }
}