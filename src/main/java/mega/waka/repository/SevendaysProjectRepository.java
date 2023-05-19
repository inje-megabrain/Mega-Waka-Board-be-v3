package mega.waka.repository;


import mega.waka.entity.SevenDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SevendaysProjectRepository extends JpaRepository<SevenDaysProject, Long> {
}
