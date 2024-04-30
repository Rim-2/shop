package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.ItemRepository;
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
public class ItemServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @Test
    public void 상품_등록() throws Exception {
        // given
        Book jpa = new Book();
        jpa.setName("JPA");

        // when
        Long itemId = itemService.saveItem(jpa);

        // then
        assertEquals(jpa, itemRepository.findOne(itemId));
    }

    @Test
    public void 상품_수정() {
        // given
        Book spring = new Book();
        spring.setName("Spring");
        Long springId = itemService.saveItem(spring);


        // when
        spring.setName("JPA");
        itemService.saveItem(spring);

        // then
        assertEquals("JPA", itemRepository.findOne(springId).getName());
    }

    @Test
    public void 재고추가() {
        // given
        Book book = new Book();
        book.setName("book1");
        book.setStockQuantity(5);
        Long bookId = itemService.saveItem(book);

        // when
        book.addStock(10);
        itemService.saveItem(book);

        // then
        assertEquals(15, itemRepository.findOne(bookId).getStockQuantity());
    }

    @Test
    public void 재고감소() {
        // given
        Book book = new Book();
        book.setName("book1");
        book.setStockQuantity(5);
        Long bookId = itemService.saveItem(book);

        // when
        book.removeStock(5);
        itemService.saveItem(book);

        // then
        assertEquals(0, itemRepository.findOne(bookId).getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 재고감소_부족() {
        // given
        Book book = new Book();
        book.setName("book1");
        book.setStockQuantity(3);
        itemService.saveItem(book);

        // when
        book.removeStock(5);

        // then
        fail("예외가 발생해야 한다.");

    }
}