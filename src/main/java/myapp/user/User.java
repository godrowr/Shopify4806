package myapp.user;

import myapp.product.Product;
import myapp.shop.Shop;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "username must not be empty")
    private String username;
    @NotBlank(message = "password must not be empty")
    private String password;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "ownedBy")
    private List<Shop> shops = new ArrayList<>();

    @ManyToMany
    private Set<Product> shoppingCartItems = new HashSet<>();

    public User() {

    }

    public User(String username, String password ) {
        this.username = username;
        this.password = password;
    }

    public List<Shop> getShops(){
        return shops;
    }

    public void addShop(Shop shop){
        shops.add(shop);

    }

    public void removeShop(Long id){
        shops.removeIf(shop -> shop.getId().equals(id));
    }

    public void removeShop(Shop shop){
        shops.remove(shop);
    }

    public Set<Product> getShoppingCartItems(){
        return shoppingCartItems;
    }

    public void addShoppingCartItem(Product shoppingCartItem){ // TODO: detect if there is an existing item of the same product
        shoppingCartItems.add(shoppingCartItem);
    }

    public void removeShoppingCartItem(Long id){
        shoppingCartItems.removeIf(shoppingCartItem -> shoppingCartItem.getId().equals(id));

    }

    public void removeShoppingCartItem(Product shoppingCartItem){
        shoppingCartItems.remove(shoppingCartItem);

    }

    public Long getId(){
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, username='%s', password='%s']",
                id, username, password);
    }


}
