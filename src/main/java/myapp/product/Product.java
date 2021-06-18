package myapp.product;
import myapp.shop.Shop;
import myapp.user.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;
    private String imageUrl;
    @Min(value = 0, message = "The value must be positive")
    @NotNull(message = "inventoryNumber must not be null")
    private Integer inventoryNumber;
    @NotBlank(message = "username is mandatory")
    private String name;
    @Min(value = 0, message = "The value must be positive")
    @NotNull(message = "price must not be null")
    private Double price;

    @ManyToOne
    @JoinColumn(name="SHOP_ID")
    private Shop shop;

    @ManyToMany(mappedBy = "shoppingCartItems")
    private Set<User> users = new HashSet<>();;

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(Integer inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public Product() {

    }

    public Product(String name, String description, String imageUrl, Integer inventoryNumber, Double price, Shop shop) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.inventoryNumber = inventoryNumber;
        this.price = price;
        this.shop = shop;
    }

    @Override
    public String toString() {
        return String.format(
                "Product[id=%d, name='%s', imageUrl='%s', inventoryNumber='%d', shop='%d']",
                id, name, imageUrl, inventoryNumber, shop);
    }

}
