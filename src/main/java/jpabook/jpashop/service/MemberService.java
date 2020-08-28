package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepositoryOld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)//★ 조회는 리드온리인것이 효율적이고 아래 조회가 많으니 기본으로 리드온리를 설정 그러나 join같은
//@RequiredArgsConstructor  롬복 기능을 사용하면 아래 파이널이 붙은 것에 대해서만 생성자를 만들어줘서 생성자 인젝션 3줄을 없앨 수 있다
public class MemberService
{
    private MemberRepositoryOld memberRepository;

    @Autowired  //생성자 인젝션 추천
    public MemberService(MemberRepositoryOld memberRepository) {
        this.memberRepository = memberRepository;
    }



    //회원가입
    @Transactional//join같은 쓰기에는 절대 사용하면 안된다 그래서 디폴트값은 false
    public Long join(Member member)
    {
        //같은 이름 회원 방지
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member)//내부에서만 검증용도로 사용하기위해 private
    {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty())//이미 같은 이름을 가진놈이 나오면
        {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    //회원 전체 조회
    public List<Member> findMembers()
    {
        return memberRepository.findAll();
    }
    //spring data jpa 이미 findAll도 다 구현되어있는거 쓰면 편리


    public Member findOne(Long id)
    {
        return memberRepository.findOne(id);
    }
    //optional로 반환해주는 기본이라 get쓴다
    
    
    //update
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
