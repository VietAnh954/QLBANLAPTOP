package vn.hoidanit.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hoidanit.laptopshop.domain.User;
//import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);

    // Lưu người dùng
    User save(User hoidanit);

    // Xóa người dùng theo id
    void deleteById(long id);

    // Tìm kiếm người dùng theo id
    User findById(long id);

}
