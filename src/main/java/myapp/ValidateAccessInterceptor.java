package myapp;

import myapp.product.Product;
import myapp.product.ProductRepository;
import myapp.shop.Shop;
import myapp.shop.ShopRepository;
import myapp.user.User;
import myapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class ValidateAccessInterceptor implements HandlerInterceptor {
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String HTTPMethod = request.getMethod();
        if (HTTPMethod.equals("GET")) {
            // TODO: Validate access to information
            return true;
        }

        String URI = request.getRequestURI();
        System.out.println("Intercepted: "+URI);
        String repoPath = URI.split("/")[1];
        System.out.println("repoPath: "+repoPath);

        if (!(repoPath.equals("users") || repoPath.equals("shops") || repoPath.equals("products"))) {
            return true;
        }



        Cookie cookie = WebUtils.getCookie(request, "userid");
        boolean hasCookie = cookie != null;
        boolean newEntity = URI.split("/").length == 2;


        if (!hasCookie) {
            // Exception to the authentication rule. Anyone can create a new user!
            if (newEntity && repoPath.equals("users")) {
                return true;
            }
            response.setStatus(401);
            return false;
        }

        Long userId = Long.valueOf(cookie.getValue());
        Optional<User> requester = userRepository.findById(userId);
        if (!requester.isPresent()) {
            response.setStatus(401);
            return false;
        }

        if (newEntity) {
            // Creating a new entity
            // TODO: Validate that the new entity has the correct owner
            return true;
        }


        // Editing an existing entity
        Long entityId = Long.valueOf(URI.split("/")[2]);
        if (repoPath.equals("shops")) {
            Optional<Shop> shop = shopRepository.findById(entityId);
            if (shop.isPresent()) {
                if (shop.get().getOwnedBy().getId().equals(requester.get().getId())) {
                    return true;
                } else {
                    response.setStatus(403);
                    return false;
                }
            } else {
                return true;
            }
        } else if (repoPath.equals("users")) {
            Optional<User> user = userRepository.findById(entityId);
            if (user.isPresent()) {
                if (user.get().getId().equals(requester.get().getId())) {
                    return true;
                } else {
                    response.setStatus(403);
                    return false;
                }
            } else {
                // Continue with normal handling
                return true;
            }
        } else if (repoPath.equals("products")) {
            Optional<Product> product = productRepository.findById(entityId);
            if (product.isPresent()) {
                User owner = product.get().getShop().getOwnedBy();
                if (owner.getId().equals(requester.get().getId())) {
                    return true;
                } else {
                    response.setStatus(403);
                    return false;
                }
            } else {
                return true;
            }
        }

        // Fail-safe defaults
        return false;
    }
}
