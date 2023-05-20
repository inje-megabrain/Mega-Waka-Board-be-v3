package mega.waka.repository;

import mega.waka.entity.ThirtyDaysAddMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirtyDaysMemberRepository extends JpaRepository<ThirtyDaysAddMember,Long> {
}
