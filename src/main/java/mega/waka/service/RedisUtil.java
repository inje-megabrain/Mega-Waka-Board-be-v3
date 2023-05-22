package mega.waka.service;

import mega.waka.entity.Member;
import mega.waka.entity.redis.SevenDaysResultHistory;
import mega.waka.entity.redis.ThirtyDaysResultHistory;
import mega.waka.repository.MemberRepository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RedisUtil {
    private final MemberRepository memberRepository;
    private final RedisTemplate<String,SevenDaysResultHistory> redisTemplate;
    private final RedisTemplate<String,ThirtyDaysResultHistory> redisTemplateThirtyDays;


    public RedisUtil(MemberRepository memberRepository, RedisTemplate<String, SevenDaysResultHistory> redisTemplate, RedisTemplate<String, ThirtyDaysResultHistory> redisTemplateThirtyDays) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
        this.redisTemplateThirtyDays = redisTemplateThirtyDays;
    }

    public void save_Redis_SevenDays() {
        List<Member> members = memberRepository.findAll();
        for(Member member : members){
            SevenDaysResultHistory sevenDaysResultHistory = new SevenDaysResultHistory();
            sevenDaysResultHistory.setSevenDaysEditors(member.getSeveneditors());
            sevenDaysResultHistory.setSevenDaysProjects(member.getSevenprojects());
            sevenDaysResultHistory.setSevenDaysLanguages(member.getSevenlanguages());
            sevenDaysResultHistory.setId(String.valueOf(member.getId()));

            ValueOperations<String,SevenDaysResultHistory> valueOperations = redisTemplate.opsForValue();
            Duration expiration = Duration.ofMinutes(10);
            valueOperations.set(member.getName(),sevenDaysResultHistory,expiration);
        }

    }
    public void save_Redis_ThirtyDays(){
        List<Member> members = memberRepository.findAll();
        for(Member member : members){
            ThirtyDaysResultHistory thirtyDaysResultHistory = new ThirtyDaysResultHistory();
            thirtyDaysResultHistory.setThirtyDaysEditors(member.getThirtyDaysEditors());
            thirtyDaysResultHistory.setThirtyDaysProjects(member.getThirtyDaysProjects());
            thirtyDaysResultHistory.setThirtyDaysLanguages(member.getThirtyDaysLanguages());
            thirtyDaysResultHistory.setId(String.valueOf(member.getId()));

            ValueOperations<String,ThirtyDaysResultHistory> valueOperations = redisTemplateThirtyDays.opsForValue();
            Duration expiration = Duration.ofMinutes(10);
            valueOperations.set(member.getName(),thirtyDaysResultHistory,expiration);
            System.out.println("valueOperations.get() = " + valueOperations.get(member.getName()));
        }
    }
    public Optional<SevenDaysResultHistory> get_Redis_SevenDays(UUID id){
        Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(()->{
            return new IllegalArgumentException("해당 유저가 없습니다.");
        });
        ValueOperations<String,SevenDaysResultHistory> valueOperations = redisTemplate.opsForValue();
        return Optional.ofNullable(valueOperations.get(member.get().getName()));

    }
    public Optional<ThirtyDaysResultHistory> get_Redis_ThirtyDays(UUID id){
        Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(()->{
            return new IllegalArgumentException("해당 유저가 없습니다.");
        });
        ValueOperations<String,ThirtyDaysResultHistory> valueOperations = redisTemplateThirtyDays.opsForValue();
        return Optional.ofNullable(valueOperations.get(member.get().getName()));
    }

}
