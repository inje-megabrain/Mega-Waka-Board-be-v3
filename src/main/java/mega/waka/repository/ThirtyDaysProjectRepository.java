package mega.waka.repository;

import mega.waka.entity.ThirtyDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirtyDaysProjectRepository extends JpaRepository<ThirtyDaysProject,Long> {
}
