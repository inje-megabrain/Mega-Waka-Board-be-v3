package mega.waka.repository.editor;

import mega.waka.entity.editor.FourteenDaysEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FourteenDaysEditorRepository extends JpaRepository<FourteenDaysEditor,Long> {
}
