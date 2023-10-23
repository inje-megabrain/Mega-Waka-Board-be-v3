package mega.waka.repository;

import mega.waka.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Member findByNameAndOrganization(String name, String organization);
    @EntityGraph(attributePaths = "seveneditors")
    @Query("SELECT m FROM Member m WHERE m.name = :name")
    Member findMemberByNameWithSevenEditors(@Param("name") String name);

    @EntityGraph(attributePaths = "sevenlanguages")
    @Query("SELECT m FROM Member m WHERE m.name = :name")
    Member findMemberByNameWithSevenLanguages(@Param("name") String name);

    @EntityGraph(attributePaths = "sevenprojects")
    @Query("SELECT m FROM Member m WHERE m.name = :name")
    Member findMemberByNameWithSevenProjects(@Param("name") String name);
    Member findByName (String name);
}
