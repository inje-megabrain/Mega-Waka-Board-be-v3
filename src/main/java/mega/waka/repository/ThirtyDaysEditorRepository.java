package mega.waka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirtyDaysEditorRepository extends JpaRepository<ThirtyDaysEditorRepository,Long> {
}
