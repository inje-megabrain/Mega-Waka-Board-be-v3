package mega.waka.repository.project;

import mega.waka.entity.project.OneDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OneDayProjectRepository extends JpaRepository<OneDaysProject,Long> {
}

