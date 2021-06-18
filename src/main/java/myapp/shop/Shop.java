package myapp.shop;
import myapp.product.Product;
import myapp.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String category;
    @NotBlank(message = "name must not be empty")
    private String name;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User ownedBy;

    private String tag;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="shop")
    private List<Product> productList = new ArrayList<>();

    public Shop(){

    }

    public Shop(String name, String category, String tag, User ownedBy){
        this.category = category;
        this.name = name;
        this.ownedBy = ownedBy;
        this.tag = tag;
    }

    public Shop(String name, String category, String tag, User ownedBy, Long id){
        this.category = category;
        this.name = name;
        this.ownedBy = ownedBy;
        this.tag = tag;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(User ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void addProduct(Product product){
        productList.add(product);
    }

    public List<Product> getProducts(){
        return productList;
    }

    public void removeProduct(Product product){
        productList.remove(product);
    }

    @Override
    public String toString() {
        return String.format(
                "Shop[id=%d, name='%s', category='%s', tag='%s', ownedBy='%s']",
                id, name, category, tag, ownedBy);
    }

}
