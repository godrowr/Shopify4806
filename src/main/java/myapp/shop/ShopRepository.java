package myapp.shop;


import myapp.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "shops", path = "shops")
public interface ShopRepository extends PagingAndSortingRepository<Shop, Long> {
    Iterable<Shop> findByNameIgnoreCase(@Param("name") String name);

    Iterable<Shop> findByOwnedByUsernameIgnoreCase(@Param("username") String username);

    Iterable<Shop> findByCategoryIgnoreCase(@Param("category") String category);

    Iterable<Shop> findByTagIgnoreCase(@Param("tag") String tag);

    @Query(value = "SELECT s FROM Shop s WHERE s.name LIKE %?1%")
    Iterable<Shop> findShopsStartingWith(@Param("name") String name);
}
