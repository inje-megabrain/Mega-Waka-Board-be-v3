package mega.waka.repository.editor;

import mega.waka.entity.editor.SevenDaysEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SevenDaysEditorRepository extends JpaRepository<SevenDaysEditor, Long> {
}
