package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", "김영한", 10000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOne(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getDelivery().getAddress()).isEqualTo(member.getAddress());

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, order.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야한다", 1, order.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, order.getTotalPrice());
        assertEquals("주문 수만큼 재고는 줄어야 한다.", 8, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void 주문상품초과주문() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", "김영한", 10000, 10);

        //when
        int orderCount = 12;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }


    @Test
    public void 상품취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", "김영한", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOne(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertEquals("주문 취소시 상태는 CANCEL이다.", OrderStatus.CANCEL, order.getStatus());
        assertThat(book.getStockQuantity()).isEqualTo(10);
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야한다.", 10, book.getStockQuantity());
    }

    private Book createBook(String name, String author, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setAuthor(author);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);

        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }


}