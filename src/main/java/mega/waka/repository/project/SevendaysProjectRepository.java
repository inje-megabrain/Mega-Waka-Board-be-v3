package mega.waka.repository.project;


import mega.waka.entity.Member;
import mega.waka.entity.project.SevenDaysProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SevendaysProjectRepository extends JpaRepository<SevenDaysProject, Long> {

    List<SevenDaysProject> findByMemberId(UUID memberId);
}
