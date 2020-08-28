package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")//어떤 아이템을 변경하는지에 따라 달라야하기때문에 pathvariable
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);//일단 책만 사용하기위해

        BookForm form = new BookForm();//북 엔테티말고 업데이트할 때 폼을 보낸다
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form)
            //위에서 날린 모델 받는 식으로
    {

        /* 이 부분이 변경감지(dirty checking) - 병합 차이의 핵심 바로 준영속엔티티. 새로운 객체 book이 새로운 아이디를
        * 얻는 것이 아니라 이미 한 번 jpa로 디비에 저장되었던 form의 아이디(식별자)를 그대로 이어받는다
        * ★즉 더는 jpa가 관리를 안한다(준영속) jpa가 관리하는 것 영속성 엔티티,객체들은 변경사항이 있으면
        * jpa가 알아서 ★변경감지를 통해 변경한 내용을 넘긴다 - 그럼 이런 준영속 엔티티는 당연히 변경감지가
        * 자동으로 안되니까.
        * ★아래 itemservice.saveitem을 지운채 set으로 변경시킨 내용만 남기면 디비에 변경사항이
        * 넘겨지지 않는다는 말이다.
        * 이 준영속엔티티를 처리하기 위한 방법이 변경감지를 다시 사용하거나 ★merge 병합을 이용
        *
        * > 주의: 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이
            변경된다. 병합시 값이 없으면 null 로 업데이트 할 위험도 있다. (병합은 모든 필드를 교체한다.)
        * */

//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.saveItem(book);

        //saveitem -> save ->save(itemrepository)로 타고 가보면 결국   em.merge(item);가 나온다
        //이미 존재하는 식별자를 사용했을 때 merge를 사용하도록 설정해놨는데
        //merge의 동작방식은 itemservice에서 변경감지편 처럼 id로 기존에 있던 내용을 찾아내고
        //그 내용들을 지금 book.set처럼 한 내용들로 다 바꿔치기 하는 것


        //좋은 방법 - 그냥 변경감지만 사용한다고 생각
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }
}
