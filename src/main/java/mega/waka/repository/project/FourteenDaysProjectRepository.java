package mega.waka.repository.project;

import mega.waka.entity.project.FourteenDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FourteenDaysProjectRepository extends JpaRepository<FourteenDaysProject,Long> {
}
