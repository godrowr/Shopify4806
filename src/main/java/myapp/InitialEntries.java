package myapp;


import myapp.product.Product;
import myapp.product.ProductRepository;
import myapp.shop.Shop;
import myapp.shop.ShopRepository;
import myapp.user.User;
import myapp.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class InitialEntries implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialEntries.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args)  {
        User u1 = new User("Paul", "pass");
        userRepository.save(u1);

        User u2 = new User("Robert", "password");
        userRepository.save(u2);

        Shop s1 = new Shop("BladeCity", "Blades", "#knifegang", u1);
        shopRepository.save(s1);

        Shop s2 = new Shop("Sharks", "Marine Life", "#Sharkgang", u2);
        shopRepository.save(s2);

        Shop s3 = new Shop("Swords'r'us", "Blades", "#swordgang", u1);
        shopRepository.save(s3);

        Product p1 = new Product("Ballistic Knife", "This bad boi travels at the same speed I walk", "https://knifeio.com", 1, 1337d, s1);
        productRepository.save(p1);

        Product p2 = new Product("White Bullshark", "Speedy lad", "https://sharky.io", 1, 0xB100DP0D, s2); // have fun figuring out how I put a P inside a hex literal :^)
        productRepository.save(p2);

    }
}