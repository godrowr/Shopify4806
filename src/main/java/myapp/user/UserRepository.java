package myapp.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByid(@Param("id") Long id);
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}

