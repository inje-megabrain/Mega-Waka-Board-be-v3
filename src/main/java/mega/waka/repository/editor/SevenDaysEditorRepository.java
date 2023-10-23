package mega.waka.repository.editor;

import mega.waka.entity.Member;
import mega.waka.entity.editor.SevenDaysEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SevenDaysEditorRepository extends JpaRepository<SevenDaysEditor, Long> {

    List<SevenDaysEditor> findByMemberId(UUID memberId);


}
