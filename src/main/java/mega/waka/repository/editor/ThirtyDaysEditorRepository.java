package mega.waka.repository.editor;

import mega.waka.entity.editor.ThirtyDaysEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirtyDaysEditorRepository extends JpaRepository<ThirtyDaysEditor,Long> {
}
