package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.dto.UpdateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

        // setter를 이용해 값을 넣어주는 것이 아니라, createBook 메서드를 만들어서 값을 넣어주는 것이 더 좋다.
        book.setStockQuantity(form.getStockQuantity());
        book.setPrice(form.getPrice());
        book.setAuthor(form.getAuthor());
        book.setName(form.getName());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        model.addAttribute("items", itemService.findItems());
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setAuthor(item.getAuthor());
        form.setPrice(item.getPrice());
        form.setName(item.getName());
        form.setIsbn(item.getIsbn());
        form.setStockQuantity(item.getStockQuantity());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
        // Controller에서 엔티티를 생성하는 것은 좋지 않다.
        // merge를 사용하는 이유는 준영속상태(Transactional 바깥에서 선언한 엔티티 객체)의 객체를 식별자를 통해 기존 엔티티를 대체하기 위해.
        // 하지만 준영속상태의 객체의 모든 값으로 변경하기 때문에 null 이 포함되어도 적용된다.
        //        Book book = new Book();
//        book.setId(form.getId());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setPrice(form.getPrice());
//        book.setAuthor(form.getAuthor());
//        book.setName(form.getName());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);
        UpdateItemDto updateItemDto = new UpdateItemDto(form.getName(), form.getPrice(), form.getStockQuantity());
        itemService.updateItem(itemId, updateItemDto);

        return "redirect:/items";
    }

}
