package myapp.product;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
    Product findByName(String name);

    Product findByInventoryNumber(Integer inventoryNumber);

    Iterable<Product> findByShop(Long shop);
}
