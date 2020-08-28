package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model)
    {
        model.addAttribute("memberForm",new MemberForm());//createMemberForm으로 이동할 떄
        return "members/createMemberForm";                  // new MemberForm() 이라는 빈 객체를 들고간다
    }

    @PostMapping("/members/new")//이젠 입력값 받아서 맵핑해야하니까
    public String create(@Valid MemberForm form, BindingResult result)
    {               //문제가 생겨서 바인딩result 에 오류가 들어왔고 다시 createMemberForm으로 돌려보내는데 타임리프랑 스프링이
                    //멤버폼에서 notempty설정해둔 거 뜸
        // + memberForm을 만들어서 쓰는 이유는 멤버엔티티를 쓸 수 있지만 엔티티가 복잡해지고 지저분해짐 간편한 방법을 사용
        if(result.hasErrors())
        {
            return "members/createMemberForm";// 이러면 만약 이름을 입력하지않으면 가입할 때 오류가나는데 다시 가입하도록
        }
        Address address = new Address(form.getCity(),form.getStreet(),form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        //controller - service - repository

        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model)
    {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
