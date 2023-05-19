package mega.waka.repository;

import mega.waka.entity.SevenDaysEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SevenDaysEditorRepository extends JpaRepository<SevenDaysEditor, Long> {
}
