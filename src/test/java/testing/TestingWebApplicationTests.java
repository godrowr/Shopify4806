package testing;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
import myapp.shop.Shop;
import myapp.user.User;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import myapp.product.Product;
import myapp.product.ProductRepository;
import myapp.shop.ShopRepository;
import myapp.user.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import javax.servlet.http.Cookie;
import java.lang.reflect.Type;

import java.util.List;


import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={myapp.BootApplication.class})
@AutoConfigureMockMvc
public class TestingWebApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Before
    public void clearRepositories() {
        userRepository.deleteAll();
        shopRepository.deleteAll();
        productRepository.deleteAll();
    }

    private void postUser(User user) throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/users")
                .content(asJsonString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }

    private void postProduct(Product product, User user) throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/products")
                .content(asJsonString(product))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("userid", user.getId().toString())))
                .andExpect(status().isCreated());
    }

    private void postShop(Shop shop, User user) throws Exception {

        mvc.perform( MockMvcRequestBuilders
                .post("http://localhost:8080/shops")
                .content(asJsonString(shop))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("userid", user.getId().toString())))
                .andExpect(status().isCreated());

    }



    @Test
    public void testGetUserRestAPI() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .get("http://localhost:8080/users"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetShopsRestAPI() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .get("http://localhost:8080/shops"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProductsRestAPI() throws Exception {
        mvc.perform( MockMvcRequestBuilders
                .get("http://localhost:8080/products"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testPostUserRestAPI() throws Exception {
        postUser(new User("Chicken", "password"));

        mvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/users"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testPostShopsRestAPI() throws Exception {
        User user = new User("George", "Password");
        Shop shop = new Shop("BladeCity", "Blades", "#knifegang", user);

        postUser(user);
        postShop(shop, userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword()));

        mvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/shops"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testAddProductToShop() throws Exception {
        User user = new User("George", "Password");
        Shop shop = new Shop("BladeCity", "Blades", "#knifegang", user);
        Product p1 = new Product("Balistic Knife", "This bad boi travels at the same speed I walk", "https://knifeio.com", 1, 1337d, shop);

        postUser(user);
        user = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        postShop(shop, user);
        postProduct(p1, user);

        mvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/products"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void testAddProductToCheckout() throws Exception {
        User user = new User("George", "Password");
        Shop shop = new Shop("BladeCity", "Blades", "#knifegang", user);
        Product p1 = new Product("Balistic Knife", "This bad boi travels at the same speed I walk", "https://knifeio.com", 1, 1337d, shop);

        userRepository.save(user);
        shopRepository.save(shop);
        productRepository.save(p1);

        String shoppingCartURL = "http://localhost:8080/users/"+user.getId()+"/shoppingCartItems";
        String productURL = "http://localhost:8080/products/"+p1.getId();

        int expected_shopping_cart_items_size = 1;

        mvc.perform(MockMvcRequestBuilders
                .put(shoppingCartURL)
                .contentType("text/uri-list")
                .content(productURL)
                .accept("text/uri-list")
                .cookie(new Cookie("userid", user.getId().toString()))).
                andExpect(status().isNoContent());

        mvc.perform(MockMvcRequestBuilders
                .get(shoppingCartURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.products.length()").value(expected_shopping_cart_items_size))
                .andExpect(jsonPath("$._embedded.products[?(@.name=='" + p1.getName() + "')].description").value(p1.getDescription()))
                .andDo(print());
    }


    @Test
    public void testCheckout() throws Exception {
        User toby = new User("Toby", "Turner");
        Shop shop = new Shop("Toby Games", "Gaming", "#introofdarknessthenrednessthenwhiteness", toby);
        Product dSword = new Product("Toby Turner Tobuscus Diamond Sword", "i can swing my sword sword i can swing my sword sword", "youtube.com/tobuscus or something idk", 1, 902000000d, shop);

        userRepository.save(toby);
        shopRepository.save(shop);
        productRepository.save(dSword);

        String shoppingCartURL = "http://localhost:8080/users/"+toby.getId()+"/shoppingCartItems";
        String productURL = "http://localhost:8080/products/"+dSword.getId();

        mvc.perform(MockMvcRequestBuilders
                .put(shoppingCartURL)
                .contentType("text/uri-list")
                .content(productURL)
                .accept("text/uri-list")
                .cookie(new Cookie("userid", toby.getId().toString()))).
                andExpect(status().isNoContent());

        int expected_shopping_cart_items_size = 1;

        mvc.perform(MockMvcRequestBuilders
                .get(shoppingCartURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.products.length()").value(expected_shopping_cart_items_size))
                .andExpect(jsonPath("$._embedded.products[?(@.name=='" + dSword.getName() + "')].description").value(dSword.getDescription()))
                .andDo(print());

        mvc.perform(MockMvcRequestBuilders
                .delete(shoppingCartURL + "/" + dSword.getId())
                .cookie(new Cookie("userid", toby.getId().toString())))
                .andExpect(status().isNoContent())
                .andDo(print());

        expected_shopping_cart_items_size = 0;

        mvc.perform(MockMvcRequestBuilders
                .get(shoppingCartURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.products.length()").value(expected_shopping_cart_items_size))
                .andDo(print());
    }


    @Test public void testSearch() throws Exception {
        User alice = new User("Alice", "password");
        User bob = new User("Bob", "password");

        userRepository.save(alice);
        userRepository.save(bob);

        Shop shop1 = new Shop("Widgets", "trinkets", "widgets", alice);
        Shop shop2 = new Shop("Gizmos", "trinkets", "gizmos", alice);

        Shop shop3 = new Shop("The Apple Store", "food", "apples", bob);
        Shop shop4 = new Shop("Edible widgets", "food", "widgets", bob);

        shopRepository.save(shop1);
        shopRepository.save(shop2);
        shopRepository.save(shop3);
        shopRepository.save(shop4);

        class ExpectedResult {
            public String url;
            public String queryString;
            public Shop[] expected;


            public ExpectedResult(String u, String q, Shop[] e) {
                url = u;
                queryString = q;
                expected = e;
            }
        }

        String nameURL = "http://localhost:8080/shops/search/findByNameIgnoreCase";
        String ownerURL = "http://localhost:8080/shops/search/findByOwnedByUsernameIgnoreCase";
        String categoryURL = "http://localhost:8080/shops/search/findByCategoryIgnoreCase";
        String tagURL = "http://localhost:8080/shops/search/findByTagIgnoreCase";
        String nameStartsWithURL = "http://localhost:8080/shops/search/findShopsStartingWith";

        ExpectedResult[] expectedResults = {
                // Make sure that an empty query string returns nothing
                new ExpectedResult(nameURL, "", new Shop[]{}),
                new ExpectedResult(ownerURL, "", new Shop[]{}),
                new ExpectedResult(categoryURL, "", new Shop[]{}),
                new ExpectedResult(tagURL, "", new Shop[]{}),
                new ExpectedResult(nameStartsWithURL, "", new Shop[]{}),

                // Basic tests for each of the search types
                new ExpectedResult(nameURL, "?name=Widgets", new Shop[]{shop1}),
                new ExpectedResult(ownerURL, "?username=bob", new Shop[]{shop3, shop4}),
                new ExpectedResult(categoryURL, "?category=trinkets", new Shop[]{shop1, shop2}),
                new ExpectedResult(tagURL, "?tag=widgets", new Shop[]{shop1, shop4}),
                new ExpectedResult(nameStartsWithURL, "?name=o", new Shop[]{shop2, shop3}),
        };

        for ( ExpectedResult e : expectedResults) {
            expectQueryResults(e.url, e.queryString, e.expected);
        }

    }

    private void expectQueryResults(String url, String queryString, Shop[] expected) throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        om.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MvcResult response = mvc.perform(MockMvcRequestBuilders
                .get(url + queryString)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String json = response.getResponse().getContentAsString();
        json = om.writeValueAsString(om.readTree(json).get("_embedded").get("shops"));

        List<Shop> shops;
        // TODO: Add a custom parser to pull out ownedBy and ID from the JSON to more accurately compare Shop objects
        shops = om.readValue(json, new TypeReference<List<Shop>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        assert(shops.size() == expected.length);
        for (Shop jsonShop : shops) {
            boolean found = false;
            for (Shop expectedShop : expected) {
                if(jsonShop.getName().equals(expectedShop.getName()) &&
                        jsonShop.getCategory().equals(expectedShop.getCategory()) &&
                        jsonShop.getTag().equals(expectedShop.getTag())) {
                    found = true;
                    break;
                }
            }
            assert(found);
        }
    }

    private void expectLoginResults(String url, String queryString, User expected) throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        om.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MvcResult response = mvc.perform(MockMvcRequestBuilders
                .get(url + queryString)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(
                        expected == null ? status().is4xxClientError() : status().isOk()
                    )
                .andReturn();

        if (expected != null){
            JSONObject json = new JSONObject( response.getResponse().getContentAsString() );
            assertEquals(json.getString("username"),expected.getUsername() );
            assertEquals(json.getString("password"),expected.getPassword() );
            assertEquals(json.getJSONObject("_links").getJSONObject("user").getString("href")  ,"http://localhost:8080/users/" + expected.getId() );

        }
    }

    @Test public void testUserSearch() throws Exception {
        User alice = new User("AliceName", "passworda");
        User bob = new User("BobName", "passwordb");

        userRepository.save(alice);
        userRepository.save(bob);

        String userSearchURL = "http://localhost:8080/users/search/findByUsernameAndPassword";

        class ExpectedResult {
            public String url;
            public String queryString;
            public User expected;
            public ExpectedResult(String u, String q, User e) {
                url = u;
                queryString = q;
                expected = e;
            }
        }

        ExpectedResult[] expectedResults = {
                // Make sure that an empty query string returns nothing
                new ExpectedResult(userSearchURL, "?username=AliceName&password=passworda", alice),
                new ExpectedResult(userSearchURL, "?username=BobName&password=passwordb", bob),
                new ExpectedResult(userSearchURL, "?username=Alice&password=passwordb", null),
        };

        for ( ExpectedResult e : expectedResults) {
            expectLoginResults(e.url,e.queryString,e.expected);
        }
    }

}
