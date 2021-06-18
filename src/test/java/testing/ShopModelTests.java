package testing;

import myapp.product.Product;
import myapp.shop.Shop;
import myapp.user.User;
import myapp.shop.ShopRepository;
import myapp.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={myapp.BootApplication.class})
public class ShopModelTests {

    private Shop shop;
    private String category = "category";
    private String name = "shopname";
    private String tag = "shoptag";
    private Long id = 1L;

    private User ownedBy;
    private String username = "uname";
    private String password = "password";

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        ownedBy = new User(username,password);
        userRepository.save(ownedBy);
        shop = new Shop(name, category, tag, ownedBy, id);
        shopRepository.save(shop);
    }

    @Test
    public void testGetters() {
        assertEquals(category, shop.getCategory());
        assertEquals(name, shop.getName());
        assertEquals(tag, shop.getTag());
        assertEquals(id, shop.getId());
        assertEquals(ownedBy, shop.getOwnedBy());
    }

    @Test
    public void testAddProduct(){
        Product product = new Product("item","","", 1,0.00, shop);
        shop.addProduct(product);
        assertEquals(product, shop.getProducts().get(0));
    }

    @Test
    public void testRemoveProduct(){
        Product product = new Product("item","","", 1,0.00, shop);
        shop.addProduct(product);
        assertEquals(product, shop.getProducts().get(0));
        shop.removeProduct(product);
        assertTrue(shop.getProducts().isEmpty());
    }

    @Test
    public void testSetters(){
        String category2 = "category2";
        String name2 = "shopname2";
        String tag2 = "shoptag2";

        shop.setCategory(category2);
        shop.setName(name2);
        shop.setTag(tag2);

        assertEquals(category2, shop.getCategory());
        assertEquals(name2, shop.getName());
        assertEquals(tag2, shop.getTag());

        User ownedBy2 = new User("test","abcd");
        userRepository.save(ownedBy2);
        shop.setOwnedBy(ownedBy2);
        assertEquals(ownedBy2, shop.getOwnedBy());
    }

}
