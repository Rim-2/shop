package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Test
    public void 상품_주문() throws Exception {
        // given
        Member member = createMember();
        Book book = createBook("book", 5, 10000);

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), 2);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품의 종류는 1개", 1, getOrder.getOrderItems().size());
        assertEquals("주문 금액 = price * count", 10000 * 2, getOrder.getTotalPrice());
        assertEquals("주문한 수량만큼 재고 감소", 3, book.getStockQuantity());
    }

    @Test
    public void 주문취소() throws Exception {
        // given
        Member member = createMember();
        Book book = createBook("book", 10, 5000);
        Long orderId = orderService.order(member.getId(), book.getId(), 3);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태 CANCEL로 변경", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문 취소 재고 원상복구", 10, book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 주문시_재고수량초과() throws Exception {
        // given
        Member member = createMember();
        Book book = createBook("book", 5, 10000);

        // when
        orderService.order(member.getId(), book.getId(), 7);

        // then
        fail("재고 초과 예외가 발생하지 않음");
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member");
        member.setAddress(new Address("city", "steet", "zipcode"));
        em.persist(member);

        return member;
    }

    private Book createBook(String name, int quantity, int price) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(quantity);
        book.setPrice(price);
        em.persist(book);

        return book;
    }
}