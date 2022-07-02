package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemControllerTest {

    ItemController itemController;
    
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        // Inject the required Autowired objects into the instance
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }
    
    @Test
    public void get_items_by_id() throws Exception {
        // Setup an item
        Optional<Item> tempItem = Optional.of(new Item());
        Item item = tempItem.get();
        item.setName("Page Header");
        item.setPrice(BigDecimal.valueOf(1.99));
        item.setDescription("A page header template");
        Long id = item.getId();

        when(itemRepository.findById(id)).thenReturn(tempItem);
        final ResponseEntity<Item> response = itemController.getItemById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item testItem = response.getBody();

        assertNotNull(testItem);
        assertEquals(id, testItem.getId());
        assertEquals("Page Header", testItem.getName());
        assertEquals("A page header template", testItem.getDescription());
    }

}
