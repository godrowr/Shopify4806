package testing;

import myapp.product.Product;
import myapp.product.ProductRepository;
import myapp.shop.Shop;
import myapp.shop.ShopRepository;
import myapp.user.User;
import myapp.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={myapp.BootApplication.class})
public class UserModelTests {

    private User user;

    private String username = "aaaa";
    private String password = "bbbb"; // very secure

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        user = new User(username, password);
        userRepository.save(user);
    }

    @Test
    public void testGetters() {
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(0, user.getShops().size());
        assertEquals(0, user.getShoppingCartItems().size());
    }

    @Test public void testShopsModifications() {
        Shop shop1 = new Shop("shopName1", "shopCategory1", "shopTag1", user);
        shopRepository.save(shop1);
        user.addShop(shop1);
        assertEquals(1, user.getShops().size());
        Shop shop2 = new Shop("shopName2", "shopCategory2", "shopTag2", user);
        Shop shop3 = new Shop("shopName2", "shopCategory2", "shopTag2", user);
        shopRepository.save(shop2);
        shopRepository.save(shop3);
        user.addShop(shop2);
        user.addShop(shop3);
        assertEquals(3, user.getShops().size());
        user.removeShop(shop2.getId());
        assertEquals(2, user.getShops().size());
        user.removeShop(shop3);
        assertEquals(1, user.getShops().size());
    }

    @Test public void testShoppingCartModifications() {
        Shop shop = new Shop("shopName", "shopCategory", "shopTag", user);
        shopRepository.save(shop);
        Product item1 = new Product("name1", "desc1", "imageUrl1", 1, 0.1, shop);
        productRepository.save(item1);
        user.addShoppingCartItem(item1);
        assertEquals(1, user.getShoppingCartItems().size());
        Product item2 = new Product("name2", "desc2", "imageUrl2", 2, 2.0, shop);
        Product item3 = new Product("name3", "desc3", "imageUrl3", 3, 30.0, shop);
        productRepository.save(item2);
        productRepository.save(item3);
        user.addShoppingCartItem(item2);
        user.addShoppingCartItem(item3);
        assertEquals(3, user.getShoppingCartItems().size());
        user.removeShoppingCartItem(item2.getId());
        assertEquals(2, user.getShoppingCartItems().size());
        user.removeShoppingCartItem(item3);
        assertEquals(1, user.getShoppingCartItems().size());
    }

    @Test public void testSetUserName() {
        user.setUsername("jfdifds");
        assertEquals("jfdifds", user.getUsername());
    }

    @Test public void testSetPassword() {
        user.setPassword("fjsdkl");
        assertEquals("fjsdkl", user.getPassword());
    }

    @Test public void testEmptyConstructor() {
        user = new User();
        assertNull(user.getUsername());
        assertNull(user.getPassword());
    }

    @Test public void testId() {
        assertNotNull(user.getId());
        assertNotEquals(new User().getId(), user.getId());
    }

    public static Throwable findCauseUsingPlainJava(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    @Test public void errorSavingNoUsernameUser(){
        user = new User("",password);

        Exception err = null;
        try {
            userRepository.save(user);
        } catch (Exception e){
            err = e;
        }
        assertNotNull(err);
        assertEquals(ConstraintViolationException.class, findCauseUsingPlainJava(err).getClass());
    }

    @Test public void errorSavingNoPasswordUser(){
        user = new User(username,"");

        Exception err = null;
        try {
            userRepository.save(user);
        } catch (Exception e){
            err = e;
        }
        assertNotNull(err);
        assertEquals(ConstraintViolationException.class, findCauseUsingPlainJava(err).getClass());
    }
}
