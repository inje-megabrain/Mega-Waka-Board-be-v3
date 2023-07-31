package mega.waka.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import mega.waka.entity.Member;
import mega.waka.entity.editor.SevenDaysEditor;
import mega.waka.entity.language.SevenDaysLanguage;
import mega.waka.entity.project.SevenDaysProject;
import mega.waka.repository.MemberRepository;
import mega.waka.repository.editor.SevenDaysEditorRepository;
import mega.waka.repository.language.SevenDaysLanguageRepository;
import mega.waka.repository.project.SevendaysProjectRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class SevenDaysWakaService {

    private final SevenDaysEditorRepository sevenDaysEditorRepository;
    private final SevenDaysLanguageRepository sevenDaysLanguageRepository;
    private final SevendaysProjectRepository sevendaysProjectRepository;
    private final MemberRepository memberRepository;

    private Map<String,String> languageList = new HashMap<>();
    private Map<String,String> projectList = new HashMap<>();
    private Map<String,String> editList = new HashMap<>();


    public SevenDaysWakaService(SevenDaysEditorRepository sevenDaysEditorRepository, SevenDaysLanguageRepository sevenDaysLanguageRepository, SevendaysProjectRepository sevendaysProjectRepository, MemberRepository memberRepository) {
        this.sevenDaysEditorRepository = sevenDaysEditorRepository;
        this.sevenDaysLanguageRepository = sevenDaysLanguageRepository;
        this.sevendaysProjectRepository = sevendaysProjectRepository;
        this.memberRepository = memberRepository;
    }
    @JsonIgnore
    public void update_SevenDays(){
        List<Member> members = memberRepository.findAll();
        String responseData="";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl ="https://wakatime.com/api/v1/users/current/summaries?range=last_7_days";
            for (Member member : members) {

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(member.getSecretKey(),"");

                ResponseEntity<String> response = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );
                responseData = response.getBody();

                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(responseData);
                JSONObject cumulative_total  = (JSONObject) jsonObject.get("cumulative_total");
                member.setSevenDays(cumulative_total.get("text").toString());

                memberRepository.save(member);

                DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
                if(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN).equals("금요일") && !member.getMoney().getUpdateDate().isEqual(LocalDate.now())){
                    int money = member.getMoney().getAmount();
                    String [] time = member.getSevenDays().split(" ");
                    int hour = Integer.valueOf(time[0]);
                    money += (hour *9620)/10000;
                    member.getMoney().setAmount(money);
                    member.getMoney().setUpdateDate(LocalDate.now());
                    memberRepository.save(member);
                }

            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
