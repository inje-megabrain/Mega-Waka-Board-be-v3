package mega.waka.repository.project;

import mega.waka.entity.project.OneDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OneDayProjectRepository extends JpaRepository<OneDaysProject,Long> {
}

