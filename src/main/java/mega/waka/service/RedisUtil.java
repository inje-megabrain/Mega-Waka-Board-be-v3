package mega.waka.service;

import mega.waka.entity.Member;
import mega.waka.entity.redis.SevenDaysResultHistory;
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


    public RedisUtil(MemberRepository memberRepository, RedisTemplate<String, SevenDaysResultHistory> redisTemplate) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
    }

    public void save_Redis_SevenDays() {
        List<Member> members = memberRepository.findAll();
        for(Member member : members){
            SevenDaysResultHistory sevenDaysResultHistory = new SevenDaysResultHistory();
            sevenDaysResultHistory.setSevenDaysEditors(member.getSeveneditors());
            sevenDaysResultHistory.setSevenDaysProjects(member.getSevenprojects());
            sevenDaysResultHistory.setSevenDaysLanguages(member.getSevenlanguages());
            sevenDaysResultHistory.setId(String.valueOf(member.getId()));

            ValueOperations<String,SevenDaysResultHistory> values = redisTemplate.opsForValue();
            Duration expiration = Duration.ofMinutes(10);
            values.set(member.getName(),sevenDaysResultHistory,expiration);
        }

    }
    /*public void save_Redis_ThirtyDays(){
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
        }
    }*/
    public Optional<SevenDaysResultHistory> get_Redis_SevenDays(UUID id){
        Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(()->{
            return new IllegalArgumentException("해당 유저가 없습니다.");
        });
        ValueOperations<String,SevenDaysResultHistory> values = redisTemplate.opsForValue();
        if(values.get(member.get().getName()) == null) {
            SevenDaysResultHistory sevenDaysResultHistory = new SevenDaysResultHistory();
            sevenDaysResultHistory.setSevenDaysLanguages(member.get().getSevenlanguages());
            sevenDaysResultHistory.setSevenDaysEditors(member.get().getSeveneditors());
            sevenDaysResultHistory.setSevenDaysProjects(member.get().getSevenprojects());
            sevenDaysResultHistory.setId(String.valueOf(member.get().getId()));
            return Optional.ofNullable(sevenDaysResultHistory);
        }else return Optional.ofNullable(values.get(member.get().getName()));

    }
    /*public Optional<ThirtyDaysResultHistory> get_Redis_ThirtyDays(UUID id){
        Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(()->{
            return new IllegalArgumentException("해당 유저가 없습니다.");
        });
        ValueOperations<String,ThirtyDaysResultHistory> valueOperations = redisTemplateThirtyDays.opsForValue();
        if(valueOperations.get(member.get().getName()) == null){
            ThirtyDaysResultHistory thirtyDaysResultHistory = new ThirtyDaysResultHistory();
            thirtyDaysResultHistory.setThirtyDaysEditors(member.get().getThirtyDaysEditors());
            thirtyDaysResultHistory.setThirtyDaysProjects(member.get().getThirtyDaysProjects());
            thirtyDaysResultHistory.setThirtyDaysLanguages(member.get().getThirtyDaysLanguages());
            thirtyDaysResultHistory.setId(String.valueOf(member.get().getId()));
            return Optional.ofNullable(thirtyDaysResultHistory);
        }else return Optional.ofNullable(valueOperations.get(member.get().getName()));
    }*/

}
