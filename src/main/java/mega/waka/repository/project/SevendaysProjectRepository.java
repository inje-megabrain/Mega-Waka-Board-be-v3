package mega.waka.repository.project;


import mega.waka.entity.project.SevenDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SevendaysProjectRepository extends JpaRepository<SevenDaysProject, Long> {
}
