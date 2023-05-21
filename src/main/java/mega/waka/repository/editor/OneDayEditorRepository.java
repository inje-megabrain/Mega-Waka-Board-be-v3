package mega.waka.repository.editor;

import mega.waka.entity.editor.OneDaysEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OneDayEditorRepository extends JpaRepository<OneDaysEditor,Long> {
}
