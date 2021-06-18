package testing;

import myapp.product.Product;
import myapp.product.ProductRepository;
import myapp.shop.Shop;
import myapp.shop.ShopRepository;
import myapp.user.User;
import myapp.user.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={myapp.BootApplication.class})
public class ProductModelTests {

    private Shop shop;
    private Product product;
    private User user;

    private String name = "aaaa";
    private String desc = "bbbb";
    private String imageUrl = "cccc";
    private Integer inventoryNum = 100;
    private Double price = 0.001;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() {
        user = new User("username","password");
        userRepository.save(user);
        shop = new Shop("shopName", "shopCategory", "shopTag", user);
        shopRepository.save(shop);
        product = new Product(name, desc, imageUrl, inventoryNum, price, shop);
        productRepository.save(product);
    }

    @Test
    public void testGetters() {
        assertEquals(name, product.getName());
        assertEquals(desc, product.getDescription());
        assertEquals(imageUrl, product.getImageUrl());
        assertEquals(inventoryNum, product.getInventoryNumber());
        assertEquals(price, product.getPrice());
    }

    @Test public void testSetName() {
        product.setName("kjlksjdf");
        assertEquals("kjlksjdf", product.getName());
    }

    @Test public void testSetDescription() {
        product.setDescription("lsdkjf");
        assertEquals("lsdkjf", product.getDescription());
    }

    @Test public void testSetImageUrl() {
        product.setImageUrl("ldsfkjdfsl");
        assertEquals("ldsfkjdfsl", product.getImageUrl());
    }

    @Test public void testSetInventoryNumber() {
        product.setInventoryNumber(742983);
        assertEquals(Integer.valueOf(742983), product.getInventoryNumber());
    }

    @Test public void testSetPrice() {
        product.setPrice(7898.098);
        assertEquals(Double.valueOf(7898.098), product.getPrice());
    }

    @Test public void testSetShop() {
        product.setShop(null);
        assertNull(product.getShop());
    }

    @Test public void testEmptyConstructor() {
        product = new Product();
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getImageUrl());
        assertNull(product.getInventoryNumber());
        assertNull(product.getPrice());
    }

    @Test public void testId() {
        assertNotNull(product.getId());
        assertNotEquals(new Product().getId(), product.getId());
    }

    public static Throwable findCauseUsingPlainJava(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    @Test public void errorSavingNegInventoryNumberProduct(){
        product = new Product(name, desc, imageUrl, -1, price, shop);

        Exception err = null;
        try {
            productRepository.save(product);
        } catch (Exception e){
            err = e;
        }
        assertNotNull(err);
        assertEquals(ConstraintViolationException.class, findCauseUsingPlainJava(err).getClass());
    }

    @Test public void errorSavingNegPriceProduct(){
        product = new Product(name, desc, imageUrl, inventoryNum, -1.0, shop);

        Exception err = null;
        try {
            productRepository.save(product);
        } catch (Exception e){
            err = e;
        }
        assertNotNull(err);
        assertEquals(ConstraintViolationException.class, findCauseUsingPlainJava(err).getClass());
    }

    @Test public void errorSavingNoNameProduct(){
        product = new Product("", desc, imageUrl, inventoryNum, price, shop);

        Exception err = null;
        try {
            productRepository.save(product);
        } catch (Exception e){
            err = e;
        }
        assertNotNull(err);
        assertEquals(ConstraintViolationException.class, findCauseUsingPlainJava(err).getClass());
    }

    @Test public void errorSavingNoPriceProduct(){
        product = new Product(name, desc, imageUrl, inventoryNum, null, shop);

        Exception err = null;
        try {
            productRepository.save(product);
        } catch (Exception e){
            err = e;
        }
        assertNotNull(err);
        assertEquals(ConstraintViolationException.class, findCauseUsingPlainJava(err).getClass());
    }

}
