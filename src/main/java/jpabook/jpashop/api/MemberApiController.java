package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;


//api 패키지를 새로만들어서 사용하는 이유는 그냥 controller는 화면에 오류를 띄우지만 api는 json 형태로 보낸닫
//api를 만들때는 절대 파라미터로 내보내든 받든 엔티티를 외부에 노출하지말아야한다
//v1 은 안좋은 예시 -> v2


//@Controller @ResponseBody 이 두개의 어노테이션을 합친게
@RestController
@RequiredArgsConstructor
public class MemberApiController
{
    private final MemberService memberService;

    //회원 조회 = 변경이 아닌 조회 --★ 보통 성능은 조회에서 !
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() //가장 단순한 예 -->> 안좋으니까 고쳐야함
    {
        return memberService.findMembers();
        //엔티티를 직접 노출하면 이 경우에 회원정보만 갖고싶은데 엔티티자체를 보내주니 회원의 order정보까지 다 나간다
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() //원하는 정보만 , 오류 방지 + 누군가 엔티티를 건드리면 여기서 컴파일 오류가난다
    {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(),collect);//Requi~ 생성자 있으니
    }

    @AllArgsConstructor
    @Data
    static class MemberDto
    {
        @NotEmpty
        private String name;
    }

    @AllArgsConstructor
    @Data
    static class Result<T> //이런식으로 한 번 감싸줘야지 안그러면 그대로 list내보내면 배열로 나가고 배열은 유연성이 낮다
    {
        //만약 count도 필요하다하면 그냥
        private int count;
        private T data;
    }



    //회원등록 -
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member)
    //requsetBody를 통해 json으로 넘어온 데이터를 멤버와 알아서 맵핑해준다 + ★ 엔티티를 파라미터로 받는다는것은
    //엔티티를 외부에 노출시키는것 == 절대 안된다.
    {
        //그러면 파라미터로 넘어오는 멤버에 맵핑이 되어서 데이터 딱딱 들어가있는걸
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //위 내용을 커버한게 v2
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request)
    //위의 문제점을 보완해서 엔티티를 외부에 노출시키지않고 DTO에 requestbody로 맵핑시켜서 DTO로 만든api스펙에 맞는 데이터만 가져온다
    {
        Member member= new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //등록 DTO
    @Data  //엔티티를 파라미터로 받을 땐 id 이름 주소 등 뭘 받을지 까보기전에 모르는데 이렇게 DTO로 받으면 아 이름만 받는구나라고 api스펙을 알 수 있다
    static class CreateMemberRequest {
        private String name;
    }
    @Data
    static class CreateMemberResponse
    {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }


    //간단한 수정을 위한 api -> put맵핑으로
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request)//업데이트용 DTO도 따로 만들어준다
    {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }
    //수정 DTO
    @Data
    static class UpdateMemberRequest  //업데이트멤버리퀘스트라는 어떤 폼에다가 requestbody로 데이터 맵핑시킨걸 받으면
    {                               //예를들어 회원수정을 이름만 수정하도록 하면 DTO로 이름만 받아서
        private String name;
    }
    @Data
    static class UpdateMemberResponse//결과물을 내보내줄때. 확인할려면 동일한 id 였는데 이름만 바뀌었으면 성공
    {
        private Long id;
        private String name;

        public UpdateMemberResponse(Long id, String name) {
            this.id=id;
            this.name=name;
        }
    }


}
