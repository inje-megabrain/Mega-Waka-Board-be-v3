package mega.waka.repository.project;

import mega.waka.entity.project.ThirtyDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirtyDaysProjectRepository extends JpaRepository<ThirtyDaysProject,Long> {
}
